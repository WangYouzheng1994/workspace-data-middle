package org.jeecg.yqwl.datamiddle.config.driver.driver;

import org.apache.commons.lang3.StringUtils;
import org.jeecg.yqwl.datamiddle.config.driver.entity.SystemConfiguration;

/**
 * SqlUtil
 * @Author: XiaoKai
 * @Date: 2022-011-11
 * @Version: V2.0
 */
public class SqlUtil {

    private static final String SEMICOLON = ";";

    public static String[] getStatements(String sql) {
        return getStatements(sql, SystemConfiguration.getInstances().getSqlSeparator());
    }

    public static String[] getStatements(String sql, String sqlSeparator) {
        if (StringUtils.isEmpty(sql)) {
            return new String[0];
        }

        String[] splits = sql.replace(";\r\n", ";\n").split(sqlSeparator);
        String lastStatement = splits[splits.length - 1].trim();
        if (lastStatement.endsWith(SEMICOLON)) {
            splits[splits.length - 1] = lastStatement.substring(0, lastStatement.length() - 1);
        }

        return splits;
    }

    public static String removeNote(String sql) {
        if (StringUtils.isNoneBlank(sql)) {
            sql = sql.replaceAll("\u00A0", " ")
                .replaceAll("[\r\n]+", "\n")
                .replaceAll("--([^'\n]{0,}('[^'\n]{0,}'){0,1}[^'\n]{0,}){0,}", "").trim();
        }
        return sql;
    }

    public static String replaceAllParam(String sql, String name, String value) {
        return sql.replaceAll("\\$\\{" + name + "\\}", value);
    }
}
