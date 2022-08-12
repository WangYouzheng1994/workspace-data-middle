package org.datamiddle.cdc.oracle;

/**
 * @Description: Oracle Logminer所有的SCN查询动作
 * @Author: WangYouzheng
 * @Date: 2022/8/3 14:31
 * @Version: V1.0
 */
public class SqlUtil {

    // ------------------- 初始化阶段的SQL

    // 修改当前会话的date日期格式
    public static final String SQL_ALTER_NLS_SESSION_PARAMETERS =
            "ALTER SESSION SET "
                    + "  NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI:SS'"
                    + "  NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SS.FF6'"
                    + "  NLS_TIMESTAMP_TZ_FORMAT = 'YYYY-MM-DD HH24:MI:SS.FF6'";

    // 获取logfile 最小的SCN偏移量
    public static final String SQL_GET_LOG_FILE_START_POSITION =
            "select min(FIRST_CHANGE#) FIRST_CHANGE# from (select FIRST_CHANGE# from v$log union select FIRST_CHANGE# from v$archived_log where standby_dest='NO' and name is not null)";

    // 根据日期获取最小的SCN偏移量
    public static final String SQL_GET_LOG_FILE_START_POSITION_BY_TIME =
            "select min(FIRST_CHANGE#) FIRST_CHANGE# from (select FIRST_CHANGE# " +
                    "from v$log " +
                    "where " +
                    "TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS') between FIRST_TIME " +
                    "and NVL(NEXT_TIME, TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS')) " +
                    "union select FIRST_CHANGE# from v$archived_log " +
                    "where TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS') " +
                    "between FIRST_TIME and NEXT_TIME and standby_dest='NO' and name is not null)";

    // 获取当前的SCN偏移量
    public static final String SQL_GET_CURRENT_SCN = "select min(CURRENT_SCN) CURRENT_SCN from gv$database";

    // ------------------- 查询log ------

    public static final String SQL_QUERY_LOG_FILE =
            "SELECT\n"
                    + "    MIN(name) as name,\n"
                    + "    MIN(thread#) as thread#,\n"
                    + "    first_change#,\n"
                    + "    MIN(next_change#) as next_change#,\n"
                    + "    MIN(BYTES) as BYTES,\n"
                    + "    MIN(TYPE) as TYPE\n"
                    + "FROM\n"
                    + "    (\n"
                    + "        SELECT\n"
                    + "            member AS name,\n"
                    + "            thread#,\n"
                    + "            first_change#,\n"
                    + "            next_change#,\n"
                    + "            BYTES,\n"
                    + "            'ONLINE' as TYPE\n"
                    + "        FROM\n"
                    + "            v$log       l\n"
                    + "            INNER JOIN v$logfile   f ON l.group# = f.group#\n"
                    + "        WHERE l.STATUS = 'CURRENT' OR l.STATUS = 'ACTIVE'\n"
                    + "        UNION\n"
                    + "        SELECT\n"
                    + "            name,\n"
                    + "            thread#,\n"
                    + "            first_change#,\n"
                    + "            next_change#,\n"
                    + "            BLOCKS*BLOCK_SIZE as BYTES,\n"
                    + "            'ARCHIVE' as TYPE\n"
                    + "        FROM\n"
                    + "            v$archived_log\n"
                    + "        WHERE\n"
                    + "            name IS NOT NULL\n"
                    + "            AND STANDBY_DEST='NO' \n"
                    + "    )\n"
                    + "WHERE\n"
                    + "    first_change# >= ?\n"
                    + "    OR ? < next_change#\n"
                    + "        GROUP BY\n"
                    + "            first_change#\n"
                    + "ORDER BY\n"
                    + "    first_change#";

    public static final String SQL_QUERY_ARCHIVE_LOG_FILE =
            "SELECT\n"
                    + "    MIN(name) as name,\n"
                    + "    MIN(thread#) as thread#,\n"
                    + "    first_change#,\n"
                    + "    MIN(next_change#) as next_change#,\n"
                    + "    MIN(BYTES) as BYTES,\n"
                    + "    'ARCHIVE' as TYPE\n"
                    + "FROM\n"
                    + "    (\n"
                    + "        SELECT\n"
                    + "            name,\n"
                    + "            thread#,\n"
                    + "            first_change#,\n"
                    + "            next_change#,\n"
                    + "            BLOCKS*BLOCK_SIZE as BYTES\n"
                    + "        FROM\n"
                    + "            v$archived_log\n"
                    + "        WHERE\n"
                    + "            name IS NOT NULL\n"
                    + "            AND STANDBY_DEST='NO' \n"
                    + "    )\n"
                    + "WHERE\n"
                    + "    first_change# >= ?\n"
                    + "    OR ? < next_change#\n"
                    + "        GROUP BY\n"
                    + "            first_change#\n"
                    + "ORDER BY\n"
                    + "    first_change#";

