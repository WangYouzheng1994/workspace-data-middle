package org.jeecg.yqwl.datamiddle.job.service;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwdSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsOneOrderToEnd;

/**
 * @Description: 定时任务接口
 * @Author: xiaofeng
 * @Date:   2022-06-13
 * @Version: V1.0
 */

public interface DataMiddleOdsSptb02Service extends IService<DwdSptb02> {
    /**
     * 查询Ods_sptb02表
     */
    void getOdsVlmsSptb02();
}
