package com.bjsxt.service;

import com.bjsxt.commons.pojo.BaizhanResult;

/**
 * 前台系统 - 商品详情服务接口
 */
public interface DetailsService {

    BaizhanResult getItemById(Long id);

    BaizhanResult getItemDescByItemId(Long itemId);

    BaizhanResult getItemParamItemByItemId(Long itemId);

}
