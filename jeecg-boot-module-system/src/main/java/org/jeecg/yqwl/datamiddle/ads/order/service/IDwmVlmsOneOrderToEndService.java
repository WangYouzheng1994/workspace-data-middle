package org.jeecg.yqwl.datamiddle.ads.order.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;


import org.jeecg.common.api.vo.Result;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsOneOrderToEnd;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;



/**
 * @Description: 一单到底
 * @Author: jeecg-boot
 * @Date:   2022-06-06
 * @Version: V1.0
 */
@DS("slave2")
public interface IDwmVlmsOneOrderToEndService extends IService<DwmVlmsOneOrderToEnd> {

    DwmVlmsOneOrderToEnd selectOneOrderToEndList(GetQueryCriteria queryCriteria);
}
