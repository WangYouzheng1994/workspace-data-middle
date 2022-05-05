package com.yqwl.datamiddle.realtime.common;

/**
 * @Description: 配置文件
 * @Author: WangYouzheng
 * @Date: 2021/12/28 11:21
 * @Version: V1.0
 */
public class PhoenixConfig {
    /**
     * HBase 命名空间
     */
    public static final String HBASE_SCHEMA = "my_test";

    /**
     * phoenix 需要连接的 zookeeper的地址
     */
    public static final String PHOENIX_SERVER = "jdbc:phoenix:hadoop95,hadoop96,hadoop97:2181";

    /**
     * phoenix 驱动
     */
    public static final String PHOENIX_DRIVER = "org.apache.phoenix.jdbc.PhoenixDriver";

    /**
     * phoenix 是否开启创建命名空间
     */
    public static final String NAMESPACE_MAPPING_ENABLED = "phoenix.schema.isNamespaceMappingEnabled";

}
