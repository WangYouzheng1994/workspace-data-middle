package org.jeecg.yqwl.datamiddle.ads.order.entity;

import lombok.Data;

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
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 基地
     */
    private String transModeCode;

    /**
     * 品牌
     */
    private String hostComCode;
}
