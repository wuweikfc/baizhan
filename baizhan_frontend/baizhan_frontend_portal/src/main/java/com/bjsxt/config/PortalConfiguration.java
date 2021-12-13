package com.bjsxt.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;

@Configuration
public class PortalConfiguration extends RedisConfig {

    @Override
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

        return super.redisTemplate(factory);
    }

    /**
     * 可以改变spring cache的管理逻辑
     *
     * @param connectionFactory spring-data-redis自动构建
     * @return
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        //创建默认缓存配置
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig();
        configuration = configuration.entryTtl(Duration.ofDays(1))    //有效期1天
                .disableCachingNullValues();        //null数据不缓存

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(connectionFactory)   //设置链接工厂
                .cacheDefaults(configuration)               //设置默认配置
                .build();                                   //构建缓存管理对象
    }

}
