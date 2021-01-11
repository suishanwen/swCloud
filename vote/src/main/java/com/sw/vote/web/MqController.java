package com.sw.vote.web;

import com.sw.vote.model.CtrlCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value = "MQ", description = "MQ", tags = "0")
@RestController
@RequestMapping(path = "/mq")
public class MqController {


    /**
     * AmqpTemplate接口定义了发送和接收消息的基本操作,目前spring官方也只集成了Rabbitmq一个消息队列。。
     */
    @Autowired
    AmqpTemplate rabbitmqTemplate;

    @ApiOperation(value = "发送指令", notes = "发送指令")
    @ApiImplicitParam(name = "quque", paramType = "path", value = "quque", required = true, dataType = "String")
    @PostMapping("/send/{quque}")
    public void send(@PathVariable("quque") String quque, @RequestBody CtrlCode ctrlCode) {
        String data = ctrlCode.toJsonString();
        rabbitmqTemplate.convertAndSend(quque, data);
    }

}
