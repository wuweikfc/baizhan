package com.bjsxt.service;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.pojo.TbContentCategory;

/**
 * 内容分类服务接口
 */
public interface ContentCategoryService {

    BaizhanResult getContentCategoriesByParent(Long id);

    BaizhanResult createContentCategory(TbContentCategory contentCategory);

    BaizhanResult removeContentCategory(Long id);

    BaizhanResult modifyContentCategory(TbContentCategory contentCategory);

}
