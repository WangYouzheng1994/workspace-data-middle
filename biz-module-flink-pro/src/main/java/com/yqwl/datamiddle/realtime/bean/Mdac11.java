package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

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
public class Mdac11 implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 20190406 30-50
     */

    private String CCXDM;


    private String CCXDL;


    private String VCXSM;


    private String CTYBS;


    private Long DTYRQ;


    private String CPP;


    private String CJSCX;


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
