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
 * 线路主表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_sptb_xl01")
public class SptbXl01 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 线路类型： M干线，F分驳线路
     */
      @TableField("CXLLX")
    private String cxllx;

      /**
     * 线路代码：默认为线路起点-线路终点代码
     */
      @TableField("CXLDM")
    private String cxldm;

      /**
     * 线路名称：默认为线路起点-线路终点城市
     */
      @TableField("VXLMC")
    private String vxlmc;

      /**
     * 线路状态：0，初始；1提交。提交时，校验占比100；校验各线路重复；
     */
      @TableField("CZT")
    private String czt;

      /**
     * 操作员代码
     */
      @TableField("CCZYDM")
    private String cczydm;

      /**
     * 操作日期
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

      /**
     * 基地
     */
      @TableField("CQWH")
    private String cqwh;

      /**
     * 区域变更标识：线路上的区域是否随中标方而变化 0否，1是 
     */
      @TableField("CQYBG")
    private String cqybg;

      /**
     * 选择运输商方式：线路是否按区域选择中标方       0否，1是 
     */
      @TableField("CXZYSS")
    private String cxzyss;

      /**
     * 审核标识1
     */
      @TableField("CBS1")
    private String cbs1;

      /**
     * 审核标识2
     */
      @TableField("CBS2")
    private String cbs2;

      /**
     * 审核标识3
     */
      @TableField("CBS3")
    private String cbs3;

      /**
     * 审核标识4
     */
      @TableField("CBS4")
    private String cbs4;

      /**
     * 操作员代码1
     */
      @TableField("CCZYDM1")
    private String cczydm1;

      /**
     * 操作员代码2
     */
      @TableField("CCZYDM2")
    private String cczydm2;

      /**
     * 操作员代码3
     */
      @TableField("CCZYDM3")
    private String cczydm3;

      /**
     * 操作员代码4
     */
      @TableField("CCZYDM4")
    private String cczydm4;

      /**
     * 审核日期1
     */
      @TableField("DSHRQ1")
    private Long dshrq1;

      /**
     * 审核日期2
     */
      @TableField("DSHRQ2")
    private Long dshrq2;

      /**
     * 审核日期3
     */
      @TableField("DSHRQ3")
    private Long dshrq3;

      /**
     * 审核日期4
     */
      @TableField("DSHRQ4")
    private Long dshrq4;

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
