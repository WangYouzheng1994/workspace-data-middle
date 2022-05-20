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
public class Mdac32 implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 到货地代码 6
     */

    private String CDHDDM;

    /**
     * 经销商代码 10
     */

    private String CJXSDM;

    /**
     * 省区代码    4
     */

    private String CSQDM;

    /**
     * 市县代码    4
     */

    private String CSXDM;

    /**
     * 联系人
     */

    private String CLXR;

    /**
     * 电话
     */

    private String VDH;

    /**
     * 移动电话
     */

    private String VYDDH;

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
     * 地址 100   modify by dtf 20181113 100-200与TDS一致！
     */

    private String VDZ;

    /**
     * 收车人
     */

    private String VSCR;

    /**
     * 有效证件
     */

    private String VYXZJ;

    /**
     * 证件号
     */

    private String VZJH;

    /**
     * 到货地名称 100
     */

    private String VDHDMC;

    /**
     * ID
     */

    private Integer ID;

    /**
     * 传真
     */

    private String VCZ;

    /**
     * ???1
     */

    private String CLXR1;

    /**
     * ??1
     */

    private String VDH1;

    /**
     * ????1
     */

    private String VYDDH1;

    /**
     * ???1
     */

    private String VSCR1;

    /**
     * ???1
     */

    private String VZJH1;


    private String CSSXT;

    /**
     * ????2
     */

    private String VYXZJ1;


    private Long DCZRQ;


    private String CCZYDM;

    /**
     * 时间戳。BI提数据
     */

    private Long DSTAMP;

    /**
     * 20181009 add by dtf 备注
     */

    private String VBZ;

    /**
     * 20181207 add by lwx 备注
     */

    private String VBZ1;


    private Long WAREHOUSE_CREATETIME;


    private Long WAREHOUSE_UPDATETIME;


}
