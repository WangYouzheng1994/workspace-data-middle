package com.yqwl.datamiddle.realtime.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.google.common.base.CaseFormat;
import com.yqwl.datamiddle.realtime.enums.TableName;
import com.yqwl.datamiddle.realtime.enums.TransientSink;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Description: 主要是执行mysql 查询
 * @Author: WangYouzheng
 * @Date: 2021/12/28 11:09
 * @Version: V1.0
 */
@Slf4j
public class MysqlUtil {


    /**
     * mysql 查询方法，根据给定的 class 类型 返回对应类型的元素列表
     *
     * @param sql
     * @param clazz
     * @param underScoreToCamel 是否把对应DB字段的下划线名转为驼峰名
     * @param <T>
     * @return
     */
    public static <T> List<T> queryList(String sql, Class<T> clazz, Boolean underScoreToCamel) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            //获取连接
            conn = DbUtil.getDruidConnection();
            //创建数据库操作对象
            ps = conn.prepareStatement(sql);
            //执行 SQL 语句
            rs = ps.executeQuery();
            //处理结果集
            ResultSetMetaData md = rs.getMetaData();
            //声明集合对象，用于封装返回结果
            List<T> resultList = new ArrayList<T>();
            //每循环一次，获取一条查询结果
            while (rs.next()) {
                //通过反射创建要将查询结果转换为目标类型的对象
                T obj = clazz.newInstance();
                //对查询出的列进行遍历，每遍历一次得到一个列名
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    String propertyName = md.getColumnName(i);
                    //如果开启了下划线转驼峰的映射，那么将列名里的下划线转换为属性的打
                    if (underScoreToCamel) {
                        //直接调用 Google 的 guava 的 CaseFormat LOWER_UNDERSCORE 小写开头+下划线->LOWER_CAMEL 小写开头+驼峰
                        propertyName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, propertyName);
                    }
                    //调用 apache 的 commons-bean 中的工具类，给 Bean 的属性赋值
                    BeanUtils.setProperty(obj, propertyName, rs.getObject(i));
                }
                resultList.add(obj);
            }
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("查询 mysql 失败！");
        } finally {
            //释放资源
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 根据类组装sql
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> String getSql(Class<T> clazz) {
        TableName tableName = clazz.getAnnotation(TableName.class);
        if (Objects.isNull(tableName)) {
            throw new RuntimeException("当前类没有添加 TableName 注解");
        }
        Field[] fields = clazz.getDeclaredFields();
        StringBuffer sql = new StringBuffer();
        sql.append("replace into ").append(tableName.value());
        List<String> columnList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();
        for (Field field : fields) {
            if (field.getAnnotation(TransientSink.class) != null) {
                continue;
            }
            if (StringUtils.equals(field.getName(), "serialVersionUID")) {
                continue;
            }
            columnList.add(field.getName());
            valueList.add("?");
        }
        sql.append(" (").append(StringUtils.join(columnList, ",")).append(") ");
        sql.append("values");
        sql.append(" (").append(StringUtils.join(valueList, ",")).append(") ");
        return sql.toString();
    }

    /**
     * 根据类组装sql
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> String getOnDuplicateKeySql(Class<T> clazz) {
        TableName tableName = clazz.getAnnotation(TableName.class);
        if (Objects.isNull(tableName)) {
            throw new RuntimeException("当前类没有添加 TableName 注解");
        }
        Field[] fields = clazz.getDeclaredFields();
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO ").append(tableName.value());
        List<String> columnList = new ArrayList<>();
        List<String> valueList = new ArrayList<>();
        List<String> columnAndValueList = new ArrayList<>();
        for (Field field : fields) {
            if (field.getAnnotation(TransientSink.class) != null) {
                continue;
            }
            if (StringUtils.equals(field.getName(), "serialVersionUID")) {
                continue;
            }
            columnList.add(field.getName());
            columnAndValueList.add(field.getName()+" = VALUES("+ field.getName() + ")");
            valueList.add("?");
        }

        //  INSERT INTO test ( id, `name`, auto )
        //  VALUES
        //	( 1, '3', 666661 )
        //	ON DUPLICATE KEY UPDATE auto = VALUES( auto );

        sql.append(" (").append(StringUtils.join(columnList, ",")).append(") ");
        sql.append("values");
        sql.append(" (").append(StringUtils.join(valueList, ",")).append(") ");
        sql.append(" ON DUPLICATE KEY UPDATE ");
        sql.append(StringUtils.join(columnAndValueList,","));

        return sql.toString();
    }


    public static JSONObject querySingle(String tableName, String sql, Object... value) {
        Long startTime = System.currentTimeMillis();
        String redisKey = "dwm:vlms:" + tableName.toLowerCase() + ":";
        for (int i = 0; i < value.length; i++) {
            Object fieldValue = value[i];
            if (i > 0) {
                redisKey += ":";
            }
            redisKey += fieldValue;
        }
        log.info("redisKey:{}", redisKey);
        //从Redis中获取数据
        Jedis jedis = null;
        //维度数据的json字符串形式
        String dimJsonStr = null;
        //维度数据的json对象形式
        JSONObject dimJsonObj = null;
        try {
            //获取jedis客户端
            jedis = RedisUtil.getJedis();
            //根据key到Redis中查询value
            dimJsonStr = jedis.get(redisKey);
            //判断是否从Redis中查询到了数据
            if (dimJsonStr != null && dimJsonStr.length() > 0) {
                dimJsonObj = JSON.parseObject(dimJsonStr);
            } else {
                List<JSONObject> dimList = DbUtil.queryList(sql, JSONObject.class, false);
                //对于维度查询来讲，一般都是根据主键进行查询，不可能返回多条记录，只会有一条
                if (dimList.size() > 0) {
                    dimJsonObj = dimList.get(0);
                    //将查询出来的数据放到Redis中缓存起来
                    if (jedis != null) {
                        jedis.setex(redisKey, 3600 * 24, dimJsonObj.toJSONString());
                    }
                } else {
                    log.info("维度数据没有找到:{}", sql);
                }
                dimList = null;
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            //关闭Jedis
            if (jedis != null) {
                jedis.close();
            }
        }
        Long endTime = System.currentTimeMillis();
        Long resultTime = endTime - startTime;
            if (resultTime > 10) {
                log.error("出现了慢sql:{}" + sql);
                log.error("花费的时间为:{}", resultTime);
            }
        return dimJsonObj;
    }


}
