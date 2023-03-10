package org.jeecg.yqwl.datamiddle.job.controller;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.yqwl.datamiddle.job.service.DataMiddleOdsSptb02Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

/**
 * @Description: 定时器获取OdsSptb02数据更新到DwdSptb02的表里
 * @Author: XiaoFeng
 * @Date: 2022/6/13 16:01
 * @Version: V1.0
 */
@Slf4j
@Component
public class GetOdsSptb02 {
    @Autowired
    private DataMiddleOdsSptb02Service dataMiddleOdsSptb02Service;

    /**
     * 分页拉取OdsSptb02运单表业务数据。
     * 每天23:59:59秒拉一次，
     * 每天10分钟拉一次。
     */
//    @Schedules({@Scheduled(cron = "59 59 23 * * ? "), @Scheduled(cron = "* 0/10 * * * ? ")})
//    public void getSptb02HandleInfo() {
//        log.info("开始运行【获取OdsSptb02数据任务");
//        this.dataMiddleOdsSptb02Service.getOdsVlmsSptb02();
//        log.info("结束运行【获取OdsSptb02数据任务】任务");
//    }


}
