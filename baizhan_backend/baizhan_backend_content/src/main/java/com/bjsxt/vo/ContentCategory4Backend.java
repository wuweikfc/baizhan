package com.bjsxt.vo;

import com.bjsxt.pojo.TbContentCategory;
import lombok.Data;

@Data
public class ContentCategory4Backend extends TbContentCategory {

    //定义属性推导
    public boolean getLeaf() {

        return !getIsParent();
    }

    public void setLeaf(boolean leaf) {

    }

}
