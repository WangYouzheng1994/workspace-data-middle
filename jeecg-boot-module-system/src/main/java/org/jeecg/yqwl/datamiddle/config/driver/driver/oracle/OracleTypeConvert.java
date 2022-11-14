package org.jeecg.yqwl.datamiddle.config.driver.driver.oracle;

import org.jeecg.yqwl.datamiddle.config.driver.entity.Column;
import org.jeecg.yqwl.datamiddle.config.driver.entity.ColumnType;
import org.jeecg.yqwl.datamiddle.config.driver.entity.ITypeConvert;

/**
 * OracleTypeConvert
 * @Author: XiaoKai
 * @Date: 2022-011-14
 * @Version: V2.0
 */
public class OracleTypeConvert implements ITypeConvert {
    @Override
    public ColumnType convert(Column column) {
        ColumnType columnType = ColumnType.STRING;
        if (null==column) {
            return columnType;
        }
        String t = column.getType().toLowerCase();
        boolean isNullable = !column.isKeyFlag() && column.isNullable();
        if (t.contains("char")) {
            columnType = ColumnType.STRING;
        } else if (t.contains("date")) {
            columnType = ColumnType.LOCALDATETIME;
        } else if (t.contains("timestamp")) {
            columnType = ColumnType.TIMESTAMP;
        } else if (t.contains("number")) {
            if (t.matches("number\\(+\\d\\)")) {
                if (isNullable) {
                    columnType = ColumnType.INTEGER;
                } else {
                    columnType = ColumnType.INT;
                }
            } else if (t.matches("number\\(+\\d{2}+\\)")) {
                if (isNullable) {
                    columnType = ColumnType.JAVA_LANG_LONG;
                } else {
                    columnType = ColumnType.LONG;
                }
            } else {
                columnType = ColumnType.DECIMAL;
            }
        } else if (t.contains("float")) {
            if (isNullable) {
                columnType = ColumnType.JAVA_LANG_FLOAT;
            } else {
                columnType = ColumnType.FLOAT;
            }
        } else if (t.contains("clob")) {
            columnType = ColumnType.STRING;
        } else if (t.contains("blob")) {
            columnType = ColumnType.BYTES;
        }
        return columnType;
    }

    @Override
    public String convertToDB(ColumnType columnType) {
        switch (columnType) {
            case STRING:
                return "varchar";
            case DATE:
                return "date";
            case TIMESTAMP:
                return "timestamp";
            case INTEGER:
            case INT:
            case LONG:
            case JAVA_LANG_LONG:
            case DECIMAL:
                return "number";
            case FLOAT:
            case JAVA_LANG_FLOAT:
                return "float";
            case BYTES:
                return "blob";
            default:
                return "varchar";
        }
    }
}
