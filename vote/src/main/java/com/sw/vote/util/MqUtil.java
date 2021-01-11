package com.sw.vote.util;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.sw.vote.middleware.CtrlDeliverSocket;
import com.sw.vote.model.BusinessException;
import com.sw.vote.model.CtrlCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class MqUtil {

    public static final int TYPE_CLIENT = 1;
    public static final int TYPE_MOBILE = 2;

    private static final Logger logger = LoggerFactory.getLogger(MqUtil.class);

    public static String getQueue(int type) {
        if (type == TYPE_CLIENT) {
            return "ctrl_code";
        } else {
            return "sync_code";
        }
    }


    public static void consume(Channel channel, Message message, String data, int type) {
        try {
            logger.info("<=============监听到" + getQueue(type) + "队列消息============>" + data);
            CtrlCode ctrlCode = JSON.parseObject(data, CtrlCode.class);
            String identity = ctrlCode.getIdentity();
            if (type == TYPE_MOBILE) {
                identity += "_mobi";
            }
            if (CtrlDeliverSocket.wsClientMap.containsKey(identity)) {
                CtrlDeliverSocket ctrlDeliverSocket = CtrlDeliverSocket.wsClientMap.get(identity);
                ctrlDeliverSocket.sendMessage(ctrlCode.getCode());
                channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
            } else {
                throw new BusinessException(HttpStatus.SERVICE_UNAVAILABLE, BusinessException.SERVICE_UNAVAILABLE, identity + ",不在线！");
            }
        } catch (Exception e) {
            if (message.getMessageProperties().getRedelivered()) {
                logger.error("消息已重复处理失败,拒绝再次接收:" + data);
                try {
                    //requeue为false,拒绝
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else {
                logger.error("消息处理失败:" + e.getMessage() + ",返回队列:" + data);
                try {
                    //消息再次返回队列
                    channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

}
