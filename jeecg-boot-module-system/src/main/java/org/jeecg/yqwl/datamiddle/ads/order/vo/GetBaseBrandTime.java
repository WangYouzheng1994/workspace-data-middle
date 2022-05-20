package org.jeecg.yqwl.datamiddle.ads.order.vo;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import lombok.Data;

import java.util.Date;

/**
 * @Description:
 * @Author: XiaoFeng
 * @Date: 2022/5/18 10:30
 * @Version: V1.0
 */
@Data
public class GetBaseBrandTime {
    /**
     * 时间种类
     * 日/星期/月份/季度/年
     * (day/week/month/quarter/year)
     */
    private String timeType;
    /**
     * 开始时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;

    /**
     * 基地
     */
    private String cqwh;

    /**
     * 主机厂品牌
     */
    private String czjgsdm;


    public static void main(String[] args) {
        DateUtil.rangeToList(DateUtil.offsetDay(new Date(), -7), new Date(), DateField.DAY_OF_YEAR);
    }
}
