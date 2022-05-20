package com.yqwl.datamiddle.realtime.util;

import cn.hutool.setting.dialect.Props;

/**
 * 解析properties配置文件
 */
public class PropertiesUtil {

    //开发模式
    public static final String ACTIVE_DEV = "dev";
    //生产模式
    public static final String ACTIVE_PROD = "prod";

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

}
