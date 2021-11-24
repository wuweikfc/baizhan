package com.bjsxt.controller;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.pojo.TbItemParam;
import com.bjsxt.service.ItemParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 商品规格控制器
 */
@RestController
@CrossOrigin
public class ItemParamController {

    @Autowired
    private ItemParamService itemParamService;

    /**
     * 根据商品类型主键，查询商品规格模板
     *
     * @param itemCatId
     * @return
     */
    @GetMapping("/backend/itemParam/selectItemParamByItemCatId/{itemCatId}")
    public BaizhanResult getItemParamByItemCat(@PathVariable("itemCatId") Long itemCatId) {

        return itemParamService.getItemParamByItemCat(itemCatId);
    }

    /**
     * 根据主键，删除商品规格
     *
     * @param id
     * @return
     */
    @PostMapping("/backend/itemParam/deleteItemParamById")
    public BaizhanResult removeItemParamById(Long id) {

        try {
            return itemParamService.removeItemParamById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return BaizhanResult.error("服务器繁忙，请稍后重试");
        }
    }

    /**
     * 新增商品规格
     *
     * @param itemParam
     * @return
     */
    @PostMapping("/backend/itemParam/insertItemParam")
    public BaizhanResult createItemParam(TbItemParam itemParam) {

        try {
            return itemParamService.createItemParam(itemParam);
        } catch (Exception e) {
            e.printStackTrace();
            return BaizhanResult.error("服务器繁忙，请稍后重试");
        }

    }

    @GetMapping("/backend/itemParam/selectHaveParam")
    public BaizhanResult isHaveItemParamByItemCat(Long itemCatId) {

        return itemParamService.isHaveItemParamByItemCat(itemCatId);
    }

    /**
     * 查询所有的商品规格
     *
     * @return
     * {
     *      "status":200,
     *      "msg":"OK",
     *      "data":[{ItemParam类型对象},{}...]
     * }
     */
    @GetMapping("/backend/itemParam/selectItemParamAll")
    public BaizhanResult getAllItemParams() {

        return itemParamService.getAllItemParams();
    }

}
