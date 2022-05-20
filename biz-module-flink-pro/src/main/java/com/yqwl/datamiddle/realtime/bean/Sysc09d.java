package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 数据字典表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Sysc09d implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 对应sysc09c.CZDDM 对应
     */

    private String CZDDM;

    /**
     * 要展开数据项的编码
     */

    private String CSJXM;

    /**
     * 数据项名称 由500改为4000
     */

    private String VSJXC;

    /**
     * 级别
     */

    private String CJB;

    /**
     * 停用标识
     */

    private String CTYBS;

    /**
     * 停用日期
     */

    private Long DTYRQ;

    /**
     * 备注
     */

    private String VBZ;


    private String VBZ2;

    /**
     * 用于特殊运输商
     */

    private String VBZ3;

    /**
     * 创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */

    private Long WAREHOUSE_UPDATETIME;


}
