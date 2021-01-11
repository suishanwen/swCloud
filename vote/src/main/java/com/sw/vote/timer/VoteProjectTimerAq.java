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
import tk.mybatis.mapper.util.StringUtil;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VoteProjectTimerAq {

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

    private final String service = "AQ";
    private final Pattern patternD4 = Pattern.compile("\\d{4}");
    private final Pattern patternD3 = Pattern.compile("\\d{3}");
    private boolean running = false;

    public void run() {
        ActiveNode.addService(service);
        Runnable runnable = () -> {
            if (running) {
                return;
            }
            running = true;
            try {
                TimedCache timedCache = timedCaches.get("listAq");
                if (!timedCache.expired(timedCaches.getDuration())) {
                    saveVoteProject(new LinkedList<>(Objects.requireNonNull(JSON.parseArray(ZipUtil.decompress(timedCache.cache()), VoteProject.class))));
                    return;
                }
                String html = getHtml();
                LinkedList<VoteProject> voteProjectList = analyzeHtml(html);
                saveVoteProject(voteProjectList);
                voteProjectCache.checkDownload(voteProjectList);
                timedCache.set(ZipUtil.compress(JSON.toJSONString(voteProjectList)));
                timedCaches.put("listAq", timedCache);
            } catch (Exception e) {
                if (!e.getMessage().contains("SocketTimeoutException")
                        && !e.getMessage().contains("Connection refused")
                        && !e.getMessage().contains("Connection reset")) {
                    clientDirectService.bugReport("server-aq", e.getMessage());
                }
            } finally {
                running = false;
            }
        };
        ScheduledExecutorUtil.scheduleAtFixedRate(runnable, 0, scheduledInterval.getSpider());
    }

    private String getHtml() {
        String dataApi = serviceUrl.getAq();
        String url = dataApi + "?t=" + Math.random();
        Charset charset = Charset.forName("gb2312");
        url = new String(url.getBytes(StandardCharsets.UTF_8), charset);
        return restTemplate.getForObject(url, String.class);
    }

    private LinkedList<VoteProject> analyzeHtml(String html) {
        LinkedList<VoteProject> voteProjectList = Lists.newLinkedList();
        Elements tableElements = Jsoup.parse(html).select("table[id='MainContent_GridView2']").select("tr");
        Date date = new Date();
        for (int i = 1; i < tableElements.size(); i++) {
            Elements tdElements = tableElements.get(i).select("td");
            if (tdElements.get(0).text().contains("没有符合条件的任务")) {
                continue;
            }
            Element backgroundTd = tdElements.get(11);
            String backGrounInfo = backgroundTd.text();
            if (backGrounInfo != null) {
                if (backGrounInfo.toUpperCase().contains("Q")) {
                    continue;
                }
                Matcher matcher = patternD4.matcher(tdElements.get(7).text());
                if (matcher.find()) {
                    backGrounInfo = matcher.group(0);
                } else {
                    matcher = patternD3.matcher(tdElements.get(7).text());
                    if (matcher.find()) {
                        backGrounInfo = matcher.group(0);
                    } else {
                        backGrounInfo = null;
                    }
                }
            }
            Integer backGroundNo = Strings.isNotEmpty(backGrounInfo) ? Integer.parseInt(backGrounInfo) : null;
            int ipDial = tdElements.get(4).text().contains("不换") ? 0 : 1;
            double price = Double.parseDouble(tdElements.get(5).text());
            String downloadAddress = tdElements.get(8).select("a").attr("href").trim();
            String backgroundAddress = tdElements.get(7).select("a").attr("href");
            String projectName = tdElements.get(2).text();
            int hot = 0;
            String hotText = tdElements.get(3).text().replace("(", "").replace(")", "");
            if (StringUtil.isNotEmpty(hotText) && !"-".equals(hotText)) {
                hot = Double.valueOf(hotText).intValue();
            }
            Element amountInfoTd = tdElements.get(9);
            String[] amountArr = amountInfoTd.attr("title").split("\n")[0].split("/");
            long finishQuantity = Long.parseLong(amountArr[0]);
            long totalRequire = Long.parseLong(amountArr[1]);
            long remains = Long.parseLong(amountInfoTd.text());
            VoteProject voteProject = new VoteProject(projectName, ipDial, hot, price, totalRequire, finishQuantity, remains, backGroundNo, backgroundAddress, downloadAddress, "AQ", date);
            voteProjectList.add(voteProject);
        }
        return voteProjectList;
    }

    private void saveVoteProject(LinkedList<VoteProject> voteProjectList) {
        voteProjectCache.setListAq(voteProjectList);
        activeNode.setState(service);
    }

}


