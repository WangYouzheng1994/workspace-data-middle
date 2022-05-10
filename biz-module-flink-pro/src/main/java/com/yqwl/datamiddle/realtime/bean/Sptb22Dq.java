package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;

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
public class Sptb22Dq implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

        
    private Integer id;

        
    private String cpzh;

        
    private String vwz;

        
    private Long dcjsj;

        
    private String vsd;

        
    private String cjsdbh;

        
    private String csqdm;

        
    private String csxdm;

        
    private Long dczrq;

        
    private String cdhbs;

      /**
     * 0 gps/ 1 lbs
     */
          
    private String clybs;

      /**
     * 离长时间
     */
          
    private Long dlcsj;

        
    private String csq;

        
    private String csx;

        
    private Long ddksj;

      /**
     * 车辆滞留标识（两次取得数据相同认为是滞留）
     */
          
    private String czlbs;

      /**
     * 上次位置
     */
          
    private String vywz;

        
    private String cbz;

        
    private String csjh;

        
    private Long dlbssj;

        
    private String vlbswz;

      /**
     * 采点时段形如'20091004 02'
     */
          
    private String csd;

      /**
     * 滞留小时
     */
          
    private Integer nljzt;

      /**
     * 经度
     */
          
    private BigDecimal ccbjd;

      /**
     * 纬度
     */
          
    private BigDecimal ccbwd;

      /**
     * 创建时间
     */
          
    private Long warehouseCreatetime;

      /**
     * 更新时间
     */
          
    private Long warehouseUpdatetime;


}
