package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 *
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SptbXl01d {

    /**
     * 线路代码
     */

    private String CXLDM;

    /**
     * 起点代码
     */

    private String CQDDM;

    /**
     * 起点名称
     */

    private String VQDMC;

    /**
     * 终点代码
     */

    private String CZDDM;

    /**
     * 终点名称
     */

    private String VZDMC;

    /**
     * 操作员代码
     */

    private String CCZYDM;

    /**
     * 操作时间
     */

    private Long DCZSJ;

    /**
     * 停用标识
     */

    private String CTYBS;

    /**
     * 停用日期
     */

    private Long DTYRQ;

    /**
     * 主机公司
     */

    private String CZJGSDM;


    private Long WAREHOUSE_CREATETIME;


    private Long WAREHOUSE_UPDATETIME;


}
