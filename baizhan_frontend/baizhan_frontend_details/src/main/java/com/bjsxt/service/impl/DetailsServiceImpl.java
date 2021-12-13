package com.bjsxt.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.dao.redis.RedisDao;
import com.bjsxt.mapper.TbItemDescMapper;
import com.bjsxt.mapper.TbItemMapper;
import com.bjsxt.mapper.TbItemParamItemMapper;
import com.bjsxt.pojo.TbItem;
import com.bjsxt.pojo.TbItemDesc;
import com.bjsxt.pojo.TbItemParamItem;
import com.bjsxt.service.DetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class DetailsServiceImpl implements DetailsService {

    @Autowired
    private TbItemMapper itemMapper;

    @Autowired
    private TbItemDescMapper itemDescMapper;

    @Autowired
    private TbItemParamItemMapper itemParamItemMapper;

    @Autowired
    private RedisDao redisDao;  //注入Redis数据访问接口

    @Value("${baizhan.details.prefix.item}")
    private String itemPrefix;  //商品缓存key前缀， key=前缀+商品主键

    @Value("${baizhan.details.prefix.itemDesc}")
    private String itemDescPrefix;  //商品描述缓存key前缀，key=前缀+商品主键

    @Value("${baizhan.details.prefix.itemParamItem}")
    private String itemParamItemPrefix; //商品规格缓存key前缀，key=前缀+商品主键

    private final Random random = new Random(); //随机生成器

    @Value("${baizhan.details.lock.item}")
    private String itemLockPrefix;  //商品分布式锁key前缀，key = 前缀+商品主键

    @Value("${baizhan.details.lock.itemDesc}")
    private String itemDescLockPrefix;  //商品描述分布式锁key前缀 key=前缀+商品主键

    @Value("${baizhan.details.lock.itemParamItem}")
    private String itemParamItemLockPrefix; //商品规格分布式锁key前缀 key=前缀+商品主键

    private ThreadLocal<Integer> times = new ThreadLocal<>();


    /**
     * 主键查询商品基本信息
     * 执行流程：必须保证Redis访问无论是否发生异常，当前逻辑正常运行
     * 1. 访问redis， 查询缓存，有缓存直接返回，没有缓存进入流程2
     * 2. 访问数据库，查询商品数据
     * 3. 缓存查询结果到redis
     * 4. 返回最终结果
     * <p>
     * 缓存雪崩问题：不能让所有的商品相关缓存，有效时间集中，使用3天 + - 随机时长
     * 解决方案：
     * 1. 提供定时机制，在有效期过期之前，刷新缓存数据，如：有效期3天，每天执行一次定时任务，刷新有效期为3天
     * 优势：保证数据始终有缓存，缺点：占用缓存空间，把不必要的缓存数据保存在缓存中
     * 2. 随机散列有效时长，采用定长 + - 变量的方式，实现数据缓存时间配置
     * <p>
     * 缓存穿透问题：如果有高并发请求，都查询同一个商品，且redis中没有对应缓存，所有的请求都会执行数据库访问
     * 数据库压力提升，效率降低，如何解决？
     * 解决方案：
     * 1. 使用分布式锁+自旋解决缓存穿透问题
     * 锁：分布式锁，不能使用本地锁（Synchronized|Lock）；因为本地锁，只能保证一个服务节点下，单线程处理
     * 分布式锁，相同的思想，不同的解决方案。在一个共享的位置，记录一个标记，当作是锁。获取锁，可以访问；没有锁，自旋等待
     * 如：在redis中，使用setnx保存一个键值对，保存成功有锁，失败无锁
     * <p>
     * 缓存击穿（缓存热点）问题：如果有高并发请求，查询不同|相同的商品，且每个商品都不存在，如何处理？
     * 当查询的数据是不存在的数据，且有高并发处理，那么redis缓存中永远没有缓存数据，所有请求进入数据库执行查询逻辑
     * 这种情况叫缓存击穿。可能发生在恶意攻击和用户F5刷新的情况下
     * 解决方案：
     * 1. 即使数据不存在，也要缓存，减少缓存有效时长，让系统更加健壮
     *
     * @param id
     * @return
     */
    @Override
    public BaizhanResult getItemById(Long id) {
        //维护缓存key
        String key = itemPrefix + id;
        //查询缓存，保证缓存访问无论成功与否，不影响当前代码的正常执行
        try {
            TbItem cacheItem = redisDao.get(key);
            if (cacheItem != null) {
                //删除自旋标记
                times.remove();
                return BaizhanResult.ok(cacheItem);
            }
        } catch (Exception e) {
            //异常处理，如：记录异常日志
            e.printStackTrace();
        }

        //查询数据库
        //使用分布式锁，实现单线程访问数据库
        TbItem item = null;
        //分布式锁标记
        String lockKey = itemLockPrefix + id;

        try {
            //获取分布式锁，设定锁的有效时长为1秒，1秒后自动删除，避免可能发生的线程错误，导致死锁问题
            boolean isLocked = redisDao.setNx(lockKey, "lock", 1L, TimeUnit.SECONDS);
            if (!isLocked) {
                //查询自旋次数
                Integer t = times.get();
                if (t >= 10) {
                    //自旋10次，停止自旋，返回托底数据，相当于服务降级
                    //删除自旋次数标记
                    times.remove();
                    return BaizhanResult.error("服务器繁忙，请稍后重试");
                }

                if (t == null) {
                    //没有自旋过
                    t = 0;
                } else {
                    //自旋过
                    t = t + 1;
                }
                times.set(t);
                //没有锁，等待，自旋，思考，自旋多少次？设定一个线程自旋次数阈值是10，10次以内可以自旋等待，10次以上，返回假数据
                Thread.sleep(100);
                //调用当前方法，重新访问redis缓存
                return getItemById(id);
            }
            //获取到锁标记
            item = itemMapper.selectById(id);
            //要保证商品状态是1，上架状态
            if (item.getStatus() != 1) {
                //下架或删除状态，查询无结果
                item = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //保存查询结果到redis缓存,保证缓存访问成功与否，不影响正常代码流程
        try {
            int rand = random.nextInt(20 * 60);
            if (random.nextInt(10000) % 2 == 0) {
                //随机2的整数倍，rand整数取反
                rand = rand * -1;
            }
            if (item != null) {
                redisDao.set(key, item, 3L * 24 * 60 * 60 + rand, TimeUnit.SECONDS);
            } else {
                //查询的商品不存在，减少有效时长，设置为3分钟，不考虑缓存雪崩问题
                item = new TbItem();
                //和前端工程师协商决定，只要商品主键是负数，代表错误数据，重定向到门户页面
                item.setId(-1L);
                redisDao.set(key, item, 3L, TimeUnit.MINUTES);
            }
            //保存缓存成功后，删除分布式锁
            redisDao.delete(lockKey);
        } catch (Exception e) {
            e.printStackTrace();

        }

        //返回最终结果
        return BaizhanResult.ok(item);
    }

    /**
     * 根据商品主键，查询商品描述
     *
     * @param itemId
     * @return
     */
    @Override
    public BaizhanResult getItemDescByItemId(Long itemId) {

        String key = itemDescPrefix + itemId;
        try {
            TbItemDesc cacheItemDesc = redisDao.get(key);
            if (cacheItemDesc != null) {
                return BaizhanResult.ok(cacheItemDesc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        TbItemDesc itemDesc = itemDescMapper.selectById(itemId);

        try {
            redisDao.set(key, itemDesc, 3L, TimeUnit.DAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return BaizhanResult.ok(itemDesc);
    }

    /**
     * 根据商品主键，查询商品规格
     *
     * @param itemId
     * @return
     */
    @Override
    public BaizhanResult getItemParamItemByItemId(Long itemId) {

        String key = itemParamItemPrefix + itemId;
        try {
            TbItemParamItem cacheItemParamItem = redisDao.get(key);
            if (cacheItemParamItem != null) {
                return BaizhanResult.ok(cacheItemParamItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        QueryWrapper<TbItemParamItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id", itemId);
        TbItemParamItem itemParamItem = itemParamItemMapper.selectOne(queryWrapper);

        try {
            redisDao.set(key, itemParamItem, 3L, TimeUnit.DAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return BaizhanResult.ok(itemParamItem);
    }

}
