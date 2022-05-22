package org.jeecg.yqwl.datamiddle.ads.order.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
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
     * 查询出库量列表
     * @param baseBrandTime
     * @return 品牌或基地  数量  时间
     */
    @Override
    public Result<ShipmentVO> findTop10StockOutList(GetBaseBrandTime baseBrandTime) {
        List<ShipmentDTO> shipment = dwmVlmsSptb02Mapper.stockOutList(baseBrandTime);
        //todo:对返回前端的值做处理
        List<String> timingList = this.formatTimingList(baseBrandTime);
        ShipmentVO resultVO = ShipmentVO.of(timingList);

        /**
         * 时间，基地/品牌，数量
         */
        Map<String, Map> dbMap = new HashMap();

        if ( CollectionUtils.isNotEmpty(shipment)) {
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
     * 查询top10发运量列表
     * @param baseBrandTime
     * @return  品牌或基地  数量  时间
     */

    @Override
    public Result<ShipmentVO> findTop10SendList(GetBaseBrandTime baseBrandTime) {
        List<ShipmentDTO> shipment = dwmVlmsSptb02Mapper.sendList(baseBrandTime);
        //todo:对返回前端的值做处理
        List<String> timingList = this.formatTimingList(baseBrandTime);
        ShipmentVO resultVO = ShipmentVO.of(timingList);

        /**
         * 时间，基地/品牌，数量
         */
        Map<String, Map> dbMap = new HashMap();

        if ( CollectionUtils.isNotEmpty(shipment)) {
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
     * 查询top10待发量列表
     * @param  baseBrandTime
     * @return  品牌或基地  数量  时间
     */
    @Override
    public Result<ShipmentVO> findTop10PendingList(GetBaseBrandTime baseBrandTime) {
        List<ShipmentDTO> shipment = dwmVlmsSptb02Mapper.pendingList(baseBrandTime);
        //todo:对返回前端的值做处理
        List<String> timingList = this.formatTimingList(baseBrandTime);
        ShipmentVO resultVO = ShipmentVO.of(timingList);

        /**
         * 时间，基地/品牌，数量
         */
        Map<String, Map> dbMap = new HashMap();

        if ( CollectionUtils.isNotEmpty(shipment)) {
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
     * 查询top10在途量列表
     * @param baseBrandTime
     * @return  品牌或基地  数量  时间
     */
    @Override
    public Result<ShipmentVO> findTop10OnWayList(GetBaseBrandTime baseBrandTime) {
        List<ShipmentDTO> shipment = dwmVlmsSptb02Mapper.onWayList(baseBrandTime);
        //todo:对返回前端的值做处理
        List<String> timingList = this.formatTimingList(baseBrandTime);
        ShipmentVO resultVO = ShipmentVO.of(timingList);

        /**
         * 时间，基地/品牌，数量
         */
        Map<String, Map> dbMap = new HashMap();

        if ( CollectionUtils.isNotEmpty(shipment)) {
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
        String timeType = baseBrandTime.getTimeType();
        List<ShipmentDTO> shipment = dwmVlmsSptb02Mapper.getShipment(baseBrandTime);
        //todo:对返回前端的值做处理
        List<String> timingList = this.formatTimingList(baseBrandTime);
        ShipmentVO resultVO = ShipmentVO.of(timingList);

        /**
         * 时间，基地/品牌，数量
         */
        Map<String, Map> dbMap = new HashMap();

        if ( CollectionUtils.isNotEmpty(shipment)) {
            String dates = "";
            String baseName = "";
            String customerName = "";
            Integer totalNum = null;
            String yearAndData="";
//            1. 日  : ”day”   默认为日
//            2. 周  : ”week”
//            2. 月  : ”month”
//            3. 季  : ”quarter”
//            4. 年  : ”year”
            for (ShipmentDTO shipmentDTO : shipment) {
                // 时间
                dates = shipmentDTO.getDates();
                String yearOfDates = shipmentDTO.getYearOfDates();
                if (timeType.equals("week")||timeType.equals("month")||timeType.equals("quarter")){
                    yearAndData=yearOfDates+"-"+dates;
                }
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
                // 放数据之前 比较一下是否在应返回的范围内。 这里可以实现的原因是map可以自动去重
                if (timeType.equals("week")||timeType.equals("month")||timeType.equals("quarter")){
                    itemMap.put(yearAndData, totalNum);
                }else {
                    itemMap.put(dates, totalNum);
                }
            }

            System.out.println(JSONArray.toJSONString(timingList));
//             Map<String, Map> dbMap = new HashMap();  dbMap.forEach(baseName,itemMap)
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
         *1.获取已到货未超理论实践的值
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
        String yearWeek="";
        String timeType = baseBrandTime.getTimeType();
        List<DateTime> dateTimes = null;
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
        List<String> timingList = new ArrayList<>();


        // 从今天开始前推七天
        if (baseBrandTime.getStartTime() == null && baseBrandTime.getEndTime() == null) {
            dateTimes = DateUtil.rangeToList(DateUtil.offsetDay(new Date(), -7), new Date(), DateField.DAY_OF_YEAR);
        } else {
            if (timeType.equals("day")){            //时间类型如果是天
                dateTimes = DateUtil.rangeToList(DateUtils.getDate(baseBrandTime.getStartTime()), DateUtils.getDate(baseBrandTime.getEndTime()), DateField.DAY_OF_YEAR);
                for (DateTime dateTime : dateTimes) {
                    timingList.add(dateTime.toDateStr());
                }

            } else if (timeType.equals("week")){    //时间类型如果是周
                 dateTimes = DateUtil.rangeToList(DateUtils.getDate(baseBrandTime.getStartTime()), DateUtils.getDate(baseBrandTime.getEndTime()), DateField.DAY_OF_YEAR);
                //分别取年份和一年的第几个周拼接成"20xx-x"的形式
                 for (DateTime dateTime : dateTimes) {
                    Integer field = dateTime.getField(DateField.YEAR);
                    String year = field.toString();
                    Integer weekOfYear = dateTime.weekOfYear();
                    yearWeek = year +"-"+ weekOfYear;
                    linkedHashSet.add(yearWeek);
                }
                System.out.println(linkedHashSet);
                // 格式化成前端要求的格式。
                /* 注掉是因为有更简洁的写法,下面是他的升级版:
                for (String date : linkedHashSet) {
                      timingList.add(date);
                }*/
                timingList.addAll(linkedHashSet);

            } else if(timeType.equals("month")){    //时间类型如果是月
                dateTimes = DateUtil.rangeToList(DateUtils.getDate(baseBrandTime.getStartTime()), DateUtils.getDate(baseBrandTime.getEndTime()), DateField.DAY_OF_YEAR);
                //分别取年份和月份拼接成"20xx-x"的形式
                for (DateTime dateTime : dateTimes) {
                    Integer field = dateTime.getField(DateField.YEAR);
                    String year = field.toString();
                    Integer monthOfYear = dateTime.monthStartFromOne();
                    yearWeek = year +"-"+ monthOfYear;
                    linkedHashSet.add(yearWeek);
                }
                System.out.println(linkedHashSet);
                // 格式化成前端要求的格式。
                timingList.addAll(linkedHashSet);
            } else if (timeType.equals("quarter")){ //时间类型如果是季度
                dateTimes = DateUtil.rangeToList(DateUtils.getDate(baseBrandTime.getStartTime()), DateUtils.getDate(baseBrandTime.getEndTime()), DateField.DAY_OF_YEAR);
                //分别取年份和季度拼接成"20xx-x"的形式
                for (DateTime dateTime : dateTimes) {
                    Integer field = dateTime.getField(DateField.YEAR);
                    String year = field.toString();
                    Integer quarterOfYear = dateTime.quarter();
                    yearWeek = year +"-"+ quarterOfYear;
                    linkedHashSet.add(yearWeek);
                }
                System.out.println(linkedHashSet);
                // 格式化成前端要求的格式。
                timingList.addAll(linkedHashSet);
            } else if (timeType.equals("year")){    //时间类型如果是年
                dateTimes = DateUtil.rangeToList(DateUtils.getDate(baseBrandTime.getStartTime()), DateUtils.getDate(baseBrandTime.getEndTime()), DateField.DAY_OF_YEAR);
                //直接取年份
                for (DateTime dateTime : dateTimes) {
                    Integer field = dateTime.getField(DateField.YEAR);
                    String year = field.toString();
                    linkedHashSet.add(year);
                }
                System.out.println(linkedHashSet);
                // 格式化成前端要求的格式。
                timingList.addAll(linkedHashSet);
            }
        }
        return timingList;

    }
}
