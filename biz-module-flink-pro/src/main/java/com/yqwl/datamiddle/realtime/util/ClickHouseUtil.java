package com.yqwl.datamiddle.realtime.util;

import com.yqwl.datamiddle.realtime.common.ClickhouseConfig;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class ClickHouseUtil {

    public static <T> SinkFunction<T> getSink(String sql) {

        return JdbcSink.<T>sink(sql,
                new JdbcStatementBuilder<T>() {
                    @Override
                    public void accept(PreparedStatement preparedStatement, T t) throws SQLException {
                        try {
                            // 获取所有的属性信息
                            Field[] fields = t.getClass().getDeclaredFields();
                            // 遍历字段
                            for (int i = 0; i < fields.length; i++) {
                                // 获取字段
                                Field field = fields[i];
                                // 设置私有属性可访问
                                field.setAccessible(true);
                                // 获取值
                                Object value = field.get(t);
                                // 给预编译SQL对象赋值
                                preparedStatement.setObject(i + 1, value);
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(5000)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withDriverName(ClickhouseConfig.CLICKHOUSE_DRIVER)
                        .withUrl(ClickhouseConfig.CLICKHOUSE_URL)
                        .build());
    }
}
