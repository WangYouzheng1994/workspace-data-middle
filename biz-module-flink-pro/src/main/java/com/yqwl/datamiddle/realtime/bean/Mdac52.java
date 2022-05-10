package com.yqwl.datamiddle.realtime.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 承运队信息
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_mdac52")
public class Mdac52 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

    @TableField("CCYSDM")
    private String ccysdm;

      /**
     * 代码
     */
      @TableField("CCYDDM")
    private String ccyddm;

      /**
     * 名称
     */
      @TableField("VCYDMC")
    private String vcydmc;

      /**
     * 0/未认证，1/认证
     */
      @TableField("CRZBS")
    private String crzbs;

    @TableField("CTYBS")
    private String ctybs;

    @TableField("DTYRQ")
    private Long dtyrq;

    @TableField("VBZ")
    private String vbz;

      /**
     * 简称
     */
      @TableField("VCYDJC")
    private String vcydjc;

      /**
     * 地址
     */
      @TableField("VDZ")
    private String vdz;

      /**
     * 法人代表
     */
      @TableField("CFRDB")
    private String cfrdb;

      /**
     * 开户行
     */
      @TableField("VKHH")
    private String vkhh;

      /**
     * 帐号
     */
      @TableField("VYHZH")
    private String vyhzh;

      /**
     * 税号
     */
      @TableField("VNSDJH")
    private String vnsdjh;

      /**
     * 负责人
     */
      @TableField("VFZR")
    private String vfzr;

      /**
     * 联系人
     */
      @TableField("CLXR")
    private String clxr;

      /**
     * 电话
     */
      @TableField("VDH")
    private String vdh;

      /**
     * 移动电话
     */
      @TableField("VYDDH")
    private String vyddh;

      /**
     * 传真
     */
      @TableField("VCZ")
    private String vcz;

    @TableField("VE_MAIL")
    private String veMail;

      /**
     * 注册地址
     */
      @TableField("VZCDZ")
    private String vzcdz;

      /**
     * 运输车辆情况：比如：大/50，中/40，小/100等，既字符也可数字
     */
      @TableField("VCLQK")
    private String vclqk;

      /**
     * dd
     */
      @TableField("CYB")
    private String cyb;

      /**
     * 对应个人代码
     */
      @TableField("CGRDM")
    private String cgrdm;

      /**
     * 收取管理费方式:D收取运费的5%,0正常收,即单价为运费3%+里程1%；该字段无前台界面支持按陆捷公司要求手工改
     */
      @TableField("CBS")
    private String cbs;

      /**
     * 区域
     */
      @TableField("VQY")
    private String vqy;

      /**
     * 所属
     */
      @TableField("CSS")
    private String css;

      /**
     * 0 不收管理费，1收管理费 用于业务系统接口
     */
      @TableField("CGLFBS")
    private String cglfbs;

      /**
     * 多公司模式下的公司-SPTC60
     */
      @TableField("CGS")
    private String cgs;

      /**
     * SPG码
     */
      @TableField("CSPGDM")
    private String cspgdm;

      /**
     * 2010-06-08才增加的对流标识本应该早发布的
     */
      @TableField("CDLBS")
    private String cdlbs;

      /**
     * 运输商性质
     */
      @TableField("CYSSXZ")
    private String cyssxz;

      /**
     * 运输商属性（自有、站队），用于卡车运输商。字典：YSSSX
     */
      @TableField("CYSSSX")
    private String cysssx;

      /**
     * 序号
     */
      @TableField("CXH")
    private String cxh;

      /**
     * 历史SPG码
     */
      @TableField("CSPGDM_H")
    private String cspgdmH;

      /**
     * 结算单位代码
     */
      @TableField("CLJSDM")
    private String cljsdm;

      /**
     * 针对非长春有效
     */
      @TableField("CYSSDM_O")
    private String cyssdmO;

      /**
     * 物流标准编码。选择，来源于M平台
     */
      @TableField("CWLBM")
    private String cwlbm;

      /**
     * 物流标准名称。来源于M平台
     */
      @TableField("CWLMC")
    private String cwlmc;

      /**
     * 同步日期
     */
      @TableField("DTBRQ")
    private Long dtbrq;

      /**
     * 版本号
     */
      @TableField("BATCHNO")
    private Long batchno;

    @TableField("CCYSDM_O")
    private String ccysdmO;

      /**
     * 运输商类型 全程控制标识
     */
      @TableField("CYSSLX")
    private String cysslx;

      /**
     * 轿车接口运输商代码。来源于tds_vlms_ccyddm
     */
      @TableField("CYSSDM_2")
    private String cyssdm2;

      /**
     * 时间戳。BI提数据
     */
      @TableField("DSTAMP")
    private Long dstamp;

      /**
     * 客户标准码
     */
      @TableField("CWLBM_K")
    private String cwlbmK;

      /**
     * 轿车TDS的运输商名称
     */
      @TableField("VCYDMC_2")
    private String vcydmc2;

      /**
     * 红旗接口运输商代码。来源于tds_vlms_ccyddm
     */
      @TableField("CYSSDM_3")
    private String cyssdm3;

      /**
     * 红旗TDS的运输商名称
     */
      @TableField("VCYDMC_3")
    private String vcydmc3;

      /**
     * 马自达接口运输商代码。来源于tds_vlms_ccyddm
     */
      @TableField("CYSSDM_4")
    private String cyssdm4;

      /**
     * 马自达TDS的运输商名称
     */
      @TableField("VCYDMC_4")
    private String vcydmc4;

      /**
     * 审批标识：0  未审批  1：已审批
     */
      @TableField("APPROVAL_FLAG")
    private String approvalFlag;

      /**
     * 审批人
     */
      @TableField("APPROVAL_USER")
    private String approvalUser;

      /**
     * 审批日期
     */
      @TableField("APPROVAL_DATE")
    private Long approvalDate;

      /**
     * 解放青岛接口运输商代码。
     */
      @TableField("CYSSDM_5")
    private String cyssdm5;

      /**
     * 解放青岛TDS的运输商名称
     */
      @TableField("VCYDMC_5")
    private String vcydmc5;

      /**
     * 终审审批标识：0  未审批  1：已审批
     */
      @TableField("FINAL_APPROVAL_FLAG")
    private String finalApprovalFlag;

      /**
     * 终审审批人
     */
      @TableField("FINAL_APPROVAL_USER")
    private String finalApprovalUser;

      /**
     * 终审审批日期
     */
      @TableField("FINAL_APPROVAL_DATE")
    private Long finalApprovalDate;

      /**
     * G公路 T 铁路 S 水路
     */
      @TableField("VYSSNATURE")
    private String vyssnature;

      /**
     * 20200811统销 解放青岛接口运输商代码。
     */
      @TableField("CYSSDM_6")
    private String cyssdm6;

      /**
     * 20200811统销 解放青岛TDS的运输商名称
     */
      @TableField("VCYDMC_6")
    private String vcydmc6;

    @TableField("WAREHOUSE_CREATETIME")
    private Long warehouseCreatetime;

    @TableField("WAREHOUSE_UPDATETIME")
    private Long warehouseUpdatetime;


}
