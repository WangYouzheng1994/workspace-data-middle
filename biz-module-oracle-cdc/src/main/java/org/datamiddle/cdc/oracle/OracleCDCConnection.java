package org.datamiddle.cdc.oracle;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.datamiddle.cdc.oracle.bean.LogFile;
import org.datamiddle.cdc.oracle.bean.QueueData;
import org.datamiddle.cdc.oracle.bean.RecordLog;
import org.datamiddle.cdc.oracle.bean.TransactionManager;
import org.datamiddle.cdc.oracle.bean.element.ColumnRowData;
import org.datamiddle.cdc.oracle.bean.element.column.StringColumn;
import org.datamiddle.cdc.oracle.bean.element.column.TimestampColumn;
import org.datamiddle.cdc.oracle.constants.ConstantValue;
import org.jeecgframework.boot.DateUtil;

import java.io.*;
import java.math.BigInteger;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @Description: Oracle CDC连接。 每个实例里面保存了当前JDBC的上下文，以及任务级别的配置，还有cdc任务级别的缓存transactionManager
 * @Author: WangYouzheng
 * @Date: 2022/8/3 15:02
 * @Version: V1.0
 */
@Slf4j
@Data
public class OracleCDCConnection {

    // -------- JDBC Start
    private Connection connection;
    /**
     * 发起数据挖掘结果
     */
    private CallableStatement logMinerStartStmt;
    /**
     * 获取数据挖掘结果
     */
    private PreparedStatement logMinerSelectStmt;
    private ResultSet logMinerData;
    // --------JDBC END

    // 查询logminer的超时时间
    private Long logminerQueryTimeout = 300L;

    // 本连接相关联任务的 启动配置参数
    private OracleCDCConfig config;
    // 本连接加入的logFiles
    private List<LogFile> addedLogFiles = new ArrayList<>();
    // 本连接抽取的scn开始
    private BigInteger startScn = null;
    // 本连接抽取的scn结束
    private BigInteger endScn = null;
    // 本次任务抽取相关联的缓存。（持有的是任务级别的，非连接级别）
    private TransactionManager transactionManager;
    /** 为delete类型的rollback语句查找对应的insert语句的connection */
    private OracleCDCConnection queryDataForRollbackConnection;
    private List<QueueData> result;
    //重新启动异常标识
    private int identification=0;
    //V$LOGMNR_CONTENTS 的唯一值
    private String  rs_id;
    //记录当前要执行的日志
    private LogFile LogFileExecute=null;
    public OracleCDCConnection() {
    }

    public OracleCDCConnection(OracleCDCConfig config, TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
        this.config = config;
    }

    /**
     * 获取连接，并且设置好字符集
     *
     * @return
     */
    public boolean getConnection(OracleCDCConfig config) {
        int interval = 1;
        log.debug("connection driver class: {}", config.getDriverClass());
        log.info("connection user: {}", config.getUsername());
        log.info("connection password: {}", config.getPassword());

        // 加载驱动
        try {
            Class.forName(config.getDriverClass());
        } catch (ClassNotFoundException e) {
            log.error(e.getMessage(), e);
            return false;
        }

        do {
            try {
                connection = DriverManager.getConnection(config.getJdbcUrl(), config.getUsername(), config.getPassword());
                interval = 5;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                closeResources(null, null, connection);
                interval++;
            }
        } while(interval < 3);

        interval = 1;
        // 设置编码和日期类型

        // 获取当前Oracle的字符集。

        do {
            try (PreparedStatement preparedStatement =
                         connection.prepareStatement(SqlUtil.SQL_ALTER_NLS_SESSION_PARAMETERS)) {
                // preparedStatement.setQueryTimeout(logMinerConfig.getQueryTimeout().intValue());
                preparedStatement.execute();
                interval = 5;
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                closeResources(null, null, connection);
                interval++;
            }
        } while(interval < 3);

        boolean result = false;
        if (connection == null) {
            result = false;
        } else {
            result = true;
        }
        return result;
    }

