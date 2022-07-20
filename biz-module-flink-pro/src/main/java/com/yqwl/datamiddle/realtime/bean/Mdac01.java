package com.yqwl.datamiddle.realtime.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>
 * 销售大区。销售代表处属于销售组织，销售大区属于销售区域，所以销售大区不属于部门级次中。	但大区与销售代表处有对照关系，主要用于查询
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Mdac01 implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     *  大区代码
     */

    private String CDQDM;

    /**
     * 大区名称
     */

    private String VDQMC;

    /**
     * 大区联系人
     */

    private String CLXRDM;

    /**
     * 大区联系电话
     */

    private String VDH;

    /**
     * 大区负责人
     */

    private String CFZRDM;

    /**
     * 省区代码
     */

    private String CSQDM;

    /**
     * 市县代码
     */

    private String CSXDM;

    /**
     * 地址
     */

    private String VDZ;

    /**
     * 传真
     */

    private String VCZ;

    /**
     * e_mail
     */

    private String VE_MAIL;

    /**
     * 邮编
     */

    private String CYB;

    /**
     * 没有可录入项，但需要特别说明的信息
     */

    private String VBZ;

    /**
     * 0、在用，1、停用
     */

    private String CTYBS;

    /**
     * 停用日期
     */

    private Long DTYRQ;

    /**
     * 创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */

    private Long WAREHOUSE_UPDATETIME;

    /**
     * 更新时间
     */
    @JSONField(serialize = false)
    private Long ts;


}
