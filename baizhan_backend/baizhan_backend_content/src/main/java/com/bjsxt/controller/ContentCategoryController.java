package com.bjsxt.controller;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.pojo.TbContentCategory;
import com.bjsxt.service.ContentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 内容分类控制器
 */
@RestController
@CrossOrigin
public class ContentCategoryController {

    @Autowired
    private ContentCategoryService contentCategoryService;

    /**
     * 修改内容分类
     *
     * @param contentCategory
     * @return
     */
    @PostMapping("/backend/contentCategory/updateContentCategory")
    public BaizhanResult modifyContentCategory(TbContentCategory contentCategory) {

        try {
            return contentCategoryService.modifyContentCategory(contentCategory);
        } catch (Exception e) {
            e.printStackTrace();
            return BaizhanResult.error("服务器繁忙，请稍后重试");
        }
    }

    /**
     * 删除内容分类
     * 确定：如果要删除的内容分类还有子节点（有效子节点，status=1），是否可以删除
     * 1、级联删除，删除当前节点和所有子孙节点，使用递归处理或循环处理
     * 2、不删除，提示错误，要求必须从叶子节点开始依次删除
     *
     * @param id
     * @return
     */
    @PostMapping("/backend/contentCategory/deleteContentCategoryById")
    public BaizhanResult removeContentCategory(Long id) {

        try {
            return contentCategoryService.removeContentCategory(id);
        } catch (Exception e) {
            e.printStackTrace();
            return BaizhanResult.error("服务器繁忙，请稍后重试");
        }
    }

    /**
     * 创建内容分类
     *
     * @param contentCategory
     * @return
     */
    @PostMapping("/backend/contentCategory/insertContentCategory")
    public BaizhanResult createContentCategory(TbContentCategory contentCategory) {

        try {
            return contentCategoryService.createContentCategory(contentCategory);
        } catch (Exception e) {
            e.printStackTrace();
            return BaizhanResult.error("服务器繁忙，请稍后重试");
        }

    }

    /**
     * 根据父节点查询子节点集合
     *
     * @param id
     * @return
     */
    @PostMapping("/backend/contentCategory/selectContentCategoryByParentId")
    public BaizhanResult getContentCategoriesByParent(@RequestParam(defaultValue = "0") Long id) {

        return contentCategoryService.getContentCategoriesByParent(id);
    }

}
