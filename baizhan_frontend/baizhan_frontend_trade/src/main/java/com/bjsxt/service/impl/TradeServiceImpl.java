package com.bjsxt.service.impl;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.dao.redis.RedisDao;
import com.bjsxt.message.vo.BaizhanMailMessage;
import com.bjsxt.message.vo.BaizhanSyncCartMessage;
import com.bjsxt.message.vo.BaizhanTradeMessage;
import com.bjsxt.pojo.TbOrder;
import com.bjsxt.pojo.TbOrderItem;
import com.bjsxt.pojo.TbOrderShipping;
import com.bjsxt.pojo.TbUser;
import com.bjsxt.sender.TradeMessageSender;
import com.bjsxt.service.TradeService;
import com.bjsxt.utils.IDUtils;
import com.bjsxt.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class TradeServiceImpl implements TradeService {

    @Autowired
    private TradeMessageSender messageSender;

    @Value("${baizhan.trade.mq.exchange}")
    private String exchange;    //订单交换器

    @Value("${baizhan.trade.mq.routingKeyPrefix}")
    private String routingKeyPrefix; // 路由键前缀

    @Value("${baizhan.trade.redis.key}")
    private String tradeRedisKey; // redis中用于数字自增的数据key

    @Value("${baizhan.trade.mq.nums}")
    private int numbers; // 队列数量

    @Autowired
    private RedisDao redisDao;

    @Value("${baizhan.trade.cartMQ.exchange}")
    private String cartExchange;

    @Value("${baizhan.trade.cartMQ.routingKey}")
    private String cartRoutingKey;

    @Value("${baizhan.cart.prefix}")
    private String cartPrefix;

    @Value("${baizhan.trade.mailMQ.exchange}")
    private String mailExchange;

    /**
     * 创建订单
     * 注意：
     * 1. 考虑排队问题，相对公平，使用RabbitMQ排队创建订单
     * 2. 考虑高并发问题，如果瞬间并发请求过大，一个队列处理过慢，如何解决？
     * 3. 消息发送后，消费消息的时候，要考虑可能出现的分布式事务（创建订单，订单项，订单物流，更新商品库存）
     * 4. 考虑分布式锁问题，通过MQ排队后，只能解决公平问题，不能解决可能出现超售
     *
     * @param tbOrder         订单数据
     * @param tbOrderShipping 订单物流
     * @param orderItemsJson  订单项结合（JSON格式字符串）
     * @param tbUser
     * @return
     */
    @Override
    public BaizhanResult submitOrder(TbOrder tbOrder, TbOrderShipping tbOrderShipping, String orderItemsJson, TbUser tbUser) {
        //准备发送消息
        List<TbOrderItem> tbOrderItems = JsonUtils.jsonToList(orderItemsJson, TbOrderItem.class);

        //完善数据内容
        Date now = new Date();  //当前时间
        Long orderId = IDUtils.genItemId(); //生成订单主键
        tbOrder.setOrderId(orderId);    //设置订单主键
        tbOrder.setStatus(1);           //设置订单状态，未付款
        tbOrder.setCreateTime(now);
        tbOrder.setUpdateTime(now);
        tbOrder.setUserId(tbUser.getId());  //购买者主键
        tbOrder.setBuyerNick(tbUser.getUsername()); //购买者昵称
        tbOrder.setBuyerRate(0);    //购买者是否已评价

        tbOrderShipping.setOrderId(orderId);    //设置主键，就是订单的主键
        tbOrderShipping.setCreated(now);
        tbOrderShipping.setUpdated(now);

        List<Long> itemIds = new ArrayList<>();
        for (TbOrderItem orderItem : tbOrderItems) {
            orderItem.setId(IDUtils.genItemId());   //订单项主键
            orderItem.setOrderId(orderId);  //订单主键
            itemIds.add(orderItem.getItemId()); //收集后期可能使用到的，同步购物车商品主键
        }

        //创建消息对象
        BaizhanTradeMessage message = new BaizhanTradeMessage();
        message.setTbOrder(tbOrder);
        message.setTbOrderShipping(tbOrderShipping);
        message.setTbOrderItems(tbOrderItems);

        //获取自增数字
        long tmp = redisDao.incr(tradeRedisKey);
        //计算队列命名和路由键后缀
        long suffix = tmp % numbers;
        //拼接路由键
        String currentRoutingKey = routingKeyPrefix + suffix;
        //发送消息，保证订单创建结果一定已知
        boolean isCreated = (boolean) messageSender.sendSyncMessage(exchange, currentRoutingKey, message);
        if (isCreated) {
            //创建订单成功
            //通过异步消息，实现购物车同步逻辑
            BaizhanSyncCartMessage cartMessage = new BaizhanSyncCartMessage();
            cartMessage.setCartKey(cartPrefix + tbUser.getId());
            cartMessage.setItemIds(itemIds);
            messageSender.sendAsyncMessage(cartExchange, cartRoutingKey, cartMessage);

            //通过异步消息，实现信息提醒，当前使用javamail发送邮件
            BaizhanMailMessage mailMessage = new BaizhanMailMessage();
            mailMessage.setOrderId(orderId);
            mailMessage.setTo(tbUser.getEmail());
            messageSender.sendAsyncMessage(mailExchange, "mail", mailMessage);

            return BaizhanResult.ok(orderId);
        } else {
            //创建订单失败
            return BaizhanResult.error("您购买的商品已售罄，请重新下单。");
        }
    }

}
