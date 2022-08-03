package org.test.cdc.oracle;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @Description: Oracle CDC JDBC连接处理工具 把SCN的交互解耦。以便后续其他的地方使用
 * @Author: WangYouzheng
 * @Date: 2022/8/3 15:02
 * @Version: V1.0
 */
@Slf4j
public class OracleDBUtil {

    /** 关闭数据库连接资源 */
    public static void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        if (null != rs) {
            try {
                rs.close();
            } catch (SQLException e) {
                log.warn("Close resultSet error: {}", ExceptionUtil.getErrorMessage(e));
            }
        }

        closeStmt(stmt);

        if (null != conn) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.warn("Close connection error:{}", ExceptionUtil.getErrorMessage(e));
            }
        }
    }

    /**
     * 获取当前SCN
     *
     * @param connection
     * @return
     */
    public static BigInteger getCurrentScn(Connection connection) {
        BigInteger currentScn = null;
        CallableStatement currentScnStmt = null;
        ResultSet currentScnResultSet = null;

        try {
            currentScnStmt = connection.prepareCall(SqlUtil.SQL_GET_CURRENT_SCN);

            currentScnResultSet = currentScnStmt.executeQuery();
            while (currentScnResultSet.next()) {
                currentScn = new BigInteger(currentScnResultSet.getString(LogminerKeyConstants.KEY_CURRENT_SCN));
            }

            return currentScn;
        } catch (SQLException e) {
            log.error("获取当前的SCN出错:", e);
            throw new RuntimeException(e);
        } finally {
            closeResources(currentScnResultSet, currentScnStmt, null);
        }
    }

    /**
     * 获取指定时间的起始Log的偏移量
     *
     * @param connection
     * @param time 13位时间戳
     * @return
     */
    public static BigInteger getLogFileStartPositionByTime(Connection connection, Long startTime) {
        BigInteger logFileFirstChange = null;

        PreparedStatement lastLogFileStmt = null;
        ResultSet lastLogFileResultSet = null;

        try {
            String timeStr = DateFormatUtils.format(startTime, "yyyy-MM-dd HH:mm:ss");

            lastLogFileStmt = connection.prepareCall(SqlUtil.SQL_GET_LOG_FILE_START_POSITION_BY_TIME);

            lastLogFileStmt.setString(1, timeStr);
            lastLogFileStmt.setString(2, timeStr);
            lastLogFileStmt.setString(3, timeStr);

            lastLogFileResultSet = lastLogFileStmt.executeQuery();
            while (lastLogFileResultSet.next()) {
                logFileFirstChange =
                        new BigInteger(lastLogFileResultSet.getString(LogminerKeyConstants.KEY_FIRST_CHANGE));
            }

            return logFileFirstChange;
        } catch (SQLException e) {
            log.error("根据时间:[{}]获取指定归档日志起始位置出错", startTime, e);
            throw new RuntimeException(e);
        } finally {
            closeResources(lastLogFileResultSet, lastLogFileStmt, null);
        }
    }

    /**
     *
     *
     * @param connection
     * @return
     */
    public static BigInteger getMinScn(Connection connection) {
        BigInteger minScn = null;
        PreparedStatement minScnStmt = null;
        ResultSet minScnResultSet = null;

        try {
            minScnStmt = connection.prepareCall(SqlUtil.SQL_GET_LOG_FILE_START_POSITION);

            minScnResultSet = minScnStmt.executeQuery();
            while (minScnResultSet.next()) {
                minScn = new BigInteger(minScnResultSet.getString(KEY_FIRST_CHANGE));
            }

            return minScn;
        } catch (SQLException e) {
            LOG.error(" obtaining the starting position of the earliest archive log error", e);
            throw new RuntimeException(e);
        } finally {
            closeResources(minScnResultSet, minScnStmt, null);
        }
    }
}
