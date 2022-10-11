package org.jeecg.yqwl.datamiddle.ads.order.vo;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import lombok.Data;
import org.jeecg.yqwl.datamiddle.ads.order.entity.ext.ShipmentHaveTimestamp;

import java.util.Date;
import java.util.List;

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

    private Integer pageNo;

    private Integer pageSize;

    private Integer limitStart;

    private Integer limitEnd;

    /**
     * 运输方式 公路 G 铁路T 水路S
     */
    private String trafficType;

    /**
     * 是否为分拨中心
     */
    private Boolean cqrr;

    /**
     * 用于在途量获取每天的时间戳
     */
    List<ShipmentHaveTimestamp> allTime;


    public static void main(String[] args) {
        DateUtil.rangeToList(DateUtil.offsetDay(new Date(), -7), new Date(), DateField.DAY_OF_YEAR);
    }
}
