package org.jeecg.yqwl.datamiddle.ads.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DimProvinceVo {

    /**
     * 表的唯一id
     */
    private Long idnum;

    /**
     * 市县名称
     */
    private String vsxmc;

    /**
     * 精度
     */
    private BigDecimal njd;

    /**
     * 纬度
     */
    private BigDecimal nwd;
}
