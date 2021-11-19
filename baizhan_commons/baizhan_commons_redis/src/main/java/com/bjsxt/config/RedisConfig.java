package com.bjsxt.config;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * 定义基础配置类型
 */
//@Configuration
public abstract class RedisConfig {

//    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setKeySerializer(new StringRedisSerializer());

        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        redisTemplate.setConnectionFactory(factory);

        return redisTemplate;
    }

}
