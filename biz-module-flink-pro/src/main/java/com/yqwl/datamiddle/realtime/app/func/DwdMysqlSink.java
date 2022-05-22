package com.yqwl.datamiddle.realtime.app.func;

import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yqwl.datamiddle.realtime.bean.DwdSptb02;
import com.yqwl.datamiddle.realtime.common.KafkaTopicConst;
import com.yqwl.datamiddle.realtime.common.MysqlConfig;
import com.yqwl.datamiddle.realtime.enums.TransientSink;
import com.yqwl.datamiddle.realtime.util.JsonPartUtil;
import com.yqwl.datamiddle.realtime.util.PropertiesUtil;
import io.debezium.data.Json;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;

import java.lang.reflect.Field;
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
public class DwdMysqlSink extends RichSinkFunction<String> {

    //定义mysql连接对象
    private Connection conn = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        //对连接对象进行初始化
        Class.forName(MysqlConfig.DRIVER);
        Props props = PropertiesUtil.getProps(PropertiesUtil.ACTIVE_TYPE);
        conn = DriverManager.getConnection(props.getStr("mysql.url"), props.getStr("mysql.username"), props.getStr("mysql.password"));
    }

    /**
     * 对流中的数据进行处理
     */
    @Override
    public void invoke(String json, Context context) throws Exception {
        //传输的是after里的真实表结构数据
        System.out.println("DwdMysqlSink.invoke接收到json数据:" + json);
        JSONObject jsonObj = JSON.parseObject(json);
        DwdSptb02 dwdSptb02 = JSON.parseObject(json, DwdSptb02.class);
        System.out.println("DwdMysqlSink.invoke接收到jsonObj数据:" + json);
        //对没有值的属性赋默认值
        DwdSptb02 bean = JsonPartUtil.getBean(dwdSptb02);
        System.out.println("默认值赋值后数据：" + bean);
        JSONObject jsonObject = JSON.parseObject(bean.toString());
        String sql = genInsertSql(KafkaTopicConst.DWD_VLMS_SPTB02, jsonObject);
        //根据data中属性名和属性值  生成mysql语句
        System.out.println("向mysql库中插入数据的SQL:" + sql);
        log.info("向mysql库中插入数据的SQL:" + sql);
        //执行SQL
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            ps.execute();
        } catch (SQLException e) {
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
        log.info("获取当前数据所有values:{}", values);
        System.out.println("获取当前数据所有values:" + values);
        StringBuffer insertSql = new StringBuffer();
        insertSql.append("replace into ").append(tableName).append(" (")
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
        System.out.println("组装values子句:" + valueList);
        System.out.println("join后的子句:" + StringUtils.join(valueList, ","));
        insertSql.append(StringUtils.join(valueList, ",")).append(" )");
        return insertSql.toString();
    }
}
