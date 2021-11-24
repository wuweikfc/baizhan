package com.bjsxt.vo;

import com.bjsxt.pojo.TbItemCat;
import lombok.Data;

/**
 * 给后台商品系统提供的自定义类型，用于处理商品分类实体
 */
@Data
public class ItemCat4BackendItem extends TbItemCat {

    public boolean getLeaf() {

        return !getIsParent();
    }

    public void setLeaf(boolean leaf) {
        // 推导属性leaf，值根据父类型中的isParent进行推导计算
    }

}
