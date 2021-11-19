package com.bjsxt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bjsxt.pojo.TbItem;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 商品表 mapper 接口
 */
@Mapper
@Component
public interface TbItemMapper extends BaseMapper<TbItem> {

}
