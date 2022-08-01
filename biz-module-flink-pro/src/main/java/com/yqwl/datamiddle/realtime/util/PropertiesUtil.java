package com.yqwl.datamiddle.realtime.util;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.setting.dialect.Props;
import com.yqwl.datamiddle.realtime.common.ClickhouseConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;

import java.io.InputStream;
import java.util.Properties;

/**
 * 解析properties配置文件
 * 默认走的是dev环境。即：resources/cdc-dev.properties
 */
@Slf4j
public class PropertiesUtil {
    /**
     * 当前打包环境 跟着maven的profile走的
     */
    public static String ACTIVE_TYPE = "dev";

    static {
        try (InputStream resourceAsStream =
                     PropertiesUtil.class.getResourceAsStream("/application.properties")) {
            Properties pro = new Properties();
            pro.load(resourceAsStream);

            Object o = pro.get("spring.profiles.active");
            if (o != null) {
                ACTIVE_TYPE = o.toString();
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 获取当前环境版本下的配置文件。
     *
     * @return cn.hutool.setting.dialect.Props
     */
    public static Props getProps() {
        return new Props("cdc-"+ACTIVE_TYPE+".properties", CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 获取字符串类型Value值
     *
     * @return java.lang.String
     */
    public static String getPropsStr(String key) {
        return GetterUtil.getString(getProps().getStr(key));
    }

    /**
     * 获取 int类型Value值
     *
     * @return
     */
    public static int getPropsInt(String key) {
        return GetterUtil.getInt(getProps().getInt(key));

    }

    /**
     * 获取当前环境版本下的 checkPoint
     *
     * @param checkpointSubDir 在公共目录下面的二级目录
     * @return java.lang.String
     */
    public static String getCheckpointStr(String checkpointSubDir) {
        return PropertiesUtil.getProps().getStr("checkpoint.hdfs.url") + checkpointSubDir;
    }

    /**
     * 获取当前环境版本下的 checkPoint
     *
     * @param savePointSubDir 在公共目录下面的二级目录
     * @return java.lang.String
     */
    public static String getSavePointStr(String savePointSubDir) {
        return PropertiesUtil.getProps().getStr("savepoint.hdfs.url") + savePointSubDir;
    }

    /**
     * 读取配置文件，获取Clickhouse当前连接信息
     *
     * @return org.apache.flink.connector.jdbc.JdbcConnectionOptions
     */
    public static JdbcConnectionOptions getClickhouseJDBCConnection() {
        return new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                .withDriverName(GetterUtil.getString(getProps().get("clickhouse.driver")))
                .withUrl(GetterUtil.getString(getProps().get("clickhouse.url")))
                .withUsername(GetterUtil.getString(getProps().get("clickhouse.username")))
                .withPassword(GetterUtil.getString(getProps().get("clickhouse.password")))
                .build();
    }

    /**
     * 读取配置文件，获取MySQL Master当前连接信息
     *
     * @return org.apache.flink.connector.jdbc.JdbcConnectionOptions
     */
    public static JdbcConnectionOptions getMysqlJDBCConnection() {
        return new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                .withDriverName(getPropsStr("mysql.driver"))
                .withUrl(getPropsStr("mysql.url"))
                .withUsername(getPropsStr("mysql.username"))
                .withPassword(getPropsStr("mysql.password"))
                .build();
    }
}
