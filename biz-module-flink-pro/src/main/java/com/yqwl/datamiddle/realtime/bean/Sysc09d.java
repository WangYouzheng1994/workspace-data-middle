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
 * 数据字典表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_sysc09d")
public class Sysc09d implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 对应sysc09c.CZDDM 对应
     */
      @TableField("CZDDM")
    private String czddm;

      /**
     * 要展开数据项的编码
     */
      @TableField("CSJXM")
    private String csjxm;

      /**
     * 数据项名称 由500改为4000
     */
      @TableField("VSJXC")
    private String vsjxc;

      /**
     * 级别
     */
      @TableField("CJB")
    private String cjb;

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
     * 备注
     */
      @TableField("VBZ")
    private String vbz;

    @TableField("VBZ2")
    private String vbz2;

      /**
     * 用于特殊运输商
     */
      @TableField("VBZ3")
    private String vbz3;

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
