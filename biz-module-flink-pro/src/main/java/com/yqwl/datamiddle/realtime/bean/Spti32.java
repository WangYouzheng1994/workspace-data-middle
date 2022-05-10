package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 物流标准时间
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data

public class Spti32 implements Serializable {

    private static final long serialVersionUID = 1L;


      private Long idnum;

      /**
     * 起始省区代码
     */
    private String cqssqdm;

      /**
     * 起始市县代码
     */
    private String cqscsdm;

      /**
     * 目标省区代码
     */
      
    private String cmbsqdm;

      /**
     * 目标市县代码
     */
      
    private String cmbcsdm;

      /**
     * 里程
     */
      
    private BigDecimal nlc;

      /**
     * 主机公司。字典：WTDW
     */
      
    private String czjgs;

      /**
     * 运输方式  G-公路 T-铁路 D-短驳
     */
      
    private String vysfs;

      /**
     * 在途时间(出库-到货)
     */
      
    private Long nztsj;

      /**
     * 物流时间
     */
      
    private Long nts;

      /**
     * 备注
     */
      
    private String cbz;

      /**
     * 操作日期
     */
      
    private Long dczrq;

      /**
     * 操作员
     */
      
    private String cczydm;

      /**
     * 标准GPS到货时间
     */
      
    private Long ndhsjGps;

      /**
     * 标准系统到货时间
     */
      
    private Long ndhsjXt;

      /**
     * 创建时间
     */
      
    private Long warehouseCreatetime;

      /**
     * 更新时间
     */
      
    private Long warehouseUpdatetime;


}
