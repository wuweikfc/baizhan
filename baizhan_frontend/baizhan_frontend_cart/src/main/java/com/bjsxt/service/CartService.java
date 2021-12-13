package com.bjsxt.service;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.pojo.TbUser;

/**
 * 前台系统，购物车服务接口
 */
public interface CartService {

    BaizhanResult addItem2Cart(Long itemId, Integer num, TbUser loginUser);

    BaizhanResult showCart(TbUser loginUser);

    BaizhanResult modifyCartItemNum(Long itemId, Integer num, TbUser loginUser);

    BaizhanResult removeItemFromCart(Long itemId, TbUser loginUser);

    BaizhanResult getSettlement(Long[] id, TbUser loginUser);

}
