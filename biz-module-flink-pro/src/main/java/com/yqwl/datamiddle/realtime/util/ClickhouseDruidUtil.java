package com.yqwl.datamiddle.realtime.util;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: 获取Druid连接下的 Clickhouse操作
 * @Author: WangYouzheng
 * @Date: 2022/7/20 17:02
 * @Version: V1.0
 */
@Slf4j
public class ClickhouseDruidUtil {
    // Druid数据源，全局唯一（只创建一次）
    private static volatile DruidDataSource druidDataSource;

    /**
     * 初始化数据源
     *
     * @return
     * @throws SQLException
     */
    private static DruidDataSource getDruidDataSource() throws SQLException {
        // 保证Druid数据源在多线程下只创建一次
        if (druidDataSource == null) {
            synchronized (DbUtil.class) {
                if (druidDataSource == null) {
                    druidDataSource = createDruidDataSource();
                    return druidDataSource;
                }
            }
        }
        log.info(">>>>>>>>>>> 复用Druid数据源:url={}, username={}, password={}",
                druidDataSource.getUrl(), druidDataSource.getUsername(), druidDataSource.getPassword());
        return druidDataSource;
    }

    /**
     * 创建Druid数据源
     *
     * @return
     * @throws SQLException
     */
    private static DruidDataSource createDruidDataSource() throws SQLException {
        DruidDataSource druidDataSource = new DruidDataSource();
        //Props props = PropertiesUtil.getProps(PropertiesUtil.ACTIVE_TYPE);
        druidDataSource.setUrl(PropertiesUtil.getPropsStr("clickhouse.url"));
        druidDataSource.setUsername(PropertiesUtil.getPropsStr("clickhouse.username"));
        druidDataSource.setPassword(PropertiesUtil.getPropsStr("clickhouse.password"));

        /*----下面的具体配置参数自己根据项目情况进行调整----*/
        druidDataSource.setMaxActive(1500);
        druidDataSource.setInitialSize(10);
        druidDataSource.setMinIdle(1);
        druidDataSource.setMaxWait(60000);

        druidDataSource.setValidationQuery("select 1");

        druidDataSource.setTimeBetweenEvictionRunsMillis(60000);
        druidDataSource.setMinEvictableIdleTimeMillis(300000);

        druidDataSource.setTestWhileIdle(true);
        druidDataSource.setTestOnBorrow(false);
        druidDataSource.setTestOnReturn(false);
        //无连接可用超时
        //此配置项会影响性能，只在排查的时候打开，系统运行时最好关闭
        druidDataSource.setRemoveAbandoned(true);
        druidDataSource.setRemoveAbandonedTimeout(180);

        druidDataSource.setPoolPreparedStatements(true);
        druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(20);

        // 指定clickhouse 驱动
        druidDataSource.setDriverClassName("com.clickhouse.jdbc.ClickHouseDriver");
        druidDataSource.init();
        log.info(">>>>>>>>>>> 创建Druid数据源:url={}, username={}, password={}",
                druidDataSource.getUrl(), druidDataSource.getUsername(), druidDataSource.getPassword());
        return druidDataSource;
    }

    /**
     * 释放资源
     *
     * @param connection
     * @param statement
     * @param resultSet
     * @throws SQLException
     */
    private static void closeResource(Connection connection, Statement statement, ResultSet resultSet) throws SQLException {
        // 注意资源释放顺序
        if (resultSet != null) {
            resultSet.close();
        }
        if (statement != null) {
            statement.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * 获取Druid连接
     *
     * @return
     * @throws SQLException
     */
    public static DruidPooledConnection getDruidConnection() throws SQLException {
        DruidDataSource druidDataSource = getDruidDataSource();
        DruidPooledConnection connection = druidDataSource.getConnection();
        return connection;
    }

    /**
     * 执行SQL更新
     *
     * @param insertChMap 批量写入的集合 {表名: {insert语句 : [values部分数值结合]}}
     * @throws SQLException
     */
    public static void insertPrepare(Map<String, Map<String, List<List<Object>>>> insertChMap) throws SQLException {
        Connection connection = null;
        final PreparedStatement[] preparedStatement = {null};

        if (MapUtils.isNotEmpty(insertChMap)) {

            try {
                connection = getDruidConnection();

                Connection finalConnection = connection;
                final PreparedStatement[] finalPreparedStatement = {preparedStatement[0]};
                insertChMap.forEach((k, v) -> {
                    Map.Entry<String, List<List<Object>>> stringListEntry = v.entrySet().stream().findFirst().get();
                    String insertTable = stringListEntry.getKey();
                    List<List<Object>> value = stringListEntry.getValue();
                    try {
                        finalPreparedStatement[0] = finalConnection.prepareStatement(insertTable);

                        // 循环每个对象
                        for (List<Object> itemObj : value) {
                            // 循环每一列
                            int fieldIdx = 1;
                            for (Object field : itemObj) {
                                finalPreparedStatement[0].setObject(fieldIdx, field);
                                fieldIdx++;
                            }
                            finalPreparedStatement[0].addBatch();
                        }
                        finalPreparedStatement[0].executeBatch();
                    } catch (SQLException e) {
                        log.error(e.getMessage(), e);
                    }
                });
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            } finally {
                // 切记!!! 一定要释放资源
                closeResource(connection, preparedStatement[0], null);
            }
        }
    }
}
