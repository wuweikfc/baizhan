package com.bjsxt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bjsxt.pojo.TbItemDesc;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 商品描述 mapper接口
 */
@Mapper
@Component
public interface TbItemDescMapper extends BaseMapper<TbItemDesc> {

}
