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
    public static final String HBASE_SCHEMA = "GMALL2021_REALTIME";

    /**
     * phoenix 需要连接的 zookeeper的地址
     */
    public static final String PHOENIX_SERVER = "jdbc:phoenix:hadoop100,hadoop101,hadoop102:2181";
    // public static final String PHOENIX_SERVER = "jdbc:phoenix:hadoop100:2181";
}
