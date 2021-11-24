package com.bjsxt.service;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.pojo.TbItem;
import com.bjsxt.pojo.TbItemDesc;
import com.bjsxt.pojo.TbItemParamItem;

public interface ItemService {

    BaizhanResult getItemsByPage(Integer page, Integer rows);

    BaizhanResult createItem(TbItem item, TbItemParamItem itemParamItem, TbItemDesc itemDesc);

    BaizhanResult removeItemById(Long id);

    BaizhanResult onShelfById(Long id);

    BaizhanResult offShelfById(Long id);

    BaizhanResult preUpdateItem(Long id);

    BaizhanResult modifyItem(TbItem item, String itemDesc, String paramData);

    BaizhanResult getAllItems();

    BaizhanResult updateItemNum4Trade(Long id, Integer num);

    void updateItemNum4Redis(Long id, Integer num);

}
