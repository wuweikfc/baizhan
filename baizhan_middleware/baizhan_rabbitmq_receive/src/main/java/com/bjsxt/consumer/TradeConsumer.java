package com.bjsxt.consumer;

import com.bjsxt.message.vo.BaizhanTradeMessage;
import com.bjsxt.service.TradeService4Consumer;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 创建订单消息消费者
 */
@Component
public class TradeConsumer {

    @Autowired
    private TradeService4Consumer tradeService4Consumer;

    /**
     * 订单消费方法
     * 1、创建订单，订单项，订单物流。本地数据库访问
     * 2、更新商品库存，远程服务调用
     * 事务管理：
     * 1、如果所有的数据库访问，都在当前代码中实现，本地事务
     * 2、如果数据访问，通过远程服务调用的方式实现，分布式事务
     */
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = "baizhan-trade-queue-0", autoDelete = "false"),
                    exchange = @Exchange(name = "baizhan-trade-exchange", type = "topic"),
                    key = "baizhan-trade-routing-key-0"
            )
    })
    public boolean onMessageAt0(BaizhanTradeMessage message) {

        try {
            return tradeService4Consumer.createOrder(message.getTbOrder(), message.getTbOrderShipping(), message.getTbOrderItems());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = "baizhan-trade-queue-1", autoDelete = "false"),
                    exchange = @Exchange(name = "baizhan-trade-exchange", type = "topic"),
                    key = "baizhan-trade-routing-key-1"
            )
    })
    public boolean onMessageAt1(BaizhanTradeMessage message) {

        try {
            return tradeService4Consumer.createOrder(message.getTbOrder(), message.getTbOrderShipping(), message.getTbOrderItems());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = "baizhan-trade-queue-2", autoDelete = "false"),
                    exchange = @Exchange(name = "baizhan-trade-exchange", type = "topic"),
                    key = "baizhan-trade-routing-key-2"
            )
    })
    public boolean onMessageAt2(BaizhanTradeMessage message) {

        try {
            return tradeService4Consumer.createOrder(message.getTbOrder(), message.getTbOrderShipping(), message.getTbOrderItems());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

}
