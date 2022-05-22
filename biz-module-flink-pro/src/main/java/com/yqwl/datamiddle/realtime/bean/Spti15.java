package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 运输调度员所管运输商
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Spti15 implements Serializable {
    private static final long serialVersionUID = 1L;

    private String CDDY;


    private String CYYSDM;


    private Long DCZRQ;


    private String CCZYDM;


    private String VBZ;

    /**
     * 多公司
     */

    private String CGS;

    /**
     * 停用标识
     */

    private String CTYBS;

    /**
     * 停用日期
     */

    private Long DTYRQ;

    /**
     * 停用原因
     */

    private String CTYYY;

    /**
     * 创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */

    private Long WAREHOUSE_UPDATETIME;


}
