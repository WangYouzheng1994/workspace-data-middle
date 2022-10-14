package org.jeecg.yqwl.datamiddle.ads.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OnWayCountVo {

    /**
     * dwd基地省区代码-起货地
     */
    private String startProvinceCode;

    /**
     * dwd基地市县代码-起货地
     */
    private String startCityCode;

    private String startCityName;

    private BigDecimal startNjd;

    private BigDecimal startNwd;

    /**
     * dwd到货地省区代码
     */
    private String endProvinceCode;

    /**
     * dwd到货地市县代码
     */
    private String endCityCode;

    private String endCityName;

    private BigDecimal endNjd;

    private BigDecimal endNwd;

    private Integer value;
}
