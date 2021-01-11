package com.sw.vote.middleware;

import com.rabbitmq.client.Channel;
import com.sw.vote.util.MqUtil;
import org.springframework.amqp.core.Message;
//
//import javax.websocket.OnMessage;
//
//@Component
//@RabbitListener(queues = "ctrl")
@SuppressWarnings("unused")
public class CtrlCodeListener {
//
//    @RabbitHandler
//    @OnMessage
    public void receiver(Channel channel, Message message, String data) {
        MqUtil.consume(channel, message, data, MqUtil.TYPE_CLIENT);
    }
}
