package com.sw.vote.middleware;

import com.rabbitmq.client.Channel;
import com.sw.vote.util.MqUtil;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;

import javax.websocket.OnMessage;

//@Component
//@RabbitListener(queues = "sync")
public class SyncCodeListener {


    @RabbitHandler
    @OnMessage
    public void receiver(Channel channel, Message message, String data) {
        MqUtil.consume(channel, message, data, MqUtil.TYPE_MOBILE);
    }
}
