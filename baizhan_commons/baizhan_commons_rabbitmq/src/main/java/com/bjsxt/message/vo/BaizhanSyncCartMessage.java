package com.bjsxt.message.vo;

import com.bjsxt.message.BaizhanMessage;
import lombok.Data;

import java.util.List;

/**
 * 订单创建成功后用于同步购物车的消息
 */
@Data
public class BaizhanSyncCartMessage implements BaizhanMessage {

    private String cartKey; //购物车保存在Redis中的key

    private List<Long> itemIds; //要从购物车中删除的商品主键集合

}
