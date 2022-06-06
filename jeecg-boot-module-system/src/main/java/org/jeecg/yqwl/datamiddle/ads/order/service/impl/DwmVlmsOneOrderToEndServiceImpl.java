package org.jeecg.yqwl.datamiddle.ads.order.service.impl;


import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsOneOrderToEnd;
import org.jeecg.yqwl.datamiddle.ads.order.mapper.DwmVlmsOneOrderToEndMapper;
import org.jeecg.yqwl.datamiddle.ads.order.service.IDwmVlmsOneOrderToEndService;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.util.List;


/**
 * @Description: 一单到底
 * @Author: jeecg-boot
 * @Date:   2022-06-06
 * @Version: V1.0
 */

@Slf4j
@DS("slave0")
@Service
public class DwmVlmsOneOrderToEndServiceImpl extends ServiceImpl<DwmVlmsOneOrderToEndMapper, DwmVlmsOneOrderToEnd> implements IDwmVlmsOneOrderToEndService {

    @Resource
    private DwmVlmsOneOrderToEndMapper dwmVlmsOneOrderToEndMapper;

    @Override
    public List<DwmVlmsOneOrderToEnd> selectOneOrderToEndList(GetQueryCriteria queryCriteria) {
        List<DwmVlmsOneOrderToEnd> oneOrderToEndList = dwmVlmsOneOrderToEndMapper.selectOneOrderToEndList(queryCriteria);
        return oneOrderToEndList;
    }
}
