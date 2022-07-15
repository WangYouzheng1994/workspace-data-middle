package com.yqwl.datamiddle.realtime.util;

import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: Felix
 * Date: 2021/2/5
 * Desc: 通过JedisPool连接池获取Jedis连接
 */
public class RedisUtil {
    private static volatile JedisPool jedisPool;

    public static Jedis getJedis() {
        if (jedisPool == null) {
            synchronized (RedisUtil.class) {
                if (jedisPool == null) {
                    JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
                    jedisPoolConfig.setMaxTotal(PropertiesUtil.getPropsInt("redis.MaxTotal")); //最大可用连接数
                    jedisPoolConfig.setBlockWhenExhausted(true); //连接耗尽是否等待
                    jedisPoolConfig.setMaxWaitMillis(PropertiesUtil.getPropsInt("redis.MaxWaitMillis")); //等待时间
                    jedisPoolConfig.setMaxIdle(PropertiesUtil.getPropsInt("redis.MaxIdle")); //最大闲置连接数
                    jedisPoolConfig.setMinIdle(PropertiesUtil.getPropsInt("redis.MinIdle")); //最小闲置连接数
                    jedisPoolConfig.setTestOnBorrow(false); //取连接的时候进行一下测试 ping pong
                    jedisPool = new JedisPool(
                            jedisPoolConfig,
                            PropertiesUtil.getPropsStr("redis.hostname"),
                            PropertiesUtil.getPropsInt("redis.port"),
                            PropertiesUtil.getPropsInt("redis.timeout"),
                            PropertiesUtil.getPropsStr("redis.password"));
                }
            }
        }
        return jedisPool.getResource();
    }


    //key 命名规则
    //层名 + ":" + 系统名 + ":" + 表名 + ":" + 原oracle表中主键值
    private static final String SEPARATE_CHAR = ":";
    private static final String DIM_LEVEL_NAME = "dim";
    private static final String VLMS_SYSTEM_NAME = "vlms";


    //获取redis key 名称
    public static String getRedisKey(String tableName, String pk) {
        List<String> list = new ArrayList<>();
        list.add(DIM_LEVEL_NAME);
        list.add(VLMS_SYSTEM_NAME);
        list.add(tableName);
        list.add(pk);
        return StringUtils.join(list, SEPARATE_CHAR);
    }

    //获取redis key 名称
    public static String getRedisKey(String levelName, String systemName, String tableName, String pk) {
        List<String> list = new ArrayList<>();
        list.add(levelName);
        list.add(systemName);
        list.add(tableName);
        list.add(pk);
        return StringUtils.join(list, SEPARATE_CHAR);
    }

    /**
     * 根据key让Redis中的缓存失效
     */
    public static void deleteKey(String tableName, String id) {
        String redisKey = getRedisKey(tableName, id);
        try {
            Jedis jedis = RedisUtil.getJedis();
            // 通过key清除缓存
            jedis.del(redisKey);
            jedis.close();
        } catch (Exception e) {
            throw new RuntimeException("删除redis key异常");
        }
    }


    public static void main(String[] args) {
        Jedis jedis = getJedis();
        System.out.println(jedis.ping());
    }
}
