package com.bjsxt.service.impl;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.feign.BaizhanBackendItemRemoteInterface;
import com.bjsxt.mapper.TbOrderItemMapper;
import com.bjsxt.mapper.TbOrderMapper;
import com.bjsxt.mapper.TbOrderShippingMapper;
import com.bjsxt.pojo.TbOrder;
import com.bjsxt.pojo.TbOrderItem;
import com.bjsxt.pojo.TbOrderShipping;
import com.bjsxt.service.TradeService4Consumer;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TradeService4ConsumerImpl implements TradeService4Consumer {

    @Autowired
    private TbOrderMapper orderMapper;

    @Autowired
    private TbOrderShippingMapper orderShippingMapper;

    @Autowired
    private TbOrderItemMapper orderItemMapper;

    @Autowired
    private BaizhanBackendItemRemoteInterface itemRemoteInterface;

    /**
     * 订单创建逻辑的方法，并且调用远程服务，处理分布式事务
     *
     * @param order
     * @param orderShipping
     * @param orderItems
     * @return
     */
    @Override
    @LcnTransaction
    @Transactional(rollbackFor = {RuntimeException.class})
    public boolean createOrder(TbOrder order, TbOrderShipping orderShipping, List<TbOrderItem> orderItems) {

        int rows = orderMapper.insert(order);
        if (rows != 1) {
            //创建订单错误
            throw new RuntimeException("创建订单错误");
        }

        rows = orderShippingMapper.insert(orderShipping);
        if (rows != 1) {
            throw new RuntimeException("创建订单物流错误");
        }

        rows = 0;
        for (TbOrderItem orderItem : orderItems) {
            rows += orderItemMapper.insert(orderItem);

        }
        if (rows != orderItems.size()) {
            throw new RuntimeException("创建订单项错误");
        }

        //调用远程服务，实现商品库存修改，使用后台商品系统(baizhan_backend_item)实现具体逻辑
        for (TbOrderItem orderItem : orderItems) {
            BaizhanResult result = itemRemoteInterface.updateItemNum4Trade(orderItem.getItemId(), orderItem.getNum());
            if (result.getStatus() != 200) {
                //更新库存错误
                throw new RuntimeException("更新商品库存出错");
            }
        }
        return true;
    }

}
