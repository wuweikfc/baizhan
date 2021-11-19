package com.bjsxt.service;

import com.bjsxt.pojo.TbOrder;
import com.bjsxt.pojo.TbOrderItem;
import com.bjsxt.pojo.TbOrderShipping;

import java.util.List;

/**
 * 订单创建功能接口
 */
public interface TradeService4Consumer {
    public boolean createOrder(TbOrder order, TbOrderShipping orderShipping, List<TbOrderItem> orderItems);

}
