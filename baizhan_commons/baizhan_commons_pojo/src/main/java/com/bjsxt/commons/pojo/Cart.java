package com.bjsxt.commons.pojo;

import lombok.Data;

import java.io.Serializable;
import java.util.*;

/**
 * 自定义购物车类型
 * 内部封装一个Map，用于保存商品数据
 * 并提供若干可操作的方法，让购物车使用方便
 */
@Data
public class Cart implements Serializable {

    /**
     * 购物车数据最终保存在Redis中，任何不能最终确认的数据，都不能保存在数据库DB中
     * 在Redis中保存的数据，默认类型都是字符串。如果使用Long作为key，在反向序列化的时候
     * 可能有类型匹配问题
     * 保存商品的集合
     * key - 商品主键
     * value - 商品对象
     */
    private Map<String, CartItem> contents = new HashMap<>();

    /**
     * 获取购物车内商品数据信息
     *
     * @param id
     * @return
     */
    public List<CartItem> fetchItems(Long[] id) {

        List<CartItem> result = new ArrayList<>();
        for (Long itemId : id) {
            result.add(contents.get(itemId.toString()));
        }
        return result;
    }

    /**
     * 删除购物车中商品
     *
     * @param itemId
     */
    public void removeItem(Long itemId) {

        contents.remove(itemId.toString());
    }

    /**
     * 修改商品数量
     *
     * @param itemId
     * @param num
     */
    public void modifyItemNum(Long itemId, Integer num) {

        CartItem current = contents.get(itemId.toString());
        current.setNum(num);
    }

    /**
     * 返回购物车中的商品集合
     *
     * @return
     */
    public Collection<CartItem> showContents() {

        return contents.values();
    }

    /**
     * 新增商品到购物车
     * @param item
     */
    public void addItem(CartItem item) {

        CartItem current = contents.get(item.getId().toString());
        if (current == null) {
            //购物车中没有相同的商品
            contents.put(item.getId().toString(), item);
        } else {
            //购物车中有相同商品，修改商品数量
            current.setNum(current.getNum() + item.getNum());
        }
    }

}
