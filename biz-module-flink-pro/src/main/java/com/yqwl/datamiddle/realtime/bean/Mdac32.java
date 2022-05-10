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
    @TableName("ods_vlms_mdac32")
public class Mdac32 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 到货地代码 6
     */
      @TableField("CDHDDM")
    private String cdhddm;

      /**
     * 经销商代码 10
     */
      @TableField("CJXSDM")
    private String cjxsdm;

      /**
     * 省区代码    4 
     */
      @TableField("CSQDM")
    private String csqdm;

      /**
     * 市县代码    4
     */
      @TableField("CSXDM")
    private String csxdm;

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
     * 地址 100   modify by dtf 20181113 100-200与TDS一致！
     */
      @TableField("VDZ")
    private String vdz;

      /**
     * 收车人
     */
      @TableField("VSCR")
    private String vscr;

      /**
     * 有效证件
     */
      @TableField("VYXZJ")
    private String vyxzj;

      /**
     * 证件号
     */
      @TableField("VZJH")
    private String vzjh;

      /**
     * 到货地名称 100
     */
      @TableField("VDHDMC")
    private String vdhdmc;

      /**
     * ID
     */
      @TableField("ID")
    private Integer id;

      /**
     * 传真
     */
      @TableField("VCZ")
    private String vcz;

      /**
     * ???1
     */
      @TableField("CLXR1")
    private String clxr1;

      /**
     * ??1
     */
      @TableField("VDH1")
    private String vdh1;

      /**
     * ????1
     */
      @TableField("VYDDH1")
    private String vyddh1;

      /**
     * ???1
     */
      @TableField("VSCR1")
    private String vscr1;

      /**
     * ???1
     */
      @TableField("VZJH1")
    private String vzjh1;

    @TableField("CSSXT")
    private String cssxt;

      /**
     * ????2
     */
      @TableField("VYXZJ1")
    private String vyxzj1;

    @TableField("DCZRQ")
    private Long dczrq;

    @TableField("CCZYDM")
    private String cczydm;

      /**
     * 时间戳。BI提数据
     */
      @TableField("DSTAMP")
    private Long dstamp;

      /**
     * 20181009 add by dtf 备注
     */
      @TableField("VBZ")
    private String vbz;

      /**
     * 20181207 add by lwx 备注
     */
      @TableField("VBZ1")
    private String vbz1;

    @TableField("WAREHOUSE_CREATETIME")
    private Long warehouseCreatetime;

    @TableField("WAREHOUSE_UPDATETIME")
    private Long warehouseUpdatetime;


}
