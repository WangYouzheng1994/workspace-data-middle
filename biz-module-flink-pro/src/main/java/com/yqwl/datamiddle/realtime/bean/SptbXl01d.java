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
    @TableName("ods_vlms_sptb_xl01d")
public class SptbXl01d implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 线路代码
     */
      @TableField("CXLDM")
    private String cxldm;

      /**
     * 起点代码
     */
      @TableField("CQDDM")
    private String cqddm;

      /**
     * 起点名称
     */
      @TableField("VQDMC")
    private String vqdmc;

      /**
     * 终点代码
     */
      @TableField("CZDDM")
    private String czddm;

      /**
     * 终点名称
     */
      @TableField("VZDMC")
    private String vzdmc;

      /**
     * 操作员代码
     */
      @TableField("CCZYDM")
    private String cczydm;

      /**
     * 操作时间
     */
      @TableField("DCZSJ")
    private Long dczsj;

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
     * 主机公司
     */
      @TableField("CZJGSDM")
    private String czjgsdm;

    @TableField("WAREHOUSE_CREATETIME")
    private Long warehouseCreatetime;

    @TableField("WAREHOUSE_UPDATETIME")
    private Long warehouseUpdatetime;


}
