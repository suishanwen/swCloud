package com.sw.vote.service;

import com.sw.vote.cache.BackGroundCache;
import com.sw.vote.cache.ClientDirectCache;
import com.sw.vote.cache.VoteProjectCache;
import com.sw.vote.config.props.UserProps;
import com.sw.vote.model.BackgroundData;
import com.sw.vote.model.entity.VoteProject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class VoteProjectSerivce {


    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    ClientDirectCache clientDirectCache;
    @Autowired
    VoteProjectCache voteProjectCache;
    @Autowired
    BackGroundCache backGroundCache;
    @Autowired
    UserProps userProps;

    public List<VoteProject> query() {
        clientDirectCache.fetchIncr();
        return voteProjectCache.get();
    }

    public String queryLite(Integer mgr) {
        clientDirectCache.fetchIncr();
        if (mgr == null || mgr != 1) {
            return voteProjectCache.getZip();
        } else {
            return voteProjectCache.getZipAll();
        }
    }

    public String borrow(String user, String url) {
        List<String> idList;
        BackgroundData backgroundData = backGroundCache.find(url);
        if (backgroundData == null || System.currentTimeMillis() - backgroundData.getMills() > 60 * 1000) {
            String html = restTemplate.getForObject(url, String.class);
            if (html != null && url.contains("mmrj")) {
                Elements tableElements = Jsoup.parse(html).select("tr[class='t1'],tr[class='t2']").select("td:eq(0)>font");
                idList = tableElements.stream().map(Element::text).collect(Collectors.toList());
                backgroundData = new BackgroundData(idList, System.currentTimeMillis());
                backGroundCache.set(url, backgroundData);
            }
            if (html != null && url.contains("120.25.13.127")) {
                Elements tableElements = Jsoup.parse(html).select("tr[bgcolor='White'],tr[bgcolor='Azure']").select("td:eq(0)").select("font[color='Blue']");
                idList = tableElements.stream().map(Element::text).collect(Collectors.toList());
                backgroundData = new BackgroundData(idList, System.currentTimeMillis());
                backGroundCache.set(url, backgroundData);
            }
        }
        if (backgroundData != null) {
            idList = backgroundData.getOnlineIdList();
            return randomId(user, idList);
        }
        return "";
    }

    private String randomId(String user, List<String> idList) {
        if (user.equals(userProps.getManager1())) {
            idList = idList.stream().filter(id -> id.contains("TX-18-") || id.contains("AQ-14-") || id.contains("Q7-43-")).collect(Collectors.toList());
        } else {
            idList = idList.stream().filter(id -> id.contains("TX-111-") || id.contains("AQ-111-") || id.contains("Q7-111-")).collect(Collectors.toList());
        }
        int len = idList.size();
        if (len > 0) {
            return idList.get(new Random().nextInt(len));
        }
        return "";
    }

    public String corsGet(String url) {
        return restTemplate.getForObject(url, String.class);
    }
}
