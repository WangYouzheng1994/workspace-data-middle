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
 * 地市级代码表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_sysc09")
public class Sysc09 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 地市代码
     */
      @TableField("CDSDM")
    private String cdsdm;

      /**
     * 省区代码
     */
      @TableField("CSQDM")
    private String csqdm;

      /**
     * 地市名称 20 - 50
     */
      @TableField("VDSMC")
    private String vdsmc;

      /**
     * 备注 20 50
     */
      @TableField("VBZ")
    private String vbz;

      /**
     * 时间戳。BI提数据
     */
      @TableField("DSTAMP")
    private Long dstamp;

      /**
     * "物流标准编码。选择，来源于M平台
"
     */
      @TableField("CWLBM")
    private String cwlbm;

      /**
     * "物流标准名称。来源于M平台
"
     */
      @TableField("CWLMC")
    private String cwlmc;

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
