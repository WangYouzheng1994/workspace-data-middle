package com.yqwl.datamiddle.realtime.app.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 维护铁路批次，或水路批次
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
public class Sptb013 implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

      /**
     * 批次号
     */
          
    private String vph;

      /**
     * 状态：0、初始；1、提交；2、完成
     */
          
    private String czt;

      /**
     * 发车仓库
     */
          
    private String vfcck;

      /**
     * 发车站台
     */
          
    private String vfczt;

      /**
     * 收车站台
     */
          
    private String vsczt;

      /**
     * 计划数量
     */
          
    private Integer njhsl;

      /**
     * 实际数量
     */
          
    private Integer nsjsl;

      /**
     * 运输商
     */
          
    private String vyss;

      /**
     * 操作员
     */
          
    private String vczy;

      /**
     * 操作日期
     */
          
    private Long dczrq;

      /**
     * 提交日期
     */
          
    private Long dtjrq;

      /**
     * 停用标识
     */
          
    private String ctybs;

      /**
     * 停用日期
     */
          
    private Long dtyrq;

      /**
     * 公司
     */
          
    private String vgs;

      /**
     * 铁路列号
     */
          
    private String vtllh;

      /**
     * 标识：1铁路，2水路
     */
          
    private String cbs;

      /**
     * 分驳标识  0否，1是
     */
          
    private String cfbbs;

      /**
     * 短驳运输商
     */
          
    private String cdbyss;

      /**
     * LJS代码 
     */
          
    private String cljsdm;

      /**
     * 船舶号
     */
          
    private String ccbh;

      /**
     * 创建时间
     */
          
    private Long warehouseCreatetime;

      /**
     * 更新时间
     */
          
    private Long warehouseUpdatetime;


}
