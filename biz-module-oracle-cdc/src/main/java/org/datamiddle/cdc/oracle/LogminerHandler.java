package org.datamiddle.cdc.oracle;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.update.Update;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.table.data.RowData;
import org.datamiddle.cdc.oracle.bean.LogData;
import org.datamiddle.cdc.oracle.converter.AbstractCDCRowConverter;
import org.datamiddle.cdc.oracle.converter.oracle.LogminerConverter;
import org.jeecgframework.boot.DateUtil;
import org.jeecgframework.boot.IdWorker;
import org.datamiddle.cdc.oracle.bean.EventRow;
import org.datamiddle.cdc.oracle.bean.EventRowData;
import org.datamiddle.cdc.oracle.bean.QueueData;
import org.datamiddle.cdc.oracle.bean.element.ColumnRowData;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: Oracle Logminer 日志处理器
 * @Author: WangYouzheng
 * @Date: 2022/8/11 11:31
 * @Version: V1.0
 */
@Slf4j
public class LogminerHandler {

    public static IdWorker idWorker = new IdWorker(1, 1, 1);
    // public static SnowflakeIdWorker idWorker = new SnowflakeIdWorker(1, 1);

/*    private final LogMinerConf config;

    public LogParser(LogMinerConf config) {
        this.config = config;
    }*/

    private static String cleanString(String str) {
        if ("NULL".equalsIgnoreCase(str)) {
            return null;
        }

        if (str.startsWith("TIMESTAMP")) {
            str = str.replace("TIMESTAMP ", "");
        }

        if (str.startsWith("'") && str.endsWith("'") && str.length() != 1) {
            str = str.substring(1, str.length() - 1);
        }

        if (str.startsWith("\"") && str.endsWith("\"") && str.length() != 1) {
            str = str.substring(1, str.length() - 1);
        }
         //生产数据会有很多带空格的数据  不能trim（）
       // return str.replace("IS NULL", "= NULL").trim();
        return str.replace("IS NULL", "= NULL");
    }

    private static void parseInsertStmt(
            Insert insert, ArrayList<EventRowData> beforeData, ArrayList<EventRowData> afterData) {
        ArrayList<String> columnLists = new ArrayList<>();
        for (Column column : insert.getColumns()) {
            columnLists.add(cleanString(column.getColumnName()));
        }

        ExpressionList eList = (ExpressionList) insert.getItemsList();
        List<Expression> valueList = eList.getExpressions();
        int i = 0;
        for (String key : columnLists) {
            String value = cleanString(valueList.get(i).toString());
            String newValue = null;
            if(StringUtils.isBlank(value)){
                newValue = value;
            }else{
                newValue =parseTime(value);
            }
            afterData.add(new EventRowData(key, newValue, Objects.isNull(newValue)));
            beforeData.add(new EventRowData(key, null, true));
            i++;
        }
    }

    private static void parseUpdateStmt(
            Update update,
            ArrayList<EventRowData> beforeData,
            ArrayList<EventRowData> afterData,
            String sqlRedo) {
        Iterator<Expression> iterator = update.getExpressions().iterator();
        HashSet<String> columns = new HashSet<>(32);
        for (Column c : update.getColumns()) {
            String valueold = cleanString(iterator.next().toString());
            String newValue = null;
            if(StringUtils.isBlank(valueold)){
                newValue = valueold;
            }else{
                newValue =parseTime(valueold);
            }
            String columnName = cleanString(c.getColumnName());
            boolean isNull = Objects.isNull(newValue) || newValue.equalsIgnoreCase("= NULL");

            afterData.add(new EventRowData(columnName, isNull ? null : newValue, isNull));
            columns.add(columnName);
        }

        if (update.getWhere() != null) {
            update.getWhere()
                    .accept(
                            new ExpressionVisitorAdapter() {
                                @Override
                                public void visit(final EqualsTo expr) {
                                    String col = cleanString(expr.getLeftExpression().toString());
                                    String valueold =
                                            cleanString(expr.getRightExpression().toString());
                                    String newValue = null;
                                    if(StringUtils.isBlank(valueold)){
                                        newValue = valueold;
                                    }else{
                                        newValue =parseTime(valueold);
                                    }
                                    boolean isNull =
                                            Objects.isNull(newValue)
                                                    || newValue.equalsIgnoreCase("= NULL");
                                    beforeData.add(
                                            new EventRowData(col, isNull ? null : newValue, isNull));
                                    if (!columns.contains(col)) {
                                        afterData.add(
                                                new EventRowData(
                                                        col, isNull ? null : newValue, isNull));
                                    }
                                }
                            });
        } else {
            log.error(
                    "where is null when LogParser parse sqlRedo, sqlRedo = {}, update = {}",
                    sqlRedo,
                    update.toString());
        }
    }

