package com.bjsxt.dao.redis.impl;

import com.bjsxt.dao.redis.RedisDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.TimeUnit;

@Repository
public class RedisDaoImpl implements RedisDao {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void set(String key, Object value) {

        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void set(String key, Object value, long times, TimeUnit unit) {

        redisTemplate.opsForValue().set(key, value, times, unit);
    }

    @Override
    public <T> T get(String key) {

        return (T) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String key) {

        redisTemplate.delete(key);
    }

    @Override
    public long ttl(String key) {

        return redisTemplate.getExpire(key);
    }

    @Override
    public boolean setNx(String key, Object value) {

        //在redisTemplate.opsForValue()中，setIfAbsent等同与redis中的命令setnx
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    @Override
    public boolean setNx(String key, Object value, long times, TimeUnit unit) {

        return redisTemplate.opsForValue().setIfAbsent(key, value, times, unit);
    }

    @Override
    public long incr(String key) {

        return redisTemplate.opsForValue().increment(key);
    }

}
