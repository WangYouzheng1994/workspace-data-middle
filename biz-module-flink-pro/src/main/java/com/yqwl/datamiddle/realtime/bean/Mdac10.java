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
 * 
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_mdac10")
public class Mdac10 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

    @TableField("CPP")
    private String cpp;

    @TableField("VPPSM")
    private String vppsm;

    @TableField("CTYBS")
    private String ctybs;

    @TableField("DTYRQ")
    private Long dtyrq;

      /**
     * 对应委托单位.与字典表SYSC09D对应CZDDM = 'WTDW'
     */
      @TableField("NGSDJ")
    private Integer ngsdj;

      /**
     * 排序
     */
      @TableField("CPX")
    private String cpx;

      /**
     * 计划标识.0不检查，1/检查
     */
      @TableField("CFHBS")
    private String cfhbs;

      /**
     * ID
     */
      @TableField("ID")
    private Integer id;

    @TableField("CCQCK")
    private String ccqck;

    @TableField("CWLWZ")
    private String cwlwz;

      /**
     * 主机厂公司
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
