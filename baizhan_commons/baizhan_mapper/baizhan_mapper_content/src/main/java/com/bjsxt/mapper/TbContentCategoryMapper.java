package com.bjsxt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bjsxt.pojo.TbContentCategory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 内容分类 mapper接口
 */
@Mapper
@Component
public interface TbContentCategoryMapper extends BaseMapper<TbContentCategory> {

}
