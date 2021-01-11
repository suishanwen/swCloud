package com.sw.vote.cache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TimedDlCaches extends TimedCaches {
    private final String setKey = "listDl";

    @Autowired
    VoteProjectCache voteProjectCache;

    @Override
    public int getDuration() {
        return super.getScheduledInterval().getDl();
    }

    @Override
    public TimedCache put(String k, TimedCache v) {
        super.put(k, v);
        super.getRedisTemplate().opsForSet().add(setKey, k);
        super.getRedisTemplate().opsForHash().putAll(k, v.toMap());
        super.removeExpired();
        return v;
    }

    @Override
    public TimedCache remove(Object k) {
        super.getRedisTemplate().opsForSet().remove(setKey, k);
        voteProjectCache.removeDownload(k.toString());
        return super.remove(k);
    }

    public void initAll() {
        Set<Object> keys = super.getRedisTemplate().opsForSet().members(setKey);
        if (keys != null && keys.size() > 0) {
            super.init(keys.stream().map(Object::toString).collect(Collectors.toSet()));
        }
    }
}
