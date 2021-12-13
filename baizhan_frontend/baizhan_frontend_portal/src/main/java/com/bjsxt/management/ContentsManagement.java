package com.bjsxt.management;

import com.bjsxt.pojo.TbContent;

import java.util.List;

/**
 * 内容管理服务接口
 */
public interface ContentsManagement {
    List<TbContent> queryContentsFromDB(Long bigAdCategoryId);

}
