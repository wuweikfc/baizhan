package com.bjsxt.commons.pojo;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.util.Date;

/**
 * 保存在Elasticsearch中的商品类型，用于前台搜索使用
 */
@Data
@Document(indexName = "baizhan_item", shards = 1, replicas = 0)
public class Item4Elasticsearch implements Serializable {

    @Id
    @Field(name = "id", type = FieldType.Keyword)
    private String id;  //主键, Elasticsearch中的主键是字符串

    @Field(name = "title", type = FieldType.Text, analyzer = "ik_max_word")
    private String title;   //商品标题

    @Field(name = "sellPoint", type = FieldType.Text, analyzer = "ik_max_word")
    private String sellPoint;   //商品卖点

    @Field(name = "price", type = FieldType.Long, index = false)
    private Long price; //单价

    @Field(name = "image", type = FieldType.Keyword, index = false)
    private String image; // 图片， 多图片使用逗号分隔

    @Field(name = "categoryName", type = FieldType.Text, analyzer = "ik_max_word")
    private String categoryName; // 商品类型名称

    @Field(name = "itemDesc", type = FieldType.Text, analyzer = "ik_max_word")
    private String itemDesc; // 商品描述

    @Field(name = "updated", type = FieldType.Date, format = DateFormat.date_time)
    private Date updated; // 更新时间

}
