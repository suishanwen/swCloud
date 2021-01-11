package com.sw.vote.model;

public class LockInfo {
    private String name;
    private long ttl;

    public LockInfo() {
    }

    public LockInfo(String name, long ttl) {
        this.name = name;
        this.ttl = ttl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTtl() {
        return ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }
}
