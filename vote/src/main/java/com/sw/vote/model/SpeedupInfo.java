package com.sw.vote.model;

public class SpeedupInfo {
    private Integer percent;
    private long ttl;

    public SpeedupInfo() {
    }

    public SpeedupInfo(Integer percent, long ttl) {
        this.percent = percent;
        this.ttl = ttl;
    }

    public Integer getPercent() {
        return percent;
    }

    public void setPercent(Integer percent) {
        this.percent = percent;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }
}
