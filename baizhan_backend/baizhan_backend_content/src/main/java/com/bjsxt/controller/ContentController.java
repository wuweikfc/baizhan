package com.bjsxt.controller;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.pojo.TbContent;
import com.bjsxt.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class ContentController {

    @Autowired
    private ContentService contentService;

    /**
     * 删除内容
     *
     * @param id
     * @return
     */
    @PostMapping("/backend/content/deleteContentByIds")
    public BaizhanResult removeContentById(Long id) {

        try {
            return contentService.removeContentById(id);
        } catch (Exception e) {
            e.printStackTrace();
            return BaizhanResult.error("服务器繁忙，请稍后重试");
        }

    }

    /**
     * 创建内容
     *
     * @param content 要新增的内容
     * @return
     */
    @PostMapping("/backend/content/insertTbContent")
    public BaizhanResult createContent(TbContent content) {

        try {
            return contentService.createContent(content);
        } catch (Exception e) {
            e.printStackTrace();
            return BaizhanResult.error("服务器繁忙，请稍后重试");
        }
    }

    /**
     * 根据内容分类主键，查询内容集合
     *
     * @param categoryId
     * @return
     */
    @PostMapping("/backend/content/selectTbContentAllByCategoryId")
    public BaizhanResult getContentsByCategory(Long categoryId) {

        return contentService.getContentsByCategory(categoryId);
    }

}
