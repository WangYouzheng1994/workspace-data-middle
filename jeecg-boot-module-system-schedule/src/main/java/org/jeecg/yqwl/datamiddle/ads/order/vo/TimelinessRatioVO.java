package org.jeecg.yqwl.datamiddle.ads.order.vo;

import lombok.Data;

/**
 * @Description: 及时率查询
 * @Author: XiaoFeng
 * @Date: 2022/5/20 11:26
 * @Version: V1.0
 */
@Data
public class TimelinessRatioVO {
    /**
     * 分配及时率
     */
    private Integer allotPercent;
    /**
     * 出库及时率
     */
    private Integer outWarehousePercent;
    /**
     * 起运及时率
     */
    private Integer startPercent;
    /**
     * 到货及时率
     */
    private Integer endPercent;
}
