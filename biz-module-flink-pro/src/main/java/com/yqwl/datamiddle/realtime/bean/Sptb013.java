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
 * 维护铁路批次，或水路批次
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_sptb013")
public class Sptb013 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 批次号
     */
      @TableField("VPH")
    private String vph;

      /**
     * 状态：0、初始；1、提交；2、完成
     */
      @TableField("CZT")
    private String czt;

      /**
     * 发车仓库
     */
      @TableField("VFCCK")
    private String vfcck;

      /**
     * 发车站台
     */
      @TableField("VFCZT")
    private String vfczt;

      /**
     * 收车站台
     */
      @TableField("VSCZT")
    private String vsczt;

      /**
     * 计划数量
     */
      @TableField("NJHSL")
    private Integer njhsl;

      /**
     * 实际数量
     */
      @TableField("NSJSL")
    private Integer nsjsl;

      /**
     * 运输商
     */
      @TableField("VYSS")
    private String vyss;

      /**
     * 操作员
     */
      @TableField("VCZY")
    private String vczy;

      /**
     * 操作日期
     */
      @TableField("DCZRQ")
    private Long dczrq;

      /**
     * 提交日期
     */
      @TableField("DTJRQ")
    private Long dtjrq;

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

      /**
     * 公司
     */
      @TableField("VGS")
    private String vgs;

      /**
     * 铁路列号
     */
      @TableField("VTLLH")
    private String vtllh;

      /**
     * 标识：1铁路，2水路
     */
      @TableField("CBS")
    private String cbs;

      /**
     * 分驳标识  0否，1是
     */
      @TableField("CFBBS")
    private String cfbbs;

      /**
     * 短驳运输商
     */
      @TableField("CDBYSS")
    private String cdbyss;

      /**
     * LJS代码 
     */
      @TableField("CLJSDM")
    private String cljsdm;

      /**
     * 船舶号
     */
      @TableField("CCBH")
    private String ccbh;

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
