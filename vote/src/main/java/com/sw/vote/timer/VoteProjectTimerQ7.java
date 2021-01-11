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
import java.util.Date;
import java.util.LinkedList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class VoteProjectTimerQ7 {

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

    private final String service = "Q7";
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
                TimedCache timedCache = timedCaches.get("listQ7");
                if (!timedCache.expired(timedCaches.getDuration())) {
                    saveVoteProject(new LinkedList<>(Objects.requireNonNull(JSON.parseArray(ZipUtil.decompress(timedCache.cache()), VoteProject.class))));
                    return;
                }
                String html = getHtml();
                LinkedList<VoteProject> voteProjectList = analyzeHtml(html);
                saveVoteProject(voteProjectList);
                voteProjectCache.checkDownload(voteProjectList);
                timedCache.set(ZipUtil.compress(JSON.toJSONString(voteProjectList)));
                timedCaches.put("listQ7", timedCache);
            } catch (Exception e) {
                if (!e.getMessage().contains("SocketTimeoutException")
                        && !e.getMessage().contains("Connection refused")
                        && !e.getMessage().contains("Connection reset")) {
                    clientDirectService.bugReport("server-q7", e.getMessage());
                }
            } finally {
                running = false;
            }
        };
        ScheduledExecutorUtil.scheduleAtFixedRate(runnable, 0, scheduledInterval.getSpider());
    }

    private String getHtml() {
        String dataApi = serviceUrl.getQ7();
        String url = dataApi + "?t=" + Math.random();
        Charset charset = Charset.forName("gb2312");
        url = new String(url.getBytes(StandardCharsets.UTF_8), charset);
        return restTemplate.getForObject(url, String.class);
    }


    private LinkedList<VoteProject> analyzeHtml(String html) {
        LinkedList<VoteProject> voteProjectList = Lists.newLinkedList();
        Elements tableElements = Jsoup.parse(html).select("table").select("tr");
        Date date = new Date();
        for (int i = 1; i < tableElements.size(); i++) {
            Elements tdElements = tableElements.get(i).select("td");
            Element projectTd = tdElements.get(1);
            Element amountTd = tdElements.get(2);
            Element backgroundTd = tdElements.get(3);
            String backGrounInfo = backgroundTd.text();
            if (backGrounInfo != null) {
                if (backGrounInfo.toUpperCase().contains("Q")) {
                    continue;
                }
                while (true) {
                    Matcher matcher = patternD4.matcher(backGrounInfo);
                    String text = "";
                    if (matcher.find()) {
                        text = matcher.group(0);
                    } else {
                        matcher = patternD3.matcher(backGrounInfo);
                        if (matcher.find()) {
                            text = matcher.group(0);
                        } else {
                            backGrounInfo = null;
                        }
                    }
                    if (!backGrounInfo.contains(text + "换")) {
                        backGrounInfo = text;
                        break;
                    } else {
                        backGrounInfo = backGrounInfo.replace(text + "换", "");
                    }
                }
            }
            Integer backGroundNo = Strings.isNotEmpty(backGrounInfo) ? Integer.parseInt(backGrounInfo) : null;
            Elements projectInfo = projectTd.select("span");
            Elements addressInfo = projectTd.select("a");
            String[] autoInfo = projectInfo.get(2).selectFirst("span").text().split("-");
            if (!"自动".equals(autoInfo[0])) {
                continue;
            }
            int ipDial = autoInfo[1].contains("不换") ? 0 : 1;
            double price = Double.parseDouble(projectInfo.get(1).select("span").text().split("/")[0]);
            Element projectElement = projectInfo.get(0);
            String downloadAddress = addressInfo.get(0).attr("href");
            String backgroundAddress = addressInfo.get(1).attr("href");
            String projectName = projectElement.text().split("-")[3].replace("_" + backGroundNo, "");
            if (projectName.contains("公告")) {
                continue;
            }
            String[] amountInfo = amountTd.text().split("：");
            int hot = 0;
            if (!amountInfo[1].contains("新任务") && !amountInfo[1].contains("未知")) {
                hot = Double.valueOf(amountInfo[1].split(" ")[0]).intValue();
            }
            long finishQuantity = Long.parseLong(amountInfo[2].split("/")[0]);
            long totalRequire = Long.parseLong(amountInfo[2].split("/")[1]);
            long remains = totalRequire >= finishQuantity ? totalRequire - finishQuantity : 0;
            VoteProject voteProject = new VoteProject(projectName, ipDial, hot, price, totalRequire, finishQuantity, remains, backGroundNo, backgroundAddress, downloadAddress, "Q7", date);
            voteProjectList.add(voteProject);
        }
        return voteProjectList;
    }

    private void saveVoteProject(LinkedList<VoteProject> voteProjectList) {
        voteProjectCache.setListQ7(voteProjectList);
        activeNode.setState(service);
    }
}


