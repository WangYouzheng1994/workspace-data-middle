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
    private String transModeCode;

    /**
     * 品牌
     */
    private String hostComCode;
}
