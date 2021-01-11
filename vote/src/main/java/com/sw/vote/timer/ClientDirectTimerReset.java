package com.sw.vote.timer;

import com.sw.vote.cache.BackGroundCache;
import com.sw.vote.cache.ClientDirectCache;
import com.sw.vote.cache.VoteProjectCache;
import com.sw.vote.config.props.UserProps;
import com.sw.vote.config.props.ScheduledInterval;
import com.sw.vote.mapper.VmResetMapper;
import com.sw.vote.model.entity.ClientDirect;
import com.sw.vote.model.entity.VmReset;
import com.sw.vote.service.ClientDirectService;
import com.sw.vote.util.ScheduledExecutorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RefreshScope
public class ClientDirectTimerReset {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ClientDirectService clientDirectService;
    @Autowired
    private VmResetMapper vmResetMapper;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    ClientDirectCache clientDirectCache;
    @Autowired
    VoteProjectCache voteProjectCache;
    @Autowired
    BackGroundCache backGroundCache;
    @Autowired
    UserProps userProps;
    @Autowired
    ScheduledInterval scheduledInterval;

    private boolean running = false;

    public void run() {
        Runnable runnable = () -> {
            if (running) {
                return;
            }
            running = true;
            try {
                reset();
                backGroundCache.clear();
            } catch (Exception e) {
                clientDirectService.bugReport("server-reset", e.getMessage());
            }
            running = false;
        };
        ScheduledExecutorUtil.scheduleAtFixedRate(runnable, 0, scheduledInterval.getReset());
    }

    private MultiValueMap<String, String> generateData(String user, String resetId, String instance) {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("host", userProps.getHost());
        map.add("username", userProps.getUsername1().equals(user) ? userProps.getUsername1() : userProps.getUsername());
        map.add("password", userProps.getUsername1().equals(user) ? userProps.getPassword1() : userProps.getPassword());
        map.add("resetId", resetId);
        map.add("instance", instance);
        return map;
    }

    private void doReset(List<ClientDirect> clientDirectList) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        String resetId = clientDirectList.stream().map(ClientDirect::getResetId).collect(Collectors.joining(","));
        String instance = clientDirectList.stream().map(ClientDirect::getInstance).collect(Collectors.joining(","));
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(
                generateData(userProps.getUsername1(), resetId, instance), headers);
        //发送请求，设置请求返回数据格式为String
        String status = "0";
        try {
            logger.warn("批量重启:" + instance);
            ResponseEntity<String> responseEntity = restTemplate.postForEntity("https://tl.bitcoinrobot.cn/reset/", request, String.class);
            if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
                status = responseEntity.getBody();
            }
        } catch (Exception e) {
            logger.warn("vm-reset:" + e.getMessage());
        }
        final String rStatus = status;
        clientDirectList.forEach(clientDirect -> {
            try {
                vmResetMapper.insert(new VmReset(clientDirect.getUserId(), String.valueOf(clientDirect.getSortNo()), rStatus, new Date()));
            } catch (Exception e) {
                logger.warn("vm-reset-insert:" + e.getMessage());
            }
        });
    }

    private void reset() {
        long current = System.currentTimeMillis();
        long timeout = 45 * 60 * 1000;
        long resetInterval = scheduledInterval.getResetFor() * 1000;
        List<ClientDirect> clientDirectList = clientDirectService.selectByUserId(userProps.getSuperuser());
        long nonCount = clientDirectList.stream()
                .filter(clientDirect -> StringUtils.isEmpty(clientDirect.getProjectName()))
                .count();
        if (nonCount < clientDirectList.size() / 2) {
            clientDirectList = clientDirectList.stream()
                    .filter(clientDirect -> {
                        if (!StringUtils.isEmpty(clientDirect.getResetId()) &&
                                (current - clientDirect.getUpdateTime().getTime() > timeout) &&
                                (StringUtils.isEmpty(voteProjectCache.getLocked()) ||
                                        (!StringUtils.isEmpty(clientDirect.getProjectName()) &&
                                                !clientDirect.getProjectName().contains(voteProjectCache.getLocked())))) {
                            VmReset vmReset = vmResetMapper.getLastReset(clientDirect.getUserId(), String.valueOf(clientDirect.getSortNo()));
                            return vmReset == null || current - vmReset.getDate().getTime() > resetInterval;
                        }
                        return false;
                    }).collect(Collectors.toList());
            if (clientDirectList.size() > 0) {
                doReset(clientDirectList);
            }
        }
    }
}


