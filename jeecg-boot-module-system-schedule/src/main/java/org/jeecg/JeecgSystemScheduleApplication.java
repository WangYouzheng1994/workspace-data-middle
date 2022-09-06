package org.jeecg;

import lombok.extern.slf4j.Slf4j;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
* 单体启动类（采用此类启动为单体模式）
*/
@Slf4j
@EnableScheduling
@SpringBootApplication
@MapperScan("org.jeecg.yqwl.datamiddle.ads.order.mapper")
public class JeecgSystemScheduleApplication {


    public static void main(String[] args) {
        SpringApplication.run(JeecgSystemScheduleApplication.class, args);

    }

}