package com.yqwl.datamiddle.realtime.bean;

import com.alibaba.fastjson.annotation.JSONField;
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
public class Mdac12 implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 产品代码
     */

    private String CCPDM;

    /**
     * 产品名称
     */

    private String VCPMC;


    private String VCPJC;


    private Integer NAQKC;


    private Integer NPL;


    private Integer NPLZL;


    private String CTYBS;


    private Long DTYRQ;


    private String CPP;


    private String CCXDL;

    /**
     * 20190404 30-40 红旗车型33  40-100 20210528
     */

    private String CCXDM;


    private String CJHTY;


    private Long DJHTYRQ;


    private String CDDTY;


    private Long DDDTYRQ;


    private String CLSTY;


    private Long DLSTYRQ;


    private BigDecimal NCCFDJ;


    private String CPHBS;


    private String CJHDBBS;


    private String VBZ;


    private String CJKBS;


    private Long ID;

    /**
     * 描述
     */

    private String VMS;


    private Integer NFWFDJ;

    /**
     * 库龄设置，单位为天
     */

    private Integer NKLSZ;


    private String CXSTY;

    /**
     * 多公司模式下的公司-SPTC60
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


    private String FINAL_APPROVAL_FLAG;


    private String FINAL_APPROVAL_USER;


    private Long FINAL_APPROVAL_DATE;

    /**
     * 创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */

    private Long WAREHOUSE_UPDATETIME;

    /**
     * 数据进来的时间
     */
    @JSONField(serialize = false)
    private Long ts;
}
