package com.yqwl.datamiddle.realtime.bean;


import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 铁水标准物流时间
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data

public class Spti32RailSea implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

      /**
     * 主机公司代码
     */
    private String czjgsdm;

      /**
     * 运输方式  T-铁路 S-水运
     */
    private String vysfs;

      /**
     * 起始站台/港口代码
     */
    private String cqsztdm;

      /**
     * 目标站台/港口代码
     */
    private String cmbztdm;

      /**
     * 在途标准物流时间（出库-系统到货）
     */
    private BigDecimal ndhsjZt;

      /**
     * 系统到货标准物流时间-天
（满列）26节以上
     */
    private BigDecimal ndhsjXtdhMl;

      /**
     * 系统到货标准物流时间-天
（大组）15-26节
     */
    private BigDecimal ndhsjXtdhDz;

      /**
     * 系统到货标准物流时间-天
（散列）15节及以下
     */
    private BigDecimal ndhsjXtdhSl;

      /**
     * 到站标准物流时间-天
（满列）26节以上
     */
    private BigDecimal ndhsjDzMl;

      /**
     * 到站标准物流时间-天
（大组）15-26节
     */
    private BigDecimal ndhsjDzDz;

      /**
     * 到站标准物流时间-天
（散列）15节及以下
     */
    private BigDecimal ndhsjDzSl;

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

    
    private Long warehouseCreatetime;

    
    private Long warehouseUpdatetime;


}
