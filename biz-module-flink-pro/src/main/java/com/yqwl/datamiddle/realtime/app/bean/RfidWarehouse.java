package com.yqwl.datamiddle.realtime.app.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 20191026 大屏展示 默认仓库表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
public class RfidWarehouse implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

        
    private Integer id;

      /**
     * 库房代码
     */
          
    private String warehouseCode;

      /**
     * 库房名称
     */
          
    private String warehouseName;

      /**
     * 归属基地
     */
          
    private String partBase;

      /**
     * 库房类型（基地库：T1  分拨中心库:T2  港口  T3  站台  T4）
     */
          
    private String warehouseType;

      /**
     * 站台对应的城市
     */
          
    private String city;

      /**
     * 默认在库量  number 转 int
     */
          
    private Integer defalutValue;

      /**
     * 标准库容     number 转 int
     */
          
    private Integer bzkr;

      /**
     * 最大库容     number 转 int
     */
          
    private Integer zdkr;

      /**
     * 数据中台代码
     */
          
    private String datacenterCode;

      /**
     * 顺序 2019 12 23 宋琳添加   number 转 int
     */
          
    private Integer sequence;

      /**
     * 省份2019 12 23 宋琳添加
     */
          
    private String province;

      /**
     * 整车数据代码
     */
          
    private String zcCode;

      /**
     * 主机公司代码 1 大众  2 奔腾 3解放  17 红旗  29 马自达
     */
          
    private String czjgsdm;

      /**
     * 数据中台代码备份
     */
          
    private String datacenterCodeBzk;

      /**
     * 所属省份
     */
          
    private String provice;

        
    private Long warehouseCreatetime;

        
    private Long warehouseUpdatetime;


}
