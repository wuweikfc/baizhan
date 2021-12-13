package com.bjsxt.service.impl;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.commons.pojo.Item4Elasticsearch;
import com.bjsxt.dao.SearchDao;
import com.bjsxt.feign.BackendItemFeignInterface;
import com.bjsxt.pojo.TbItem;
import com.bjsxt.pojo.TbItemCat;
import com.bjsxt.pojo.TbItemDesc;
import com.bjsxt.service.SearchService;
import com.bjsxt.utils.JsonUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 搜索服务实现
 */
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private BackendItemFeignInterface backendItemFeignInterface;

    @Autowired
    private SearchDao searchDao;

    /**
     * 初始化：
     * 1. 访问远程微服务 backend_item ，查询需要的所有数据。
     * 需要的数据： 所有的商品，对应的商品类型，所有的商品描述
     * 2. 转换成需要的实体类型
     * 3. 保存到Elasticsearch中。
     *
     * @return
     */
    @Override
    public boolean initElasticsearch() {
        //查询所有需要的数据
        BaizhanResult result = backendItemFeignInterface.getAllItems();
        Map<String, Object> data = (Map<String, Object>) result.getData();

        //数据转换
        //全部商品
        Object itemTmp = data.get("item");
        List<TbItem> items = JsonUtils.jsonToList(JsonUtils.objectToJson(itemTmp), TbItem.class);
        //全部商品类型
        List<TbItemCat> itemCats = JsonUtils.jsonToList(JsonUtils.objectToJson(data.get("itemCat")), TbItemCat.class);
        //全部商品描述
        List<TbItemDesc> itemDescs = JsonUtils.jsonToList(JsonUtils.objectToJson(data.get("itemDesc")), TbItemDesc.class);

        //创建集合，提高处理效率，key - 商品类型主键 value - 商品类型对象
        Map<Long, TbItemCat> itemCatMap = new HashMap<>();
        itemCats.forEach(itemCat -> {
            itemCatMap.put(itemCat.getId(), itemCat);
        });

        //key - 商品描述主键 value - 商品描述对象
        Map<Long, TbItemDesc> itemDescMap = new HashMap<>();
        itemDescs.forEach(itemDesc -> {
            itemDescMap.put(itemDesc.getItemId(), itemDesc);
        });

        //定义要保存到Elasticsearch中的数据集合
        List<Item4Elasticsearch> list = new ArrayList<>();
        for (TbItem item : items) {
            Item4Elasticsearch item4Elasticsearch = new Item4Elasticsearch();
            BeanUtils.copyProperties(item, item4Elasticsearch);
            item4Elasticsearch.setId(item.getId().toString());  //主键
            TbItemCat catTmp = itemCatMap.get(item.getCid());
            item4Elasticsearch.setCategoryName(catTmp == null ? "" : catTmp.getName()); //商品类型名称
            TbItemDesc descTmp = itemDescMap.get(item.getId());
            item4Elasticsearch.setItemDesc(descTmp == null ? "" : descTmp.getItemDesc()); //商品描述
            list.add(item4Elasticsearch);
        }

        //保存数据到Elasticsearch中
        //创建索引
        searchDao.createIndex();
        //批量保存数据到Elasticsearch中
        searchDao.batchSave(list);

        return true;
    }

    /**
     * 搜索
     *
     * @param q
     * @param page
     * @param rows
     * @return
     */
    @Override
    public BaizhanResult search(String q, Integer page, Integer rows) {

        Map<String, Object> result = searchDao.search(q, page, rows);

        return BaizhanResult.ok(result);
    }

}
