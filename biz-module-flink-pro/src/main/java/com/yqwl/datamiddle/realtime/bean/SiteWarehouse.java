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
 * 站台-仓库对照表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_site_warehouse")
public class SiteWarehouse implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 站点代码
     */
      @TableField("VWLCKDM")
    private String vwlckdm;

      /**
     * 站点名称
     */
      @TableField("VWLCKMC")
    private String vwlckmc;

      /**
     * 库房ID
     */
      @TableField("WAREHOUSE_ID")
    private Integer warehouseId;

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

      /**
     * 类型：对照contrast；仓库：warehouse
     */
      @TableField("TYPE")
    private String type;

    @TableField("WAREHOUSE_CREATETIME")
    private Long warehouseCreatetime;

    @TableField("WAREHOUSE_UPDATETIME")
    private Long warehouseUpdatetime;


}
