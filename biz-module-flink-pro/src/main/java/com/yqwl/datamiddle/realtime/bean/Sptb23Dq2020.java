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
    @TableName("ods_vlms_sptb23_dq_2020")
public class Sptb23Dq2020 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

    @TableField("VIN")
    private String vin;

    @TableField("CH")
    private String ch;

    @TableField("JJDH")
    private String jjdh;

    @TableField("XDSJ")
    private Long xdsj;

    @TableField("PP")
    private String pp;

    @TableField("DCS")
    private String dcs;

    @TableField("FZ")
    private String fz;

    @TableField("DZ")
    private String dz;

    @TableField("FCSJ")
    private Long fcsj;

    @TableField("DDSJ")
    private Long ddsj;

    @TableField("SHENG")
    private String sheng;

    @TableField("SHI")
    private String shi;

    @TableField("XIAN")
    private String xian;

    @TableField("BGSJ")
    private Long bgsj;

    @TableField("ZKBZ")
    private String zkbz;

    @TableField("DCZRQ")
    private Long dczrq;

    @TableField("CBS")
    private String cbs;

    @TableField("JD")
    private BigDecimal jd;

    @TableField("WD")
    private BigDecimal wd;

      /**
     * 主键 S_SPTB23_DQ_2020
     */
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
