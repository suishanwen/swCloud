package com.sw.vote.cache;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sw.vote.config.props.UserProps;
import com.sw.vote.model.entity.VoteProject;
import com.sw.vote.util.StringUtils;
import com.sw.vote.util.ZipUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class VoteProjectCache {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    TimedCaches timedCaches;
    @Autowired
    TimedDlCaches timedDlCaches;
    @Autowired
    UserProps userProps;

    private String listZip = null;
    private String listZipAll = null;
    private LinkedList<VoteProject> list = Lists.newLinkedList();
    private LinkedList<VoteProject> listQ7 = Lists.newLinkedList();
    private LinkedList<VoteProject> listTx = Lists.newLinkedList();
    private LinkedList<VoteProject> listAq = Lists.newLinkedList();

    public String getLocked() {
        Object val = redisTemplate.opsForValue().get("locked");
        return val == null ? "" : val.toString();
    }

    public long getLockedTTl() {
        try {
            return redisTemplate.opsForValue().getOperations().getExpire("locked");
        }catch (NullPointerException e){
            return -2;
        }
    }

    public Set<String> getDropped() {
        Set<Object> set = redisTemplate.opsForSet().members("dropped");
        if (set == null) {
            return Sets.newHashSet();
        }
        return set.stream().map(Object::toString).collect(Collectors.toSet());
    }

    public Set<String> getRemoved() {
        Set<Object> set = redisTemplate.opsForSet().members("removed");
        if (set == null) {
            return Sets.newHashSet();
        }
        return set.stream().map(Object::toString).collect(Collectors.toSet());
    }

    public Map<Object, Object> getStrategy() {
        return redisTemplate.opsForHash().entries("strategy");
    }

    public void setLocked(String locked) {
        redisTemplate.opsForValue().set("locked", locked, userProps.getLockTTl(), TimeUnit.SECONDS);
    }

    public void setDropped(String dropped, int remove) {
        Lists.newArrayList(dropped.split(",")).forEach(p -> {
            if (remove == 1) {
                redisTemplate.opsForSet().remove("dropped", p);
            } else {
                redisTemplate.opsForSet().add("dropped", p);
            }
        });
    }

    public void setRemoved(String removed, int remove) {
        Lists.newArrayList(removed.split(",")).forEach(p -> {
            if (remove == 1) {
                redisTemplate.opsForSet().remove("removed", p);
            } else {
                redisTemplate.opsForSet().add("removed", p);
            }
        });
    }

    public void setStrategy(Map<Object, Object> strategy) {
        redisTemplate.opsForHash().putAll("strategy", strategy);
    }

    public void addProject(VoteProject voteProject, Integer remove) {
        TimedCache timedCache = timedCaches.get("listMn");
        LinkedList<VoteProject> voteProjects = new LinkedList<>(Objects.requireNonNull(JSON.parseArray(ZipUtil.decompress(timedCache.cache()), VoteProject.class)));
        voteProjects = voteProjects.stream().filter(v -> !v.getProjectName().equals(voteProject.getProjectName()))
                .collect(Collectors.toCollection(LinkedList::new));
        if (remove == null || remove != 1) {
            voteProjects.add(voteProject);
        }
        timedCache.set(ZipUtil.compress(JSON.toJSONString(voteProjects)));
        timedCaches.put("listMn", timedCache);
    }

    public boolean isProjectFiltered(Map<String, Double> projectFilterMap, VoteProject voteProject) {
        String projectName = voteProject.getProjectName();
        double price = voteProject.getPrice();
        return projectFilterMap.entrySet().stream().anyMatch(entry -> projectName.contains(entry.getKey()) && price < entry.getValue());
    }

    public boolean isUrlFiltered(List<String> urlList, VoteProject voteProject) {
        return urlList.stream().anyMatch(url -> voteProject.getBackgroundAddress().contains(url));

    }

    public LinkedList<VoteProject> get() {
        return list;
    }

    public String getZip() {
        return listZip;
    }

    public String getZipAll() {
        return listZipAll;
    }


    public void merge() {
        LinkedList<VoteProject> listMn = new LinkedList<>(Objects.requireNonNull(JSON.parseArray(ZipUtil.decompress(timedCaches.get("listMn").cache()), VoteProject.class)));
        LinkedList<VoteProject> listTmp = Lists.newLinkedList();
        listTmp.addAll(listTx);
        listTmp.addAll(listQ7);
        listTmp.addAll(listAq);
        listTmp.addAll(listMn);
        List<String> keyList = Lists.newArrayList();
        String locked = getLocked();
        Set<String> dropped = getDropped();
        Set<String> removed = getRemoved();
        Map<Object, Object> strategy = getStrategy();
        double priceFilter = Double.parseDouble(StringUtils.getString(strategy.get("priceFilter"), "0"));
        double valueFilter = Double.parseDouble(StringUtils.getString(strategy.get("valueFilter"), "0"));
        String projectFilter = StringUtils.getString(strategy.get("projectFilter"), "");
        Map<String, Double> projectFilterMap = Lists.newArrayList(projectFilter.split("\\|")).stream()
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toMap(item ->
                                item.split("-")[0]
                        , item ->
                                item.split("-").length == 2 ? Double.parseDouble(item.split("-")[1]) : 99999d
                ));
        List<String> urlFilter = Lists.newArrayList(Optional.ofNullable(strategy.get("urlFilter")).orElse("").toString()
                .split("\\|")).stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        list = listTmp.stream()
                .sorted(Comparator.comparing(VoteProject::getPrice, Comparator.reverseOrder()))
                .filter(voteProject -> {
                    boolean inExist = !keyList.contains(voteProject.getProjectName());
                    if (inExist) {
                        keyList.add(voteProject.getProjectName());
                    }
                    return inExist;
                }).peek(voteProject -> {
                    if (voteProject.getProjectName().equals(locked)) {
                        voteProject.setLocked(1);
                    }
                    if (dropped.contains(voteProject.getProjectName())) {
                        voteProject.setDrop(1);
                    }
                    if (removed.contains(voteProject.getProjectName()) || voteProject.invalid()
                            || (voteProject.getIpDial() == 1 && voteProject.getPrice() < priceFilter)
                            || (voteProject.getIpDial() == 1 && !voteProject.remains(valueFilter))
                            || isUrlFiltered(urlFilter, voteProject)
                            || isProjectFiltered(projectFilterMap, voteProject)) {
                        voteProject.setRemoved(1);
                    }
                }).collect(Collectors.toCollection(LinkedList::new));
        listZipAll = ZipUtil.compress(JSON.toJSONString(list));
        listZip = ZipUtil.compress(JSON.toJSONString(list.stream().filter(i -> i.getRemoved() != 1).collect(Collectors.toCollection(LinkedList::new))));
    }

    public void setListQ7(LinkedList<VoteProject> listQ7) {
        this.listQ7 = listQ7;
    }

    public void setListTx(LinkedList<VoteProject> listTx) {
        this.listTx = listTx;
    }

    public void setListAq(LinkedList<VoteProject> listAq) {
        this.listAq = listAq;
    }

    public void checkDownload(LinkedList<VoteProject> list) {
        list.forEach(voteProject -> {
            String url = voteProject.getDownloadAddress();
            if (StringUtils.isNotEmpty(url)) {
                TimedCache timedCache = timedDlCaches.get(url);
                if (timedCache == null || timedCache.expired(timedDlCaches.getDuration())) {
                    redisTemplate.opsForSet().add("listDl", url);
                    timedDlCaches.put(url, new TimedCache("1"));
                    try {
                        logger.info("request download from " + url);
                        restTemplate.getForObject("https://tl.bitcoinrobot.cn/download/?url=" + url, String.class);
                    } catch (Exception e) {
                        logger.warn(e.getMessage());
                    }
                }
            }
        });
    }

    public void removeDownload(String url) {
        String file = url.substring(url.lastIndexOf("/") + 1);
        try {
            logger.info("remove download " + file);
            restTemplate.getForObject("https://tl.bitcoinrobot.cn/delDownload/?file=" + file, String.class);
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }
}
