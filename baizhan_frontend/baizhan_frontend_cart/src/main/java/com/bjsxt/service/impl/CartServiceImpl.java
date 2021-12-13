package com.bjsxt.service.impl;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.commons.pojo.Cart;
import com.bjsxt.commons.pojo.CartItem;
import com.bjsxt.dao.redis.RedisDao;
import com.bjsxt.feign.ItemFeignInterface;
import com.bjsxt.pojo.TbItem;
import com.bjsxt.pojo.TbUser;
import com.bjsxt.service.CartService;
import com.bjsxt.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private ItemFeignInterface itemFeignInterface;

    @Value("${baizhan.cart.prefix}")
    private String cartPrefix;

    @Value("${baizhan.details.prefix.item}")
    private String itemPrefix;

    /**
     * 添加商品到购物车
     * 1. 找到当前登录用户的购物车
     * 1.1 购物车保存在Redis中，查询
     * 1.2 查询结果未必存在，如果没有购物车，需要创建一个新的
     * 2. 增加商品到购物车
     * 2.1 把购物车保存到redis中
     *
     * @param itemId
     * @param num
     * @param loginUser
     * @return
     */
    @Override
    public BaizhanResult addItem2Cart(Long itemId, Integer num, TbUser loginUser) {
        //查询购物车
        String key = cartPrefix + loginUser.getId();
        Cart cart = redisDao.get(key);

        //判断购物车是否存在
        if (cart == null) {
            //购物车不存在，初始化购物车
            cart = new Cart();
        }

        //从Redis中查询要购买的商品数据
        TbItem item = redisDao.get(itemPrefix + itemId);
        //增加商品到购物车
        CartItem cartItem = new CartItem(itemId, item.getTitle(), item.getSellPoint(), item.getImage(), item.getPrice(), num, false);
        cart.addItem(cartItem);

        //保存新购物车数据到Redis
        redisDao.set(key, cart);

        return BaizhanResult.ok();
    }

    /**
     * 查看购物车
     *
     * @param loginUser
     * @return
     */
    @Override
    public BaizhanResult showCart(TbUser loginUser) {
        //从Redis中查询当前登录用户的购物车
        String key = cartPrefix + loginUser.getId();
        Cart cart = redisDao.get(key);

        //判断购物车是否存在
        if (cart == null) {
            cart = new Cart();
        }

        //获取购物车内容
        Collection<CartItem> contents = cart.showContents();

        return BaizhanResult.ok(contents);
    }

    /**
     * 修改商品数量
     *
     * @param itemId
     * @param num
     * @param loginUser
     * @return
     */
    @Override
    public BaizhanResult modifyCartItemNum(Long itemId, Integer num, TbUser loginUser) {
        //查询购物车
        String key = cartPrefix + loginUser.getId();
        Cart cart = redisDao.get(key);

        //判断购物车是否存在
        if (cart == null) {
            return BaizhanResult.error("商品不存在，请刷新购物车");
        }

        //修改商品数量
        cart.modifyItemNum(itemId, num);

        //保存购物车到Redis
        redisDao.set(key, cart);

        return BaizhanResult.ok();
    }

    /**
     * 从购物车中删除商品
     *
     * @param itemId
     * @param loginUser
     * @return
     */
    @Override
    public BaizhanResult removeItemFromCart(Long itemId, TbUser loginUser) {
        //查询购物车
        String key = cartPrefix + loginUser.getId();
        Cart cart = redisDao.get(key);

        //判断购物车是否存在
        if (cart == null) {
            return BaizhanResult.error("要删除的商品不存在，请刷新购物车");
        }

        //删除商品
        cart.removeItem(itemId);

        //保存购物车到Redis
        redisDao.set(key, cart);

        return BaizhanResult.ok();
    }

    /**
     * 去结算
     *
     * @param id
     * @param loginUser
     * @return
     */
    @Override
    public BaizhanResult getSettlement(Long[] id, TbUser loginUser) {
        //查询购物车
        String key = cartPrefix + loginUser.getId();
        Cart cart = redisDao.get(key);

        //获取要购买的商品数据集合
        List<CartItem> contents = cart.fetchItems(id);

        //检验要购买的商品库存是否充足
        //查询商品的持久化信息，从数据库查询商品
        for (CartItem cartItem : contents) {
            String itemJson = JsonUtils.objectToJson(itemFeignInterface.getItemById(cartItem.getId()).getData());
            TbItem item = JsonUtils.jsonTpPojo(itemJson, TbItem.class);
            cartItem.setEnough(item.getNum() >= cartItem.getNum());
        }

        return BaizhanResult.ok(contents);
    }

}
