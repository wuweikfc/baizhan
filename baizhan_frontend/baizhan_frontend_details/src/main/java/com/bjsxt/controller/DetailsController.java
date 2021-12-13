package com.bjsxt.controller;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.service.DetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台系统 - 商品详情控制器
 */
@RestController
@CrossOrigin
public class DetailsController {

    @Autowired
    private DetailsService detailsService;

    /**
     * 根据商品主键，查询商品规格，访问tb_item_param_item表
     *
     * @param itemId
     * @return
     */
    @PostMapping("/item/selectTbItemParamItemByItemId")
    public BaizhanResult getItemParamItemByItemId(Long itemId) {

        return detailsService.getItemParamItemByItemId(itemId);
    }

    /**
     * 根据商品主键，查询商品描述，访问tb_item_desc表
     *
     * @param itemId
     * @return
     */
    @PostMapping("/item/selectItemDescByItemId")
    public BaizhanResult getItemDescByItemId(Long itemId) {

        return detailsService.getItemDescByItemId(itemId);
    }

    /**
     * 根据主键（SKU），查询商品基本信息，访问tb_item表
     *
     * @param id
     * @return
     */
    @PostMapping("/item/selectItemInfo")
    public BaizhanResult getItemById(Long id) {

        return detailsService.getItemById(id);
    }

}
