package com.bjsxt.controller;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.pojo.TbOrder;
import com.bjsxt.pojo.TbOrderShipping;
import com.bjsxt.pojo.TbUser;
import com.bjsxt.service.TradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * 前台系统，订单控制器
 */
@RestController
@CrossOrigin
public class TradeController {

    @Autowired
    private TradeService tradeService;

    /**
     * 提交订单
     * 参数包括： 订单信息（总价，支付方式），订单物流信息（收货人相关数据），
     * 订单项集合（JSON描述的数据）
     * 使用自定义类型对象，实现请求参数处理。
     * 使用已有类型对象，处理请求。
     * 订单信息 - TbOrder
     * 订单物流 - TbOrderShipping
     * 订单项集合 - String, 通过Jackson转换字符串-> List集合
     *
     * @return
     */
    @PostMapping("/order/insertOrder")
    public BaizhanResult submitOrder(TbOrder tbOrder, TbOrderShipping tbOrderShipping, String orderItem, HttpSession session) {

        try {
            TbUser user = (TbUser) session.getAttribute("loginUser");
            return tradeService.submitOrder(tbOrder, tbOrderShipping, orderItem, user);
        } catch (Exception e) {
            e.printStackTrace();
            return BaizhanResult.error("服务器忙，请稍后重试");
        }
//        System.out.println("订单 - " + tbOrder);
//        System.out.println("订单物流 - " + tbOrderShipping);
//        System.out.println("订单项 - " + JsonUtils.jsonToList(orderItem, TbOrderItem.class));
    }

}
