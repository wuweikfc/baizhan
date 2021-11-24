package com.bjsxt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.mapper.TbItemCatMapper;
import com.bjsxt.pojo.TbItemCat;
import com.bjsxt.service.ItemCategoryService;
import com.bjsxt.vo.ItemCat4BackendItem;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ItemCategoryServiceImpl implements ItemCategoryService {

    @Autowired
    private TbItemCatMapper itemCatMapper;

    /**
     * 根据父节点主键，查询子节点集合
     *
     * @param id
     * @return
     */
    @Override
    public BaizhanResult getItemCatsByParent(Long id) {

        try {
            //创建查询条件
            QueryWrapper<TbItemCat> queryWrapper = new QueryWrapper<>();
            //维护查询条件
            queryWrapper.eq("parent_id", id);
            List<TbItemCat> list = itemCatMapper.selectList(queryWrapper);

            if (list == null || list.size() == 0) {
                //无数据
                return BaizhanResult.error("没有子节点数据");
            }
            //把查询的TbItemCat类型对象，转换成ItemCat4BackendItem类型的对象
            List<ItemCat4BackendItem> resultList = new ArrayList<>();
            list.forEach(item -> {
                ItemCat4BackendItem itemCat4BackendItem = new ItemCat4BackendItem();
                //把item对象中的属性数据，复制到itemCat4BackendItem
                //是根据Property赋值属性的，也就是get/set方法赋值。从源中调用get方法，给目标的set方法赋值。
                BeanUtils.copyProperties(item, itemCat4BackendItem);
                resultList.add(itemCat4BackendItem);
            });
            return BaizhanResult.ok(resultList);
        } catch (Exception e) {
            e.printStackTrace();
            return BaizhanResult.error("服务器繁忙，请稍后重试");
        }
    }

}
