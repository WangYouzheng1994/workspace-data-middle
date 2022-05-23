package org.jeecg.yqwl.datamiddle.util;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import org.jeecg.common.util.DateUtils;
import org.jeecg.yqwl.datamiddle.ads.order.entity.ext.ShipmentDTO;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetBaseBrandTime;
import org.jeecg.yqwl.datamiddle.ads.order.vo.ShipmentVO;

import java.util.*;

/**
 * @Description: 处理返回给前端数据的工具类
 * @Author: XiaoFeng
 * @Date: 2022/5/23 11:10
 * @Version: V1.0
 */
public class FormatDataUtil {
    /**
     * 根据前端起始结束 获取 大屏图标用的x轴 时间列表
     * @param baseBrandTime
     * @return
     */
    public static List<String> formatTimingList(GetBaseBrandTime baseBrandTime) {

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
                // 格式化成前端要求的格式。
                timingList.addAll(linkedHashSet);
            }
        }
        return timingList;
    }
    /**
     * 处理返回给前端的数据,自动补0
     * @param shipment
     * @param baseBrandTime
     * @return
     */
    public static ShipmentVO formatDataList(List<ShipmentDTO> shipment, GetBaseBrandTime baseBrandTime){
        String timeType = baseBrandTime.getTimeType();
        List<String> timingList = formatTimingList(baseBrandTime);
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

            dbMap.forEach((k, v) -> {
                ShipmentVO.Item item = new ShipmentVO.Item();
                item.setName(k);
                item.setDataList(new ArrayList<>(v.values()));
                resultVO.addResultItem(item);
            });
        }
        return resultVO;
    }

}