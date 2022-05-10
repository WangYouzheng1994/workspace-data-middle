package com.yqwl.datamiddle.realtime.app.bean;

import lombok.Data;
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
public class SptbXl01 implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

      /**
     * 线路类型： M干线，F分驳线路
     */
          
    private String cxllx;

      /**
     * 线路代码：默认为线路起点-线路终点代码
     */
          
    private String cxldm;

      /**
     * 线路名称：默认为线路起点-线路终点城市
     */
          
    private String vxlmc;

      /**
     * 线路状态：0，初始；1提交。提交时，校验占比100；校验各线路重复；
     */
          
    private String czt;

      /**
     * 操作员代码
     */
          
    private String cczydm;

      /**
     * 操作日期
     */
          
    private Long dczsj;

      /**
     * 停用标识
     */
          
    private String ctybs;

      /**
     * 停用日期
     */
          
    private Long dtyrq;

      /**
     * 主机公司
     */
          
    private String czjgsdm;

      /**
     * 基地
     */
          
    private String cqwh;

      /**
     * 区域变更标识：线路上的区域是否随中标方而变化 0否，1是 
     */
          
    private String cqybg;

      /**
     * 选择运输商方式：线路是否按区域选择中标方       0否，1是 
     */
          
    private String cxzyss;

      /**
     * 审核标识1
     */
          
    private String cbs1;

      /**
     * 审核标识2
     */
          
    private String cbs2;

      /**
     * 审核标识3
     */
          
    private String cbs3;

      /**
     * 审核标识4
     */
          
    private String cbs4;

      /**
     * 操作员代码1
     */
          
    private String cczydm1;

      /**
     * 操作员代码2
     */
          
    private String cczydm2;

      /**
     * 操作员代码3
     */
          
    private String cczydm3;

      /**
     * 操作员代码4
     */
          
    private String cczydm4;

      /**
     * 审核日期1
     */
          
    private Long dshrq1;

      /**
     * 审核日期2
     */
          
    private Long dshrq2;

      /**
     * 审核日期3
     */
          
    private Long dshrq3;

      /**
     * 审核日期4
     */
          
    private Long dshrq4;

      /**
     * 创建时间
     */
          
    private Long warehouseCreatetime;

      /**
     * 更新时间
     */
          
    private Long warehouseUpdatetime;


}
