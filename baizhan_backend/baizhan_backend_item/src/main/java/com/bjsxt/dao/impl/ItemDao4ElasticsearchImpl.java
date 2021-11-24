package com.bjsxt.dao.impl;

import com.bjsxt.commons.pojo.Item4Elasticsearch;
import com.bjsxt.dao.ItemDao4Elasticsearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.stereotype.Repository;

/**
 * Elasticsearch数据访问实现
 */
@Repository
public class ItemDao4ElasticsearchImpl implements ItemDao4Elasticsearch {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    /**
     * 根据主键删除
     *
     * @param id
     */
    @Override
    public void removeById(Long id) {

        restTemplate.delete(id.toString(), Item4Elasticsearch.class);

    }

    /**
     * 保存数据到Elasticsearch
     *
     * @param item4Elasticsearch
     */
    @Override
    public void save(Item4Elasticsearch item4Elasticsearch) {

        restTemplate.save(item4Elasticsearch);
    }

}
