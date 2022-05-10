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
 * 铁水标准物流时间
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_spti32_rail_sea")
public class Spti32RailSea implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 主机公司代码
     */
      @TableField("CZJGSDM")
    private String czjgsdm;

      /**
     * 运输方式  T-铁路 S-水运
     */
      @TableField("VYSFS")
    private String vysfs;

      /**
     * 起始站台/港口代码
     */
      @TableField("CQSZTDM")
    private String cqsztdm;

      /**
     * 目标站台/港口代码
     */
      @TableField("CMBZTDM")
    private String cmbztdm;

      /**
     * 在途标准物流时间（出库-系统到货）
     */
      @TableField("NDHSJ_ZT")
    private BigDecimal ndhsjZt;

      /**
     * 系统到货标准物流时间-天
（满列）26节以上
     */
      @TableField("NDHSJ_XTDH_ML")
    private BigDecimal ndhsjXtdhMl;

      /**
     * 系统到货标准物流时间-天
（大组）15-26节
     */
      @TableField("NDHSJ_XTDH_DZ")
    private BigDecimal ndhsjXtdhDz;

      /**
     * 系统到货标准物流时间-天
（散列）15节及以下
     */
      @TableField("NDHSJ_XTDH_SL")
    private BigDecimal ndhsjXtdhSl;

      /**
     * 到站标准物流时间-天
（满列）26节以上
     */
      @TableField("NDHSJ_DZ_ML")
    private BigDecimal ndhsjDzMl;

      /**
     * 到站标准物流时间-天
（大组）15-26节
     */
      @TableField("NDHSJ_DZ_DZ")
    private BigDecimal ndhsjDzDz;

      /**
     * 到站标准物流时间-天
（散列）15节及以下
     */
      @TableField("NDHSJ_DZ_SL")
    private BigDecimal ndhsjDzSl;

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

    @TableField("WAREHOUSE_CREATETIME")
    private Long warehouseCreatetime;

    @TableField("WAREHOUSE_UPDATETIME")
    private Long warehouseUpdatetime;


}
