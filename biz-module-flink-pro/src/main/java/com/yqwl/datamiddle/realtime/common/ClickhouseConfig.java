package com.yqwl.datamiddle.realtime.common;

/**
 * Desc: 项目常用配置
 */
public class ClickhouseConfig {

    // ClickHouse_Url
    public static final String CLICKHOUSE_URL = "jdbc:clickhouse://192.168.3.95:8123/default";
    //public static final String CLICKHOUSE_URL = "jdbc:clickhouse://10.123.175.195:8123/default";

    // ClickHouse_Driver
    public static final String CLICKHOUSE_DRIVER = "ru.yandex.clickhouse.ClickHouseDriver";

    //一汽正式生产有用户名和密码
    public static final String CLICKHOUSE_USERNAME = "sjztcla";
    public static final String CLICKHOUSE_PASSWORD = "GtDIg7HifD9MiJNN";
}
