package com.yqwl.datamiddle.realtime.app.func;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.yqwl.datamiddle.realtime.common.MysqlConfig;
import com.yqwl.datamiddle.realtime.common.OperateTypeConst;
import com.yqwl.datamiddle.realtime.common.PhoenixConfig;
import com.yqwl.datamiddle.realtime.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * @Description: 自定义mysql sink function
 * @Author: muqing
 * @Date: 2022/05/07
 * @Version: V1.0
 */
@Slf4j
public class DimMysqlSink extends RichSinkFunction<JSONObject> {
    private static final Logger LOGGER = LogManager.getLogger(TableProcessDivideFunction.class);

    //定义mysql连接对象
    private Connection conn = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        //对连接对象进行初始化
        Class.forName(MysqlConfig.DRIVER);
        Props props = PropertiesUtil.getProps();
        conn = DriverManager.getConnection(props.getStr("mysql.url"), props.getStr("mysql.username"), props.getStr("mysql.password"));
    }

    /**
     * 对流中的数据进行处理
     *
     * @param jsonObj
     * @param context
     * @throws Exception
     */
    @Override
    public void invoke(JSONObject jsonObj, Context context) throws Exception {
        //获取目标表名
        String sinkTableName = jsonObj.getString("sink_table");
        //获取目标表的主键
        String sinkPk = jsonObj.getString("sink_pk");
        //获取当前数据中的操作类型
        String operateType = JsonPartUtil.getTypeStr(jsonObj);
        log.info("当前数据操作类型：" + operateType);
        System.out.println("当前数据操作类型：" + operateType);
        //获取json中after数据
        JSONObject afterJsonObj = JsonPartUtil.getAfterObj(jsonObj);
        log.info("当前要处理数据：" + afterJsonObj);
        String sql = genInsertSql(sinkTableName, afterJsonObj);

        //根据data中属性名和属性值  生成mysql语句
        System.out.println("向mysql库中插入数据的SQL:" + sql);
        LOGGER.info("向mysql库中插入数据的SQL:" + sql);
        //执行SQL
        PreparedStatement ps = null;
        try {
            //ps = conn.prepareStatement(sql);
            //ps.execute();
            DbUtil.insert(sql);
        } catch (Exception e) {
            log.error("向mysql插入数据失败, {}", e.getMessage());
            throw new RuntimeException("向mysql插入数据失败");
        } finally {
            if (ps != null) {
                ps.close();
            }
        }

    }

    // 根据data属性和值生成 insert sql语句
    private String genInsertSql(String tableName, JSONObject dataJsonObj) {
        //"insert into 表名(列名.....) values (值....)"
        Set<String> columns = dataJsonObj.keySet();
        log.info("获取当前数据所有key:{}", columns);
        System.out.println("获取当前数据所有key:" + columns);
        Collection<Object> values = dataJsonObj.values();
        log.info("获取当前数据所有values:{}", columns);
        System.out.println("获取当前数据所有values:" + columns);
        StringBuffer insertSql = new StringBuffer();
        insertSql.append("insert into ").append(tableName).append(" (")
                .append(StringUtils.join(columns, ",")).append(" )")
                .append(" values ( ");

        //循环组装value子句
        Iterator<Object> iterator = values.iterator();
        List<Object> valueList = new ArrayList<>();
        while (iterator.hasNext()) {
            Object next = iterator.next();
            if (next instanceof String) {
                valueList.add("'" + next + "'");
            } else {
                valueList.add(next);
            }
        }
        insertSql.append(StringUtils.join(valueList, ",")).append(" )");
        return insertSql.toString();
    }

    // 根据data属性和值生成 update sql语句
    private String genUpdateSql(String tableName, JSONObject dataJsonObj) {
        //获取目标表的主键
        String sinkPk = dataJsonObj.getString("sink_pk");
        //UPDATE 表名 SET 字段1 = 值1,字段2=值2... WHERE 字句
        Set<String> columns = dataJsonObj.keySet();
        Collection<Object> values = dataJsonObj.values();
        StringBuffer updateSql = new StringBuffer();
        updateSql.append("update ").append(tableName).append("set ");

        List<String> setValues = new ArrayList<>();
        Set<Map.Entry<String, Object>> entries = dataJsonObj.entrySet();
        Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
        StringBuffer setSql = new StringBuffer();
        Object pkValue = "";
        //循环组装 set 子句
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            updateSql.append(next.getKey()).append("=");
            if (next.getValue() instanceof String) {
                setSql.append("'").append(next.getValue()).append("'");
            } else {
                setSql.append(next.getValue());
            }
            if (sinkPk.equals(next.getKey())) {
                pkValue = next.getValue();
            }
            setValues.add(setSql.toString());
        }
        updateSql.append(StringUtils.join(setValues, ","));
        //合并添加 where 子句
        updateSql.append(" where ").append(sinkPk).append("=").append("'").append(pkValue).append("'");
        return updateSql.toString();
    }

    //获取当前数据中主键的值
    private String getPKValue(JSONObject data) {
        //获取目标表的主键
        String sinkPk = data.getString("sink_pk");
        Set<Map.Entry<String, Object>> entries = data.entrySet();
        Iterator<Map.Entry<String, Object>> iterator = entries.iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Object> next = iterator.next();
            if (sinkPk.equals(next.getKey())) {
                return next.getValue().toString();
            }
        }
        return null;
    }


    public static void main(String[] args) {
        String json = "{\"order_no\":\"20220303911728\",\"create_time\":1649412632000,\"product_count\":1,\"product_id\":434,\"id\":297118,\"product_amount\":3426}";
        JSONObject jsonObject = JSON.parseObject(json);
        Set<String> stringSet = jsonObject.keySet();
        Collection<Object> values = jsonObject.values();
        System.out.println(StringUtils.join(stringSet, ","));
        System.out.println(StringUtils.join(values, ","));
    }


}
