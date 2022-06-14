package org.jeecg.yqwl.datamiddle.job.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwdSptb02;

public interface DataMiddleDwdSptb02Service extends IService<DwdSptb02> {

    /**
     * 查询dwd_vlms_sptb02表数据(mysql)
     */
    void getDwdVlmsSptb02();
}
