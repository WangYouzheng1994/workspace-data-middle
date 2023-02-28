package com.yqwl.datamiddle.realtime.bean;

import com.yqwl.datamiddle.realtime.enums.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@TableName("ods_vlms_sptb22_dq")
@EqualsAndHashCode(callSuper = false)
public class Sptb22Dq implements Serializable {
    // private static final long serialVersionUID = 1L;

    private Integer ID;


    private String CPZH;


    private String VWZ;


    private Long DCJSJ;


    private String VSD;


    private String CJSDBH;


    private String CSQDM;


    private String CSXDM;


    private Long DCZRQ;


    private String CDHBS;

    /**
     * 0 gps/ 1 lbs
     */

    private String CLYBS;

    /**
     * 离长时间
     */

    private Long DLCSJ;


    private String CSQ;


    private String CSX;


    private Long DDKSJ;

    /**
     * 车辆滞留标识（两次取得数据相同认为是滞留）
     */

    private String CZLBS;

    /**
     * 上次位置
     */

    private String VYWZ;


    private String CBZ;


    private String CSJH;


    private Long DLBSSJ;


    private String VLBSWZ;

    /**
     * 采点时段形如'20091004 02'
     */

    private String CSD;

    /**
     * 滞留小时
     */

    private Integer NLJZT;

    /**
     * 经度
     */

    private BigDecimal CCBJD;

    /**
     * 纬度
     */

    private BigDecimal CCBWD;

    /**
     * 创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */

    private Long WAREHOUSE_UPDATETIME;


}
