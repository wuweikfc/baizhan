package com.bjsxt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bjsxt.pojo.TbContent;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * mapper接口
 */
@Mapper
@Component
public interface TbContentMapper extends BaseMapper<TbContent> {

}
