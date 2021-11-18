package com.bjsxt.message.vo;

import com.bjsxt.message.BaizhanMessage;
import com.bjsxt.pojo.TbOrder;
import com.bjsxt.pojo.TbOrderItem;
import com.bjsxt.pojo.TbOrderShipping;
import lombok.Data;

import java.util.List;

@Data
public class BaizhanTradeMessage implements BaizhanMessage {

    private TbOrder tbOrder;

    private TbOrderShipping tbOrderShipping;

    private List<TbOrderItem> tbOrderItems;

}
