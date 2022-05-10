package com.yqwl.datamiddle.realtime.util;

import cn.hutool.setting.dialect.Props;

/**
 * 解析properties配置文件
 */
public class PropertiesUtil {

    private static final String FILENAME = "cdc.properties";

    public static Props getProps() {
        return new Props(FILENAME);
    }

}
