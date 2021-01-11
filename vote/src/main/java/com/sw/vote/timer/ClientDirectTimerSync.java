package com.sw.vote.timer;

import com.sw.vote.cache.ClientDirectCache;
import com.sw.vote.config.props.ScheduledInterval;
import com.sw.vote.config.props.UserProps;
import com.sw.vote.mapper.ClientDirectMapper;
import com.sw.vote.model.entity.ClientDirect;
import com.sw.vote.service.ClientDirectService;
import com.sw.vote.util.ScheduledExecutorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class ClientDirectTimerSync {

    @Autowired
    private ClientDirectService clientDirectService;
    @Autowired
    private ClientDirectMapper clientDirectMapper;
    @Autowired
    private ClientDirectCache clientDirectCache;
    @Autowired
    private ScheduledInterval scheduledInterval;

    private boolean isSyncNode() {
        return ClientDirectCache.hostName.equals(scheduledInterval.getSyncNode());
    }

    private boolean running = false;

    public void run() {
        if (scheduledInterval.isInitCache() && isSyncNode()) {
            List<ClientDirect> clientDirectList = clientDirectMapper.selectAllCient();
            clientDirectCache.initCache(clientDirectList);
        }
        Runnable runnable = () -> {
            if (!isSyncNode()) {
                return;
            }
            if (running) {
                return;
            }
            running = true;
            try {
                clientDirectService.synchronize(clientDirectCache.unSynchronized());
            } catch (Exception e) {
                clientDirectService.bugReport("server-sync", e.getMessage());
            }
            running = false;
        };
        ScheduledExecutorUtil.scheduleAtFixedRate(runnable, 0, scheduledInterval.getSync());
    }
}


