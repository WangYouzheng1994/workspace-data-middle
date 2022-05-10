package com.yqwl.datamiddle.realtime.bean;


import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 20210312 大众新标准物流时间表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data

public class Spti32Dz implements Serializable {

    private static final long serialVersionUID = 1L;


      private Long idnum;

      /**
     * 运输方式
     */
      
    private String vysfs;

      /**
     * 类型:TC同城；YC异城；
     */
      
    private String ctype;

      /**
     * 满载率:DZ大组；ML满列；SH散货；
     */
      
    private String vmzl;

      /**
     * 始发省区代码
     */
      
    private String vqysqdm;

      /**
     * 始发城市代码
     */
      
    private String vqysxdm;

      /**
     * 分拨省区代码
     */
      
    private String vfbsqdm;

      /**
     * 分拨城市代码
     */
      
    private String vfbsxdm;

      /**
     * 终到省份代码
     */
      
    private String vmdsqdm;

      /**
     * 终到城市代码
     */
      
    private String vmdsxdm;

      /**
     * 终到地级市代码
     */
      
    private String vmdxqdm;

      /**
     * 终到运区代码
     */
      
    private String vmdyqdm;

      /**
     * 分段物流时间:公路
     */
      
    private Long nwlsjG;

      /**
     * 分段物流时间:集港
     */
      
    private Long nwlsjJ;

      /**
     * 分段物流时间:铁水
     */
      
    private Long nwlsjTs;

      /**
     * 分段物流时间:分拨
     */
      
    private Long nwlsjF;

      /**
     * 整体考核物流时间
     */
      
    private Long nwlsjAll;

      /**
     * 创建时间
     */
      
    private Long warehouseCreatetime;

      /**
     * 更新时间
     */
      
    private Long warehouseUpdatetime;


}
