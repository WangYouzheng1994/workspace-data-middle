package org.test.cdc.oracle;

/**
 * @Description: Oracle Logminer所有的SCN查询动作
 * @Author: WangYouzheng
 * @Date: 2022/8/3 14:31
 * @Version: V1.0
 */
public class SqlUtil {
    // 获取logfile 最小的SCN偏移量
    public static final String SQL_GET_LOG_FILE_START_POSITION =
            "select min(FIRST_CHANGE#) FIRST_CHANGE# from (select FIRST_CHANGE# from v$log union select FIRST_CHANGE# from v$archived_log where standby_dest='NO' and name is not null)";

    // 根据日期获取起始偏移量
    public static final String SQL_GET_LOG_FILE_START_POSITION_BY_TIME =
            "select min(FIRST_CHANGE#) FIRST_CHANGE# from (select FIRST_CHANGE# " +
                    "from v$log " +
                    "where " +
                    "TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS') between FIRST_TIME " +
                    "and NVL(NEXT_TIME, TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS')) " +
                    "union select FIRST_CHANGE# from v$archived_log " +
                    "where TO_DATE(?, 'YYYY-MM-DD HH24:MI:SS') " +
                    "between FIRST_TIME and NEXT_TIME and standby_dest='NO' and name is not null)";

    // 修改当前会话的date日期格式
    public static final String SQL_ALTER_NLS_SESSION_PARAMETERS =
            "ALTER SESSION SET "
                    + "  NLS_DATE_FORMAT = 'YYYY-MM-DD HH24:MI:SS'"
                    + "  NLS_TIMESTAMP_FORMAT = 'YYYY-MM-DD HH24:MI:SS.FF6'"
                    + "  NLS_TIMESTAMP_TZ_FORMAT = 'YYYY-MM-DD HH24:MI:SS.FF6'";

    // ------------------- 初始化阶段的SQL
    public static final String SQL_GET_CURRENT_SCN = "select min(CURRENT_SCN) CURRENT_SCN from gv$database";

    // -------------------
}
