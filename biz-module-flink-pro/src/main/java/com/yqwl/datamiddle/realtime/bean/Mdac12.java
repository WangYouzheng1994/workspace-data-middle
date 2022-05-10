package com.yqwl.datamiddle.realtime.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_mdac12")
public class Mdac12 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 产品代码
     */
      @TableField("CCPDM")
    private String ccpdm;

      /**
     * 产品名称
     */
      @TableField("VCPMC")
    private String vcpmc;

    @TableField("VCPJC")
    private String vcpjc;

    @TableField("NAQKC")
    private Integer naqkc;

    @TableField("NPL")
    private Integer npl;

    @TableField("NPLZL")
    private Integer nplzl;

    @TableField("CTYBS")
    private String ctybs;

    @TableField("DTYRQ")
    private Long dtyrq;

    @TableField("CPP")
    private String cpp;

    @TableField("CCXDL")
    private String ccxdl;

      /**
     * 20190404 30-40 红旗车型33  40-100 20210528
     */
      @TableField("CCXDM")
    private String ccxdm;

    @TableField("CJHTY")
    private String cjhty;

    @TableField("DJHTYRQ")
    private Long djhtyrq;

    @TableField("CDDTY")
    private String cddty;

    @TableField("DDDTYRQ")
    private Long dddtyrq;

    @TableField("CLSTY")
    private String clsty;

    @TableField("DLSTYRQ")
    private Long dlstyrq;

    @TableField("NCCFDJ")
    private BigDecimal nccfdj;

    @TableField("CPHBS")
    private String cphbs;

    @TableField("CJHDBBS")
    private String cjhdbbs;

    @TableField("VBZ")
    private String vbz;

    @TableField("CJKBS")
    private String cjkbs;

    @TableField("ID")
    private Integer id;

      /**
     * 描述
     */
      @TableField("VMS")
    private String vms;

    @TableField("NFWFDJ")
    private Integer nfwfdj;

      /**
     * 库龄设置，单位为天
     */
      @TableField("NKLSZ")
    private Integer nklsz;

    @TableField("CXSTY")
    private String cxsty;

      /**
     * 多公司模式下的公司-SPTC60
     */
      @TableField("CGS")
    private String cgs;

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

    @TableField("FINAL_APPROVAL_FLAG")
    private String finalApprovalFlag;

    @TableField("FINAL_APPROVAL_USER")
    private String finalApprovalUser;

    @TableField("FINAL_APPROVAL_DATE")
    private Long finalApprovalDate;

      /**
     * 创建时间
     */
      @TableField("WAREHOUSE_CREATETIME")
    private Long warehouseCreatetime;

      /**
     * 更新时间
     */
      @TableField("WAREHOUSE_UPDATETIME")
    private Long warehouseUpdatetime;


}
