package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 人员信息表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-23
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Sysc03 implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 个人代码
     */

    private String CGRDM;

    /**
     * 个人姓名
     */

    private String CGRXM;

    /**
     * 用户名
     */

    private String VYHM;

    /**
     * 登录口令 20190715 DTF 6-50
     */

    private String VDLKL;

    /**
     * 性别
     */

    private String CXB;

    /**
     * 参加工作时间
     */

    private Long DCJGJ;

    /**
     * 所属组织机构代码
     */

    private String CJGDM;

    /**
     * 所属经销商代码
     */

    private String CJXSM;

    /**
     * 电话
     */

    private String VDH;

    /**
     * 移动电话
     */

    private String VYDDH;

    /**
     * 地址
     */

    private String VDZ;

    /**
     * 邮编
     */

    private String CYB;

    /**
     * 电子邮件
     */

    private String VDZYJ;

    /**
     * 职位
     */

    private String CZW;

    /**
     * 停用标识
     */

    private String CTYBS;

    /**
     * 停用日期
     */

    private Long DTYRQ;

    /**
     * 执行标识。不用维护。
     */

    private String CZXBS;

    /**
     * 不用界面维护。
     */

    private Long DXGRQ;


    private String SZSSN;


    private String VSCDZ;


    private Long ID;

    /**
     * 多公司模式下的公司-SPTC60
     */

    private String CGS;

    /**
     * 部门代码。字典BMLB
     */

    private String CBMDM;

    /**
     * 基地代码。字典SPTDZJD
     */

    private String CQWH;

    /**
     * 时间戳。BI提数据
     */

    private Long DSTAMP;

    /**
     * 核算系统验证码20180327
     */

    private String CYZID;

    /**
     * 身份证号
     */

    private String CSFZH;

    /**
     * 微信ID
     */

    private String WXCP_ID;

    /**
     * 20190829 DTF 修改密码时间
     */

    private Long DCDATE;

    /**
     * 20190829 DTF 修改密码标识 1改过；0默认 未改过
     */

    private String CCFLAG;


    private Long WAREHOUSE_CREATETIME;


    private Long WAREHOUSE_UPDATETIME;


}
