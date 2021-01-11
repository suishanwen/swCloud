package com.sw.vote.cache;

import com.google.common.collect.Lists;
import com.sw.vote.config.props.ScheduledInterval;
import com.sw.vote.util.ZipUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class TimedCaches extends HashMap<String, TimedCache> {
    @Autowired
    private ScheduledInterval scheduledInterval;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public int getDuration() {
        return getScheduledInterval().getCache();
    }

    public ScheduledInterval getScheduledInterval() {
        return scheduledInterval;
    }

    public RedisTemplate<String, Object> getRedisTemplate() {
        return redisTemplate;
    }


    @Override
    public TimedCache put(String k, TimedCache v) {
        super.put(k, v);
        redisTemplate.opsForHash().putAll(k, v.toMap());
        return v;
    }

    @Override
    public TimedCache get(Object k) {
        TimedCache timedCache = super.get(k);
        if (timedCache == null || timedCache.expired(getDuration())) {
            try {
                Map<Object, Object> hash = redisTemplate.opsForHash().entries(k.toString());
                timedCache = new TimedCache(hash.get("cache").toString(), Long.parseLong(hash.get("mills").toString()));
                put(k.toString(), timedCache);
            } catch (NullPointerException e) {
                return null;
            }
        }
        return timedCache;
    }

    @Override
    public TimedCache remove(Object k) {
        redisTemplate.delete(k.toString());
        return super.remove(k);
    }

    public void removeExpired() {
        List<String> expiredKeys = Lists.newArrayList();
        forEach((key, value) -> {
            if (value.expired(getDuration())) {
                expiredKeys.add(key);
            }
        });
        expiredKeys.forEach(this::remove);
    }


    public void init(Set<String> keys) {
        redisTemplate.executePipelined(
                (RedisCallback<String>) redisConnection -> {
                    keys.forEach(key -> {
                        if (StringUtils.isNotEmpty(key)) {
                            Object o = redisTemplate.opsForHash().entries(key);
                            Map hash = (Map) o;
                            if (hash.get("cache") != null) {
                                TimedCache timedCache = new TimedCache(hash.get("cache").toString(), Long.parseLong(hash.get("mills").toString()));
                                put(key, timedCache);
                            }
                        }
                    });
                    return null;
                });
    }

    public void init(String key, String val) {
        if (redisTemplate.opsForHash().hasKey(key, "cache")) {
            fromRedis(key);
        } else {
            put(key, new TimedCache(ZipUtil.compress(val), 0));
        }
    }

    public void fromRedis(String k) {
        TimedCache timedCache = new TimedCache();
        Map<Object, Object> hash = redisTemplate.opsForHash().entries(k);
        timedCache.set(hash.get("cache").toString(), Long.parseLong(hash.get("mills").toString()));
        super.put(k, timedCache);
    }


}
