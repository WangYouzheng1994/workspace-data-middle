package com.yqwl.datamiddle.realtime.util;

/**
 * @Description: 配置文件
 * @Author: WangYouzheng
 * @Date: 2021/12/28 11:21
 * @Version: V1.0
 */
public class GmallConfig {
    /**
     * HBase 命名空间
     */
    public static final String HBASE_SCHEMA = "my_test";

    /**
     * phoenix 需要连接的 zookeeper的地址
     */
    public static final String PHOENIX_SERVER = "jdbc:phoenix:hadoop95,hadoop96,hadoop97:2181";
    // public static final String PHOENIX_SERVER = "jdbc:phoenix:hadoop100:2181";
}
