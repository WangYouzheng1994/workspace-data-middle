package org.jeecg.yqwl.datamiddle.ads.order.vo;

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
     * 时间种类 (day/week...)
     */
    private String timeType;
    /**
     * 开始时间
     */
    private Long  startTime;

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
}
