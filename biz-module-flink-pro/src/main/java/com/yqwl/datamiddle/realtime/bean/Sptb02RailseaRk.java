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
 * 铁水运单溯源接口入库时间
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_sptb02_railsea_rk")
public class Sptb02RailseaRk implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 结算单编号 主键
     */
      @TableField("CJSDBH")
    private String cjsdbh;

      /**
     * VIN 
     */
      @TableField("VVIN")
    private String vvin;

      /**
     * 物理仓库代码（站台代码）
     */
      @TableField("VWLCKDM")
    private String vwlckdm;

      /**
     * 物理仓库名称 增加长度：由20增至50
     */
      @TableField("VWLCKMC")
    private String vwlckmc;

      /**
     * 溯源库房ID
     */
      @TableField("WAREHOUSE_ID")
    private Integer warehouseId;

      /**
     * 溯源库房代码
     */
      @TableField("WAREHOUSE_CODE")
    private String warehouseCode;

      /**
     * 溯源库房名称
     */
      @TableField("WAREHOUSE_NAME")
    private String warehouseName;

      /**
     * 入库日期
     */
      @TableField("DRKRQ")
    private Long drkrq;

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
