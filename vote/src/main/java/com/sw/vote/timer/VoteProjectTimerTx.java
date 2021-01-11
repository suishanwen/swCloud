package com.sw.vote.timer;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.sw.vote.cache.ActiveNode;
import com.sw.vote.cache.TimedCache;
import com.sw.vote.cache.TimedCaches;
import com.sw.vote.cache.VoteProjectCache;
import com.sw.vote.config.props.ScheduledInterval;
import com.sw.vote.config.props.ServiceUrl;
import com.sw.vote.model.entity.VoteProject;
import com.sw.vote.service.ClientDirectService;
import com.sw.vote.util.ScheduledExecutorUtil;
import com.sw.vote.util.ZipUtil;
import org.apache.logging.log4j.util.Strings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VoteProjectTimerTx {

    @Autowired
    private ClientDirectService clientDirectService;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    VoteProjectCache voteProjectCache;
    @Autowired
    ScheduledInterval scheduledInterval;
    @Autowired
    TimedCaches timedCaches;
    @Autowired
    private ActiveNode activeNode;
    @Autowired
    private ServiceUrl serviceUrl;

    private final String service = "TX";
    private final Pattern pattern = Pattern.compile("\\d{3}");
    private boolean running = false;

    public void run() {
        ActiveNode.addService(service);
        Runnable runnable = () -> {
            if (running) {
                return;
            }
            running = true;
            try {
                TimedCache timedCache = timedCaches.get("listTx");
                if (!timedCache.expired(timedCaches.getDuration())) {
                    saveVoteProject(new LinkedList<>(Objects.requireNonNull(JSON.parseArray(ZipUtil.decompress(timedCache.cache()), VoteProject.class))));
                    return;
                }
                String html = getHtml();
                LinkedList<VoteProject> voteProjectList = analyzeHtml(html);
                saveVoteProject(voteProjectList);
                voteProjectCache.checkDownload(voteProjectList);
                timedCache.set(ZipUtil.compress(JSON.toJSONString(voteProjectList)));
                timedCaches.put("listTx", timedCache);
            } catch (Exception e) {
                if (!e.getMessage().contains("SocketTimeoutException")
                        && !e.getMessage().contains("Connection refused")
                        && !e.getMessage().contains("Connection reset")) {
                    clientDirectService.bugReport("server-tx", e.getMessage());
                }
            } finally {
                running = false;
            }
        };
        ScheduledExecutorUtil.scheduleAtFixedRate(runnable, 0, scheduledInterval.getSpider());
    }

    private String getHtml() {
        String dataApi = serviceUrl.getTx();
        String url = dataApi + "?t=" + Math.random();
        Charset charset = Charset.forName("gb2312");
        url = new String(url.getBytes(StandardCharsets.UTF_8), charset);
        return restTemplate.getForObject(url, String.class);
    }

    private LinkedList<VoteProject> analyzeHtml(String html) {
        Calendar cale = Calendar.getInstance();
        int month = cale.get(Calendar.MONTH) + 1;
        LinkedList<VoteProject> voteProjectList = Lists.newLinkedList();
        Elements tableElements = Jsoup.parse(html).select("table[style=\"border-left:1px solid #009cec; border-right:1px solid #009cec; background-color:#FFF\"]").select("tr");
        Date date = new Date();
        for (Element tableElement : tableElements) {
            Elements tdElements = tableElement.select("td");
            Element backgroundTd = tdElements.get(12);
            String backGrounInfo = backgroundTd.text();
            if (backGrounInfo != null) {
                if (backGrounInfo.toUpperCase().contains("Q")) {
                    continue;
                }
                Matcher matcher = pattern.matcher(backGrounInfo);
                if (matcher.find()) {
                    backGrounInfo = matcher.group(0);
                } else {
                    backGrounInfo = null;
                }
            }
            Integer backGroundNo = Strings.isNotEmpty(backGrounInfo) ? Integer.parseInt(backGrounInfo) : null;
            int ipDial = tdElements.get(4).text().contains("不换") ? 0 : 1;
            double price = Double.parseDouble(tdElements.get(5).text());
            String downloadAddress = tdElements.get(9).selectFirst("a").attr("href").replace(" ", "");
            String backgroundAddress = tdElements.get(8).selectFirst("a").attr("href").replace(" ", "");
            String projectName = tdElements.get(2).text().trim();
            int hot = 0;
            if (!tdElements.get(3).text().contains("新任务") && !tdElements.get(3).text().contains("未知")) {
                hot = Double.valueOf(tdElements.get(3).text().split("/")[0].substring(1).trim()).intValue();
            }
            String quantityInfo = tdElements.get(7).attr("title").split(String.format("%d-", month))[0];
            long finishQuantity = 0;
            long totalRequire = 0;
            if (quantityInfo.contains("/")) {
                try {
                    finishQuantity = Long.parseLong(quantityInfo.split("/")[0]);
                } catch (Exception ignored) {
                }
                try {
                    totalRequire = Long.parseLong(quantityInfo.split("/")[1]);
                } catch (Exception ignored) {
                }
            }
            long remains = Long.parseLong(tdElements.get(7).text());
            VoteProject voteProject = new VoteProject(projectName, ipDial, hot, price, totalRequire, finishQuantity, remains, backGroundNo, backgroundAddress, downloadAddress, "TX", date);
            voteProjectList.add(voteProject);
        }
        return voteProjectList;
    }

    private void saveVoteProject(LinkedList<VoteProject> voteProjectList) {
        voteProjectCache.setListTx(voteProjectList);
        activeNode.setState(service);
    }
}


