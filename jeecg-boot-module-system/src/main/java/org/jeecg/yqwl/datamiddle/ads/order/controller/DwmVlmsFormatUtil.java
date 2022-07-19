package org.jeecg.yqwl.datamiddle.ads.order.controller;

import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;

/**
 * @Description: 公共工具类
 * @Author: WangYouzheng
 * @Date: 2022/7/19 11:39
 * @Version: V1.0
 */
public class DwmVlmsFormatUtil {

    /**
     * 格式化品牌
     * t === '1' ? '大众' : t === '2' ? '红旗' : t === '3' ? '马自达' : t === '4' ? '奔腾' : t === '5' ? '解放'
     *
     * @param brandEng
     */
    public static  String formatBrandToChinese(String brandEng) {
        String value = "";

        switch(brandEng) {
            case "1" : value = "大众"; break;
            case "2" : value = "红旗"; break;
            case "3" : value = "马自达"; break;
            case "4" : value = "奔腾"; break;
            case "5" : value = "解放"; break;
        }

        return value;
    }

    /**
     * 日期转换 统一减掉8小时，目前etl没处理好。
     *
     * @param queryCriteria
     */
    public static void formatQueryTime(GetQueryCriteria queryCriteria) {
        if (queryCriteria.getLeaveFactoryTimeStart() != null) {
            queryCriteria.setLeaveFactoryTimeStart(queryCriteria.getLeaveFactoryTimeStart() + 28800000);
        }
        if (queryCriteria.getLeaveFactoryTimeEnd() != null) {
            queryCriteria.setLeaveFactoryTimeEnd(queryCriteria.getLeaveFactoryTimeEnd() + 28800000);
        }
        if (queryCriteria.getInSiteTimeStart() != null) {
            queryCriteria.setInSiteTimeStart(queryCriteria.getInSiteTimeStart() + 28800000);
        }
        if (queryCriteria.getInSiteTimeEnd() != null) {
            queryCriteria.setInSiteTimeEnd(queryCriteria.getInSiteTimeEnd() + 28800000);
        }
        if (queryCriteria.getCp9OfflineTimeStart() != null) {
            queryCriteria.setCp9OfflineTimeStart(queryCriteria.getCp9OfflineTimeStart() + 28800000);
        }
        if (queryCriteria.getCp9OfflineTimeEnd() != null) {
            queryCriteria.setCp9OfflineTimeEnd(queryCriteria.getCp9OfflineTimeEnd() + 28800000);
        }
//        if (queryCriteria.getDotSiteTimeStart() != null) {
//            queryCriteria.setDotSiteTimeStart(queryCriteria.getDotSiteTimeStart() + 28800000);
//        }
//        if (queryCriteria.getDotSiteTimeEnd() != null) {
//            queryCriteria.setDotSiteTimeEnd(queryCriteria.getDotSiteTimeEnd() + 28800000);
//        }
        if (queryCriteria.getFinalSiteTimeStart() != null) {
            queryCriteria.setFinalSiteTimeStart(queryCriteria.getFinalSiteTimeStart() + 28800000);
        }
        if (queryCriteria.getFinalSiteTimeEnd() != null) {
            queryCriteria.setFinalSiteTimeEnd(queryCriteria.getFinalSiteTimeEnd() + 28800000);
        }
        if (queryCriteria.getDtvsdhsjStart() != null) {
            queryCriteria.setDtvsdhsjStart(queryCriteria.getDtvsdhsjStart() + 28800000);
        }
        if (queryCriteria.getDtvsdhsjEnd() != null) {
            queryCriteria.setDtvsdhsjEnd(queryCriteria.getDtvsdhsjEnd() + 28800000);
        }
    }
}
