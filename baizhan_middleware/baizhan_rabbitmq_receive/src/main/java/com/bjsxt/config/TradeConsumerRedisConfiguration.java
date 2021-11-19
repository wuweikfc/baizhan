package com.bjsxt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class TradeConsumerRedisConfiguration extends RedisConfig {

    @Bean
    @Override
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        return super.redisTemplate(factory);
    }

}
