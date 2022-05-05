package com.yqwl.datamiddle.realtime.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

public class BeanUtil {

    private static final String STRING_TYPE = "java.lang.String";

    /**
     * 将某个对象中的属性值转换成Csv格式
     *
     * @param obj
     * @return
     */
    public static String transferFieldToCsv(Object obj) {
        Field[] fields = obj.getClass().getDeclaredFields();
        StringBuffer buffer = new StringBuffer();
        buffer.append("(");
        for (Field f : fields) {
            Object value = getFieldValue(obj, f.getName());
            String typeName = f.getGenericType().getTypeName();
            if (Objects.isNull(value)) {
                buffer.append("null");
                buffer.append(", ");
            } else {
                if (typeName.equals(STRING_TYPE)) {
                    buffer.append("'");
                    buffer.append(value);
                    buffer.append("', ");
                } else {
                    buffer.append(value);
                    buffer.append(", ");
                }
            }
        }
        String str = buffer.toString();
        String substring = str.substring(0, str.length() - 2);
        return substring + ")";
    }


    private static Object getFieldValue(Object owner, String fieldName) {
        return invokeMethod(owner, fieldName, null);
    }

    /**
     * 执行某个Field的getField方法
     *
     * @param owner     类
     * @param fieldName 类的属性名称
     * @param args      参数，默认为null
     * @return
     */
    private static Object invokeMethod(Object owner, String fieldName, Object[] args) {
        Class<? extends Object> ownerClass = owner.getClass();
        //fieldName -> FieldName
        String methodName = fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        Method method = null;
        try {
            method = ownerClass.getMethod("get" + methodName);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return "";
        }
        try {
            return method.invoke(owner);
        } catch (Exception e) {
            return "";
        }
    }
}






