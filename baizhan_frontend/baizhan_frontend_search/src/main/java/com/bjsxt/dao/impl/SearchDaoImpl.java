package com.bjsxt.dao.impl;

import com.bjsxt.commons.pojo.Item4Elasticsearch;
import com.bjsxt.dao.SearchDao;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索数据访问实现
 */
@Repository
public class SearchDaoImpl implements SearchDao {

    @Autowired
    private ElasticsearchRestTemplate restTemplate;

    /**
     * 创建索引并设置映射
     */
    @Override
    public void createIndex() {

        IndexOperations indexOps = restTemplate.indexOps(Item4Elasticsearch.class);
        //创建索引
        indexOps.create();
        //设置映射
        indexOps.putMapping(indexOps.createMapping());

    }

    /**
     * 批量新增数据
     *
     * @param items
     */
    @Override
    public void batchSave(List<Item4Elasticsearch> items) {

        restTemplate.save(items);

    }

    /**
     * 搜索，必须要返回高亮结果
     * 高亮字段包括：商品标题和卖点
     * 搜索条件在商品标题、卖点、描述、商品类型名称四个字段中匹配，任何字段包含搜索条件即返回
     * 搜索排序，按商品数据更新时间降序排列。（本系统处理方案）
     * 商业项目中的搜索排序：
     * 1. 按照推荐和置顶排序。后台系统对商品的管理过程，包含推荐和置顶等属性的管理
     * 如：自营主推的商品置顶，自营宣传的商品提升推荐级别。
     * 2. 按相关度排序，所谓相关度，就是商品标题，卖点，描述和搜索关键字的匹配度
     *
     * @param q
     * @param page
     * @param rows
     * @return
     */
    @Override
    public Map<String, Object> search(String q, Integer page, Integer rows) {
        //定义搜索条件
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.should(QueryBuilders.matchQuery("title", q));
        queryBuilder.should(QueryBuilders.matchQuery("sellPoint", q));
        queryBuilder.should(QueryBuilders.matchQuery("itemDesc", q));
        queryBuilder.should(QueryBuilders.matchQuery("categoryName", q));

        //高亮字段
        //标题
        HighlightBuilder.Field titleField = new HighlightBuilder.Field("title");
        titleField.preTags("<span style='color:red'>");
        titleField.postTags("</span>");
        titleField.fragmentSize(20);
        titleField.numOfFragments(1);

        //卖点
        HighlightBuilder.Field sellPointField = new HighlightBuilder.Field("sellPoint");
        sellPointField.preTags("<span style='color:red'>");
        sellPointField.postTags("</span>");
        sellPointField.fragmentSize(20);
        sellPointField.numOfFragments(1);

        NativeSearchQuery query = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .withHighlightFields(titleField, sellPointField)
                .withPageable(
                        PageRequest.of(page - 1, rows,
                                Sort.by(Sort.Direction.DESC, "updated"))
                )
                .build();

        SearchHits<Item4Elasticsearch> searchHits = restTemplate.search(query, Item4Elasticsearch.class);

        //处理返回结果
        List<Item4Elasticsearch> list = new ArrayList<>();
        //处理当前页面要显示的的数据
        searchHits.forEach(searchHit -> {
            //查询的对象
            Item4Elasticsearch item = searchHit.getContent();
            //处理高亮数据
            //标题高亮数据
            List<String> titles = searchHit.getHighlightField("title");
            if (titles != null && titles.size() > 0) {
                //有高亮标题
                item.setTitle(titles.get(0));
            }
            //卖点高亮数据
            List<String> sellPoints = searchHit.getHighlightField("sellPoint");
            if (sellPoints != null && sellPoints.size() > 0) {
                //有高亮卖点
                item.setSellPoint(sellPoints.get(0));
            }
            list.add(item);
        });

        //创建Map集合，维护返回结果中的data
        Map<String, Object> data = new HashMap<>();
        data.put("page", page);  //当前页码
        data.put("rows", rows);  //每页数据行数
        data.put("list", list);  //当前页显示的数据集合
        data.put("total", searchHits.getTotalHits());    //总计数据行数

        return data;
    }

}
