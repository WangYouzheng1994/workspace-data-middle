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
    @TableName("ods_vlms_sptb22_dq")
public class Sptb22Dq implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

    @TableField("ID")
    private Integer id;

    @TableField("CPZH")
    private String cpzh;

    @TableField("VWZ")
    private String vwz;

    @TableField("DCJSJ")
    private Long dcjsj;

    @TableField("VSD")
    private String vsd;

    @TableField("CJSDBH")
    private String cjsdbh;

    @TableField("CSQDM")
    private String csqdm;

    @TableField("CSXDM")
    private String csxdm;

    @TableField("DCZRQ")
    private Long dczrq;

    @TableField("CDHBS")
    private String cdhbs;

      /**
     * 0 gps/ 1 lbs
     */
      @TableField("CLYBS")
    private String clybs;

      /**
     * 离长时间
     */
      @TableField("DLCSJ")
    private Long dlcsj;

    @TableField("CSQ")
    private String csq;

    @TableField("CSX")
    private String csx;

    @TableField("DDKSJ")
    private Long ddksj;

      /**
     * 车辆滞留标识（两次取得数据相同认为是滞留）
     */
      @TableField("CZLBS")
    private String czlbs;

      /**
     * 上次位置
     */
      @TableField("VYWZ")
    private String vywz;

    @TableField("CBZ")
    private String cbz;

    @TableField("CSJH")
    private String csjh;

    @TableField("DLBSSJ")
    private Long dlbssj;

    @TableField("VLBSWZ")
    private String vlbswz;

      /**
     * 采点时段形如'20091004 02'
     */
      @TableField("CSD")
    private String csd;

      /**
     * 滞留小时
     */
      @TableField("NLJZT")
    private Integer nljzt;

      /**
     * 经度
     */
      @TableField("CCBJD")
    private BigDecimal ccbjd;

      /**
     * 纬度
     */
      @TableField("CCBWD")
    private BigDecimal ccbwd;

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
