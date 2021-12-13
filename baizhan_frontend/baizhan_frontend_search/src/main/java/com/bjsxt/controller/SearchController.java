package com.bjsxt.controller;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.service.SearchService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 前台搜索控制器
 */
@RestController
@CrossOrigin
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 搜索，搜索逻辑一定要注意，不能有魔鬼搜索
     * 搜索条件必须存在
     * 商业项目中
     * 前端的逻辑会实现客户端的校验
     * 1. 搜索框提供默认值
     * 2. 搜索框不输入搜索条件，不能提交请求
     * <p>
     * 后端逻辑
     * 1. 如果搜索条件不存在，直接返回重定向要求，让前端系统回到门户首页
     * 2. 提供默认搜索条件，如：当前站点是主推手机的电商平台，没有搜索条件，就返回手机的搜索内容
     * <p>
     * 本系统处理方案：
     * 如果搜索条件不存在，则默认搜索手机
     *
     * @param q
     * @param page
     * @param rows
     * @return
     */
    @PostMapping("/search/list")
    public BaizhanResult search(String q,
                                @RequestParam(defaultValue = "1") Integer page,
                                @RequestParam(defaultValue = "25") Integer rows) {
        //对请求参数中的搜索条件做空值判断
        if (StringUtils.isBlank(q)) {
            q = "手机";
        }

        //搜索
        return searchService.search(q, page, rows);
    }

    /**
     * 初始化Elasticsearch中的数据
     * 功能，查询数据库中的商品及相关数据，并保存到Elasticsearch中
     * <p>
     * 流程
     * 1. 调用远程微服务backend_item中提供的服务逻辑，查询商品及相关数据
     * 2. 把远程查询返回结果转换成需要的针对Elasticsearch中索引定义的实体类型
     * 3. 访问Elasticsearch，保存数据
     *
     * @return
     */
    @PostMapping("/initElasticsearch")
    public String initElasticsearch() {

        boolean isInit = searchService.initElasticsearch();
        if (isInit) {
            return "初始化成功";
        }
        return "初始化失败";
    }

}
