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
    @TableName("ods_vlms_mdac11")
public class Mdac11 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 20190406 30-50
     */
      @TableField("CCXDM")
    private String ccxdm;

    @TableField("CCXDL")
    private String ccxdl;

    @TableField("VCXSM")
    private String vcxsm;

    @TableField("CTYBS")
    private String ctybs;

    @TableField("DTYRQ")
    private Long dtyrq;

    @TableField("CPP")
    private String cpp;

    @TableField("CJSCX")
    private String cjscx;

    @TableField("ID")
    private Integer id;

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
