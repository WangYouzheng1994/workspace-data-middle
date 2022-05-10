package com.yqwl.datamiddle.realtime.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yqwl.datamiddle.realtime.bean.Mdac01;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 对传输数据json各部分进行分别获取
 */
public class JsonPartUtil {

    private static final String DATABASE = "database";
    private static final String BEFORE = "before";
    private static final String AFTER = "after";
    private static final String TYPE = "type";
    private static final String TABLE_NAME = "tableName";
    private static final String TS = "ts";

    /**
     * query:
     * {
     * "database":"datasource_kafka",
     * "before":{},
     * "after":{"order_no":"20220303911728","create_time":1649412632000,"product_count":1,"product_id":434,"id":297118,"product_amount":3426},
     * "type":"query",
     * "tableName":"orders_detail",
     * "ts":1651830021955
     * }
     * <p>
     * insert:
     * {
     * "database":"datasource_kafka",
     * "before":{},
     * "after":{"order_no":"20220303855787","create_time":1647859623000,"product_count":2,"product_id":39,"id":300007,"product_amount":4453},
     * "type":"insert",
     * "tableName":"orders_detail",
     * "ts":1651830458870
     * }
     * <p>
     * update:
     * {
     * "database":"datasource_kafka",
     * "before":{"order_no":"20220303855786","create_time":1647859623000,"product_count":1,"product_id":38,"id":1008,"product_amount":4443},
     * "after":{"order_no":"20220303855786","create_time":1647859623000,"product_count":1,"product_id":3878,"id":1008,"product_amount":4443},
     * "type":"update",
     * "tableName":"orders_detail",
     * "ts":1651830576944
     * }
     * <p>
     * delete:
     * {
     * "database":"datasource_kafka",
     * "before":{"order_no":"20220303855786","create_time":1647859623000,"product_count":1,"product_id":3878,"id":1008,"product_amount":4443},
     * "after":{},
     * "type":"delete",
     * "tableName":"orders_detail",
     * "ts":1651830662880
     * }
     */
    private static JSONObject toJsonObj(String json) {
        return JSON.parseObject(json);
    }


    /**
     * 获取数据库名称部分数据
     *
     * @param json
     * @return
     */
    public static String getDatabaseStr(String json) {
        JSONObject jsonObj = toJsonObj(json);
        return jsonObj.getString(DATABASE);
    }

    /**
     * 获取数据库名称部分数据
     *
     * @param json
     * @return
     */
    public static String getDatabaseStr(JSONObject json) {
        return json.getString(DATABASE);
    }

    /**
     * 获取before部分数据
     *
     * @param json
     * @return
     */
    public static String getBeforeStr(String json) {
        JSONObject jsonObj = toJsonObj(json);
        return jsonObj.getString(BEFORE);
    }

    /**
     * 获取before部分数据
     *
     * @param json
     * @return
     */
    public static String getBeforeStr(JSONObject json) {
        return json.getString(BEFORE);
    }

    /**
     * 获取after部分数据
     *
     * @param json
     * @return
     */
    public static JSONObject getBeforeObj(JSONObject json) {
        return json.getJSONObject(BEFORE);
    }

    /**
     * 获取before部分数据将其转化为对象
     *
     * @param json
     * @return
     */
    public static <T> T getBeforeObj(String json, Class<T> clazz) {
        JSONObject jsonObj = toJsonObj(json);
        String beforeStr = jsonObj.getString(BEFORE);
        return JSON.parseObject(beforeStr, clazz);
    }

    /**
     * 获取after部分数据
     *
     * @param json
     * @return
     */
    public static String getAfterStr(String json) {
        JSONObject jsonObj = toJsonObj(json);
        return jsonObj.getString(AFTER);
    }

    /**
     * 获取after部分数据
     *
     * @param json
     * @return
     */
    public static String getAfterStr(JSONObject json) {
        return json.getString(AFTER);
    }

    /**
     * 获取after部分数据
     *
     * @param json
     * @return
     */
    public static JSONObject getAfterObj(JSONObject json) {
        return json.getJSONObject(AFTER);
    }

