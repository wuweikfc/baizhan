package com.bjsxt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.commons.pojo.Item4Elasticsearch;
import com.bjsxt.dao.ItemDao4Elasticsearch;
import com.bjsxt.dao.redis.RedisDao;
import com.bjsxt.mapper.TbItemCatMapper;
import com.bjsxt.mapper.TbItemDescMapper;
import com.bjsxt.mapper.TbItemMapper;
import com.bjsxt.mapper.TbItemParamItemMapper;
import com.bjsxt.pojo.TbItem;
import com.bjsxt.pojo.TbItemCat;
import com.bjsxt.pojo.TbItemDesc;
import com.bjsxt.pojo.TbItemParamItem;
import com.bjsxt.service.ItemService;
import com.bjsxt.utils.IDUtils;
import com.codingapi.txlcn.tc.annotation.LcnTransaction;
import com.codingapi.txlcn.tc.annotation.TccTransaction;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbItemParamItemMapper itemParamItemMapper;

    @Autowired
    private TbItemDescMapper itemDescMapper;

    @Autowired
    private TbItemCatMapper itemCatMapper;

    @Autowired
    private ItemDao4Elasticsearch itemDao4Elasticsearch;

    @Autowired
    private RedisDao redisDao;

    @Value("${baizhan.details.prefix.item}")
    private String itemPrefix;

    @Value("${baizhan.details.prefix.itemDesc}")
    private String itemDescPrefix;

    @Value("${baizhan.details.prefix.itemParamItem}")
    private String itemParamItemPrefix;

    /**
     * 分页查询
     *
     * @param page
     * @param rows
     * @return
     */
    @Override
    public BaizhanResult getItemsByPage(Integer page, Integer rows) {

        //创建分页条件
        IPage<TbItem> iPage = new Page<>(page, rows);

        //提供分页处理能力查询
        //参数1 IPage接口类型参数，代表分页条件，第几页，查询多少条
        IPage<TbItem> result = itemMapper.selectPage(iPage, new QueryWrapper<TbItem>());

        //返回结果IPage类型对象中，包含当前页面中的数据集合，总计数据行数，总计页码数
        BaizhanResult result1 = new BaizhanResult();
        result1.setStatus(200);
        result1.setMsg("OK");

        Map<String, Object> data = new HashMap<>();
        data.put("total", result.getTotal());
        data.put("result", result.getRecords());
        result1.setData(data);

        return result1;
    }

    /**
     * 新增商品
     * 处理数据的完整性，如各对象主键，时间等
     * 保证所有数据，同时新增成功或者同时新增失败
     * <p>
     * 数据同步：
     * 创建Item4Elasticsearch对象，并保存到Elasticsearch中
     * 新增，上架和修改逻辑相同。Elasticsearch中，保存数据的时候，同ID覆盖
     *
     * @param item
     * @param itemParamItem
     * @param itemDesc
     * @return
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public BaizhanResult createItem(TbItem item, TbItemParamItem itemParamItem, TbItemDesc itemDesc) {

        Date now = new Date();
        Long itemId = IDUtils.genItemId();

        //处理数据完整性
        //商品数据完整
        item.setId(itemId);
        item.setStatus(1);  //1 正常  2 下架    3 删除
        item.setCreated(now);
        item.setUpdated(now);

        //商品规格数据完整
        itemParamItem.setId(IDUtils.genItemId());   //商品规格主键
        itemParamItem.setItemId(itemId);            //商品主键
        itemParamItem.setCreated(now);
        itemParamItem.setUpdated(now);

        //商品详情描述完整
        itemDesc.setItemId(itemId);
        itemDesc.setCreated(now);
        itemDesc.setUpdated(now);

        //新增商品
        int rows = itemMapper.insert(item);
        if (rows != 1) {
            //新增商品失败
            throw new RuntimeException("新增商品错误");
        }

        rows = itemDescMapper.insert(itemDesc);
        if (rows != 1) {
            //新增商品描述失败
            throw new RuntimeException("新增商品描述错误");
        }

        rows = itemParamItemMapper.insert(itemParamItem);
        if (rows != 1) {
            //新增商品规格失败
            throw new RuntimeException("新增商品规格错误");
        }

        //保证新增商品结束后，同步数据到Elasticsearch中。
        saveToElasticsearch(item.getId());
        // 同步Redis数据
        saveToRedis(item.getId());

        return BaizhanResult.ok();
    }

    private void saveToRedis(Long itemId) {
        //商品
        TbItem item = itemMapper.selectById(itemId);
        TbItemDesc itemDesc = itemDescMapper.selectById(itemId);
        QueryWrapper<TbItemParamItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id", itemId);
        TbItemParamItem itemParamItem = itemParamItemMapper.selectOne(queryWrapper);

        //同步缓存
        try {
            redisDao.set(itemPrefix + itemId, item, 3L * 24 * 60 * 60 + new Random().nextInt(1200), TimeUnit.SECONDS);
            redisDao.set(itemDescPrefix + itemId, itemDesc, 3L * 24 * 60 * 60 + new Random().nextInt(1200), TimeUnit.SECONDS);
            redisDao.set(itemParamItemPrefix + itemId, itemParamItem, 3L * 24 * 60 * 60 + new Random().nextInt(1200), TimeUnit.SECONDS);
        } catch (Exception e) {
            //发生缓存同步异常，删除缓存
            redisDao.delete(itemPrefix + itemId);
            redisDao.delete(itemDescPrefix + itemId);
            redisDao.delete(itemParamItemPrefix + itemId);
        }


    }

    /**
     * 保存商品数据到Elasticsearch
     * 1. 查询商品，描述，类型，创建Item4Elasticsearch对象
     * 2. 保存到Elasticsearch中
     *
     * @param itemId
     */
    private void saveToElasticsearch(Long itemId) {

        TbItem item = itemMapper.selectById(itemId);
        TbItemCat itemCat = itemCatMapper.selectById(item.getCid());
        TbItemDesc itemDesc = itemDescMapper.selectById(item.getId());
        Item4Elasticsearch item4Elasticsearch = new Item4Elasticsearch();

        //复制属性
        BeanUtils.copyProperties(item, item4Elasticsearch);
        item4Elasticsearch.setId(item.getId().toString());      //设置主键
        item4Elasticsearch.setCategoryName(itemCat.getName());  //设置商品类型名称
        item4Elasticsearch.setItemDesc(itemDesc.getItemDesc()); //设置商品描述

        itemDao4Elasticsearch.save(item4Elasticsearch);
    }

    /**
     * 标记删除，就是修改状态
     * 服务代码是对应具体业务逻辑的，是对应前端应用的每个请求的，不要因为内容一致，就整合方法
     * <p>
     * 后台管理写操作：需要保证数据库中的数据和Elasticsearch中的数据一致
     * 上架的商品，新增的商品，修改的商品，要同步到Elasticsearch中
     * 下架的商品，删除的商品要从Elasticsearch中删除
     * <p>
     * 实现方案：
     * 1. 异步处理：通过RabbitMQ实现，优势-当前的后台系统反应较快，劣势-数据延迟高，极端情况下可能Elasticsearch同步出错，需要处理
     * 1.1 保证数据库写成功
     * 1.2 发送同步数据的消息到RabbitMQ中
     * 1.3 当前方法执行结束，返回
     * 1.4 MQ的消息消费者（Consumer）开始消费消息，同步数据到Elasticsearch中
     * 2. 同步处理：当前系统中，如果数据库写成功，立刻访问Elasticsearch实现数据同步，优势-本地事务环境，保证数据的一致，劣势-响应慢
     * 2.1 保证数据库写成功
     * 2.2 整理要同步的数据
     * 2.3 使用任何方法，直接实现Elasticsearch的数据同步  （Spring Data Elasticsearch | Transport | rest high-level）
     * <p>
     * 思考：
     * 有没有高并发访问冲突的可能？导致数据不一致
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class}, isolation = Isolation.DEFAULT)
    public BaizhanResult removeItemById(Long id) {

        TbItem item = new TbItem();
        item.setId(id);
        item.setStatus(3);
        item.setUpdated(new Date());

        int rows = itemMapper.updateById(item);
        if (rows != 1) {
            throw new RuntimeException("删除商品失败");
        }

        //写操作成功，同步数据
        removeFromElasticsearch(id);
        //淘汰缓存
        redisDao.delete(itemPrefix + id);
        redisDao.delete(itemDescPrefix + id);
        redisDao.delete(itemParamItemPrefix + id);
        return BaizhanResult.ok();
    }

    /**
     * 根据商品主键，删除Elasticsearch中的数据
     *
     * @param itemId
     */
    private void removeFromElasticsearch(Long itemId) {

        itemDao4Elasticsearch.removeById(itemId);

    }

    /**
     * 上架
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public BaizhanResult onShelfById(Long id) {

        TbItem item = new TbItem();
        item.setId(id);
        item.setStatus(1);
        item.setUpdated(new Date());

        int rows = itemMapper.updateById(item);
        if (rows != 1) {
            throw new RuntimeException("商品上架失败");
        }

        //商品上架成功后，同步数据
        saveToElasticsearch(id);
        //商品上架成功后，同步Redis数据
        saveToRedis(id);

        return BaizhanResult.ok();
    }

    /**
     * 下架
     *
     * @param id
     * @return
     */
    @Override
    public BaizhanResult offShelfById(Long id) {

        TbItem item = new TbItem();
        item.setId(id);
        item.setStatus(2);
        item.setUpdated(new Date());

        int rows = itemMapper.updateById(item);
        if (rows != 1) {
            throw new RuntimeException("商品下架失败");
        }

        //同步Elasticsearch中数据
        removeFromElasticsearch(id);
        //淘汰缓存
        redisDao.delete(itemPrefix + id);
        redisDao.delete(itemDescPrefix + id);
        redisDao.delete(itemParamItemPrefix + id);

        return BaizhanResult.ok();
    }

    /**
     * 根据商品主键，查询数据
     * 查询商品数据，商品类型数据，商品详情描述，商品规格数据
     *
     * @param id
     * @return
     */
    @Override
    public BaizhanResult preUpdateItem(Long id) {
        //主键查询商品
        TbItem item = itemMapper.selectById(id);
        if (item == null) {
            //商品不存在
            return BaizhanResult.error("要修改的商品不存在");
        }

        //主键查询商品类型
        TbItemCat itemCat = itemCatMapper.selectById(item.getCid());
        if (itemCat == null) {
            return BaizhanResult.error("要修改的商品对应类型不存在");
        }

        //主键查询商品详情
        TbItemDesc itemDesc = itemDescMapper.selectById(id);
        if (itemDesc == null) {
            return BaizhanResult.error("要修改的商品描述不存在");
        }

        //查询商品规格
        QueryWrapper<TbItemParamItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id", id);  //根据商品主键，查询商品规格
        TbItemParamItem itemParamItem = itemParamItemMapper.selectOne(queryWrapper);

        //商品规格可以为null
        //创建返回结果
        Map<String, Object> data = new HashMap<>();
        data.put("item", item);
        data.put("itemCat", itemCat);
        data.put("itemDesc", itemDesc);
        data.put("itemParamItem", itemParamItem);

        return BaizhanResult.ok(data);
    }

    /**
     * 修改商品
     * 如果更新商品的时候，有并发操作，是否会造成数据不一致
     * 使用乐观锁，保证更新数据一定是想要更新的数据
     * 有一定的限制，只能本地操作，对数据库有要求，数据库不能是分布式数据库
     *
     * @param item
     * @param itemDesc
     * @param paramData
     * @return
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public BaizhanResult modifyItem(TbItem item, String itemDesc, String paramData) {
        //查询商品，商品描述，商品规格
        TbItem oldItem = itemMapper.selectById(item.getId());
        TbItemDesc oldItemDesc = itemDescMapper.selectById(item.getId());
        QueryWrapper<TbItemParamItem> oldQuery = new QueryWrapper<>();
        oldQuery.eq("item_id", item.getId());
        TbItemParamItem oldItemParamItem = itemParamItemMapper.selectOne(oldQuery);

        //维护数据完整
        Date now = new Date();
        item.setUpdated(now);

        TbItemDesc tbItemDesc = new TbItemDesc();
        tbItemDesc.setItemId(item.getId());     //主键
        tbItemDesc.setItemDesc(itemDesc);       //商品描述
        tbItemDesc.setUpdated(now);             //更新时间

        TbItemParamItem itemParamItem = new TbItemParamItem();
        itemParamItem.setParamData(paramData);  //规格字符串
        itemParamItem.setUpdated(now);          //更新时间

        //主键更新商品
        QueryWrapper<TbItem> itemQueryWrapper = new QueryWrapper<>();
        itemQueryWrapper.eq("id", item.getId());
        itemQueryWrapper.eq("updated", oldItem.getUpdated());    //要更新的数据更新时间，是希望更新的时间
        int rows = itemMapper.update(item, itemQueryWrapper);
        if (rows != 1) {
            throw new RuntimeException("更新商品错误");
        }

        //主键更新商品描述
        QueryWrapper<TbItemDesc> descQuery = new QueryWrapper<>();
        descQuery.eq("item_id", item.getId());
        descQuery.eq("updated", oldItemDesc.getUpdated());
        rows = itemDescMapper.update(tbItemDesc, descQuery);
        if (rows != 1) {
            throw new RuntimeException("更新商品描述错误");
        }

        //条件更新商品规格
        QueryWrapper<TbItemParamItem> itemParamItemQuery = new QueryWrapper<>();
        itemParamItemQuery.eq("item_id", item.getId());
        itemParamItemQuery.eq("updated", oldItemParamItem.getUpdated());
        rows = itemParamItemMapper.update(itemParamItem, itemParamItemQuery);
        if (rows != 1) {
            throw new RuntimeException("更新商品规格错误");
        }

        //修改商品成功后，同步Elasticsearch数据
        saveToElasticsearch(item.getId());
        //修改商品成功后，同步Redis数据，更新为最新的商品相关数据，保证数据同步不影响代码逻辑
        try {
            redisDao.set(itemPrefix + item.getId(), item, 3L * 24 * 60 * 60 + new Random().nextInt(1200), TimeUnit.SECONDS);
            redisDao.set(itemDescPrefix + item.getId(), item, 3L * 24 * 60 * 60 + new Random().nextInt(1200), TimeUnit.SECONDS);
            redisDao.set(itemParamItemPrefix + item.getId(), item, 3L * 24 * 60 * 60 + new Random().nextInt(1200), TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
            //发生异常，数据回滚，Redis中淘汰数据
            redisDao.delete(itemPrefix + item.getId());
            redisDao.delete(itemDescPrefix + item.getId());
            redisDao.delete(itemParamItemPrefix + item.getId());
            throw e;    //把异常抛出，让数据库回滚，不抛出异常也可以，数据库写成功，缓存写失败，已淘汰，等查询的时候，重置缓存数据
        }

        return BaizhanResult.ok();
    }

    /**
     * 为初始化Elasticsearch数据，提供查询服务
     * 1. 查询所有的商品
     * 2. 查询所有的商品类型
     * 3. 查询所有的商品描述
     *
     * @return
     */
    @Override
    public BaizhanResult getAllItems() {
        //全部商品
        List<TbItem> allItems = itemMapper.selectList(new QueryWrapper<>());
        //全部商品类型
        List<TbItemCat> allItemCats = itemCatMapper.selectList(new QueryWrapper<>());
        //全部商品描述
        List<TbItemDesc> allItemDescs = itemDescMapper.selectList(new QueryWrapper<>());

        //维护查询结果，封装成Map集合
        Map<String, Object> data = new HashMap<>();
        data.put("item", allItems);
        data.put("itemCat", allItemCats);
        data.put("itemDesc", allItemDescs);

        //返回
        return BaizhanResult.ok(data);
    }

    /**
     * 修改商品库存
     *
     * @param id
     * @param num
     * @return
     */
    @Override
    @LcnTransaction
    @Transactional(rollbackFor = {RuntimeException.class})
    public BaizhanResult updateItemNum4Trade(Long id, Integer num) {
        //查询商品
        TbItem item = itemMapper.selectById(id);
        if (item.getNum() < num) {
            throw new RuntimeException("商品库存不足");
        }
        item.setNum(item.getNum() - num);
        int rows = itemMapper.updateById(item);
        if (rows != 1) {
            throw new RuntimeException("修改商品库存错误");
        }

        return BaizhanResult.ok();
    }

    /**
     * 修改Redis中的商品库存
     *
     * @param id
     * @param num
     */
    @Override
    @TccTransaction
    public void updateItemNum4Redis(Long id, Integer num) {
        //减少Redis中对应的商品库存
        TbItem item = redisDao.get(itemPrefix + id);
        item.setNum(item.getNum() - num);

        redisDao.set(itemPrefix + id, item, 3L * 24 * 60 * 60 + new Random().nextInt(1200), TimeUnit.SECONDS);

    }

    // 确认逻辑
    public void confirmUpdateItemNum4Redis(Long id, Integer num) {

    }

    // 取消逻辑
    public void cancelUpdateItemNum4Redis(Long id, Integer num) {
        // 增加Redis中对应商品的库存
        TbItem item = redisDao.get(itemPrefix + id);
        item.setNum(item.getNum() + num);

        redisDao.set(itemPrefix + item.getId(), item, 3L * 24 * 60 * 60 + new Random().nextInt(1200), TimeUnit.SECONDS);

    }

}
