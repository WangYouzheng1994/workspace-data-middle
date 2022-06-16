package org.jeecg.yqwl.datamiddle.job.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * mysql 相关配置
 */
@Component
public class MysqlConfig {
    //Driver
    public static final String DRIVER = "com.mysql.cj.jdbc.Driver";

    public static String URL;
    @Value("${spring.datasource.dynamic.datasource.wareHouse.url}")
    public static String USERNAME = "fengqiwulian";
    public static String PASSWORD = "fengqiwulian";

    @Value("${spring.datasource.dynamic.datasource.wareHouse.url}")
    public void setURL(String url) {
        MysqlConfig.URL = url;
    }

    @Value("${spring.datasource.dynamic.datasource.wareHouse.username}")
    public void setUsername(String username) {
        MysqlConfig.USERNAME = username;
    }

    @Value("${spring.datasource.dynamic.datasource.wareHouse.password}")
    public void setPassword(String password) {
        MysqlConfig.PASSWORD = password;
    }

    // serverTimezone=UTC
//    public static final String URL = "jdbc:mysql://10.123.175.197:3306/data_flink?characterEncoding=utf8&serverTimezone=UTC&useSSL=false&rewriteBatchedStatements=true";
//    public static final String USERNAME = "flinkcdc";
//    public static final String PASSWORD = "2usE5x2NIwzQ0tdBSfOZng==";
}
