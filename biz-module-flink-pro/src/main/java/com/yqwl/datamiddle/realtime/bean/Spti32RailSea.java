package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 铁水标准物流时间
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Spti32RailSea implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主机公司代码
     */

    private String CZJGSDM;

    /**
     * 运输方式  T-铁路 S-水运
     */

    private String VYSFS;

    /**
     * 起始站台/港口代码
     */

    private String CQSZTDM;

    /**
     * 目标站台/港口代码
     */

    private String CMBZTDM;

    /**
     * 在途标准物流时间（出库-系统到货）
     */

    private BigDecimal NDHSJ_ZT;

    /**
     * 系统到货标准物流时间-天
     * （满列）26节以上
     */

    private BigDecimal NDHSJ_XTDH_ML;

    /**
     * 系统到货标准物流时间-天
     * （大组）15-26节
     */

    private BigDecimal NDHSJ_XTDH_DZ;

    /**
     * 系统到货标准物流时间-天
     * （散列）15节及以下
     */

    private BigDecimal NDHSJ_XTDH_SL;

    /**
     * 到站标准物流时间-天
     * （满列）26节以上
     */

    private BigDecimal NDHSJ_DZ_ML;

    /**
     * 到站标准物流时间-天
     * （大组）15-26节
     */

    private BigDecimal NDHSJ_DZ_DZ;

    /**
     * 到站标准物流时间-天
     * （散列）15节及以下
     */

    private BigDecimal NDHSJ_DZ_SL;

    /**
     * 备注
     */

    private String CBZ;

    /**
     * 操作日期
     */

    private Long DCZRQ;

    /**
     * 操作员
     */

    private String CCZYDM;


    private Long WAREHOUSE_CREATETIME;


    private Long WAREHOUSE_UPDATETIME;


}
