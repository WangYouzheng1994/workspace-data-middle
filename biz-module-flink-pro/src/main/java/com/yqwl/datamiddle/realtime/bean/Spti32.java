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
 * 物流标准时间
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_spti32")
public class Spti32 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 起始省区代码
     */
      @TableField("CQSSQDM")
    private String cqssqdm;

      /**
     * 起始市县代码
     */
      @TableField("CQSCSDM")
    private String cqscsdm;

      /**
     * 目标省区代码
     */
      @TableField("CMBSQDM")
    private String cmbsqdm;

      /**
     * 目标市县代码
     */
      @TableField("CMBCSDM")
    private String cmbcsdm;

      /**
     * 里程
     */
      @TableField("NLC")
    private BigDecimal nlc;

      /**
     * 主机公司。字典：WTDW
     */
      @TableField("CZJGS")
    private String czjgs;

      /**
     * 运输方式  G-公路 T-铁路 D-短驳
     */
      @TableField("VYSFS")
    private String vysfs;

      /**
     * 在途时间(出库-到货)
     */
      @TableField("NZTSJ")
    private Long nztsj;

      /**
     * 物流时间
     */
      @TableField("NTS")
    private Long nts;

      /**
     * 备注
     */
      @TableField("CBZ")
    private String cbz;

      /**
     * 操作日期
     */
      @TableField("DCZRQ")
    private Long dczrq;

      /**
     * 操作员
     */
      @TableField("CCZYDM")
    private String cczydm;

      /**
     * 标准GPS到货时间
     */
      @TableField("NDHSJ_GPS")
    private Long ndhsjGps;

      /**
     * 标准系统到货时间
     */
      @TableField("NDHSJ_XT")
    private Long ndhsjXt;

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
