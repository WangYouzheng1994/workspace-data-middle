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
 * 销售大区。销售代表处属于销售组织，销售大区属于销售区域，所以销售大区不属于部门级次中。	但大区与销售代表处有对照关系，主要用于查询
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_mdac01")
public class Mdac01 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 大区代码
     */
      @TableField("CDQDM")
    private String cdqdm;

      /**
     * 大区名称
     */
      @TableField("VDQMC")
    private String vdqmc;

      /**
     * 大区联系人
     */
      @TableField("CLXRDM")
    private String clxrdm;

      /**
     * 大区联系电话
     */
      @TableField("VDH")
    private String vdh;

      /**
     * 大区负责人
     */
      @TableField("CFZRDM")
    private String cfzrdm;

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
     * 地址
     */
      @TableField("VDZ")
    private String vdz;

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
     * 邮编
     */
      @TableField("CYB")
    private String cyb;

      /**
     * 没有可录入项，但需要特别说明的信息
     */
      @TableField("VBZ")
    private String vbz;

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
