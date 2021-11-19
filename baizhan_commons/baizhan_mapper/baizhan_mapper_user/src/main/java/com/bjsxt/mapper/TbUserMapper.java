package com.bjsxt.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bjsxt.pojo.TbUser;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Component;

/**
 * 用户表 mapper 接口
 */
@Mapper
@Component
public interface TbUserMapper extends BaseMapper<TbUser> {

}
