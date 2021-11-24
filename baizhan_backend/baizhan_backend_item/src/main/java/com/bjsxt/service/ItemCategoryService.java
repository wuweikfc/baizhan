package com.bjsxt.service;

import com.bjsxt.commons.pojo.BaizhanResult;

/**
 * 商品类型服务接口
 */
public interface ItemCategoryService {

    BaizhanResult getItemCatsByParent(Long id);

}
