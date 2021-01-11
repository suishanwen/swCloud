package com.sw.msg.web;

import com.alibaba.fastjson.JSONObject;
import com.sw.msg.config.properties.EmailProperty;
import com.sw.msg.config.properties.TelegramProperty;
import com.sw.msg.model.Result;
import com.sw.msg.util.MailUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@Api(value = "速递", description = "速递", tags = "1")
@RestController
@RequestMapping(path = "/msg")
public class MsgController {
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private EmailProperty emailProperty;
    @Autowired
    private TelegramProperty telegramProperty;

    @ApiOperation(value = "发送", notes = "发送")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "receiver", paramType = "query", value = "receiver", required = true, dataType = "String"),
            @ApiImplicitParam(name = "subject", paramType = "query", value = "subject", required = true, dataType = "String"),
            @ApiImplicitParam(name = "content", paramType = "query", value = "content", required = true, dataType = "String")
    })
    @GetMapping("/email")
    public Result<Object> send(@RequestParam("receiver") String receiver, @RequestParam("subject") String subject,
                               @RequestParam("content") String content) {
        if (StringUtils.isEmpty(receiver)) {
            return Result.err("收件人不能为空");
        }
        try {
            MailUtils cn = new MailUtils();
            cn.setAddress(emailProperty.getUser(), receiver, subject);
            cn.send(emailProperty.getHost(), emailProperty.getUser(), emailProperty.getPassword(), content);
            return Result.success(1);
        } catch (Exception e) {
            return Result.err(e.getMessage());
        }
    }

    @ApiOperation(value = "发送", notes = "发送")
    @PostMapping("/telegram")
    public Result<Object> send(@RequestBody String msg) {
        String url = String.format("%s/bot%s/sendMessage?chat_id=%s&text=%s",
                telegramProperty.getHost(), telegramProperty.getToken(), telegramProperty.getChatId(), msg);
        try {
            String resp = restTemplate.getForObject(url, String.class);
            JSONObject jsonObject = JSONObject.parseObject(resp);
            if (jsonObject.containsKey("ok")) {
                return Result.success(1);
            } else {
                return Result.err("发生失败！");
            }
        } catch (Exception e) {
            return Result.err(e.getMessage());
        }
    }
}
