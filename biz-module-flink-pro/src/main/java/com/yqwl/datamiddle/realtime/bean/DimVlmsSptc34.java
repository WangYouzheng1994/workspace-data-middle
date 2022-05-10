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
 * 物理仓库信息表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("dim_vlms_sptc34")
public class DimVlmsSptc34 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 物理仓库代码（站台代码)
     */
      @TableField("VWLCKDM")
    private String vwlckdm;

      /**
     * 增加长度: 由20增至50
     */
      @TableField("VWLCKMC")
    private String vwlckmc;

      /**
     * 状态(0:可用,1:盘点) 默认可用,暂时不用!
     */
      @TableField("CZT")
    private String czt;

      /**
     * 库容
     */
      @TableField("NKR")
    private Integer nkr;

      /**
     * 省区代码
     */
      @TableField("VSQDM")
    private String vsqdm;

      /**
     * 市县代码
     */
      @TableField("VSXDM")
    private String vsxdm;

      /**
     * 联系人
     */
      @TableField("VLXR")
    private String vlxr;

      /**
     * 电话
     */
      @TableField("VDH")
    private String vdh;

      /**
     * 传真
     */
      @TableField("VCZ")
    private String vcz;

      /**
     * EMAIL
     */
      @TableField("VEMAIL")
    private String vemail;

      /**
     * 移动电话
     */
      @TableField("VYDDH")
    private String vyddh;

      /**
     * 邮编
     */
      @TableField("VYB")
    private String vyb;

      /**
     * 地址20210108 50-100
     */
      @TableField("VDZ")
    private String vdz;

      /**
     * 停用标识
     */
      @TableField("CTYBS")
    private String ctybs;

      /**
     * 停用日期
     */
      @TableField("DTYRQ")
    private Long dtyrq;

    @TableField("VBZ")
    private String vbz;

      /**
     * 字典:WLCKSX,0公路 1铁路 2水路
     */
      @TableField("CCKSX")
    private String ccksx;

      /**
     * 管理库区库位. 0 管理  1 不管理
     */
      @TableField("CGLKQKW")
    private String cglkqkw;

    @TableField("CCCSDM")
    private String cccsdm;

      /**
     * 存放条件
     */
      @TableField("VCFTJ")
    private String vcftj;

      /**
     * 是否外协  0/本公司  1/外协
     */
      @TableField("CWX")
    private String cwx;

      /**
     * 多公司模式下的公司-SPTC60
     */
      @TableField("CGS")
    private String cgs;

      /**
     * 针对铁路站台,是否生成分驳计划 0 否  1 是
     */
      @TableField("CSCFBJH")
    private String cscfbjh;

      /**
     * 大众物理仓库代码
     */
      @TableField("VDZCKDM")
    private String vdzckdm;

      /**
     * 针对铁路站台,设置的默认短驳运输商
     */
      @TableField("CYSSDM")
    private String cyssdm;

      /**
     * 针对铁路站台,设置的默认运输车
     */
      @TableField("CYSCDM")
    private String cyscdm;

      /**
     * 仓库简称
     */
      @TableField("VWLCKJC")
    private String vwlckjc;

      /**
     * 物流标准编码  选择  来源于M平台
     */
      @TableField("CWLBM")
    private String cwlbm;

      /**
     * 物流标准名称 来源于M平台
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
    private Integer batchno;

    @TableField("CWLBM3")
    private String cwlbm3;

      /**
     * 字典:WLCKLX (1 基地库  2 中转库)
     */
      @TableField("CCKLX")
    private String ccklx;

      /**
     * 时间戳  BI提数据
     */
      @TableField("DSTAMP")
    private Long dstamp;

      /**
     * 审批标识  0 未审批  1 已审批
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
     * 终审审批标识  0 未审批  1 已审批
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
     * 主机公司代码  字典WTDW
     */
      @TableField("CZJGSDM")
    private String czjgsdm;

      /**
     * 20220309 中铁新接口 站点名称
     */
      @TableField("VZTMC_ZT")
    private String vztmcZt;

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
