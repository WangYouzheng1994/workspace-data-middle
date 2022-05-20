package org.jeecg.yqwl.datamiddle.ads.order.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import org.jeecg.common.api.vo.Result;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetBaseBrandTime;
import org.jeecg.yqwl.datamiddle.ads.order.mapper.DwmVlmsSptb02Mapper;
import org.jeecg.yqwl.datamiddle.ads.order.service.IDwmVlmsSptb02Service;
import org.jeecg.yqwl.datamiddle.ads.order.vo.TimelinessRatioVO;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: DwmVlmsSptb02
 * @Author: jeecg-boot
 * @Date:   2022-05-12
 * @Version: V1.0
 */
@DS("slave0")
@Service
public class DwmVlmsSptb02ServiceImpl extends ServiceImpl<DwmVlmsSptb02Mapper, DwmVlmsSptb02> implements IDwmVlmsSptb02Service {
    @Resource
    private DwmVlmsSptb02Mapper dwmVlmsSptb02Mapper;

    /**
     * 查询top10发运量列表
     *
     * @param dwmVlmsSptb02
     * @return
     */

    @Override
    public Result findTop10SendList(DwmVlmsSptb02 dwmVlmsSptb02) {
        return null;
    }

    /**
     * 按条件查询计划量
     * @param baseBrandTime
     * @return
     */
    @Override
    public List<DwmVlmsSptb02> findDayAmountOfPlan(GetBaseBrandTime baseBrandTime) {
        List<DwmVlmsSptb02> list = dwmVlmsSptb02Mapper.getPlanAmount(baseBrandTime);
        //todo:对返回前端的值做处理
        return list;
    }

    /**
     * 按条件查询发运量
     * @param baseBrandTime
     * @return
     */
    @Override
    public List<DwmVlmsSptb02> findShipment(GetBaseBrandTime baseBrandTime) {
        List<DwmVlmsSptb02> shipment = dwmVlmsSptb02Mapper.getShipment(baseBrandTime);
        //todo:对返回前端的值做处理
        return shipment;
    }

    @Override
    public TimelinessRatioVO findArrivalRate(GetBaseBrandTime baseBrandTime) {
        DwmVlmsSptb02 arrivalRate = dwmVlmsSptb02Mapper.getArrivalRate(baseBrandTime);
        Double percentage = arrivalRate.getPercentage();
        //下面开始截取两位数 原数据:0.12594458438287154
        BigDecimal bigDecimal = new BigDecimal(percentage);
        Double newPercentage = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        //
        TimelinessRatioVO timelinessRatioVO = new TimelinessRatioVO();
        timelinessRatioVO.setAllotPercent(10); //分配及时率
        timelinessRatioVO.setOutWarehousePercent(80);//出库及时率
        timelinessRatioVO.setStartPercent(50);//起运及时率
        timelinessRatioVO.setHalfwayPercent(90);//在途及时率
        timelinessRatioVO.setEndPercent(100);//到货及时率
        return timelinessRatioVO;
    }

}
