package com.bjsxt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bjsxt.pojo.TbItemCat;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 商品类目 mapper接口
 */
@Mapper
@Component
public interface TbItemCatMapper extends BaseMapper<TbItemCat> {


}
