package com.bjsxt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.mapper.TbItemParamMapper;
import com.bjsxt.pojo.TbItemParam;
import com.bjsxt.service.ItemParamService;
import com.bjsxt.utils.IDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 商品规格服务实现
 */
@Service
public class ItemParamServiceImpl implements ItemParamService {

    @Autowired
    private TbItemParamMapper itemParamMapper;

    /**
     * 查询数据库，访问tb_item_param表格，查询全部数据
     *
     * @return
     */
    @Override
    public BaizhanResult getAllItemParams() {

        try {
            //创建查询条件，创建一个查询条件空对象，代表查询所有数据
            QueryWrapper<TbItemParam> queryWrapper = new QueryWrapper<>();

            //selectList - 根据查询条件，查询多条数据，把查询结果封装成List并返回
            List<TbItemParam> list = itemParamMapper.selectList(queryWrapper);

            //返回结果处理
            if (list == null || list.size() == 0) {
                //没有数据
                return BaizhanResult.error("没有商品规格");
            }

            return BaizhanResult.ok(list);
        } catch (Exception e) {
            e.printStackTrace();
            return BaizhanResult.error("服务器繁忙，请稍后重试");
        }
    }

    /**
     * 根据商品分类主键，查询商品规格数据，如果商品规格数据存在，返回错误结果，status=400
     * 规格数据不存在，返回正确数据，status=200
     * 每个商品类型，只有唯一的商品规格数据，查询结果有，则是唯一数据，没有则是null
     *
     * @param itemCatId
     * @return
     */
    @Override
    public BaizhanResult isHaveItemParamByItemCat(Long itemCatId) {

        QueryWrapper<TbItemParam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_cat_id", itemCatId);

        TbItemParam itemParam = itemParamMapper.selectOne(queryWrapper);

        if (itemParam == null) {
            //商品分类没有规格，返回正确结果
            return BaizhanResult.ok();
        }
        return BaizhanResult.error("商品分类已存在规格，请重新选择");
    }

    /**
     * 新增商品规格数据
     * 注意：
     * 要保证新增数据的完整性
     * 主键，创建时间，更新时间等
     *
     * @param itemParam
     * @return
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public BaizhanResult createItemParam(TbItemParam itemParam) {
        //维护数据完整性
        itemParam.setId(IDUtils.genItemId());
        Date now = new Date();
        itemParam.setCreated(now);
        itemParam.setUpdated(now);

        //新增数据
        int rows = itemParamMapper.insert(itemParam);
        if (rows != 1) {
            //新增失败
            throw new RuntimeException("新增商品规格失败");
        }

        return BaizhanResult.ok();
    }

    /**
     * 通过主键，删除商品规格
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = {RuntimeException.class})
    public BaizhanResult removeItemParamById(Long id) {
        //通过主键删除数据
        int rows = itemParamMapper.deleteById(id);
        if (rows != 1) {
            //删除错误
            throw new RuntimeException("删除商品规格错误");
        }

        return BaizhanResult.ok();
    }

    /**
     * 根据商品类型主键，查询商品规格
     * 统一返回状态 status=200的结果，data属性值非空，有模板，data属性值=null，无模板
     *
     * @param itemCatId
     * @return
     */
    @Override
    public BaizhanResult getItemParamByItemCat(Long itemCatId) {

        QueryWrapper<TbItemParam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_cat_id", itemCatId);

        //条件查询
        TbItemParam itemParam = itemParamMapper.selectOne(queryWrapper);

        return BaizhanResult.ok(itemParam);
    }

}
