package com.sw.vote.config;

import com.sw.vote.cache.ActiveNode;
import com.sw.vote.cache.TimedCaches;
import com.sw.vote.cache.TimedDlCaches;
import com.sw.vote.timer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartRunner implements ApplicationRunner {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private VoteProjectTimerQ7 voteProjectTimerQ7;
    @Autowired
    private VoteProjectTimerTx voteProjectTimerTx;
    @Autowired
    private VoteProjectTimerAq voteProjectTimerAq;
    @Autowired
    private VoteProjectTimerMerge voteProjectTimerMerge;
    @Autowired
    private ClientDirectTimerSync clientDirectTimerSync;
    @Autowired
    private ClientDirectTimerReset clientDirectTimerReset;
    @Autowired
    private TimedCaches timedCaches;
    @Autowired
    private TimedDlCaches timedDlCaches;
    @Autowired
    private ActiveNode activeNode;

    @Override
    public void run(ApplicationArguments args) {
        activeNode.register();
        timedCaches.init("listTx", "[]");
        timedCaches.init("listQ7", "[]");
        timedCaches.init("listAq", "[]");
        timedCaches.init("listMn", "[]");
        timedDlCaches.initAll();
        voteProjectTimerTx.run();
        voteProjectTimerQ7.run();
        voteProjectTimerAq.run();
        voteProjectTimerMerge.run();
        clientDirectTimerReset.run();
        clientDirectTimerSync.run();
        logger.info("服务初始化成功!");
    }
}
