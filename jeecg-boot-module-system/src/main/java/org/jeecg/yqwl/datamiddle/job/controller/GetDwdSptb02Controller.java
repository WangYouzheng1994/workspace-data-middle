//package org.jeecg.yqwl.datamiddle.job.controller;
//
//
//import lombok.extern.slf4j.Slf4j;
//
//import org.jeecg.common.system.base.controller.JeecgController;
//import org.jeecg.yqwl.datamiddle.ads.order.entity.DwdSptb02;
//import org.jeecg.yqwl.datamiddle.job.service.DataMiddleDwdSptb02Service;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.scheduling.annotation.Schedules;
//import org.springframework.stereotype.Component;
//
//
//
//
///**
// * @version 1.0
// * @Description
// * @ClassName GetDwdSptb02
// * @Author YULUO
// * @Date 2022/6/14
// */
//
//@Slf4j
//@Component
//public class GetDwdSptb02Controller  extends JeecgController<DwdSptb02,DataMiddleDwdSptb02Service> {
//
//    @Autowired
//    private DataMiddleDwdSptb02Service dataMiddleDwdSptb02Service;
//
//    @Schedules({@Scheduled(cron = "59 59 23 * * ? "), @Scheduled(cron = "0/5 * * * * ? ")})
//    public void runTimeDwdSptb02() {
//        log.info("开始获取dwdSptb02表数据!");
//        this.dataMiddleDwdSptb02Service.getDwdVlmsSptb02();
//        log.info("结束获取dwdSptb02表数据!");
//    }
//
//
//
//}
