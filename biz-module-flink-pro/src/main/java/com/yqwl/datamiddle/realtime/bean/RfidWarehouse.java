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
 * 20191026 大屏展示 默认仓库表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_rfid_warehouse")
public class RfidWarehouse implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

    @TableField("ID")
    private Integer id;

      /**
     * 库房代码
     */
      @TableField("WAREHOUSE_CODE")
    private String warehouseCode;

      /**
     * 库房名称
     */
      @TableField("WAREHOUSE_NAME")
    private String warehouseName;

      /**
     * 归属基地
     */
      @TableField("PART_BASE")
    private String partBase;

      /**
     * 库房类型（基地库：T1  分拨中心库:T2  港口  T3  站台  T4）
     */
      @TableField("WAREHOUSE_TYPE")
    private String warehouseType;

      /**
     * 站台对应的城市
     */
      @TableField("CITY")
    private String city;

      /**
     * 默认在库量  number 转 int
     */
      @TableField("DEFALUT_VALUE")
    private Integer defalutValue;

      /**
     * 标准库容     number 转 int
     */
      @TableField("BZKR")
    private Integer bzkr;

      /**
     * 最大库容     number 转 int
     */
      @TableField("ZDKR")
    private Integer zdkr;

      /**
     * 数据中台代码
     */
      @TableField("DATACENTER_CODE")
    private String datacenterCode;

      /**
     * 顺序 2019 12 23 宋琳添加   number 转 int
     */
      @TableField("SEQUENCE")
    private Integer sequence;

      /**
     * 省份2019 12 23 宋琳添加
     */
      @TableField("PROVINCE")
    private String province;

      /**
     * 整车数据代码
     */
      @TableField("ZC_CODE")
    private String zcCode;

      /**
     * 主机公司代码 1 大众  2 奔腾 3解放  17 红旗  29 马自达
     */
      @TableField("CZJGSDM")
    private String czjgsdm;

      /**
     * 数据中台代码备份
     */
      @TableField("DATACENTER_CODE_BZK")
    private String datacenterCodeBzk;

      /**
     * 所属省份
     */
      @TableField("PROVICE")
    private String provice;

    @TableField("WAREHOUSE_CREATETIME")
    private Long warehouseCreatetime;

    @TableField("WAREHOUSE_UPDATETIME")
    private Long warehouseUpdatetime;


}
