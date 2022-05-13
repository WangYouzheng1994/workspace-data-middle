package org.jeecg.yqwl.datamiddle.ads.order.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.UserInfo;

/**
 * @Description: DwmVlmsSptb02
 * @Author: jeecg-boot
 * @Date:   2022-05-12
 * @Version: V1.0
 */
public interface IDwmVlmsSptb02Service extends IService<DwmVlmsSptb02> {
    /**
     * 查询top10发运量列表
     *
     * @return
     */
    Result findTop10SendList(DwmVlmsSptb02 dwmVlmsSptb02);

    @DS("dataWareHouse")
    Result findUserInfo(UserInfo userInfo);
}
