package com.yqwl.datamiddle.realtime.bean;


import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 运输调度员所管运输商
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
public class Spti15 implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long idnum;


    private String cddy;


    private String cyysdm;


    private Long dczrq;


    private String cczydm;


    private String vbz;

    /**
     * 多公司
     */

    private String cgs;

    /**
     * 停用标识
     */

    private String ctybs;

    /**
     * 停用日期
     */

    private Long dtyrq;

    /**
     * 停用原因
     */

    private String ctyyy;

    /**
     * 创建时间
     */

    private Long warehouseCreatetime;

    /**
     * 更新时间
     */

    private Long warehouseUpdatetime;


}
