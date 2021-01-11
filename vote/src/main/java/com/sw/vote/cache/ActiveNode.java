package com.sw.vote.cache;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.sw.vote.config.props.ScheduledInterval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ActiveNode {
    @Autowired
    ClientDirectCache clientDirectCache;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;
    @Autowired
    ScheduledInterval scheduledInterval;


    private static final String keySet = "activeNodes";
    private static final Set<String> services = Sets.newHashSet();

    public static void addService(String service){
        services.add(service);
    }

    private String getNode() {
        return ClientDirectCache.hostName;
    }

    private String getServiceKey(String node, String service) {
        return node + ":" + service;
    }

    public void register() {
        redisTemplate.opsForSet().add(keySet, getNode());
    }

    public void setState(String service) {
        try {
            redisTemplate.opsForValue().set(getServiceKey(getNode(), service), "1",
                    scheduledInterval.getSpider() + 5, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private Set<Object> getNodes() {
        return redisTemplate.opsForSet().members(keySet);
    }

    public Map<String, Object> info() {
        Set<Object> nodes = getNodes();
        Map<String, Object> state = Maps.newHashMap();
        state.put("nodes", nodes);
        state.put("services", services);
        nodes.forEach(node -> {
            List<String> nodeServiceKeyList = services.stream().map(service -> getServiceKey(node.toString(), service)).collect(Collectors.toList());
            state.put(node.toString(), redisTemplate.opsForValue().multiGet(nodeServiceKeyList));
        });
        return state;
    }
}
