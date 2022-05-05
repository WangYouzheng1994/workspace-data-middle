package com.yqwl.datamiddle.realtime.util;

import cn.hutool.setting.dialect.Props;

import java.io.File;

public class PropertiesUtil {

    private static final String CONFIG = "config";

    public static Props getProps(String filename) {
        return new Props(CONFIG + File.separator + filename);
    }


}