    private static void parseDeleteStmt(
            Delete delete, ArrayList<EventRowData> beforeData, ArrayList<EventRowData> afterData) {
        delete.getWhere()
                .accept(
                        new ExpressionVisitorAdapter() {
                            @Override
                            public void visit(final EqualsTo expr) {
                                String col = cleanString(expr.getLeftExpression().toString());
                                String valueold = cleanString(expr.getRightExpression().toString());
                                String newValue = null;
                                if(StringUtils.isBlank(valueold)){
                                    newValue = valueold;
                                }else{
                                    newValue =parseTime(valueold);
                                }
                                boolean isNull =
                                        Objects.isNull(newValue) || newValue.equalsIgnoreCase("= NULL");
                                beforeData.add(
                                        new EventRowData(col, isNull ? null : newValue, isNull));
                                afterData.add(new EventRowData(col, null, true));
                            }
                        });
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
    /**
     * parse time type data
     *
     * @param value
     * @return
     */
    public static String parseTime(String value) {
        if (!value.endsWith("')")) {
            return value;
        }
        //增加转换为时间戳
        // DATE类型
        if (value.startsWith("TO_DATE('")) {
            return  dateToStamp(value.substring(9, value.length() - 27));
        }
        ////增加转换为时间戳
        // TIMESTAMP类型
        if (value.startsWith("TO_TIMESTAMP('")) {
            return dateToStamp(value.substring(14, value.length() - 2));
        }
        ////增加转换为时间戳
        // TIMESTAMP类型
        if (value.startsWith("TIMESTAMP '")) {
            return dateToStamp(value.substring(12, value.length() - 1));
        }

        // TIMESTAMP WITH LOCAL TIME ZONE
        if (value.startsWith("TO_TIMESTAMP_ITZ('")) {
            return value.substring(18, value.length() - 2);
        }

        // TIMESTAMP WITH TIME ZONE 类型
        if (value.startsWith("TO_TIMESTAMP_TZ('")) {
            return value.substring(17, value.length() - 2);
        }
        return value;
    }

    public static String parseString(String value) {
        if (!value.endsWith("')")) {
            return value;
        }

        // BLOB/CLOB类型 HEXTORAW('1234')
        if (value.startsWith("HEXTORAW('")) {
            try {
                return new String(
                        Hex.decodeHex(value.substring(10, value.length() - 2).toCharArray()),
                        StandardCharsets.UTF_8);
            } catch (Exception e) {
                throw new RuntimeException("parse value [" + value + " ] failed ", e);
            }
        }

        // INTERVAL YEAR(2) TO MONTH
        if (value.startsWith("TO_YMINTERVAL('") && value.endsWith("')")) {
            return value.substring(15, value.length() - 2);
        }

        // INTERVAL DAY(2) TO SECOND(6)
        if (value.startsWith("TO_DSINTERVAL('") && value.endsWith("')")) {
            return value.substring(15, value.length() - 2);
        }

        return value;
    }

    public static LogData parse(QueueData pair, AbstractCDCRowConverter rowConverter)
            throws Exception {
        ColumnRowData logData = (ColumnRowData) pair.getData();

        String schema = logData.getField("schema").asString();
        String tableName = logData.getField("tableName").asString();
        String operation = logData.getField("operation").asString();
        String sqlLog = logData.getField("sqlLog").asString();
        String sqlRedo = sqlLog.replace("IS NULL", "= NULL");
        Timestamp timestamp = logData.getField("opTime").asTimestamp();

        Statement stmt;
        try {
            stmt = CCJSqlParserUtil.parse(sqlRedo);
        } catch (JSQLParserException e) {
            log.info("sqlRedo = {}", sqlRedo);
            stmt = CCJSqlParserUtil.parse(sqlRedo.replace("\\'", "\\ '"));
        }

        ArrayList<EventRowData> afterEventRowDataList = new ArrayList<>();
        ArrayList<EventRowData> EventRowDataList = new ArrayList<>();

        if (stmt instanceof Insert) {
            parseInsertStmt((Insert) stmt, EventRowDataList, afterEventRowDataList);
        } else if (stmt instanceof Update) {
            parseUpdateStmt((Update) stmt, EventRowDataList, afterEventRowDataList, sqlRedo);
        } else if (stmt instanceof Delete) {
            parseDeleteStmt((Delete) stmt, EventRowDataList, afterEventRowDataList);
        }

        //Long ts = idWorker.nextId();
        //ts 需要与红帽组件都是13位  ， 此处修改为当前时间， 即获取数据的时间
        Long ts = System.currentTimeMillis();

        /*if (log.isDebugEnabled()) {
            printDelay(pair.getScn(), ts, timestamp);
        }*/

        EventRow eventRow =
                new EventRow(
                        EventRowDataList,
                        afterEventRowDataList,
                        pair.getScn(),
                        operation,
                        schema,
                        tableName,
                        ts,
                        timestamp);
        return ((LogminerConverter)rowConverter).toLogData(eventRow);
    }

    private void printDelay(BigInteger scn, long ts, Timestamp timestamp) {

        long res = ts >> 22;

        long opTime = timestamp.getTime();

        log.debug("scn {} ,delay {} ms", scn, res - opTime);
    }
}