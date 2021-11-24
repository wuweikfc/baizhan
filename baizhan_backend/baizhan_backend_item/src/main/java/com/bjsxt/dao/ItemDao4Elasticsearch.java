package com.bjsxt.dao;

import com.bjsxt.commons.pojo.Item4Elasticsearch;

/**
 * 访问Elasticsearch的数据访问接口
 */
public interface ItemDao4Elasticsearch {

    /**
     * 根据主键删除
     * @param id
     */
    void removeById(Long id);

    void save(Item4Elasticsearch item4Elasticsearch);
}
