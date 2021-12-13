package com.bjsxt.sender;

import com.bjsxt.message.BaizhanMessage;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 发送消息的工具
 */
@Component
public class TradeMessageSender {

    @Autowired
    private AmqpTemplate amqpTemplate;

    /**
     * 发送异步消息
     *
     * @param exchange
     * @param routingKey
     * @param message
     */
    public void sendAsyncMessage(String exchange, String routingKey, BaizhanMessage message) {

        amqpTemplate.convertAndSend(exchange, routingKey, message);
    }

    /**
     * 发送同步消息
     *
     * @param exchange
     * @param routingKey
     * @param message
     * @return
     */
    public Object sendSyncMessage(String exchange, String routingKey, BaizhanMessage message) {

        return amqpTemplate.convertSendAndReceive(exchange, routingKey, message);
    }

}
