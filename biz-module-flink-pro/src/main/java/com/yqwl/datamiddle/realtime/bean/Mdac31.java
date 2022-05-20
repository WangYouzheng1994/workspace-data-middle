package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 承运商代码。承运商代码，用于运输
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Mdac31 implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 承运商代码
     */

    private String CCYSDM;

    /**
     * 承运商名称
     */

    private String VCYSMC;

    /**
     * 联系人
     */

    private String CLXR;

    /**
     * 省区代码
     */

    private String CSQDM;

    /**
     * 市县代码
     */

    private String CSXDM;

    /**
     * 电话
     */

    private String VDH;

    /**
     * 移动电话
     */

    private String VYDDH;

    /**
     * 传真
     */

    private String VCZ;

    /**
     * e_mail
     */

    private String VE_MAIL;

    /**
     * 0、在用，1、停用
     */

    private String CTYBS;

    /**
     * 停用日期
     */

    private Long DTYRQ;

    /**
     * 机构代码
     */

    private String CJGDM;

    /**
     * 0/承输商，1/仓储商
     */

    private String CBS;

    /**
     * 负责人
     */

    private String VFZR;

    /**
     * 开户行
     */

    private String VKHH;

    /**
     * 银行帐号
     */

    private String VYHZH;

    /**
     * 纳税登记号
     */

    private String VNSDJH;

    /**
     * 法人代表
     */

    private String CFRDB;

    /**
     * 地址
     */

    private String VDZ;

    /**
     * 简称
     */

    private String VCYSJC;

    /**
     * 注册地址
     */

    private String VZCDZ;

    /**
     * 合同起始日期
     */

    private Long DQSRQ;

    /**
     * 合同截止日期
     */

    private Long DJZRQ;


    private String VBZ;

    /**
     * 承运标识-承运商或加盟商
     */

    private String CGRDM;


    private String CSSXT;

    /**
     * 多公司模式下的公司-SPTC60
     */

    private String CGS;

    /**
     * 序号
     */

    private String CXH;

    /**
     * 时间戳。BI提数据
     */

    private Long DSTAMP;

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
