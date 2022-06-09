package com.yqwl.datamiddle.realtime.bean;

import com.alibaba.fastjson.annotation.JSONField;
import com.yqwl.datamiddle.realtime.enums.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * <p>
 * 站台-仓库对照表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@TableName("ods_vlms_site_warehouse")
@Data
@EqualsAndHashCode(callSuper = false)
public class SiteWarehouse implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 站点代码
     */

    private String VWLCKDM;

    /**
     * 站点名称
     */

    private String VWLCKMC;

    /**
     * 库房ID
     */

    private Integer WAREHOUSE_ID;

    /**
     * 库房代码
     */

    private String WAREHOUSE_CODE;

    /**
     * 库房名称
     */

    private String WAREHOUSE_NAME;

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
     * 类型：对照contrast；仓库：warehouse
     */

    private String TYPE;


    private Long WAREHOUSE_CREATETIME;


    private Long WAREHOUSE_UPDATETIME;

    @JSONField(serialize = false)
    private Timestamp ts;


}
