package com.bjsxt.service;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.pojo.TbOrder;
import com.bjsxt.pojo.TbOrderShipping;
import com.bjsxt.pojo.TbUser;

/**
 * 前台系统 - 订单服务接口
 */
public interface TradeService {
    BaizhanResult submitOrder(TbOrder tbOrder, TbOrderShipping tbOrderShipping, String orderItemsJson, TbUser tbUser);

}
