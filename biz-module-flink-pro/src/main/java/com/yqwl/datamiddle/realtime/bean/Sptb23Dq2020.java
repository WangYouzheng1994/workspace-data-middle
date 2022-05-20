package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Sptb23Dq2020 implements Serializable {
    private static final long serialVersionUID = 1L;

    private String VIN;


    private String CH;


    private String JJDH;


    private Long XDSJ;


    private String PP;


    private String DCS;


    private String FZ;


    private String DZ;


    private Long FCSJ;


    private Long DDSJ;


    private String SHENG;


    private String SHI;


    private String XIAN;


    private Long BGSJ;


    private String ZKBZ;


    private Long DCZRQ;


    private String CBS;


    private BigDecimal JD;


    private BigDecimal WD;

    /**
     * 主键 S_SPTB23_DQ_2020
     */

    private Integer ID;

    /**
     * 创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */

    private Long WAREHOUSE_UPDATETIME;


}
