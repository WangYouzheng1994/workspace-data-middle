package org.jeecg.yqwl.datamiddle.ads.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ConvertDataVo {

    /**
     * 经纬度
     */
    private BigDecimal[] coord;

    /**
     * 值
     */
    private Integer value;

    /**
     * 地区名称
     */
    private String name;
}
