package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;


import java.io.Serializable;

/**
 * <p>
 * 数据字典表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data

public class Sysc09d implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long idnum;

    /**
     * 对应sysc09c.CZDDM 对应
     */
    private String czddm;

    /**
     * 要展开数据项的编码
     */
    private String csjxm;

    /**
     * 数据项名称 由500改为4000
     */
    private String vsjxc;

    /**
     * 级别
     */
    private String cjb;

    /**
     * 停用标识
     */
    private String ctybs;

    /**
     * 停用日期
     */
    private Long dtyrq;

    /**
     * 备注
     */
    private String vbz;

    private String vbz2;

    /**
     * 用于特殊运输商
     */
    private String vbz3;

    /**
     * 创建时间
     */
    private Long warehouseCreatetime;

    /**
     * 更新时间
     */
    private Long warehouseUpdatetime;


}
