package org.test.cdc.oracle;

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

    public static void main(String[] args) {
        System.out.println(SqlUtil.SQL_QUERY_LOG_FILE);

    }
}
