package com.yqwl.datamiddle.realtime.app.bean;

import lombok.Data;

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
public class Sptb23Dq implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

      /**
     * S_SPTB23_DQ 自增列
     */
          
    private Long id;

      /**
     * 配板单号
     */
          
    private String cpzdbh;

      /**
     * 任务单号
     */
          
    private String cjhdh;

      /**
     * 清单号
     */
          
    private String vwxdwdm;

      /**
     * 底盘号
     */
          
    private String vvin;

      /**
     * 车型
     */
          
    private String ccxdm;

      /**
     * 运输方式
     */
          
    private String vysfs;

      /**
     * 收车单位名称
     */
          
    private String vscdwmc;

      /**
     * 收车地址
     */
          
    private String vscdz;

      /**
     * 始发地
     */
          
    private String vsfd;

      /**
     * 到货省区
     */
          
    private String vsqmc;

      /**
     * 市县名称
     */
          
    private String vsxmc;

      /**
     * 运输商名称
     */
          
    private String vyssmc;

      /**
     * 大众运单日期
     */
          
    private Long ddzydrq;

      /**
     * 运单日期
     */
          
    private Long dydrq;

      /**
     * 出库日期
     */
          
    private Long dckrq;

      /**
     * 航次/列次
     */
          
    private String vtllh;

      /**
     * 集运日期
     */
          
    private Long djyrq;

      /**
     * 集运车号

     */
          
    private String vjych;

      /**
     * 起运时间（水运的离港时间
铁路的出站时间）
     */
          
    private Long dqysj;

      /**
     * 在途位置 对应水路CurrentSeaArea
     */
          
    private String vztwz;

      /**
     * 到站/到港（指收车港/站台对应的城市）
     */
          
    private String vdzgcs;

      /**
     * 到站/港时间
     */
          
    private Long ddzgsj;

      /**
     * 入库时间
     */
          
    private Long drksj;

      /**
     * 分拨运输商
     */
          
    private String vfbyss;

      /**
     * 分拨时间
     */
          
    private Long dfbsj;

      /**
     * 分拨车号
     */
          
    private String vfbch;

      /**
     * 提报到货时间
     */
          
    private Long dtbdhsj;

      /**
     * 系统到货时间
     */
          
    private Long dxtdhsj;

      /**
     * 操作员(导入)
     */
          
    private String cczydm;

      /**
     * 操作日期(导入)
     */
          
    private Long dczrq;

      /**
     * 省区
     */
          
    private String csqdm;

      /**
     * 市县
     */
          
    private String csxdm;

      /**
     * 经度
     */
          
    private BigDecimal ccbjd;

      /**
     * 纬度
     */
          
    private BigDecimal ccbwd;

      /**
     * 船舶号 
     */
          
    private String ccbh;

      /**
     * 铁水标识： 0水运；1铁路     20170227 改为：T铁路；S水路；TD铁路短驳；SD水路短驳
     */
          
    private String ctsbs;

      /**
     * 针对水运 上个港口英文名称
     */
          
    private String vpreviousport;

      /**
     * 针对水运 当前港口英文名称
     */
          
    private String vcurrentport;

      /**
     * 水路用 没处理的数据（当次接过来的）置为0；处理完的置为1
     */
          
    private String cbs;

        
    private Long warehouseCreatetime;

        
    private Long warehouseUpdatetime;


}
