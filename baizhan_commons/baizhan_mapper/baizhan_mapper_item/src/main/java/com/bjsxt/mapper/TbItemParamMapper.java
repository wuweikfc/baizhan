package com.bjsxt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bjsxt.pojo.TbItemParam;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 商品规格参数 mapper 接口
 */
@Mapper
@Component
public interface TbItemParamMapper extends BaseMapper<TbItemParam> {

}
