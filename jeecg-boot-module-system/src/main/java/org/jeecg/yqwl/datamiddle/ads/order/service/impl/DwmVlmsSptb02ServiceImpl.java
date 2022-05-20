package org.jeecg.yqwl.datamiddle.ads.order.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.dynamic.datasource.annotation.DS;
import org.apache.commons.collections.CollectionUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.DateUtils;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsSptb02;
import org.jeecg.yqwl.datamiddle.ads.order.entity.ext.ShipmentDTO;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetBaseBrandTime;
import org.jeecg.yqwl.datamiddle.ads.order.mapper.DwmVlmsSptb02Mapper;
import org.jeecg.yqwl.datamiddle.ads.order.service.IDwmVlmsSptb02Service;
import org.jeecg.yqwl.datamiddle.ads.order.vo.ShipmentVO;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

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
    public Result<ShipmentVO> findShipment(GetBaseBrandTime baseBrandTime) {
        List<ShipmentDTO> shipment = dwmVlmsSptb02Mapper.getShipment(baseBrandTime);
        //todo:对返回前端的值做处理
        List<String> timingList = this.formatTimingList(baseBrandTime);
        ShipmentVO resultVO = ShipmentVO.of(timingList);

        /**
         * 时间，基地/品牌，数量
         */
        Map<String, Map> dbMap = new HashMap();

        if (CollectionUtils.isNotEmpty(shipment)) {
            String dates = "";
            String baseName = "";
            String customerName = "";
            Integer totalNum = null;

            for (ShipmentDTO shipmentDTO : shipment) {
                // 时间
                dates = shipmentDTO.getDates();
                // 基地名称 / 品牌
                baseName = shipmentDTO.getGroupName();
                // 数量
                totalNum = shipmentDTO.getTotalNum();

                Map<String, Integer> itemMap = null;
                if (dbMap.containsKey(baseName)) {
                    itemMap = dbMap.get(baseName);
                } else {
                    itemMap = new LinkedHashMap<>();
                    dbMap.put(baseName, itemMap);
                    // 设置每天的默认值。
                    Map<String, Integer> finalItemMap = itemMap;
                    timingList.forEach(i -> {
                        finalItemMap.put(i, 0);
                    });
                }
                // 放数据之前 比较一下是否在应返回的范围内。
                itemMap.put(dates, totalNum);
            }

            System.out.println(JSONArray.toJSONString(timingList));
            dbMap.forEach((k, v) -> {
                ShipmentVO.Item item = new ShipmentVO.Item();
                item.setName(k);
                item.setDataList(new ArrayList<>(v.values()));
                resultVO.addResultItem(item);
            });
        }

        return Result.OK(resultVO);
    }

    /**
     * 获取到货及时率
     *
     * @param baseBrandTime
     * @return
     */
    @Override
    public Result<BigDecimal> findArrivalRate(GetBaseBrandTime baseBrandTime) {
        /**
         *
         *
         */
        DwmVlmsSptb02 arrivalRate = dwmVlmsSptb02Mapper.getArrivalRate(baseBrandTime);
        Double percentage = arrivalRate.getPercentage();
        //下面开始截取两位数 原数据:0.12594458438287154
        BigDecimal bigDecimal = new BigDecimal(percentage);
        Double newPercentage = bigDecimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

        return null;
    }

    /**
     * 根据前端起始结束 获取 大屏图标用的x轴 时间列表
     *
     * @param baseBrandTime
     * @return
     */
    private List<String> formatTimingList(GetBaseBrandTime baseBrandTime) {

        String startTime = "";
        String endTime = "";

        List<DateTime> dateTimes = null;
        // 从今天开始前推七天
        if (baseBrandTime.getStartTime() == null && baseBrandTime.getEndTime() == null) {
            dateTimes = DateUtil.rangeToList(DateUtil.offsetDay(new Date(), -7), new Date(), DateField.DAY_OF_YEAR);
        } else {
            dateTimes = DateUtil.rangeToList(DateUtils.getDate(baseBrandTime.getStartTime()), DateUtils.getDate(baseBrandTime.getEndTime()), DateField.DAY_OF_YEAR);
        }

        // 格式化成前端要求的格式。
        List<String> timingList = new ArrayList<>();
        for (DateTime dateTime : dateTimes) {
            timingList.add(dateTime.toDateStr());
        }

        return timingList;
    }
}
