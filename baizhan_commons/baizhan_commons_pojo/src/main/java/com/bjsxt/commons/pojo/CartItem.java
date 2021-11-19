package com.bjsxt.commons.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 购物车中的商品对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItem implements Serializable {

    private Long id;    //商品主键

    private String title;   //商品标题

    private String sellPoint;   //商品卖点

    private String image;   //商品图片，所有图片数据，多个图片地址用逗号分隔

    private Long price;     //商品单价

    private Integer num;    //数量

    private Boolean enough; //库存是否充足


}
