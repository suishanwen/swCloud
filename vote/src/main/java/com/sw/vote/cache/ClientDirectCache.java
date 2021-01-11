package com.sw.vote.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sw.vote.aspect.LogCost;
import com.sw.vote.config.props.UserProps;
import com.sw.vote.model.BusinessException;
import com.sw.vote.model.Result;
import com.sw.vote.model.entity.ClientDirect;
import com.sw.vote.model.entity.ClientDirectExt;
import com.sw.vote.util.DateUtil;
import com.sw.vote.util.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.hash.BeanUtilsHashMapper;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.util.StringUtil;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class ClientDirectCache {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());
    public static String hostName;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private UserProps userProps;

    final BeanUtilsHashMapper<ClientDirect> mapper = new BeanUtilsHashMapper<>(ClientDirect.class);

    public static String date;
    public static final String keyPrefix = "user";

    private static final String CLEAR_ALL_DIRECT = "TASK_SYS_CLEAR_ALL";


    static {
        resetDate();
        try {
            InetAddress addr = InetAddress.getLocalHost();
            hostName = addr.getHostName();
        } catch (UnknownHostException e) {
            hostName = "unknown";
            e.printStackTrace();
        }
    }

    // redis hashKey
    public String getRedisHashKey(String userId, Integer sortNo) {
        return String.format("%s:%s:%d", keyPrefix, userId, sortNo);
    }

    private String getRedisHashKey(ClientDirect clientDirect) {
        return getRedisHashKey(clientDirect.getUserId(), clientDirect.getSortNo());
    }

    // 写入redis
    public void putAll(String redisHashKey, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(redisHashKey, map);
    }

    // 存入clientDirect
    public void put(ClientDirect clientDirect) {
        Map<String, Object> map = Maps.newHashMap(mapper.toHash(clientDirect));
        map.put("updateTime", new Date().getTime());
        map.put("$synchronized", true);
        putAll(getRedisHashKey(clientDirect), map);
    }

    // 初始化redis - scheduled.initCache
    @LogCost(name = "初始化缓存")
    public void initCache(List<ClientDirect> clientDirectList) {
        redisTemplate.executePipelined(
                (RedisCallback<String>) redisConnection -> {
                    clientDirectList.forEach(this::put);
                    return null;
                });
    }

    // 客户端load
    public void load(String userId, Integer sortNo, String version, String restart, String sid) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("version", version);
        map.put("restart", restart);
        map.put("$synchronized", false);
        map.put("$sid", sid);
        putAll(getRedisHashKey(userId, sortNo), map);
    }

    // 批量get
    public List<ClientDirect> batchGet(Set<String> keySet) {
        final RedisSerializer<String> stringSerializer = redisTemplate.getStringSerializer();
        ObjectMapper objectMapper = new ObjectMapper();
        List<Object> list = redisTemplate.executePipelined(
                (RedisCallback<String>) redisConnection -> {
                    keySet.forEach(key -> {
                        redisConnection.hGetAll(stringSerializer.serialize(key));
                    });
                    return null;
                }, redisTemplate.getHashValueSerializer());
        return list.stream().map(obj -> objectMapper.convertValue(obj, ClientDirect.class)).collect(Collectors.toList());
    }

    // 上报获取指令
    public String direct(ClientDirect clientDirect) {
        String redisHashKey = getRedisHashKey(clientDirect);
        Map<Object, Object> map = redisTemplate.opsForHash().entries(redisHashKey);
        String sid = StringUtils.getString(map.get("$sid"), "");
        if (userProps.isForbidConcurrent() && StringUtils.isNotEmpty(sid) && !sid.equals(clientDirect.get$sid())) {
            return "USER_SHUTDOWN";
        }
        if (clientDirect.diff(map)) {
            map.remove("userId");
            map.remove("sortNo");
            report(clientDirect);
        }
        reportIncr();
        String direct = Optional.ofNullable(map.get("direct")).orElse("").toString();
        if (direct.contains(ClientDirect.splitter)) {
            direct = direct.split(ClientDirect.splitter)[0];
        }
        return direct;
    }

    // 上报
    public void report(ClientDirect clientDirect) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("workerId", clientDirect.getWorkerId());
        map.put("projectName", clientDirect.getProjectName());
        map.put("success", clientDirect.getSuccess());
        map.put("reward", clientDirect.getReward());
        map.put("updateTime", new Date());
        map.put("$synchronized", false);
        putAll(getRedisHashKey(clientDirect), map);
    }


    // 获取用户key
    public Set<String> getUserKeys(String user) {
        if (userProps.getManager1().equals(user) || userProps.getSuperuser().equals(user)) {
            return redisTemplate.keys(keyPrefix + ":*");
        }
        return redisTemplate.keys(keyPrefix + ":" + user + ":*");
    }

    // 获取单个hash field
    public Object getVal(String redisHashKey, String field) {
        return redisTemplate.opsForHash().get(redisHashKey, field);
    }

    // 设置单个hash field
    public void setVal(String redisHashKey, String field, Object val) {
        Map<String, Object> map = Maps.newHashMap();
        map.put(field, val);
        map.put("$synchronized", false);
        putAll(redisHashKey, map);
    }

    // 修改同步状态
    public void sync(ClientDirect clientDirect) {
        redisTemplate.opsForHash().put(getRedisHashKey(clientDirect), "$synchronized", true);
    }

    // 批量修改同步状态
    @LogCost(name = "同步数据-状态")
    public void batchSync(List<ClientDirect> clientDirectList) {
        redisTemplate.executePipelined(
                (RedisCallback<String>) redisConnection -> {
                    clientDirectList.forEach(this::sync);
                    return null;
                });
    }

    // 更新实例信息
    public void updateExt(ClientDirectExt clientDirectExt) {
        Map<String, Object> map = Maps.newHashMap();
        map.put("instance", clientDirectExt.getInstance());
        map.put("resetId", clientDirectExt.getResetId());
        map.put("$synchronized", false);
        putAll(getRedisHashKey(clientDirectExt.getUserId(), clientDirectExt.getSortNo()), map);
    }

    // 批量更新实例信息
    public void batchUpdateExt(List<ClientDirectExt> clientDirectExtList) {
        redisTemplate.executePipelined(
                (RedisCallback<String>) redisConnection -> {
                    clientDirectExtList.forEach(this::updateExt);
                    return null;
                });
    }

    // 设置指令
    public void setDirect(String redisHashKey, String direct) {
        if (CLEAR_ALL_DIRECT.equals(direct)) {
            setVal(redisHashKey, "direct", "");
        } else {
            String theDirect = Objects.toString(getVal(redisHashKey, "direct"), "");
            if (StringUtil.isNotEmpty(theDirect)) {
                List<String> directs = Lists.newArrayList(theDirect.split(ClientDirect.splitter));
                directs.add(direct);
                setVal(redisHashKey, "direct", String.join(ClientDirect.splitter, directs));
            } else {
                setVal(redisHashKey, "direct", direct);
            }
        }
    }

    // 批量设置指令
    public void setDirectByKeys(Set<String> hashKeys, String direct) {
        redisTemplate.executePipelined(
                (RedisCallback<String>) redisConnection -> {
                    hashKeys.forEach(hashKey -> setDirect(hashKey, direct));
                    return null;
                });
    }

    // 全部设置指令
    public void setDirectAll(String direct) {
        Set<String> hashKeys = getUserKeys(userProps.getSuperuser());
        setDirectByKeys(hashKeys, direct);
    }

    // 删除key
    public void remove(String redisHashKey) {
        redisTemplate.delete(redisHashKey);
    }

    // 未同步列表
    @LogCost(name = "获取未同步列表")
    public List<ClientDirect> unSynchronized() {
        Set<String> hashKeys = getUserKeys(userProps.getSuperuser());
        return batchGet(hashKeys).stream().filter(clientDirect -> !clientDirect.is$synchronized() && StringUtils.isNotEmpty(clientDirect.getId())).collect(Collectors.toList());
    }

    // 查询用户机器列表
