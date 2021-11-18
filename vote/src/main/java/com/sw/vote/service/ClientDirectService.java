package com.sw.vote.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.sw.vote.aspect.LogCost;
import com.sw.vote.cache.ClientDirectCache;
import com.sw.vote.config.props.UserProps;
import com.sw.vote.ip.IPSeeker;
import com.sw.vote.mapper.BugReportMapper;
import com.sw.vote.mapper.ClientDataMapper;
import com.sw.vote.mapper.ClientDirectExtMapper;
import com.sw.vote.mapper.ClientDirectMapper;
import com.sw.vote.model.BusinessException;
import com.sw.vote.model.Result;
import com.sw.vote.model.SpeedupInfo;
import com.sw.vote.model.entity.BugReport;
import com.sw.vote.model.entity.ClientData;
import com.sw.vote.model.entity.ClientDirect;
import com.sw.vote.model.entity.ClientDirectExt;
import com.sw.vote.util.IpUtil;
import com.sw.vote.util.ZipUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


@Service
@Transactional
public class ClientDirectService {
    private final org.slf4j.Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    ClientDirectMapper clientDirectMapper;
    @Autowired
    ClientDirectExtMapper clientDirectExtMapper;
    @Autowired
    BugReportMapper bugReportMapper;
    @Autowired
    ClientDataMapper clientDataMapper;
    @Autowired
    HttpServletRequest request;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ClientDirectCache clientDirectCache;
    @Autowired
    UserProps userProps;
    @Autowired
    IPSeeker ipSeeker;


    @LogCost(name = "同步数据")
    public void synchronize(List<ClientDirect> clientDirectList) {
        if (clientDirectList.size() > 0) {
            clientDirectMapper.batchUpdate(clientDirectList);
            clientDirectCache.batchSync(clientDirectList);
        }
    }

    private void checkLimit(String userId) {
        if (!userProps.getManager().equals(userId)) {
            if (clientDirectMapper.selectUserCount(userId) >= userProps.getLimit()) {
                throw new BusinessException(HttpStatus.FORBIDDEN, BusinessException.LIMIT_EXCEEDED, "超过最大机器数量限制！");
            }
        }
    }


    private String getWorkers(String userId) {
        String workers = userProps.getWorkers();
        if (Strings.isNotEmpty(workers)) {
            JSONObject jsonObject = JSON.parseObject(workers);
            workers = jsonObject.getString(userId);
            if (Strings.isEmpty(workers)) {
                workers = jsonObject.getString(userProps.getManager());
            }
        }
        return workers;
    }

    public String load(String userId, Integer sortNo, String version, String restart) {
        ClientDirectExt ext = clientDirectMapper.selectExtByUser(userId, sortNo);
        String id, adsl = "", pwd = "";
        if (ext == null) {
            checkLimit(userId);
            id = UUID.randomUUID().toString().split("-")[0];
            clientDirectMapper.insertClient(id, userId, sortNo);
            clientDirectExtMapper.insertClient(id, userId, sortNo);
            clientDirectCache.put(new ClientDirect(id, userId, sortNo));
        } else {
            id = ext.getId();
            adsl = Optional.ofNullable(ext.getAdslUser()).orElse("").trim();
            pwd = Optional.ofNullable(ext.getAdslPwd()).orElse("").trim();
        }
        String sid = UUID.randomUUID().toString().split("-")[0];
        clientDirectCache.load(userId, sortNo, version, restart, sid);
        Integer userDelay = userProps.getManager().equals(userId) ? userProps.getDelay() : clientDirectCache.getSpeedup();
        String workers = getWorkers(userId);
        return String.format("%s|%s|%s|%s|%s|%s", id, userDelay, adsl, pwd, sid, workers);
    }

    public String direct(ClientDirect clientDirect) {
        return clientDirectCache.direct(clientDirect);
    }

