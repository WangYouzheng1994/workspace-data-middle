package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 铁水运单溯源接口入库时间
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Sptb02RailseaRk implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 结算单编号 主键
     */

    private String CJSDBH;

    /**
     * VIN
     */

    private String VVIN;

    /**
     * 物理仓库代码（站台代码）
     */

    private String VWLCKDM;

    /**
     * 物理仓库名称 增加长度：由20增至50
     */

    private String VWLCKMC;

    /**
     * 溯源库房ID
     */

    private Integer WAREHOUSE_ID;

    /**
     * 溯源库房代码
     */

    private String WAREHOUSE_CODE;

    /**
     * 溯源库房名称
     */

    private String WAREHOUSE_NAME;

    /**
     * 入库日期
     */

    private Long DRKRQ;

    /**
     * 创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */

    private Long WAREHOUSE_UPDATETIME;


}
