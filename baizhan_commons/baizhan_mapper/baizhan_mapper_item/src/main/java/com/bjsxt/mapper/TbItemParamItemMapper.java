package com.bjsxt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bjsxt.pojo.TbItemParamItem;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 商品规格和商品关系表 mapper 接口
 */
@Mapper
@Component
public interface TbItemParamItemMapper extends BaseMapper<TbItemParamItem> {

}
