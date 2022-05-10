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
 * 承运商代码。承运商代码，用于运输
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_mdac31")
public class Mdac31 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 承运商代码
     */
      @TableField("CCYSDM")
    private String ccysdm;

      /**
     * 承运商名称
     */
      @TableField("VCYSMC")
    private String vcysmc;

      /**
     * 联系人
     */
      @TableField("CLXR")
    private String clxr;

      /**
     * 省区代码
     */
      @TableField("CSQDM")
    private String csqdm;

      /**
     * 市县代码
     */
      @TableField("CSXDM")
    private String csxdm;

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

      /**
     * e_mail
     */
      @TableField("VE_MAIL")
    private String veMail;

      /**
     * 0、在用，1、停用
     */
      @TableField("CTYBS")
    private String ctybs;

      /**
     * 停用日期
     */
      @TableField("DTYRQ")
    private Long dtyrq;

      /**
     * 机构代码
     */
      @TableField("CJGDM")
    private String cjgdm;

      /**
     * 0/承输商，1/仓储商
     */
      @TableField("CBS")
    private String cbs;

      /**
     * 负责人
     */
      @TableField("VFZR")
    private String vfzr;

      /**
     * 开户行
     */
      @TableField("VKHH")
    private String vkhh;

      /**
     * 银行帐号
     */
      @TableField("VYHZH")
    private String vyhzh;

      /**
     * 纳税登记号
     */
      @TableField("VNSDJH")
    private String vnsdjh;

      /**
     * 法人代表
     */
      @TableField("CFRDB")
    private String cfrdb;

      /**
     * 地址
     */
      @TableField("VDZ")
    private String vdz;

      /**
     * 简称
     */
      @TableField("VCYSJC")
    private String vcysjc;

      /**
     * 注册地址
     */
      @TableField("VZCDZ")
    private String vzcdz;

      /**
     * 合同起始日期
     */
      @TableField("DQSRQ")
    private Long dqsrq;

      /**
     * 合同截止日期
     */
      @TableField("DJZRQ")
    private Long djzrq;

    @TableField("VBZ")
    private String vbz;

      /**
     * 承运标识-承运商或加盟商
     */
      @TableField("CGRDM")
    private String cgrdm;

    @TableField("CSSXT")
    private String cssxt;

      /**
     * 多公司模式下的公司-SPTC60
     */
      @TableField("CGS")
    private String cgs;

      /**
     * 序号
     */
      @TableField("CXH")
    private String cxh;

      /**
     * 时间戳。BI提数据
     */
      @TableField("DSTAMP")
    private Long dstamp;

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

    @TableField("WAREHOUSE_CREATETIME")
    private Long warehouseCreatetime;

    @TableField("WAREHOUSE_UPDATETIME")
    private Long warehouseUpdatetime;


}
