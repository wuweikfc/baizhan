package com.bjsxt.service;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.pojo.TbItemParam;

/**
 * 商品规格服务接口
 */
public interface ItemParamService {

    BaizhanResult getAllItemParams();

    BaizhanResult isHaveItemParamByItemCat(Long itemCatId);

    BaizhanResult createItemParam(TbItemParam itemParam);

    BaizhanResult removeItemParamById(Long id);

    BaizhanResult getItemParamByItemCat(Long itemCatId);

}
