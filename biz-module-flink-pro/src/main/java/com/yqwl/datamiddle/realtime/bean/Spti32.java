package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 物流标准时间
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Spti32 implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 起始省区代码
     */

    private String CQSSQDM;

    /**
     * 起始市县代码
     */

    private String CQSCSDM;

    /**
     * 目标省区代码
     */

    private String CMBSQDM;

    /**
     * 目标市县代码
     */

    private String CMBCSDM;

    /**
     * 里程
     */

    private BigDecimal NLC;

    /**
     * 主机公司。字典：WTDW
     */

    private String CZJGS;

    /**
     * 运输方式  G-公路 T-铁路 D-短驳
     */

    private String VYSFS;

    /**
     * 在途时间(出库-到货)
     */

    private Double NZTSJ;

    /**
     * 物流时间
     */

    private Long NTS;

    /**
     * 备注
     */

    private String CBZ;

    /**
     * 操作日期
     */

    private Long DCZRQ;

    /**
     * 操作员
     */

    private String CCZYDM;

    /**
     * 标准GPS到货时间
     */

    private Long NDHSJ_GPS;

    /**
     * 标准系统到货时间
     */

    private Long NDHSJ_XT;

    /**
     * 创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */

    private Long WAREHOUSE_UPDATETIME;


}
