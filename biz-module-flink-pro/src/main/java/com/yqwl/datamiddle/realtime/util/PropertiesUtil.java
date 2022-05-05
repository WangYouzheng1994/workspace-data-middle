package com.yqwl.datamiddle.realtime.util;

import cn.hutool.setting.dialect.Props;


public class PropertiesUtil {

    public static Props getProps(String filename) {
        return new Props(filename);
    }


}
