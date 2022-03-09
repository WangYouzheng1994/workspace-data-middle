package com.yqwl.datamiddle.realtime.util;

import org.apache.commons.lang3.StringUtils;

public class StrUtil {

    public static String[] getStrList(String str, String separator) {
        if (StringUtils.isEmpty(str)) return new String[]{};
        return str.split(separator);
    }

}