    /**
     * 获取after部分数据将其转化为对象
     *
     * @param json
     * @return
     */
    public static <T> T getAfterObj(String json, Class<T> clazz) {
        JSONObject jsonObj = toJsonObj(json);
        String afterStr = jsonObj.getString(AFTER);
        return getBean(JSON.parseObject(afterStr, clazz));
    }


    private static <T> T getBean(T object) {
        T objectCopy = null;
        try {
            Class<?> classType = object.getClass();
            //SqlRowSet srs = jdbcTemplate.queryForRowSet(sql);
            Field[] fields = classType.getDeclaredFields();//得到对象中的字段
            //每次循环时，重新实例化一个与传过来的对象类型一样的对象
            objectCopy = (T) classType.getConstructor(new Class[]{}).newInstance(new Object[]{});
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                String fieldName = field.getName();
                Object value = null;
                //根据字段类型决定结果集中使用哪种get方法从数据中取到数据
                if (field.getType().equals(String.class)) {
                    value = "";
                } else if (field.getType().equals(int.class) || field.getType().equals(Integer.class)) {
                    value = new Integer(0);
                } else if (field.getType().equals(double.class) || field.getType().equals(Double.class)) {
                    value = new Double(0);
                } else if (field.getType().equals(long.class) || field.getType().equals(Long.class)) {
                    value = new Long(0);
                } else if (field.getType().equals(Date.class)) {
                    value = new Date();
                } else if (field.getType().equals(Object.class)) {
                    value = new Object();
                }
                // 获得属性的首字母并转换为大写，与setXXX对应
                String firstLetter = fieldName.substring(0, 1).toUpperCase();
                String setMethodName = "set" + firstLetter
                        + fieldName.substring(1);
                if (fieldName.equals("serialVersionUID")) {
                    continue;
                }
                Method setMethod = classType.getMethod(setMethodName,
                        new Class[]{field.getType()});
                setMethod.invoke(objectCopy, new Object[]{value});//调用对象的setXXX方法
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return objectCopy;
    }

    /**
     * 获取数据操作类型
     *
     * @param json
     * @return
     */
    public static String getTypeStr(String json) {
        JSONObject jsonObj = toJsonObj(json);
        return jsonObj.getString(TYPE);
    }

    /**
     * 获取数据操作类型
     *
     * @param json
     * @return
     */
    public static String getTypeStr(JSONObject json) {
        return json.getString(TYPE);
    }

    /**
     * 获取数据导入时间戳
     *
     * @param json
     * @return
     */
    public static String getTableNameStr(String json) {
        JSONObject jsonObj = toJsonObj(json);
        return jsonObj.getString(TABLE_NAME);
    }

    /**
     * 获取数据导入时间戳
     *
     * @param json
     * @return
     */
    public static String getTableNameStr(JSONObject json) {
        return json.getString(TABLE_NAME);
    }

    /**
     * 获取数据导入时间戳
     *
     * @param json
     * @return
     */
    public static String getTsStr(String json) {
        JSONObject jsonObj = toJsonObj(json);
        return jsonObj.getString(TS);
    }

    /**
     * 获取数据导入时间戳
     *
     * @param json
     * @return
     */
    public static String getTsStr(JSONObject json) {
        return json.getString(TS);
    }


    public static void main(String[] args) {
        String json = "{}";
        JSONObject jsonObject = JSON.parseObject(json);
        String tableNameStr = JsonPartUtil.getTableNameStr(json);
        //操作类型  query insert update delete
        String typeStr = JsonPartUtil.getTypeStr(json);
        //{"order_no":"20220303855787","create_time":1647859623000,"product_count":2,"product_id":39,"id":300007,"product_amount":4453}



        String afterStr = JsonPartUtil.getAfterStr(json);
        String beforeStr = JsonPartUtil.getBeforeStr(json);
        Mdac01 mdac01 = JsonPartUtil.getAfterObj(json, Mdac01.class);


    }

}
