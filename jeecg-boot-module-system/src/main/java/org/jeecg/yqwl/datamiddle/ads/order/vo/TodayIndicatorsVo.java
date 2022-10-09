package org.jeecg.yqwl.datamiddle.ads.order.vo;

import lombok.Data;

/**
 * 大屏图标指标
 * @author dabao
 * @date 2022/10/9
 */
@Data
public class TodayIndicatorsVo {

    /**
     * 今日起运量
     */
    private Long shipmentToday;

    /**
     * 今日在途量
     */
    private Long onWayToday;

    /**
     * 今日待发量
     */
    private Long pendingToday;

    /**
     * 今日运力需求量
     */
    private Long capacityDemandToday;



}
