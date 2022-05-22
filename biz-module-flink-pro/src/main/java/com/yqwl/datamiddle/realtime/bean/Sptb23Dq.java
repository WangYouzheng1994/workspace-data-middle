package com.yqwl.datamiddle.realtime.bean;

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
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Sptb23Dq implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * S_SPTB23_DQ 自增列
     */

    private Long ID;

    /**
     * 配板单号
     */

    private String CPZDBH;

    /**
     * 任务单号
     */

    private String CJHDH;

    /**
     * 清单号
     */

    private String VWXDWDM;

    /**
     * 底盘号
     */

    private String VVIN;

    /**
     * 车型
     */

    private String CCXDM;

    /**
     * 运输方式
     */

    private String VYSFS;

    /**
     * 收车单位名称
     */

    private String VSCDWMC;

    /**
     * 收车地址
     */

    private String VSCDZ;

    /**
     * 始发地
     */

    private String VSFD;

    /**
     * 到货省区
     */

    private String VSQMC;

    /**
     * 市县名称
     */

    private String VSXMC;

    /**
     * 运输商名称
     */

    private String VYSSMC;

    /**
     * 大众运单日期
     */

    private Long DDZYDRQ;

    /**
     * 运单日期
     */

    private Long DYDRQ;

    /**
     * 出库日期
     */

    private Long DCKRQ;

    /**
     * 航次/列次
     */

    private String VTLLH;

    /**
     * 集运日期
     */

    private Long DJYRQ;

    /**
     * 集运车号
     */

    private String VJYCH;

    /**
     * 起运时间（水运的离港时间
     * 铁路的出站时间）
     */

    private Long DQYSJ;

    /**
     * 在途位置 对应水路CurrentSeaArea
     */

    private String VZTWZ;

    /**
     * 到站/到港（指收车港/站台对应的城市）
     */

    private String VDZGCS;

    /**
     * 到站/港时间
     */

    private Long DDZGSJ;

    /**
     * 入库时间
     */

    private Long DRKSJ;

    /**
     * 分拨运输商
     */

    private String VFBYSS;

    /**
     * 分拨时间
     */

    private Long DFBSJ;

    /**
     * 分拨车号
     */

    private String VFBCH;

    /**
     * 提报到货时间
     */

    private Long DTBDHSJ;

    /**
     * 系统到货时间
     */

    private Long DXTDHSJ;

    /**
     * 操作员(导入)
     */

    private String CCZYDM;

    /**
     * 操作日期(导入)
     */

    private Long DCZRQ;

    /**
     * 省区
     */

    private String CSQDM;

    /**
     * 市县
     */

    private String CSXDM;

    /**
     * 经度
     */

    private BigDecimal CCBJD;

    /**
     * 纬度
     */

    private BigDecimal CCBWD;

    /**
     * 船舶号
     */

    private String CCBH;

    /**
     * 铁水标识： 0水运；1铁路     20170227 改为：T铁路；S水路；TD铁路短驳；SD水路短驳
     */

    private String CTSBS;

    /**
     * 针对水运 上个港口英文名称
     */

    private String VPREVIOUSPORT;

    /**
     * 针对水运 当前港口英文名称
     */

    private String VCURRENTPORT;

    /**
     * 水路用 没处理的数据（当次接过来的）置为0；处理完的置为1
     */

    private String CBS;


    private Long WAREHOUSE_CREATETIME;


    private Long WAREHOUSE_UPDATETIME;


}
