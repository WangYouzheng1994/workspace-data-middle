package org.jeecg.yqwl.datamiddle.job.controller;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.yqwl.datamiddle.ads.order.entity.BaseStationData;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwdSptb02;
import org.jeecg.yqwl.datamiddle.job.service.DataMiddleDwdSptb02Service;
import org.jeecg.yqwl.datamiddle.job.service.DataMiddleOdsBaseStationDataAndEpcService;
import org.jeecg.yqwl.datamiddle.job.service.DataMiddleOdsSptb02Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Component;

/**
 * @Description: 定时器获取OdsBaseStationDataAndEpc数据更新到dwm_vlms_one_order_to_end的表里
 * @Author: XiaoFeng
 * @Date: 2022/6/15 13:24
 * @Version: V1.0
 */
@Slf4j
@Component
public class GetOdsBaseStationDataAndEpc extends JeecgController<BaseStationData, DataMiddleOdsBaseStationDataAndEpcService> {
    @Autowired
    private DataMiddleOdsBaseStationDataAndEpcService dataAndEpcService;



    /**
     * 分页拉取OdsBaseStationData & EPC表
     * 每天23:59:59秒拉一次，
     * 每天10分钟拉一次。
     */
    @Schedules({@Scheduled(cron = "59 59 23 * * ? "), @Scheduled(cron = "* 0/10 * * * ? ")})
    public void getBaseStationData(){
        log.info("开始运行OdsBaseStationData & Epc 数据任务");
        dataAndEpcService.getOdsVlmsBaseStationDataAndEpc();
        log.info("结束运行OdsBaseStationData & Epc 数据任务");
    }


}
