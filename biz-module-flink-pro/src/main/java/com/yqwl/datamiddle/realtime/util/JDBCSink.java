package com.yqwl.datamiddle.realtime.util;

import com.yqwl.datamiddle.realtime.common.JDBCConfig;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.connector.jdbc.JdbcConnectionOptions;
import org.apache.flink.connector.jdbc.JdbcExecutionOptions;
import org.apache.flink.connector.jdbc.JdbcSink;
import org.apache.flink.connector.jdbc.JdbcStatementBuilder;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JDBCSink {
    public static <T> SinkFunction<T> getSink(String sql) {

        return JdbcSink.<T>sink(sql,
                new JdbcStatementBuilder<T>() {
                    @Override
                    public void accept(PreparedStatement preparedStatement, T t) throws SQLException {
                        try {
                            // 获取所有的属性信息
                            Field[] fields = t.getClass().getDeclaredFields();
                            // 遍历字段
                            int paramIndex = 0;
                            for ( Field field : fields ) {
                                // 获取字段
//                                Field field = fields[i];
                                String fieldName = field.getName();
                                // 序列化id不处理
                                if ( StringUtils.equalsAny(fieldName, "serialVersionUID")) {
                                    continue;
                                }

                                // 设置私有属性可访问
                                field.setAccessible(true);
                                // 获取值
                                Object value = field.get(t);
                                // 给预编译SQL对象赋值
                                try {
                                    preparedStatement.setObject(paramIndex + 1, value);
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }

                           /*  for (int i = 0; i < fields.length; i++) {

                            }
                            System.out.println(); */
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new JdbcExecutionOptions.Builder()
                        .withBatchSize(50)
                        .build(),
                new JdbcConnectionOptions.JdbcConnectionOptionsBuilder()
                        .withDriverName(JDBCConfig.JDBC_DRIVER)
                        .withUrl(JDBCConfig.JDBC_URL)
                        .withUsername(JDBCConfig.JDBC_USERNAME)
                        .withPassword(JDBCConfig.JDBC_PASSWORD)
                        .build());
    }
}
