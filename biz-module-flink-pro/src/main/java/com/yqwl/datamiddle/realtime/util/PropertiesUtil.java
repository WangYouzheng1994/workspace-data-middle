package com.yqwl.datamiddle.realtime.util;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.setting.dialect.Props;
import com.yqwl.datamiddle.realtime.common.ClickhouseConfig;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;

import java.io.InputStream;
import java.util.Properties;

/**
 * 解析properties配置文件
 */
public class PropertiesUtil {

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
            e.printStackTrace();
        }
    }

    //开启哪个模式
    //public static final String ACTIVE_TYPE = "prod";

    //开发模式
    private static final String ACTIVE_DEV = "dev";
    //生产模式
    private static final String ACTIVE_PROD = "prod";

    private static final String FILENAME_DEV = "cdc-dev.properties";
    private static final String FILENAME_PROD = "cdc-prod.properties";

    public static Props getProps(String active) {
        if (ACTIVE_DEV.equals(active)) {
            return new Props(FILENAME_DEV);
        }
        if (ACTIVE_PROD.equals(active)) {
            return new Props(FILENAME_PROD);
        }
        return null;
    }

    /**
     * 获取当前环境版本下的配置文件。
     *
     * @return
     */
    public static Props getProps() {
        return new Props("cdc-"+ACTIVE_TYPE+".properties", CharsetUtil.CHARSET_UTF_8);
    }

    /**
     * 读取配置文件，获取Clickhouse当前连接信息
     *
     * @return
     */
    public static JdbcConnectionOptions getClickhouseJDBCConnection() {
        return new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                .withDriverName(GetterUtil.getString(getProps().get("clickhouse.driver")))
                .withUrl(GetterUtil.getString(getProps().get("clickhouse.url")))
                .withUsername(GetterUtil.getString(getProps().get("clickhouse.username")))
                .withPassword(GetterUtil.getString(getProps().get("clickhouse.password")))
                .build();
    }
}
