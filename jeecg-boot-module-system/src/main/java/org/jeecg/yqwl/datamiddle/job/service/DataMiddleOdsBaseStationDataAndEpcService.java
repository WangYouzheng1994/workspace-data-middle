package org.jeecg.yqwl.datamiddle.job.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.yqwl.datamiddle.job.entity.BaseStationData;


/**
 * @Description:OdsBSDAndEpc定时任务接口
 * @Author: XiaoFeng
 * @Date: 2022/6/15 13:49
 * @Version: V1.0
 */
public interface DataMiddleOdsBaseStationDataAndEpcService  extends IService<BaseStationData> {

    /**
     * 查询
     */
    void getOdsVlmsBaseStationDataAndEpc();
}
