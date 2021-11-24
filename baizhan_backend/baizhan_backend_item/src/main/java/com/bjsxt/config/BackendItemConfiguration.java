package com.bjsxt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 后台商品系统配置类型
 */
@Configuration
public class BackendItemConfiguration extends RedisConfig{
    @Override
    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory factory){
        return super.redisTemplate(factory);
    }

}
