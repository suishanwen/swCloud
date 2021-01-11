package com.sw.vote.timer;

import com.alibaba.fastjson.JSON;
import com.sw.vote.cache.ActiveNode;
import com.sw.vote.cache.ClientDirectCache;
import com.sw.vote.cache.VoteProjectCache;
import com.sw.vote.config.props.ScheduledInterval;
import com.sw.vote.model.entity.VoteProject;
import com.sw.vote.service.ClientDirectService;
import com.sw.vote.util.ScheduledExecutorUtil;
import com.sw.vote.util.ZipUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;


@Service
public class VoteProjectTimerMerge {

    private boolean running = false;
    @Autowired
    VoteProjectCache voteProjectCache;
    @Autowired
    ScheduledInterval scheduledInterval;
    @Autowired
    private ActiveNode activeNode;
    @Autowired
    private ClientDirectService clientDirectService;
    private final String service = "MERGE";

    public void run() {
        ActiveNode.addService(service);
        Runnable runnable = () -> {
            if (running) {
                return;
            }
            running = true;
            try {
                voteProjectCache.merge();
                activeNode.setState(service);
                ClientDirectCache.resetDate();
            } catch (Exception e) {
                clientDirectService.bugReport("MERGE", e.getMessage());
            } finally {
                running = false;
            }
        };
        ScheduledExecutorUtil.scheduleAtFixedRate(runnable, 5, scheduledInterval.getSpider());
    }
}