    /** 关闭数据库连接资源 */
    public void closeResources(ResultSet rs, Statement stmt, Connection conn) {
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
    private void closeStmt(Statement statement) {
        try {
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }
        } catch (SQLException e) {
            log.warn("Close statement error", e);
        }
    }

    /** 重置 启动logminer的statement */
    public  void resetLogminerStmt(String startSql) throws SQLException {
        closeStmt(logMinerStartStmt);
        logMinerStartStmt = connection.prepareCall(startSql);
        // configStatement(logMinerStartStmt);
    }

    /**
     * 获取当前SCN
     *
     * @param connection
     * @return
     */
    public BigInteger getCurrentScn(Connection connection) {
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
    public BigInteger getLogFileStartPositionByTime(Connection connection, Long startTime) {
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
    public BigInteger getMinScn(Connection connection) {
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
     * 根据leftScn 以及加载的日志大小限制 获取可加载的scn范围 以及此范围对应的日志文件
     *
     * @param connection
     * @param startScn
     * @param logFiles
     * @return
     * @throws SQLException
     */
    public Pair<BigInteger, Boolean> getEndScn(Connection connection, BigInteger startScn, List<LogFile> logFiles)
            throws SQLException {
        return getEndScn(connection, startScn, logFiles, true); // 这里设置成true的原因是没有去确认是否是加载归档到头了。如果要实现持续的增量获取 就得去读online redoLog
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
    public Pair<BigInteger, Boolean> getEndScn(Connection connection, BigInteger startScn, List<LogFile> logFiles, boolean addRedoLog) throws SQLException {

        List<LogFile> logFileLists = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet rs = null;
        try {
            String sql;
            if (addRedoLog) {
                // 同时查询redo与archivev
                sql = SqlUtil.SQL_QUERY_LOG_FILE;
            } else {
                // 只查询归档
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

        // 根据线程组分组
        Map<Long, List<LogFile>> map =
                logFileLists.stream().collect(Collectors.groupingBy(LogFile::getThread));

        // 对每一个线程组的文件按照起始偏移量进行升序排序
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
            // 找到最小的nextSCN 结束偏移量
            BigInteger minNextScn=null;
            //最小偏移量的数据
            LogFile logFile = tempList.stream()
                    .sorted(Comparator.comparing(LogFile::getNextChange))
                    .collect(Collectors.toList())
                    .get(0);
            //赋值
            LogFileExecute = logFile;
            minNextScn = logFile.getNextChange();

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

        // 如果加载到了重做日志，需要把currentSCN 作为当前连接的 endSCN 因为此scn只能去数据库的当前状态获取，无法通过log list获取到。原因是还在增量走。
        if (loadRedoLog) {
            // 解决logminer偶尔丢失数据问题，读取online日志的时候，需要将rightScn置为当前SCN
            endScn = getCurrentScn(connection);
            logFiles = logFileLists;
        }

        if (CollectionUtils.isEmpty(logFiles)) {
            return Pair.of(null, loadRedoLog);
        }
        //writeTxtFG("读取日志："+LogFileExecute.getFirstChange()+"|"+LogFileExecute.getNextChange()+"|"+LogFileExecute.getFileName(),"D://cdc/aaa.txt");
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
    public String buildSelectSql(
            String listenerOptions, String listenerTables, boolean isCdb) {
        StringBuilder sqlBuilder = new StringBuilder(SqlUtil.SQL_SELECT_DATA);
        sqlBuilder.append(" WHERE ");
        if (StringUtils.isNotEmpty(listenerTables)) {
            sqlBuilder.append("  ( ").append(buildSchemaTableFilter(listenerTables, isCdb));
        } else {
            sqlBuilder.append("  ( ").append(buildExcludeSchemaFilter());
        }
        //判断异常类型
        if(this.identification==4||this.identification==1){
            sqlBuilder.append(" and ").append(" rs_id > '"+this.rs_id+"'");
        }
        if (StringUtils.isNotEmpty(listenerOptions)) {
            sqlBuilder.append(" and ").append(buildOperationFilter(listenerOptions));
        }

        // 包含commit
        sqlBuilder.append(" or OPERATION_CODE = 7 )");
        String sql = sqlBuilder.toString();
        log.debug("SelectSql = {}", sql);
        return sql;
    }

    /**
     * 构建需要采集的schema+表名的过滤条件
     *
     * @param listenerTables 需要采集的schema+表名 SCHEMA1.TABLE1,SCHEMA2.TABLE2
     * @return
     */
    private String buildSchemaTableFilter(String listenerTables, boolean isCdb) {
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
    private String buildExcludeSchemaFilter() {
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
    private String buildOperationFilter(String listenerOptions) {
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

    /**
     * 启动logminer 挖掘，并且把任务上的开始与结束scn 传递给当前 jdbc连接。
     * @param connection
     * @param startScn
     * @param endScn
     * @param autoaddLog
     */
    public void startOrUpdateLogMiner(OracleCDCConnection connection, BigInteger startScn, BigInteger endScn, boolean autoaddLog,BigInteger currentSinkPosition) {

        String startSql;
        try {
            // 任务偏移量赋值
            this.startScn = startScn;
            this.endScn = endScn;
            // TODO：防止没有数据更新的时候频繁查询数据库，限定查询的最小时间间隔 QUERY_LOG_INTERVAL

            if (autoaddLog) {
                startSql = SqlUtil.SQL_START_LOG_MINER_AUTO_ADD_LOG;
            } else {
                startSql = SqlUtil.SQL_LOGMINER_DEBEZIUM;
            }

            // 重置，清空preparestatement，重新简历logminer连接
            connection.resetLogminerStmt(startSql);

            // 这个auto 不是很明白区别
            if (autoaddLog) {
                logMinerStartStmt.setString(1, this.startScn.toString());
            } else {
                logMinerStartStmt.setString(1, this.startScn.toString());
                logMinerStartStmt.setString(2, this.endScn.toString());
                //logMinerStartStmt.setString(3, this.LogFileExecute.getFileName());
            }

            logMinerStartStmt.execute();
            // 查找出加载到logMiner里的日志文件
            this.addedLogFiles = queryAddedLogFiles();
        } catch (Exception e) {
            //当出现异常的时候 停止程序 并将之前的scn 持久化
            //writeTxtFG("断点续传记录SCN，开始SCN="+startScn+",消费SCN="+currentSinkPosition+"Identification = 2","D://cdc/mysqlRecord.txt");
            // this.CURRENT_STATE.set(STATE.FAILED);
            // this.exception = e;
            throw new RuntimeException(e);
        }
    }

    /** 获取logminer加载的日志文件 */
    private List<LogFile> queryAddedLogFiles() throws SQLException {
        List<LogFile> logFileLists = new ArrayList<>();
        try (PreparedStatement statement =
                     connection.prepareStatement(SqlUtil.SQL_QUERY_ADDED_LOG)) {
            statement.setQueryTimeout(logminerQueryTimeout.intValue());
            try (ResultSet rs = statement.executeQuery()) {
                LogFile logFile = null;
                while (rs.next()) {
                    logFile = new LogFile();
                    logFile.setFileName(rs.getString("filename"));
                    logFile.setFirstChange(new BigInteger(rs.getString("low_scn")));
                    logFile.setNextChange(new BigInteger(rs.getString("next_scn")));
                    logFile.setThread(rs.getLong("thread_id"));
                    logFile.setBytes(rs.getLong("filesize"));
                    logFile.setStatus(rs.getInt("status"));
                    logFile.setType(rs.getString("type"));
                    logFileLists.add(logFile);
                }
            }
        }
        return logFileLists;
    }

    /** 从LogMiner视图查询数据 */
    public boolean queryData(String logMinerSelectSql,BigInteger startScnPosition,BigInteger currentSinkPosition) {

        try {
/*
            this.CURRENT_STATE.set(STATE.LOADING);
            checkAndResetConnection();*/

            closeStmt(logMinerSelectStmt);
            logMinerSelectStmt =
                    connection.prepareStatement(
                            logMinerSelectSql,
                            ResultSet.TYPE_FORWARD_ONLY,
                            ResultSet.CONCUR_READ_ONLY);
            // configStatement(logMinerSelectStmt);

            // 3000一批次。
            logMinerSelectStmt.setFetchSize(10000);
            //logMinerSelectStmt.setString(1, startScn.toString());
            //logMinerSelectStmt.setString(2, endScn.toString());
            long before = System.currentTimeMillis();
           /* if(("83743364".equals(startScn.toString()))){
                //scn大于固定值
                int a = 1;
                int b = a/0;
            }*/
            // 抽取出解析到的 lgminerData
            logMinerData = logMinerSelectStmt.executeQuery();

            // this.CURRENT_STATE.set(STATE.READABLE);
            // long timeConsuming = (System.currentTimeMillis() - before) / 1000;
            // LOG.info(
            //         "query LogMiner data, startScn:{},endScn:{},timeConsuming {}",
            //         startScn,
            //         endScn,
            //         timeConsuming);
            return true;
        } catch (Exception e) {
            // this.CURRENT_STATE.set(STATE.FAILED);
            // this.exception = e;
            // String message =
            //         String.format(
            //                 "query logMiner data failed, sql:[%s], e: %s",
            //                 logMinerSelectSql, ExceptionUtil.getErrorMessage(e));
            //writeTxtFG("断点续传记录SCN，开始SCN="+startScnPosition+",消费SCN="+currentSinkPosition+"Identification = 3","D://cdc/mysqlRecord.txt");
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 从当前的ResultSet读取数据
     *
     * @return
     * @throws SQLException
     * @throws UnsupportedEncodingException
     */
    public boolean hasNext() throws SQLException, UnsupportedEncodingException {
/*
            TODO: 任务的状态控制
            if (null == logMinerData
                || logMinerData.isClosed()
                || this.CURRENT_STATE.get().equals(STATE.READEND)) {
            this.CURRENT_STATE.set(STATE.READEND);
            return false;
        }*/
        result = new ArrayList<>();
        // 从JDBC ResultSEt中获取数据并解析多行SQL。如果解析到了以后 此方法返回True，并且把解析后的数据存放到QueueData中。
        String sqlLog;
        while (logMinerData.next()) {
            String sql = logMinerData.getString(LogminerKeyConstants.KEY_SQL_REDO);
            if (StringUtils.isBlank(sql)) {
                continue;
            }
            StringBuilder sqlRedo = new StringBuilder(sql);
            StringBuilder sqlUndo =
                    new StringBuilder(
                            Objects.nonNull(logMinerData.getString(LogminerKeyConstants.KEY_SQL_UNDO))
                                    ? logMinerData.getString(LogminerKeyConstants.KEY_SQL_UNDO)
                                    : "");
            // 临时表没有日志。
            if (SqlUtil.isCreateTemporaryTableSql(sqlRedo.toString())) {
                continue;
            }
            BigInteger scn = new BigInteger(logMinerData.getString(LogminerKeyConstants.KEY_SCN));
            String operation = logMinerData.getString(LogminerKeyConstants.KEY_OPERATION);
            if(StringUtils.isNoneBlank(operation)){
                operation=operation.toLowerCase();
            }
            String RS_ID = logMinerData.getString(LogminerKeyConstants.RS_ID);
            int operationCode = logMinerData.getInt(LogminerKeyConstants.KEY_OPERATION_CODE);
            String tableName = logMinerData.getString(LogminerKeyConstants.KEY_TABLE_NAME);

            boolean hasMultiSql;

            String xidSqn = logMinerData.getString(LogminerKeyConstants.KEY_XID_SQN);
            String xidUsn = logMinerData.getString(LogminerKeyConstants.KEY_XID_USN);
            String xidSLt = logMinerData.getString(LogminerKeyConstants.KEY_XID_SLT);
            String rowId = logMinerData.getString(LogminerKeyConstants.KEY_ROW_ID);
            boolean rollback = logMinerData.getBoolean(LogminerKeyConstants.KEY_ROLLBACK);

            // 操作类型为commit，清理缓存
            if (operationCode == 7) {
                transactionManager.cleanCache(xidUsn, xidSLt, xidSqn);
                continue;
            }

            // 用CSF来判断一条sql是在当前这一行结束，sql超过4000 字节，会处理成多行
            boolean isSqlNotEnd = logMinerData.getBoolean(LogminerKeyConstants.KEY_CSF);
            // 是否存在多条SQL
            hasMultiSql = isSqlNotEnd;

            // 如果出现了多行sql。那么就继续向后面迭代。
            while (isSqlNotEnd) {
                logMinerData.next();
                // redoLog 实际上不需要发生切割  但是sqlUndo发生了切割，导致redolog值为null
                String sqlRedoValue = logMinerData.getString(LogminerKeyConstants.KEY_SQL_REDO);
                if (Objects.nonNull(sqlRedoValue)) {
                    sqlRedo.append(sqlRedoValue);
                }

                String sqlUndoValue = logMinerData.getString(LogminerKeyConstants.KEY_SQL_UNDO);
                if (Objects.nonNull(sqlUndoValue)) {
                    sqlUndo.append(sqlUndoValue);
                }
                isSqlNotEnd = logMinerData.getBoolean(LogminerKeyConstants.KEY_CSF);
            }

            // delete from "table"."ID" where ROWID = 'AAADcjAAFAAAABoAAC' delete语句需要rowid条件需要替换
            // update "schema"."table" set "ID" = '29' 缺少where条件
            if (rollback && (operationCode == 2 || operationCode == 3)) {
                StringBuilder undoLog = new StringBuilder(1024);

                // 从缓存里查找rollback对应的DML语句
                RecordLog recordLog =
                        transactionManager.queryUndoLogFromCache(
                                xidUsn, xidSLt, xidSqn, rowId, scn);

                if (Objects.isNull(recordLog)) {
                    // 如果DML语句不在缓存 或者 和rollback不再同一个日志文件里 会递归从日志文件里查找，这个步骤其实就是一种兜底，万一任务重启了 缓存肯定是没有数据的。
                    recordLog =
                            recursionQueryDataForRollback(
                                    new RecordLog(
                                            scn,
                                            "",
                                            "",
                                            xidUsn,
                                            xidSLt,
                                            xidSqn,
                                            rowId,
                                            operationCode,
                                            false,
                                            logMinerData.getString(LogminerKeyConstants.KEY_TABLE_NAME)));
                }

                if (Objects.nonNull(recordLog)) {
                    RecordLog rollbackLog =
                            new RecordLog(
                                    scn,
                                    sqlUndo.toString(),
                                    sqlRedo.toString(),
                                    xidUsn,
                                    xidSLt,
                                    xidSqn,
                                    rowId,
                                    operationCode,
                                    hasMultiSql,
                                    tableName);
                    String rollbackSql = getRollbackSql(rollbackLog, recordLog);
                    undoLog.append(rollbackSql);
                    hasMultiSql = recordLog.getHasMultiSql();
                }

                if (undoLog.length() == 0) {
                    // 没有查找到对应的insert语句 会将delete where rowid=xxx 语句作为redoLog
                    log.warn("has not found undoLog for scn {}", scn);
                } else {
                    sqlRedo = undoLog;
                }
                log.debug(
                        "find rollback sql,scn is {},rowId is {},xisSqn is {}", scn, rowId, xidSqn);
            }

            // oracle10中文编码且字符串大于4000，LogMiner可能出现中文乱码导致SQL解析异常
            // if (hasMultiSql && oracleInfo.isOracle10() && oracleInfo.isGbk()) {
            //     String redo = sqlRedo.toString();
            //     String hexStr = new String(Hex.encodeHex(redo.getBytes("GBK")));
            //     boolean hasChange = false;
            //
            //     // delete 条件不以'结尾 如 where id = '1 以?结尾的需要加上'
            //     if (operationCode == 2 && hexStr.endsWith("3f")) {
            //         LOG.info(
            //                 "current scn is: {},\noriginal redo sql is: {},\nhex redo string is: {}",
            //                 scn,
            //                 redo,
            //                 hexStr);
            //         hexStr = hexStr + "27";
            //         hasChange = true;
            //     }
            //
            //     if (operationCode == 1 && hexStr.contains("3f2c")) {
            //         LOG.info(
            //                 "current scn is: {},\noriginal redo sql is: {},\nhex redo string is: {}",
            //                 scn,
            //                 redo,
            //                 hexStr);
            //         hasChange = true;
            //         hexStr = hexStr.replace("3f2c", "272c");
            //     }
            //     if (operationCode != 1) {
            //         if (hexStr.contains("3f20616e64")) {
            //             LOG.info(
            //                     "current scn is: {},\noriginal redo sql is: {},\nhex redo string is: {}",
            //                     scn,
            //                     redo,
            //                     hexStr);
            //             hasChange = true;
            //             // update set "" = '' and "" = '' where "" = '' and "" = '' where后可能存在中文乱码
            //             // delete from where "" = '' and "" = '' where后可能存在中文乱码
            //             // ?空格and -> '空格and
            //             hexStr = hexStr.replace("3f20616e64", "2720616e64");
            //         }
            //
            //         if (hexStr.contains("3f207768657265")) {
            //             LOG.info(
            //                     "current scn is: {},\noriginal redo sql is: {},\nhex redo string is: {}",
            //                     scn,
            //                     redo,
            //                     hexStr);
            //             hasChange = true;
            //             // ? where 改为 ' where
            //             hexStr = hexStr.replace("3f207768657265", "27207768657265");
            //         }
            //     }
            //
            //     if (hasChange) {
            //         sqlLog = new String(Hex.decodeHex(hexStr.toCharArray()), "GBK");
            //         LOG.info("final redo sql is: {}", sqlLog);
            //     } else {
            //         sqlLog = sqlRedo.toString();
            //     }
            // } else {
                sqlLog = sqlRedo.toString();
            // }

            String schema = logMinerData.getString(LogminerKeyConstants.KEY_SEG_OWNER);
            Timestamp timestamp = logMinerData.getTimestamp(LogminerKeyConstants.KEY_TIMESTAMP);

            ColumnRowData columnRowData = new ColumnRowData(5);
            columnRowData.addField(new StringColumn(schema));
            columnRowData.addHeader("schema");

            columnRowData.addField(new StringColumn(tableName));
            columnRowData.addHeader("tableName");

            columnRowData.addField(new StringColumn(operation));
            columnRowData.addHeader("operation");

            columnRowData.addField(new StringColumn(sqlLog));
            columnRowData.addHeader("sqlLog");

            columnRowData.addField(new TimestampColumn(timestamp));
            columnRowData.addHeader("opTime");

            QueueData queueData = new QueueData(scn, columnRowData,RS_ID);
            result.add(queueData);

            // 只有非回滚的insert update解析的数据放入缓存
            if (!rollback) {
                transactionManager.putCache(
                        new RecordLog(
                                scn,
                                sqlUndo.toString(),
                                sqlLog,
                                xidUsn,
                                xidSLt,
                                xidSqn,
                                rowId,
                                operationCode,
                                hasMultiSql,
                                tableName));
            }
            //return true;
        }

        // this.CURRENT_STATE.set(STATE.READEND); TODO: 任务控制
        return true;
    }

    // --------------- 处理CDC日志解析的动作

    /**
     * 回滚语句根据对应的dml日志找出对应的undoog
     *
     * @param rollbackLog 回滚语句
     * @param dmlLog 对应的dml语句
     */
    public String getRollbackSql(RecordLog rollbackLog, RecordLog dmlLog) {
        // 如果回滚日志是update，则其where条件没有 才会进入
        if (rollbackLog.getOperationCode() == 3 && dmlLog.getOperationCode() == 3) {
            return dmlLog.getSqlUndo();
        }

        // 回滚日志是delete
        // delete回滚两种场景 如果客户表字段存在blob字段且插入时blob字段为空 此时会出现insert emptyBlob语句以及一个update语句之外
        // 之后才会有一个delete语句，而此delete语句rowid对应的上面update语句 所以直接返回delete from "table"."ID" where ROWID =
        // 'AAADcjAAFAAAABoAAC'（blob不支持）
        if (rollbackLog.getOperationCode() == 2 && dmlLog.getOperationCode() == 1) {
            return dmlLog.getSqlUndo();
        }
        log.warn("dmlLog [{}]  is not hit for rollbackLog [{}]", dmlLog, rollbackLog);
        return "";
    }

    /**
     * 递归查找 delete的rollback对应的insert语句 TODO: 未完成。
     *
     * @param rollbackRecord rollback参数
     * @return insert语句
     */
    public RecordLog recursionQueryDataForRollback(RecordLog rollbackRecord)
            throws SQLException, UnsupportedEncodingException {
        if (Objects.isNull(queryDataForRollbackConnection)) {
            queryDataForRollbackConnection =
                    new OracleCDCConnection(config, transactionManager);
        }

        if (Objects.isNull(queryDataForRollbackConnection.connection)
                || queryDataForRollbackConnection.connection.isClosed()) {
            // LOG.info("queryDataForRollbackConnection start connect");
            queryDataForRollbackConnection.getConnection();
        }

        // 查找出当前加载归档日志文件里的最小scn  递归查找此scn之前的文件
        List<LogFile> logFiles =
                queryAddedLogFiles().stream()
                        .filter(
                                i ->
                                        i.getStatus() != 4
                                                && i.getType().equalsIgnoreCase("ARCHIVED")) // 归档日志
                        .collect(Collectors.toList());

        // 默认每次往前查询4000个scn
        BigInteger step = new BigInteger("4000");
        if (CollectionUtils.isNotEmpty(logFiles)) {
            // nextChange-firstChange 为一个文件包含多少的scn，将其*2 代表加载此scn之前2个文件
            step =
                    logFiles.get(0)
                            .getNextChange()
                            .subtract(logFiles.get(0).getFirstChange())
                            .multiply(new BigInteger("2"));
        }

        BigInteger startScn = rollbackRecord.getScn().subtract(step);
        BigInteger endScn = rollbackRecord.getScn();

       /* for (int i = 0; i < 2; i++) {
            queryDataForRollbackConnection.startOrUpdateLogMiner(startScn, endScn);
            queryDataForRollbackConnection.queryDataForDeleteRollback(
                    rollbackRecord, SqlUtil.queryDataForRollback);
            // while循环查找所有数据 并都加载到缓存里去
            while (queryDataForRollbackConnection.hasNext()) {}
            // 从缓存里取
            RecordLog dmlLog =
                    transactionManager.queryUndoLogFromCache(
                            rollbackRecord.getXidUsn(),
                            rollbackRecord.getXidSlt(),
                            rollbackRecord.getXidSqn(),
                            rollbackRecord.getRowId(),
                            rollbackRecord.getScn());
            if (Objects.nonNull(dmlLog)) {
                return dmlLog;
            }
            endScn = startScn;
            startScn = startScn.subtract(step);
        }*/
        return null;
    }

    /**
     * 获取元数据信息，用以对应log日志的转化。
     *
     * @param cataLog
     * @param schema
     * @param tableName
     * @param dbConn
     * @return 列名称集合， 列类型结合
     */
    public static Pair<List<String>, List<String>> getTableMetaData(
            String cataLog, String schema, String tableName, Connection dbConn) {
        ResultSet tableRs= null;
        ResultSet rs = null;
        try {
            // check table exists
            if ("*".equalsIgnoreCase(tableName.trim())) {
                return Pair.of(new LinkedList<>(), new LinkedList<>());
            }

            tableRs = dbConn.getMetaData().getTables(cataLog, schema, tableName, null);
            if (!tableRs.next()) {
                String tableInfo = schema == null ? tableName : schema + "." + tableName;
                throw new RuntimeException(String.format("table %s not found.", tableInfo));
            }

            rs = dbConn.getMetaData().getColumns(cataLog, schema, tableName, null);
            List<String> fullColumnList = new LinkedList<>();
            List<String> fullColumnTypeList = new LinkedList<>();
            while (rs.next()) {
                // COLUMN_NAME
                fullColumnList.add(rs.getString(4));
                // TYPE_NAME
                fullColumnTypeList.add(rs.getString(6));
            }
            return Pair.of(fullColumnList, fullColumnTypeList);
        } catch (SQLException e) {
            throw new RuntimeException(
                    String.format("error to get meta from [%s.%s]", schema, tableName), e);
        }finally {
            try {
                tableRs.close();
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /***
     *  测试 将内容写入到文件
     */
    private void writeTxtFG(String datastr,String diststr){
        FileWriter fw = null;
        try {
            //如果文件存在，则追加内容；如果文件不存在，则创建文件
            File f = new File(diststr);
            fw = new FileWriter(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(datastr);
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
   //初始化init All
    public void initOracleAllDataConnect(Connection connection, BigInteger currentMaxScn, KafkaProducer<Object, Object> producer,String KafkaTopicName,String schema,String tableName) {
        PreparedStatement preparedStatement = null;
        ResultSet rs= null;
        try {
            String sql = "SELECT * FROM "+tableName+" AS OF SCN "+currentMaxScn;
            preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            preparedStatement.setFetchSize(10000);
            //增加参数
            //preparedStatement.setString(1,tableName);
            //preparedStatement.setInt(2,currentMaxScn.intValue());
            //执行
            log.info("表："+tableName+",开始查询");
            rs= preparedStatement.executeQuery();
            log.info("表："+tableName+",查询结束");
            //检索列名列表
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int count = rsMetaData.getColumnCount();
            //封装数据，如果是date类型额外处理
            List<String>  columnList = new ArrayList<>();
            List<String>  typeList = new ArrayList<>();
            for(int i = 1; i<=count; i++) {
                typeList.add(rsMetaData.getColumnTypeName(i));
                columnList.add(rsMetaData.getColumnName(i));
            }
            //取数据
            //int j=0;
            Map<String,String> map = null;
            Map<String,Object> kafkaData = new HashMap<>();
            while(rs.next()) {
                //System.out.println(j++);
                map= new HashMap<>();
                //遍历拼接数据
                for (int i =0;i<columnList.size();i++) {
                    String colum = columnList.get(i);
                    String value = rs.getString(colum);
                    //判断如果是日期类型
                    if("DATE".equals(typeList.get(i))){
                        //格式化时间
                        if(StringUtils.isNoneBlank(value))
                         value = dateToStamp(value);
                    }
                    if("TIMESTAMP".equals(typeList.get(i))){
                        //格式化时间
                        if(StringUtils.isNoneBlank(value))
                            value = dateToStamp(value);
                    }
                    map.put(colum,value);
                }
                //封装kafkaDate数据
                kafkaData.put("after",map);
                kafkaData.put("before","");
                kafkaData.put("database",schema);
                kafkaData.put("scn",currentMaxScn);
                kafkaData.put("tableName",tableName.split("\\.")[1]);
                kafkaData.put("ts",System.currentTimeMillis());
                kafkaData.put("type","insert");
                //json格式输出数据
                String kafkaStr = JSON.toJSONString(kafkaData);
                //推送kafka
                producer.send(new ProducerRecord<>(KafkaTopicName, kafkaStr));
            }
        } catch (SQLException e) {
            log.info("初始化数据ing失败");
            e.printStackTrace();
        }finally {
            try {
                preparedStatement.close();
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }
}