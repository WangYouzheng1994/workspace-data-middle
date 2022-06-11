package com.yqwl.datamiddle.realtime.common;

/**
 * mysql 相关配置
 */
public class MysqlConfig {
    //Driver
    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";
    //public static final String URL = "jdbc:mysql://192.168.3.4:3306/data_middle_flink?characterEncoding=utf8&serverTimezone=UTC&useSSL=false&rewriteBatchedStatements=true";
    //public static final String USERNAME = "fengqiwulian";
    //public static final String PASSWORD = "fengqiwulian";

    // serverTimezone=UTC
    public static final String URL = "jdbc:mysql://10.123.175.197:3306/data_flink?characterEncoding=utf8&serverTimezone=UTC&useSSL=false&rewriteBatchedStatements=true";
    public static final String USERNAME = "flinkus";
    public static final String PASSWORD = "2usE5x2NIwzQ0tdBSfOZng==";
}
