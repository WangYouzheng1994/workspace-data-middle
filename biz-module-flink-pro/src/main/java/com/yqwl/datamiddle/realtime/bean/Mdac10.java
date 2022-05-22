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
public class Mdac10 implements Serializable {

    private static final long serialVersionUID = 1L;


    private String CPP;


    private String VPPSM;


    private String CTYBS;


    private Long DTYRQ;

    /**
     * 对应委托单位.与字典表SYSC09D对应CZDDM = 'WTDW'
     */

    private Integer NGSDJ;

    /**
     * 排序
     */

    private String CPX;

    /**
     * 计划标识.0不检查，1/检查
     */

    private String CFHBS;

    /**
     * ID
     */

    private Integer ID;


    private String CCQCK;


    private String CWLWZ;

    /**
     * 主机厂公司
     */

    private String CGS;

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

    /**
     * 创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */

    private Long WAREHOUSE_UPDATETIME;


}
