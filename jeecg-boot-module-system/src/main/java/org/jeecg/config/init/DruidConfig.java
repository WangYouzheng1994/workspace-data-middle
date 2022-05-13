//package org.jeecg.config.init;
//
//import com.alibaba.druid.pool.DruidDataSource;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import javax.annotation.Resource;
//import javax.sql.DataSource;
//
///**
// * @Description:
// * @Author: XiaoFeng
// * @Date: 2022/5/12 19:57
// * @Version: V1.0
// */
//@Configuration
//public class DruidConfig {
//    @Resource
//    private JdbcParamConfig jdbcParamConfig ;
//    @Bean
//    public DataSource dataSource() {
//        DruidDataSource datasource = new DruidDataSource();
//        datasource.setUrl(jdbcParamConfig.getUrl());
//        datasource.setDriverClassName(jdbcParamConfig.getDriverClassName());
//        datasource.setInitialSize(jdbcParamConfig.getInitialSize());
//        datasource.setMinIdle(jdbcParamConfig.getMinIdle());
//        datasource.setMaxActive(jdbcParamConfig.getMaxActive());
//        datasource.setMaxWait(jdbcParamConfig.getMaxWait());
//        return datasource;
//    }
//}