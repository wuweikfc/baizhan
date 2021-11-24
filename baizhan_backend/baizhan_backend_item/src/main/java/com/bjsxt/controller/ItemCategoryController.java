package com.bjsxt.controller;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.service.ItemCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品类型控制器
 */
@RestController
@CrossOrigin
public class ItemCategoryController {
    @Autowired
    private ItemCategoryService itemCategoryService;

    /**
     * 根据商品类型父节点主键，查询所有子节点集合
     * @param id    父节点主键，第一次请求的时候，没有参数，默认查询父节点主键为0的数据
     * @return
     */
    @PostMapping("/backend/itemCategory/selectItemCategoryByParentId")
    public BaizhanResult getItemCatsByParent(@RequestParam(defaultValue = "0") Long id){
        return itemCategoryService.getItemCatsByParent(id);

    }

}