//    @LogCost(name = "查询用户机器列表")
    public List<ClientDirect> selectByUserId(String userId) {
        if (userProps.getManager1().equals(userId)) {
            userId = userProps.getSuperuser();
        }
        Set<String> keySet = getUserKeys(userId);
        if (keySet == null || keySet.size() == 0) {
            return Lists.newArrayList();
        }
        return batchGet(keySet);
    }

    // 检查用户机器列表
    @LogCost(name = "检查用户机器列表")
    public Map<String, ClientDirect> checkByUserId(String userId) {
        if (userProps.getManager1().equals(userId)) {
            userId = userProps.getSuperuser();
        }
        Map<String, ClientDirect> result = Maps.newHashMap();
        Set<String> keySet = getUserKeys(userId);
        if (keySet != null && keySet.size() > 0) {
            List<ClientDirect> clientDirects = batchGet(keySet);
            int i = 0;
            for (String key : keySet) {
                ClientDirect c = clientDirects.get(i);
                if (c.getSortNo() == 0) {
                    result.put(key, c);
                }
                i++;
            }
        }
        return result;
    }

    // 检查版本
    @LogCost(name = "检查版本")
    public long checkVersion() {
        Set<String> keySet = getUserKeys(userProps.getSuperuser());
        if (keySet == null || keySet.size() == 0) {
            return 0;
        }
        return batchGet(keySet).stream().map(ClientDirect::getVersion).distinct().count();
    }

    // 更新最新
    public void updateLatestVersion() {
        Set<String> keySet = getUserKeys(userProps.getSuperuser());
        if (keySet == null || keySet.size() == 0) {
            return;
        }
        List<ClientDirect> clientDirectList = batchGet(keySet);
        String version = clientDirectList.stream().map(ClientDirect::getVersion).max(String::compareTo).orElse("0.1");
        redisTemplate.executePipelined(
                (RedisCallback<String>) redisConnection -> {
                    clientDirectList.stream().filter(clientDirect -> !version.equals(clientDirect.getVersion()))
                            .forEach(clientDirect -> {
                                setVal(getRedisHashKey(clientDirect), "direct", "TASK_SYS_UPDATE");
                            });
                    return null;
                });

    }

    // 统计
    public static void resetDate() {
        date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
    }

    // 获取提速
    public Integer getSpeedup() {
        Object val = redisTemplate.opsForValue().get("speedup");
        return val == null ? userProps.getDelay1() : Integer.parseInt(val.toString());
    }


    public long getSpeedupLockTTl() {
        try {
            return redisTemplate.opsForValue().getOperations().getExpire("speedupLock");
        } catch (NullPointerException e) {
            return -2;
        }
    }

    // 最大可提速
    public Integer maxSpeedup() {
        return (int) Math.ceil(userProps.getDelay1() * userProps.getSpeedupPercent() / 100.0 / userProps.getSpeedupTimes()) * userProps.getSpeedupTimes();
    }

    // 提速
    public Result<Object> setSpeedup() {
        if (redisTemplate.hasKey("speedupLock")) {
            return Result.err("加速时间未到！");
        }
        int maxSpeed = maxSpeedup();
        Integer speedUp = getSpeedup();
        if (userProps.getDelay1() - speedUp >= maxSpeed) {
            return Result.err("已达最大加速时间！");
        }
        speedUp -= maxSpeed / userProps.getSpeedupTimes();
        redisTemplate.opsForValue().set("speedup", speedUp.toString(), DateUtil.getSecondsLeft(), TimeUnit.SECONDS);
        if (userProps.getDelay1() - speedUp < maxSpeed) {
            redisTemplate.opsForValue().set("speedupLock", "1", (int) (userProps.getSpeedupInterval() * 3600), TimeUnit.SECONDS);
        }
        return Result.success(1);
    }

    // 分布式锁
    public void keyLock(String key, Integer ttl) {
        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, "1", ttl, TimeUnit.SECONDS);
        if (result == null || !result) {
            throw new BusinessException(HttpStatus.MULTI_STATUS, BusinessException.LIMIT_EXCEEDED, "lock failed！");
        }
    }

    public void reportIncr() {
        if (userProps.isTrackReport()) {
            redisTemplate.opsForHash().increment("count", "report:" + date, 1);
        }
        if (userProps.isTrackReportDetail()) {
            redisTemplate.opsForHash().increment("count", "report:" + date + ":" + hostName, 1);
        }
    }

    public void fetchIncr() {
        if (userProps.isTrackReport()) {
            redisTemplate.opsForHash().increment("count", "fetch:" + date, 1);
        }
        if (userProps.isTrackReportDetail()) {
            redisTemplate.opsForHash().increment("count", "fetch:" + date + ":" + hostName, 1);
        }
    }


    public List<Object> getCount() {
        return redisTemplate.opsForHash().multiGet("count", Lists.newArrayList("report:" + date, "fetch:" + date));
    }
}