    /**
     * 构建sql v$logmnr_contents 结果
     */
    public static final String SQL_SELECT_DATA =
            ""
                    + "SELECT\n"
                    + "    scn,\n"
                    +
                    // oracle 10 没有该字段
                    //            "    commit_scn,\n" +
                    "    timestamp,\n"
                    + "    operation,\n"
                    + "    operation_code,\n"
                    + "    seg_owner,\n"
                    + "    table_name,\n"
                    + "    sql_redo,\n"
                    + "    sql_undo,\n"
                    + "    xidusn,\n"
                    + "    xidslt,\n"
                    + "    xidsqn,\n"
                    + "    row_id,\n"
                    + "    rollback,\n"
                    + "    csf\n"
                    + "FROM\n"
                    + "    v$logmnr_contents\n"
                    + "WHERE\n"
                    + "    scn >= ? \n"
                    + "    AND scn < ? \n";


    /** 加载包含startSCN和endSCN之间日志的日志文件 */
    public static final String SQL_START_LOGMINER =
            "DECLARE \n"
                    + "    st          BOOLEAN := true;\n"
                    + "    start_scn   NUMBER := ?;\n"
                    + "    endScn   NUMBER := ?;\n"
                    + "BEGIN\n"
                    + "    FOR l_log_rec IN (\n"
                    + "        SELECT\n"
                    + "            MIN(name) name,\n"
                    + "            first_change#\n"
                    + "        FROM\n"
                    + "          (\n"
                    + "            SELECT \n"
                    + "              member AS name, \n"
                    + "              first_change# \n"
                    + "            FROM \n"
                    + "              v$log   l \n"
                    + "           INNER JOIN v$logfile   f ON l.group# = f.group# \n"
                    + "           WHERE (l.STATUS = 'CURRENT' OR l.STATUS = 'ACTIVE' )\n"
                    + "           AND first_change# < endScn \n"
                    + "           UNION \n"
                    + "           SELECT  \n"
                    + "              name, \n"
                    + "              first_change# \n"
                    + "           FROM \n"
                    + "              v$archived_log \n"
                    + "           WHERE \n"
                    + "              name IS NOT NULL \n"
                    + "           AND STANDBY_DEST='NO'\n"
                    + "           AND  first_change# < endScn  AND next_change# > start_scn )group by first_change# order by first_change#  )LOOP IF st THEN \n"
                    + "  SYS.DBMS_LOGMNR.add_logfile(l_log_rec.name, SYS.DBMS_LOGMNR.new); \n"
                    + "      st := false; \n"
                    + "  ELSE \n"
                    + "  SYS.DBMS_LOGMNR.add_logfile(l_log_rec.name); \n"
                    + "  END IF; \n"
                    + "  END LOOP;\n"
                    + "  SYS.DBMS_LOGMNR.start_logmnr(       options =>          SYS.DBMS_LOGMNR.skip_corruption        + SYS.DBMS_LOGMNR.no_sql_delimiter        + SYS.DBMS_LOGMNR.no_rowid_in_stmt\n"
                    + "  + SYS.DBMS_LOGMNR.dict_from_online_catalog    );\n"
                    + "   end;";

    /**
     * 启动logminer，以自动添加日志的模式
     */
    public static final String SQL_START_LOG_MINER_AUTO_ADD_LOG =
            ""
                    + "BEGIN SYS.DBMS_LOGMNR.START_LOGMNR("
                    + "   STARTSCN => ?,"
                    + "   OPTIONS => SYS.DBMS_LOGMNR.SKIP_CORRUPTION "
                    + "       + SYS.DBMS_LOGMNR.NO_SQL_DELIMITER "
                    + "       + SYS.DBMS_LOGMNR.NO_ROWID_IN_STMT "
                    + "       + SYS.DBMS_LOGMNR.DICT_FROM_ONLINE_CATALOG "
                    + "       + SYS.DBMS_LOGMNR.CONTINUOUS_MINE "
                    + "       + SYS.DBMS_LOGMNR.COMMITTED_DATA_ONLY "
                    + "       + SYS.DBMS_LOGMNR.STRING_LITERALS_IN_STMT"
                    + ");"
                    + "END;";

    // 查找加载到logminer的日志文件
    public static final String SQL_QUERY_ADDED_LOG =
            "select filename ,thread_id ,low_scn,next_scn,type,filesize,status,type from  V$LOGMNR_LOGS ";

    public static boolean isCreateTemporaryTableSql(String sql) {
        return sql.contains("temporary tables");
    }
}