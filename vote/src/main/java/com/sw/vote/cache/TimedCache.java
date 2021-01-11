package com.sw.vote.cache;

import java.util.HashMap;
import java.util.Map;

public class TimedCache {
    private String cache;
    private long mills;

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("cache", cache);
        map.put("mills", mills);
        return map;
    }

    public TimedCache() {
        this.cache = "";
        this.mills = 0;
    }

    public TimedCache(String cache) {
        set(cache);
    }

    public TimedCache(String cache, long mills) {
        set(cache, mills);
    }

    public void set(String cache) {
        this.cache = cache;
        this.mills = System.currentTimeMillis();
    }

    public void set(String cache, long mills) {
        this.cache = cache;
        this.mills = mills;
    }

    public String cache() {
        return this.cache;
    }


    public boolean expired(int duration) {
        return System.currentTimeMillis() - this.mills > duration * 1000;
    }
}
