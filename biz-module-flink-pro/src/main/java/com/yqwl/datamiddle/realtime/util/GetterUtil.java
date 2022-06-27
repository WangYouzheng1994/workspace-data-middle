package com.yqwl.datamiddle.realtime.util;

import cn.hutool.core.lang.Validator;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/6/23 16:16
 * @Version: V1.0
 */
public class GetterUtil {

    /**
     * 取得String
     *
     * @param obj
     * @return
     */
    public static String getString(Object obj) {
        if (Validator.isNull(obj)) {
            return "";
        }
        return String.valueOf(obj);
    }
}