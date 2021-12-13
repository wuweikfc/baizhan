package com.bjsxt.management.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bjsxt.management.ContentsManagement;
import com.bjsxt.mapper.TbContentMapper;
import com.bjsxt.pojo.TbContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ContentsManagementImpl implements ContentsManagement {

    @Autowired
    private TbContentMapper contentMapper;

    /**
     * Spring Cache 可以解决的问题：永久保存的数据，如：门户系统中的导航数据
     * <p>
     * 从数据库，根据内容类型主键，查询内容集合
     * 1. 访问redis，查询缓存数据，如果存在则返回，不存在，执行后续代码
     * 2. 查询数据库，并缓存查询结果到redis
     * 3. 返回
     * <p>
     * 限制：只能注解描述公开方法，不能在当前类型中做方法调用
     * <p>
     * 问题：
     * 1. 后台写内容数据的时候，前台轮播广告不能同步
     * 解决：修改后台内容管理系统代码，增加缓存淘汰机制
     * 2. 缓存有效时间，广告的有效时间应该有多久
     * 应该是记录缓存的当天最后一秒
     * 使用configuration配置CacheManager，设置Spring Cache的特征，提供有效期设置
     * 延伸出第三个问题
     * 3. 缓存雪崩：在某一时刻，大量缓存同时失效，且有高并发查询请求，因为缓存中有大量缓存数据失效，需要访问数据库
     * 造成数据库压力过高，是Spring Cache解决不了的问题
     * 结局方案：
     * 1）设置定时任务，提前把数据缓存到redis中，预缓存
     * 2）设置变化的有效时长，如：固定一天的缓存时长，每条缓存数据的有效时长+|-20分钟
     *
     * @param bigAdCategoryId
     * @return
     */
    @Override
    @Cacheable(cacheNames = "baizhan:portal:contents", key = "#bigAdCategoryId")
    public List<TbContent> queryContentsFromDB(Long bigAdCategoryId) {

        QueryWrapper<TbContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("category_id", bigAdCategoryId);

        return contentMapper.selectList(queryWrapper);
    }

}
