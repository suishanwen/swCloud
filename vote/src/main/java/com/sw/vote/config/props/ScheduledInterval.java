package com.sw.vote.config.props;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

@Component
@RefreshScope
public class ScheduledInterval {

    @Value("${scheduled.reset:600}")
    private int reset;

    @Value("${scheduled.resetFor:3600}")
    private int resetFor;

    @Value("${scheduled.sync:120}")
    private int sync;

    @Value("${scheduled.spider:15}")
    private int spider;

    @Value("${scheduled.cache:10}")
    private int cache;

    @Value("${scheduled.dl:86400}")
    private int dl;

    @Value("${scheduled.syncNode:xxx}")
    private String syncNode;

    @Value("${scheduled.initCache:false}")
    private boolean initCache;

    public int getReset() {
        return reset;
    }

    public void setReset(int reset) {
        this.reset = reset;
    }

    public int getSync() {
        return sync;
    }

    public void setSync(int sync) {
        this.sync = sync;
    }

    public int getSpider() {
        return spider;
    }

    public void setSpider(int spider) {
        this.spider = spider;
    }

    public int getCache() {
        return cache;
    }

    public void setCache(int cache) {
        this.cache = cache;
    }

    public int getDl() {
        return dl;
    }

    public void setDl(int dl) {
        this.dl = dl;
    }

    public String getSyncNode() {
        return syncNode;
    }

    public void setSyncNode(String syncNode) {
        this.syncNode = syncNode;
    }

    public boolean isInitCache() {
        return initCache;
    }

    public void setInitCache(boolean initCache) {
        this.initCache = initCache;
    }

    public int getResetFor() {
        return resetFor;
    }

    public void setResetFor(int resetFor) {
        this.resetFor = resetFor;
    }
}
