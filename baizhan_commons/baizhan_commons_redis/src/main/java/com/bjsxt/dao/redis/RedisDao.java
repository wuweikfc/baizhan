package com.bjsxt.dao.redis;

import java.util.concurrent.TimeUnit;

/**
 * 公共Redis数据访问接口
 */
public interface RedisDao {

    //保存数据，不设置有效期
    void set(String key, Object value);

    //保存数据，同时设置有效期
    void set(String key, Object value, long times, TimeUnit unit);

    //查询数据
    <T> T get(String key);

    //删除键值对
    void delete(String key);

    //查询有效时长
    long ttl(String key);

    //当key不存在的时候保存数据，不设置有效期，保存成功返回true，失败返回false
    boolean setNx(String key, Object value);

    //当key不存在的时候保存数据，同时设置有效期，保存成功返回true，失败返回false
    boolean setNx(String key, Object value, long times, TimeUnit unit);

    //获取自增数字
    long incr(String key);

}
