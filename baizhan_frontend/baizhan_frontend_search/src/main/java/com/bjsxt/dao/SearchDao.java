package com.bjsxt.dao;

import com.bjsxt.commons.pojo.Item4Elasticsearch;

import java.util.List;
import java.util.Map;

/**
 * 搜索数据访问接口
 */
public interface SearchDao {

    void createIndex();

    void batchSave(List<Item4Elasticsearch> items);

    Map<String, Object> search(String q, Integer page, Integer rows);

}
