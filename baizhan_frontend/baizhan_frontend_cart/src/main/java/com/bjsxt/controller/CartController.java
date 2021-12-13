package com.bjsxt.controller;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.pojo.TbUser;
import com.bjsxt.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

/**
 * 前台系统，购物车控制器
 */
@RestController
@CrossOrigin
public class CartController {

    @Autowired
    private CartService cartService;

    /**
     * 去结算，确认要购买的商品信息
     * 需要检查，商品库存是否充足
     *
     * @param id
     * @param session
     * @return
     */
    @PostMapping("/cart/goSettlement")
    public BaizhanResult goSettlement(Long[] id, HttpSession session) {

        TbUser loginUser = (TbUser) session.getAttribute("loginUser");

        return cartService.getSettlement(id, loginUser);

    }

    /**
     * 从购物车中删除商品
     *
     * @param itemId
     * @param session
     * @return
     */
    @PostMapping("/cart/deleteItemFromCart")
    public BaizhanResult removeItemFromCart(Long itemId, HttpSession session) {

        TbUser loginUser = (TbUser) session.getAttribute("loginUser");

        return cartService.removeItemFromCart(itemId, loginUser);
    }

    /**
     * 修改购物车中商品数量
     *
     * @param itemId  商品主键
     * @param num     修改后的数量
     * @param session
     * @return
     */
    @PostMapping("/cart/updateItemNum")
    public BaizhanResult modifyCartItemNum(Long itemId, Integer num, HttpSession session) {

        TbUser loginUser = (TbUser) session.getAttribute("loginUser");

        return cartService.modifyCartItemNum(itemId, num, loginUser);
    }

    /**
     * 查看购物车内容，查看当前登录用户的购物车
     *
     * @param session
     * @return
     */
    @GetMapping("/cart/showCart")
    public BaizhanResult showCart(HttpSession session) {

        TbUser loginUser = (TbUser) session.getAttribute("loginUser");

        return cartService.showCart(loginUser);

    }

    /**
     * 增加商品到购物车
     * 分析：购物车是什么？
     * 购物车是保存客户想要购买的商品的一个集合
     * 使用Map集合来模拟一个购物车，key是商品主键，value是想要购买的商品对象
     * <p>
     * 购物车中保存的商品数据包括：
     * 主键  id
     * 图片  image
     * 标题  title
     * 卖点  sellPoint
     * 单价  price
     * 数量  num
     *
     * @param itemId
     * @param num
     * @param session
     * @return
     */
    @PostMapping("/cart/addItem")
    public BaizhanResult addItem2Cart(Long itemId, Integer num, HttpSession session) {
        //获取当前登录的用户
        TbUser loginUser = (TbUser) session.getAttribute("loginUser");
        return cartService.addItem2Cart(itemId, num, loginUser);

    }

}
