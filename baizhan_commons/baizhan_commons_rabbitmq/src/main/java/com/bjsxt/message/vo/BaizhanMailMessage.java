package com.bjsxt.message.vo;

import com.bjsxt.message.BaizhanMessage;

/**
 * 发送订单创建成功的邮件消息
 */
public class BaizhanMailMessage implements BaizhanMessage {

    private String to;  //邮件发送给谁

    private Long orderId;   //创建的订单编号是什么

}
