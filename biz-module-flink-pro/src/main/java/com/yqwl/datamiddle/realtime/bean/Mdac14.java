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
 * 车型 大类
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_mdac14")
public class Mdac14 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

    @TableField("CCXDL")
    private String ccxdl;

    @TableField("VCXDLSM")
    private String vcxdlsm;

    @TableField("CPP")
    private String cpp;

    @TableField("CYSCX")
    private String cyscx;

    @TableField("CTYBS")
    private String ctybs;

    @TableField("DTYRQ")
    private Long dtyrq;

    @TableField("CVIN")
    private String cvin;

    @TableField("CBS")
    private String cbs;

    @TableField("CCXLB")
    private String ccxlb;

      /**
     * 运费对应价格分类。根据SPTB77.CPP计算运费
     */
      @TableField("VCPFL")
    private String vcpfl;

    @TableField("VWLFL")
    private String vwlfl;

    @TableField("ID")
    private Integer id;

    @TableField("CJHTY")
    private String cjhty;

      /**
     * 平台代码
     */
      @TableField("CPTDM")
    private String cptdm;

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
