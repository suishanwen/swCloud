package com.sw.vote.cache;

import com.alibaba.fastjson.JSON;
import com.sw.vote.model.BackgroundData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BackGroundCache {
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    final String hashKey = "backgroundData";

    public Map<String, BackgroundData> all() {
        Map<Object, Object> data = redisTemplate.opsForHash().entries(hashKey);
        return data.entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().toString(), entry -> JSON.parseObject(entry.getValue().toString(), BackgroundData.class)));
    }

    public BackgroundData find(String url) {
        Object json = redisTemplate.opsForHash().get(hashKey, url);
        if (json == null) {
            return null;
        }
        return JSON.parseObject(json.toString(), BackgroundData.class);
    }

    public void set(String url, BackgroundData backgroundData) {
        redisTemplate.opsForHash().put(hashKey, url, JSON.toJSONString(backgroundData));
    }

    public void clear() {
        long nowMills = System.currentTimeMillis();
        all().forEach((key, value) -> {
            if (nowMills - value.getMills() > 60 * 60 * 1000) {
                redisTemplate.opsForHash().delete(hashKey, key);
            }
        });
    }

}
