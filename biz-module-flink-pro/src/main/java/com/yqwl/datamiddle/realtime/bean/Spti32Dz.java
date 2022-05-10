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
 * 20210312 大众新标准物流时间表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_spti32_dz")
public class Spti32Dz implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 运输方式
     */
      @TableField("VYSFS")
    private String vysfs;

      /**
     * 类型:TC同城；YC异城；
     */
      @TableField("CTYPE")
    private String ctype;

      /**
     * 满载率:DZ大组；ML满列；SH散货；
     */
      @TableField("VMZL")
    private String vmzl;

      /**
     * 始发省区代码
     */
      @TableField("VQYSQDM")
    private String vqysqdm;

      /**
     * 始发城市代码
     */
      @TableField("VQYSXDM")
    private String vqysxdm;

      /**
     * 分拨省区代码
     */
      @TableField("VFBSQDM")
    private String vfbsqdm;

      /**
     * 分拨城市代码
     */
      @TableField("VFBSXDM")
    private String vfbsxdm;

      /**
     * 终到省份代码
     */
      @TableField("VMDSQDM")
    private String vmdsqdm;

      /**
     * 终到城市代码
     */
      @TableField("VMDSXDM")
    private String vmdsxdm;

      /**
     * 终到地级市代码
     */
      @TableField("VMDXQDM")
    private String vmdxqdm;

      /**
     * 终到运区代码
     */
      @TableField("VMDYQDM")
    private String vmdyqdm;

      /**
     * 分段物流时间:公路
     */
      @TableField("NWLSJ_G")
    private Long nwlsjG;

      /**
     * 分段物流时间:集港
     */
      @TableField("NWLSJ_J")
    private Long nwlsjJ;

      /**
     * 分段物流时间:铁水
     */
      @TableField("NWLSJ_TS")
    private Long nwlsjTs;

      /**
     * 分段物流时间:分拨
     */
      @TableField("NWLSJ_F")
    private Long nwlsjF;

      /**
     * 整体考核物流时间
     */
      @TableField("NWLSJ_ALL")
    private Long nwlsjAll;

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
