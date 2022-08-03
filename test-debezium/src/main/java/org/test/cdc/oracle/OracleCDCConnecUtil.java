package org.test.cdc.oracle;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/8/3 14:36
 * @Version: V1.0
 */
@Slf4j
public class OracleCDCConnecUtil {
    private Connection connection;
    private CallableStatement logMinerStartStmt;
    private PreparedStatement logMinerSelectStmt;
    private ResultSet logMinerData;
    private OracleCDCConfig config;

    public OracleCDCConnecUtil() {

    }

    public OracleCDCConnecUtil(OracleCDCConfig config) {
        this.config = config;
        return this;
    }

    /**
     * 获取连接，并且设置好字符集
     *
     * @return
     */
    protected boolean getConnection(OracleCDCConfig config) {
        int interval = 1;
        log.debug("connection driver class: {}", config.getDriverClass());
        log.info("connection user: {}", config.getUsername());
        log.info("connection password: {}", config.getPassword());

        // 加载驱动
        Class.forName(config.getDriverClass());

        do {
            try {
                connection = DriverManager.getConnection(config.getJdbcUrl(), config.getUsername(), config.getPassword());
                interval = 5;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                OracleDBUtil.closeResources(null, null, connection);
                interval++;
            }
        } while(interval < 3);

        interval = 1;
        // 设置编码和日期类型
        do {
            try {
                connection.prepareStatement(SqlUtil.SQL_ALTER_NLS_SESSION_PARAMETERS);
                preparedStatement.execute();
                interval = 5;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                OracleDBUtil.closeResources(null, null, connection);
                interval++;
            }
        } while(interval < 3);

        connection == null ? return false : return true;
    }
}