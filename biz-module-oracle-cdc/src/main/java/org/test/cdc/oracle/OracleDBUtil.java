package org.test.cdc.oracle;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.test.cdc.oracle.bean.LogFile;
import org.test.cdc.oracle.constants.ConstantValue;

import java.math.BigInteger;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

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
                log.warn("Close resultSet error: {}", e.getMessage());
            }
        }
        if (null != stmt) {
            closeStmt(stmt);
        }

        if (null != conn) {
            try {
                conn.close();
            } catch (SQLException e) {
                log.warn("Close connection error:{}", e.getMessage());
            }
        }
    }

    /** 关闭Statement */
    private static void closeStmt(Statement statement) {
        try {
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }
        } catch (SQLException e) {
            log.warn("Close statement error", e);
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
     * @param startTime 13位时间戳
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
     * 获取最小SCN
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
                minScn = new BigInteger(minScnResultSet.getString(LogminerKeyConstants.KEY_FIRST_CHANGE));
            }

            return minScn;
        } catch (SQLException e) {
            log.error(" obtaining the starting position of the earliest archive log error", e);
            throw new RuntimeException(e);
        } finally {
            closeResources(minScnResultSet, minScnStmt, null);
        }
    }

    /**
     *  根据leftScn 以及加载的日志大小限制 获取可加载的scn范围 以及此范围对应的日志文件
     */
    public static Pair<BigInteger, Boolean> getEndScn(Connection connection, BigInteger startScn, List<LogFile> logFiles)
            throws SQLException {
        return getEndScn(connection, startScn, logFiles, true);
    }

    /**
     * 获取结束End Scn
     *
     * @param startScn
     * @param logFiles
     * @param addRedoLog 是否包含 redolog 如果是false 就只抽归档日志。
     * @return org.apache.commons.lang3.tuple.Pair
     * @throws SQLException
     */
    public static Pair<BigInteger, Boolean> getEndScn(Connection connection, BigInteger startScn, List<LogFile> logFiles, boolean addRedoLog) throws SQLException {

        List<LogFile> logFileLists = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            String sql;
            if (addRedoLog) {
                sql = SqlUtil.SQL_QUERY_LOG_FILE;
            } else {
                sql = SqlUtil.SQL_QUERY_ARCHIVE_LOG_FILE;
            }
            statement = connection.prepareStatement(sql);
            statement.setString(1, startScn.toString());
            statement.setString(2, startScn.toString());
            rs = statement.executeQuery();
            while (rs.next()) {
                LogFile logFile = new LogFile();
                logFile.setFileName(rs.getString("name"));
                logFile.setFirstChange(new BigInteger(rs.getString("first_change#")));
                logFile.setNextChange(new BigInteger(rs.getString("next_change#")));
                logFile.setThread(rs.getLong("thread#"));
                logFile.setBytes(rs.getLong("BYTES"));
                logFile.setType(rs.getString("TYPE"));
                logFileLists.add(logFile);
            }
        } finally {
            closeResources(rs, statement, null);
        }

        Map<Long, List<LogFile>> map =
                logFileLists.stream().collect(Collectors.groupingBy(LogFile::getThread));

        // 对每一个thread的文件进行排序
        map.forEach(
                (k, v) ->
                        map.put(
                                k,
                                v.stream()
                                        .sorted(Comparator.comparing(LogFile::getFirstChange))
                                        .collect(Collectors.toList())));

        BigInteger endScn = startScn;
        Boolean loadRedoLog = false;

        long fileSize = 0L;
        Collection<List<LogFile>> values = map.values();

        // 不知道为什么要判定最大文件大小。。。
        // while (fileSize < logMinerConfig.getMaxLogFileSize()) {
            List<LogFile> tempList = new ArrayList<>(8);
            for (List<LogFile> logFileList : values) {
                for (LogFile logFile1 : logFileList) {
                    if (!logFiles.contains(logFile1)) {
                        // 每个thread组文件每次只添加第一个
                        tempList.add(logFile1);
                        break;
                    }
                }
            }
            // 如果为空 代表没有可以加载的日志文件 结束循环
            if (CollectionUtils.isEmpty(tempList)) {
                // break;
            } else {
                // 找到最小的nextSCN
                BigInteger minNextScn =
                        tempList.stream()
                                .sorted(Comparator.comparing(LogFile::getNextChange))
                                .collect(Collectors.toList())
                                .get(0)
                                .getNextChange();

                for (LogFile logFile1 : tempList) {
                    if (logFile1.getFirstChange().compareTo(minNextScn) < 0) {
                        logFiles.add(logFile1);
                        fileSize += logFile1.getBytes();
                        if (logFile1.isOnline()) {
                            loadRedoLog = true;
                        }
                    }
                }
                endScn = minNextScn;
            }
        // }

        if (loadRedoLog) {
            // 解决logminer偶尔丢失数据问题，读取online日志的时候，需要将rightScn置为当前SCN
            endScn = getCurrentScn(connection);
            logFiles = logFileLists;
        }

        if (CollectionUtils.isEmpty(logFiles)) {
            return Pair.of(null, loadRedoLog);
        }

        log.info(
                "getEndScn success,startScn:{},endScn:{}, loadRedoLog:{}",
                startScn,
                endScn,
                loadRedoLog);
        return Pair.of(endScn, loadRedoLog);
    }

    /**
     * 构建查询v$logmnr_contents视图SQL
     *
     * @param listenerOptions 需要采集操作类型字符串 delete,insert,update
     * @param listenerTables 需要采集的schema+表名 SCHEMA1.TABLE1,SCHEMA2.TABLE2
     * @return
     */
    public static String buildSelectSql(
            String listenerOptions, String listenerTables, boolean isCdb) {
        StringBuilder sqlBuilder = new StringBuilder(SqlUtil.SQL_SELECT_DATA);

        if (StringUtils.isNotEmpty(listenerTables)) {
            sqlBuilder.append(" and ( ").append(buildSchemaTableFilter(listenerTables, isCdb));
        } else {
            sqlBuilder.append(" and ( ").append(buildExcludeSchemaFilter());
        }

        if (StringUtils.isNotEmpty(listenerOptions)) {
            sqlBuilder.append(" and ").append(buildOperationFilter(listenerOptions));
        }

        // 包含commit
        sqlBuilder.append(" or OPERATION_CODE = 7 )");
        String sql = sqlBuilder.toString();
        log.info("SelectSql = {}", sql);
        return sql;
    }

    /**
     * 构建需要采集的schema+表名的过滤条件
     *
     * @param listenerTables 需要采集的schema+表名 SCHEMA1.TABLE1,SCHEMA2.TABLE2
     * @return
     */
    private static String buildSchemaTableFilter(String listenerTables, boolean isCdb) {
        List<String> filters = new ArrayList<>();

        String[] tableWithSchemas = listenerTables.split(ConstantValue.COMMA_SYMBOL);
        for (String tableWithSchema : tableWithSchemas) {
            List<String> tables = Arrays.asList(tableWithSchema.split("\\."));
            if (ConstantValue.STAR_SYMBOL.equals(tables.get(0))) {
                throw new IllegalArgumentException(
                        "Must specify the schema to be collected:" + tableWithSchema);
            }

            StringBuilder tableFilterBuilder = new StringBuilder(256);
            if (isCdb && tables.size() == 3) {
                tableFilterBuilder.append(String.format("SRC_CON_NAME='%s' and ", tables.get(0)));
            }

            tableFilterBuilder.append(
                    String.format(
                            "SEG_OWNER='%s'",
                            isCdb && tables.size() == 3 ? tables.get(1) : tables.get(0)));

            if (!ConstantValue.STAR_SYMBOL.equals(
                    isCdb && tables.size() == 3 ? tables.get(2) : tables.get(1))) {
                tableFilterBuilder
                        .append(" and ")
                        .append(
                                String.format(
                                        "TABLE_NAME='%s'",
                                        isCdb && tables.size() == 3
                                                ? tables.get(2)
                                                : tables.get(1)));
            }

            filters.add(String.format("(%s)", tableFilterBuilder));
        }

        return String.format("(%s)", StringUtils.join(filters, " or "));
    }

    public static List<String> EXCLUDE_SCHEMAS = Collections.singletonList("SYS");
    /**
     * 过滤系统表
     *
     * @return
     */
    private static String buildExcludeSchemaFilter() {
        List<String> filters = new ArrayList<>();
        for (String excludeSchema : EXCLUDE_SCHEMAS) {
            filters.add(String.format("SEG_OWNER != '%s'", excludeSchema));
        }

        return String.format("(%s)", StringUtils.join(filters, " and "));
    }

    /**
     * 构建需要采集操作类型字符串的过滤条件
     *
     * @param listenerOptions 需要采集操作类型字符串 delete,insert,update
     * @return
     */
    private static String buildOperationFilter(String listenerOptions) {
        List<String> standardOperations = new ArrayList<>();

        String[] operations = listenerOptions.split(ConstantValue.COMMA_SYMBOL);
        for (String operation : operations) {

            int operationCode;
            switch (operation.toUpperCase()) {
                case "INSERT":
                    operationCode = 1;
                    break;
                case "DELETE":
                    operationCode = 2;
                    break;
                case "UPDATE":
                    operationCode = 3;
                    break;
                default:
                    throw new RuntimeException("Unsupported operation type:" + operation);
            }

            standardOperations.add(String.format("'%s'", operationCode));
        }

        return String.format(
                "OPERATION_CODE in (%s) ",
                StringUtils.join(standardOperations, ConstantValue.COMMA_SYMBOL));
    }

    /** 启动LogMiner */
    public static void startOrUpdateLogMiner(Connection connection, BigInteger startScn, BigInteger endScn) {

        String startSql;
        try {
            /*this.startScn = startScn;
            this.endScn = endScn;
            this.CURRENT_STATE.set(STATE.FILEADDING);*/

            // checkAndResetConnection();

            // 防止没有数据更新的时候频繁查询数据库，限定查询的最小时间间隔 QUERY_LOG_INTERVAL
            /*if (lastQueryTime > 0) {
                long time = System.currentTimeMillis() - lastQueryTime;
                if (time < QUERY_LOG_INTERVAL) {
                    try {
                        Thread.sleep(QUERY_LOG_INTERVAL - time);
                    } catch (InterruptedException e) {
                        LOG.warn("", e);
                    }
                }
            }*/
            // lastQueryTime = System.currentTimeMillis();

           /* if (logMinerConfig.getSupportAutoAddLog()) {
                startSql =
                        oracleInfo.isOracle10()
                                ? SqlUtil.SQL_START_LOG_MINER_AUTO_ADD_LOG_10
                                : SqlUtil.SQL_START_LOG_MINER_AUTO_ADD_LOG;
            } else {*/
                startSql = SqlUtil.SQL_START_LOGMINER;
            // }

            // resetLogminerStmt(startSql);

            /*
            if (logMinerConfig.getSupportAutoAddLog()) {
                logMinerStartStmt.setString(1, startScn.toString());
            } else {
                logMinerStartStmt.setString(1, startScn.toString());
                logMinerStartStmt.setString(2, endScn.toString());
            }

            logMinerStartStmt.execute();
            this.CURRENT_STATE.set(STATE.FILEADDED);*/
            // 查找出加载到logMiner里的日志文件
            // this.addedLogFiles = queryAddedLogFiles();
            /*LOG.info(
                    "Log group changed, startScn = {},endScn = {} new log group = {}",
                    startScn,
                    endScn,
                    GsonUtil.GSON.toJson(this.addedLogFiles));*/
        } catch (Exception e) {
            // this.CURRENT_STATE.set(STATE.FAILED);
            // this.exception = e;
            throw new RuntimeException(e);
        }
    }
}