    public int confirm(String userId, Integer sortNo, String direct) {
        direct = direct.trim();
        String redisHashKey = clientDirectCache.getRedisHashKey(userId, sortNo);
        String theDirect = Objects.toString(clientDirectCache.getVal(redisHashKey, "direct"), "");
        try {
            if (StringUtils.isNotEmpty(theDirect)) {
                List<String> directs = Lists.newArrayList(theDirect.split(ClientDirect.splitter));
                if (directs.get(0).equals(direct)) {
                    directs.remove(0);
                    clientDirectCache.setVal(redisHashKey, "direct", String.join(ClientDirect.splitter, directs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    public void deleteClient(String userId, Integer sortNo) {
        String redisHashKey = clientDirectCache.getRedisHashKey(userId, sortNo);
        String id = clientDirectCache.getVal(redisHashKey, "id").toString();
        clientDirectMapper.deleteByPrimaryKey(id);
        clientDirectExtMapper.deleteByPrimaryKey(id);
        clientDirectCache.remove(redisHashKey);
    }

    public String track() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("host", userProps.getHost());
        map.add("username", userProps.getUsername1());
        map.add("password", userProps.getPassword1());
        return restTemplate.postForObject("https://tl.bitcoinrobot.cn/track/", map, String.class);
    }


    public List<ClientDirect> selectByUserId(String userId) {
        return clientDirectCache.selectByUserId(userId);
    }

    public Map<String, ClientDirect> checkByUserId(String userId) {
        return clientDirectCache.checkByUserId(userId);
    }

    public String selectByUserIdLite(String userId) {
        return ZipUtil.compress(JSON.toJSONString(selectByUserId(userId)));
    }

    public void updateDirect(String keys, Integer all, String direct) {
        if (StringUtils.isEmpty(keys) && all != 1) {
            return;
        }
        direct = direct.trim();
        if (all == 1) {
            clientDirectCache.setDirectAll(direct);
        } else {
            Set<String> keySet = Sets.newHashSet(keys.split(",")).stream()
                    .map(key -> ClientDirectCache.keyPrefix + ":" + key)
                    .collect(Collectors.toSet());
            clientDirectCache.setDirectByKeys(keySet, direct);
        }
    }

    public long checkVersion() {
        return clientDirectCache.checkVersion();
    }

    public void upgradeLatest() {
        clientDirectCache.updateLatestVersion();
    }

    @LogCost(name = "数据上传")
    public int dataUpload(ClientData clientData) {
        Date today = new Date();
        Date prvDay = new Date(today.getTime() - 3600 * 23 * 1000);
        String date = new SimpleDateFormat("yyyy-MM-dd").format(prvDay);
        clientData.setDate(date);
        clientData.setIp(IpUtil.getIpAddr(request));
        clientData.setLocation(ipSeeker.getAddress(clientData.getIp()));
        clientDirectCache.setVal(clientDirectCache.getRedisHashKey(clientData.getUserId(), clientData.getSortNo()), "restart", "0");
        try {
            clientDataMapper.insert(clientData);
            return 1;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public String selectDataByUserId(String userId, String date) {
        List<ClientData> clientDataList;
        if (userProps.getSuperuser().equals(userId) || userProps.getManager1().equals(userId)) {
            clientDataList = clientDataMapper.selectAllData(date);
        } else {
            clientDataList = clientDataMapper.selectDataByUserId(userId, date);
        }
        return ZipUtil.compress(JSON.toJSONString(clientDataList));
    }

    public String selectDetail(String id, String date) {
        return clientDataMapper.selectDetail(id, date);
    }

    public String selectDetails(String id, String date) {
        return ZipUtil.compress(JSON.toJSONString(clientDataMapper.selectDetails(id, date)));
    }

    public String selectMonthDetails(String date) {
        return ZipUtil.compress(JSON.toJSONString(clientDataMapper.selectMonthDetails(date)));
    }

    public String selectEliminatedDetails(int day, int limit, String order) {
        return ZipUtil.compress(JSON.toJSONString(clientDataMapper.selectEliminatedDetails(day, limit, order)));
    }

    public String renew() {
        return ZipUtil.compress(JSON.toJSONString(clientDirectMapper.selectVmInfo()));
    }

    public BufferedImage activeClient(String now) {
        int activeClient = clientDirectMapper.selectActive();
        int width = 300;
        int height = 130;
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //得到它的绘制环境(这张图片的笔)
        Graphics2D g2 = (Graphics2D) bi.getGraphics();
        //填充一个矩形 左上角坐标(0,0),宽350,高230;填充整张图片
        g2.fillRect(0, 0, width, height);
        //设置颜色
        g2.setColor(Color.WHITE);
        //填充整张图片(其实就是设置背景颜色)
        g2.fillRect(0, 0, width, height);
        g2.setColor(Color.WHITE);
        //画边框
        g2.drawRect(0, 0, width - 1, height - 1);
        //设置字体:字体、字号、大小
        g2.setFont(new Font("Arial", Font.PLAIN, 20));
        //设置背景颜色
        g2.setColor(Color.red);
        //向图片上写字符串
        g2.drawString(String.format("%s", now), 15, 30);
        g2.setColor(Color.red);

        g2.drawString("Active Robots : " + activeClient, 55, 60);
        g2.setColor(Color.red);
        g2.drawString("Reports Today : " + clientDirectCache.getCount().get(0), 55, 90);
        g2.setColor(Color.red);
        g2.drawString("Fetches Today : " + clientDirectCache.getCount().get(1), 55, 120);
        return bi;
    }

    public void bugReport(String id, String msg) {
        if (userProps.isErrorIgnore()) {
            return;
        }
        try {
            bugReportMapper.insert(new BugReport(id, msg));
        } catch (Exception e) {
            logger.warn("bugReport:" + e.getMessage());
        }
    }

    public Result<Object> speedup() {
        String location = ipSeeker.getAddress(IpUtil.getIpAddr(request));
        logger.info(location + " speedup... ");
        List<String> excludes = Lists.newArrayList(userProps.getSpeedupExclude().split(","));
        if (excludes.stream().anyMatch(location::contains)) {
            return Result.err("当前地区不能触发加速!");
        }
        return clientDirectCache.setSpeedup();
    }

    public SpeedupInfo getSpeedupInfo() {
        int percent = (userProps.getDelay1() - clientDirectCache.getSpeedup()) * 100 / clientDirectCache.maxSpeedup();
        return new SpeedupInfo(percent, percent >= 100 ? -2 : clientDirectCache.getSpeedupLockTTl());
    }

    public void keyLock(String key, Integer ttl) {
        clientDirectCache.keyLock(key, ttl);
    }
}
