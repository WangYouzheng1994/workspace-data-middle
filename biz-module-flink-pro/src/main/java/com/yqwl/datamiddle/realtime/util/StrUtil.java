package com.yqwl.datamiddle.realtime.util;

import com.yqwl.datamiddle.realtime.enums.TransientSink;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class StrUtil {

    public static String[] getStrList(String str, String separator) {
        if (StringUtils.isEmpty(str)) return new String[]{};
        return str.split(separator);
    }


    //组装sql values 部分 ?,?
    public static <T> String getValueSql(Class<T> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        List<Field> list = new ArrayList<>();
        for (Field field : fields) {
            String fieldName = field.getName();
            // 序列化id不处理
            if (StringUtils.equals(fieldName, "serialVersionUID")) {
                continue;
            }
            // 获取字段上的注解
            TransientSink annotation = field.getAnnotation(TransientSink.class);
            if (annotation != null) {
                continue;
            }
            list.add(field);
        }
        List<String> wildcard = new ArrayList<>();
        for (Field field : list) {
            wildcard.add("?");
        }
        return "(" + StringUtils.join(wildcard, ",") + ")";
    }


}
