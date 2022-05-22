package com.yqwl.datamiddle.realtime.bean;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>
 * 地市级代码表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Sysc09 implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 地市代码
     */

    private String CDSDM;

    /**
     * 省区代码
     */

    private String CSQDM;

    /**
     * 地市名称 20 - 50
     */

    private String VDSMC;

    /**
     * 备注 20 50
     */

    private String VBZ;

    /**
     * 时间戳。BI提数据
     */

    private Long DSTAMP;

    /**
     * "物流标准编码。选择，来源于M平台
     * "
     */

    private String CWLBM;

    /**
     * "物流标准名称。来源于M平台
     * "
     */

    private String CWLMC;

    /**
     * 创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */

    private Long WAREHOUSE_UPDATETIME;


    @JSONField(serialize = false)
    private Timestamp ts;
}
