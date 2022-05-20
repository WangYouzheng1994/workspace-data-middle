package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 承运队信息
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Mdac52 implements Serializable {
    private static final long serialVersionUID = 1L;

    private String CCYSDM;

    /**
     * 代码
     */

    private String CCYDDM;

    /**
     * 名称
     */

    private String VCYDMC;

    /**
     * 0/未认证，1/认证
     */

    private String CRZBS;


    private String CTYBS;


    private Long DTYRQ;


    private String VBZ;

    /**
     * 简称
     */

    private String VCYDJC;

    /**
     * 地址
     */

    private String VDZ;

    /**
     * 法人代表
     */

    private String CFRDB;

    /**
     * 开户行
     */

    private String VKHH;

    /**
     * 帐号
     */

    private String VYHZH;

    /**
     * 税号
     */

    private String VNSDJH;

    /**
     * 负责人
     */

    private String VFZR;

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
     * 传真
     */

    private String VCZ;


    private String VE_MAIL;

    /**
     * 注册地址
     */

    private String VZCDZ;

    /**
     * 运输车辆情况：比如：大/50，中/40，小/100等，既字符也可数字
     */

    private String VCLQK;

    /**
     * dd
     */

    private String CYB;

    /**
     * 对应个人代码
     */

    private String CGRDM;

    /**
     * 收取管理费方式:D收取运费的5%,0正常收,即单价为运费3%+里程1%；该字段无前台界面支持按陆捷公司要求手工改
     */

    private String CBS;

    /**
     * 区域
     */

    private String VQY;

    /**
     * 所属
     */

    private String CSS;

    /**
     * 0 不收管理费，1收管理费 用于业务系统接口
     */

    private String CGLFBS;

    /**
     * 多公司模式下的公司-SPTC60
     */

    private String CGS;

    /**
     * SPG码
     */

    private String CSPGDM;

    /**
     * 2010-06-08才增加的对流标识本应该早发布的
     */

    private String CDLBS;

    /**
     * 运输商性质
     */

    private String CYSSXZ;

    /**
     * 运输商属性（自有、站队），用于卡车运输商。字典：YSSSX
     */

    private String CYSSSX;

    /**
     * 序号
     */

    private String CXH;

    /**
     * 历史SPG码
     */

    private String CSPGDM_H;

    /**
     * 结算单位代码
     */

    private String CLJSDM;

    /**
     * 针对非长春有效
     */

    private String CYSSDM_O;

    /**
     * 物流标准编码。选择，来源于M平台
     */

    private String CWLBM;

    /**
     * 物流标准名称。来源于M平台
     */

    private String CWLMC;

    /**
     * 同步日期
     */

    private Long DTBRQ;

    /**
     * 版本号
     */

    private Long BATCHNO;


    private String CCYSDM_O;

    /**
     * 运输商类型 全程控制标识
     */

    private String CYSSLX;

    /**
     * 轿车接口运输商代码。来源于tds_vlms_ccyddm
     */

    private String CYSSDM_2;

    /**
     * 时间戳。BI提数据
     */

    private Long DSTAMP;

    /**
     * 客户标准码
     */

    private String CWLBM_K;

    /**
     * 轿车TDS的运输商名称
     */

    private String VCYDMC_2;

    /**
     * 红旗接口运输商代码。来源于tds_vlms_ccyddm
     */

    private String CYSSDM_3;

    /**
     * 红旗TDS的运输商名称
     */

    private String VCYDMC_3;

    /**
     * 马自达接口运输商代码。来源于tds_vlms_ccyddm
     */

    private String CYSSDM_4;

    /**
     * 马自达TDS的运输商名称
     */

    private String VCYDMC_4;

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
     * 解放青岛接口运输商代码。
     */

    private String CYSSDM_5;

    /**
     * 解放青岛TDS的运输商名称
     */

    private String VCYDMC_5;

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
     * G公路 T 铁路 S 水路
     */

    private String VYSSNATURE;

    /**
     * 20200811统销 解放青岛接口运输商代码。
     */

    private String CYSSDM_6;

    /**
     * 20200811统销 解放青岛TDS的运输商名称
     */

    private String VCYDMC_6;


    private Long WAREHOUSE_CREATETIME;


    private Long WAREHOUSE_UPDATETIME;


}
