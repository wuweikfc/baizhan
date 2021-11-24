package com.bjsxt.controller;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.pojo.TbItem;
import com.bjsxt.pojo.TbItemDesc;
import com.bjsxt.pojo.TbItemParamItem;
import com.bjsxt.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商品控制器
 */
@RestController
@CrossOrigin
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * 给订单提供的商品库存修改服务
     *
     * @param id  商品主键
     * @param num 购买数量
     * @return
     */
    @PostMapping("/forTrade/updateItemNum")
    public BaizhanResult updateItemNum4Trade(Long id, Integer num) {

        try {
            //更新数据库中的商品库存，LCN分布式事务
            BaizhanResult result = itemService.updateItemNum4Trade(id, num);
            if (result.getStatus() == 200) {
                //更新Redis中的商品库存，TCC分布式事务
                itemService.updateItemNum4Redis(id, num);
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return BaizhanResult.error("修改商品库存失败");
        }
    }

    /**
     * 为初始化Elasticsearch提供数据查询的服务
     *
     * @return {
     * "status" : 200,
     * "data": {
     * "item":[{TbItem对象},{}],
     * "itemCat":[{TbItemCat对象},{}],
     * "itemDesc":[{TbItemDesc对象},{}]
     * }
     * }
     */
    @PostMapping("/backend/item/getAllItems")
    public BaizhanResult getAllItems() {

        return itemService.getAllItems();
    }

    /**
     * 修改商品
     *
     * @param item
     * @param itemDesc  商品描述
     * @param paramData 商品规格
     * @return
     */
    @PostMapping("/backend/item/updateTbItem")
    public BaizhanResult modifyItem(TbItem item, String itemDesc, String paramData) {

        try {
            return itemService.modifyItem(item, itemDesc, paramData);
        } catch (Exception e) {
            e.printStackTrace();
            return BaizhanResult.error("服务器繁忙，请稍后重试");
        }
    }

    /**
     * 预加载商品数据，为修改商品提供数据参考
     *
     * @param id
     * @return
     */
    @PostMapping("/backend/item/preUpdateItem")
    public BaizhanResult preUpdateItem(Long id) {

        return itemService.preUpdateItem(id);
    }

    /**
     * 下架
     *
     * @param id
     * @return
     */
    @PostMapping("/backend/item/offshelfItemById")
    public BaizhanResult offShelfById(Long id) {

        try {
            return itemService.offShelfById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return BaizhanResult.error("服务器繁忙，请稍后重试");
        }

    }

    /**
     * 上架
     * 修改status字段值为1
     *
     * @param id
     * @return
     */
    @PostMapping("/backend/item/onshelfItemById")
    public BaizhanResult onShelfById(Long id) {

        try {
            return itemService.onShelfById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return BaizhanResult.error("服务器繁忙，请稍后重试");
        }
    }

    /**
     * 删除商品
     * 注意：本处删除是标记删除，不是物理删除
     * 标记删除：通过数据的状态，设定数据为删除状态，在商业项目中，99.5%数据删除，都是标记删除
     * 物理删除：delete数据，从数据库层面删除
     * 是修改tb_item表格中的status状态字段值，修改为3
     *
     * @param id
     * @return
     */
    @PostMapping("/backend/item/deleteItemById")
    public BaizhanResult removeItemById(Long id) {

        try {
            return itemService.removeItemById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return BaizhanResult.error("服务器繁忙，请稍后重试");
        }
    }

    /**
     * 新增商品
     *
     * @param item          商品数据
     * @param itemParamItem 商品规格
     * @param itemDesc      商品详情
     * @return
     */
    @PostMapping("/backend/item/insertTbItem")
    public BaizhanResult createItem(TbItem item, TbItemParamItem itemParamItem, TbItemDesc itemDesc) {

        try {
            return itemService.createItem(item, itemParamItem, itemDesc);
        } catch (Exception e) {
            e.printStackTrace();
            return BaizhanResult.error("服务器繁忙，请稍后重试");
        }

    }

    /**
     * 分页查询商品
     *
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/backend/item/selectTbItemAllByPage")
    public BaizhanResult getItemsByPage(@RequestParam(defaultValue = "1") Integer page,
                                        @RequestParam(defaultValue = "10") Integer rows) {

        return itemService.getItemsByPage(page, rows);
    }

}
