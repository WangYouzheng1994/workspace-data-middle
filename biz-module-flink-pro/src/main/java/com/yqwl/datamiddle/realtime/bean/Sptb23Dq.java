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
 * 铁水在途导入表样 数据结构 当前数据表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_sptb23_dq")
public class Sptb23Dq implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * S_SPTB23_DQ 自增列
     */
      @TableField("ID")
    private Long id;

      /**
     * 配板单号
     */
      @TableField("CPZDBH")
    private String cpzdbh;

      /**
     * 任务单号
     */
      @TableField("CJHDH")
    private String cjhdh;

      /**
     * 清单号
     */
      @TableField("VWXDWDM")
    private String vwxdwdm;

      /**
     * 底盘号
     */
      @TableField("VVIN")
    private String vvin;

      /**
     * 车型
     */
      @TableField("CCXDM")
    private String ccxdm;

      /**
     * 运输方式
     */
      @TableField("VYSFS")
    private String vysfs;

      /**
     * 收车单位名称
     */
      @TableField("VSCDWMC")
    private String vscdwmc;

      /**
     * 收车地址
     */
      @TableField("VSCDZ")
    private String vscdz;

      /**
     * 始发地
     */
      @TableField("VSFD")
    private String vsfd;

      /**
     * 到货省区
     */
      @TableField("VSQMC")
    private String vsqmc;

      /**
     * 市县名称
     */
      @TableField("VSXMC")
    private String vsxmc;

      /**
     * 运输商名称
     */
      @TableField("VYSSMC")
    private String vyssmc;

      /**
     * 大众运单日期
     */
      @TableField("DDZYDRQ")
    private Long ddzydrq;

      /**
     * 运单日期
     */
      @TableField("DYDRQ")
    private Long dydrq;

      /**
     * 出库日期
     */
      @TableField("DCKRQ")
    private Long dckrq;

      /**
     * 航次/列次
     */
      @TableField("VTLLH")
    private String vtllh;

      /**
     * 集运日期
     */
      @TableField("DJYRQ")
    private Long djyrq;

      /**
     * 集运车号

     */
      @TableField("VJYCH")
    private String vjych;

      /**
     * 起运时间（水运的离港时间
铁路的出站时间）
     */
      @TableField("DQYSJ")
    private Long dqysj;

      /**
     * 在途位置 对应水路CurrentSeaArea
     */
      @TableField("VZTWZ")
    private String vztwz;

      /**
     * 到站/到港（指收车港/站台对应的城市）
     */
      @TableField("VDZGCS")
    private String vdzgcs;

      /**
     * 到站/港时间
     */
      @TableField("DDZGSJ")
    private Long ddzgsj;

      /**
     * 入库时间
     */
      @TableField("DRKSJ")
    private Long drksj;

      /**
     * 分拨运输商
     */
      @TableField("VFBYSS")
    private String vfbyss;

      /**
     * 分拨时间
     */
      @TableField("DFBSJ")
    private Long dfbsj;

      /**
     * 分拨车号
     */
      @TableField("VFBCH")
    private String vfbch;

      /**
     * 提报到货时间
     */
      @TableField("DTBDHSJ")
    private Long dtbdhsj;

      /**
     * 系统到货时间
     */
      @TableField("DXTDHSJ")
    private Long dxtdhsj;

      /**
     * 操作员(导入)
     */
      @TableField("CCZYDM")
    private String cczydm;

      /**
     * 操作日期(导入)
     */
      @TableField("DCZRQ")
    private Long dczrq;

      /**
     * 省区
     */
      @TableField("CSQDM")
    private String csqdm;

      /**
     * 市县
     */
      @TableField("CSXDM")
    private String csxdm;

      /**
     * 经度
     */
      @TableField("CCBJD")
    private BigDecimal ccbjd;

      /**
     * 纬度
     */
      @TableField("CCBWD")
    private BigDecimal ccbwd;

      /**
     * 船舶号 
     */
      @TableField("CCBH")
    private String ccbh;

      /**
     * 铁水标识： 0水运；1铁路     20170227 改为：T铁路；S水路；TD铁路短驳；SD水路短驳
     */
      @TableField("CTSBS")
    private String ctsbs;

      /**
     * 针对水运 上个港口英文名称
     */
      @TableField("VPREVIOUSPORT")
    private String vpreviousport;

      /**
     * 针对水运 当前港口英文名称
     */
      @TableField("VCURRENTPORT")
    private String vcurrentport;

      /**
     * 水路用 没处理的数据（当次接过来的）置为0；处理完的置为1
     */
      @TableField("CBS")
    private String cbs;

    @TableField("WAREHOUSE_CREATETIME")
    private Long warehouseCreatetime;

    @TableField("WAREHOUSE_UPDATETIME")
    private Long warehouseUpdatetime;


}
