package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 车型 大类
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Mdac14 implements Serializable {
    private static final long serialVersionUID = 1L;

    private String CCXDL;


    private String VCXDLSM;


    private String CPP;


    private String CYSCX;


    private String CTYBS;


    private Long DTYRQ;


    private String CVIN;


    private String CBS;


    private String CCXLB;

    /**
     * 运费对应价格分类。根据SPTB77.CPP计算运费
     */

    private String VCPFL;


    private String VWLFL;


    private Integer ID;


    private String CJHTY;

    /**
     * 平台代码
     */

    private String CPTDM;

    /**
     * 审批标识：0  未审批  1：已审批
     */

    private String APPROVAL_FLAG;

    /**
     * 审批人
     */

    private String APPROVAL_USER;

    /**
     * 审批日期
     */

    private Long APPROVAL_DATE;

    /**
     * 终审审批标识：0  未审批  1：已审批
     */

    private String FINAL_APPROVAL_FLAG;

    /**
     * 终审审批人
     */

    private String FINAL_APPROVAL_USER;

    /**
     * 终审审批日期
     */

    private Long FINAL_APPROVAL_DATE;


    private Long WAREHOUSE_CREATETIME;


    private Long WAREHOUSE_UPDATETIME;


}
