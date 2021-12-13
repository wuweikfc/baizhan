package com.bjsxt.service;

import com.bjsxt.commons.pojo.BaizhanResult;

/**
 * 搜索服务接口
 */
public interface SearchService {

    boolean initElasticsearch();

    BaizhanResult search(String q, Integer page, Integer rows);

}
