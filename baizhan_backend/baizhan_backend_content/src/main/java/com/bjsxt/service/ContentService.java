package com.bjsxt.service;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.pojo.TbContent;

/**
 * 内容服务接口
 */
public interface ContentService {

    BaizhanResult getContentsByCategory(Long categoryId);

    BaizhanResult createContent(TbContent tbContent);

    BaizhanResult removeContentById(Long id);

}
