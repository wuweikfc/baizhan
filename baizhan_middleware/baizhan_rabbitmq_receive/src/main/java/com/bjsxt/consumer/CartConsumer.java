package com.bjsxt.consumer;

import com.bjsxt.commons.pojo.Cart;
import com.bjsxt.dao.redis.RedisDao;
import com.bjsxt.message.vo.BaizhanSyncCartMessage;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 同步购物车消息 消费者
 */
@Component
public class CartConsumer {

    @Autowired
    private RedisDao redisDao;

    /**
     * 同步购物车
     *
     * @param cartMessage
     */
    @RabbitListener(bindings = {
            @QueueBinding(
                    value = @Queue(name = "${baizhan.trade.cartMQ.queue}"),
                    exchange = @Exchange(name = "baizhan-trade-sync-cart", type = "topic"),
                    key = "baizhan-trade-sync-cart-routingKey"
            )
    })
    public void onMessage(BaizhanSyncCartMessage cartMessage) {

        Cart cart = redisDao.get(cartMessage.getCartKey());

        //删除购物车中的数据
        for (Long itemId : cartMessage.getItemIds()) {
            cart.removeItem(itemId);
        }

        //保存购物车到redis
        redisDao.set(cartMessage.getCartKey(), cart);

    }

}
