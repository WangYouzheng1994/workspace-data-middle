package com.yqwl.datamiddle.realtime.bean;


import lombok.Data;
import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data

public class SptbXl01d implements Serializable {

    private static final long serialVersionUID = 1L;


    private Long idnum;

    /**
     * 线路代码
     */

    private String cxldm;

    /**
     * 起点代码
     */

    private String cqddm;

    /**
     * 起点名称
     */

    private String vqdmc;

    /**
     * 终点代码
     */

    private String czddm;

    /**
     * 终点名称
     */

    private String vzdmc;

    /**
     * 操作员代码
     */

    private String cczydm;

    /**
     * 操作时间
     */

    private Long dczsj;

    /**
     * 停用标识
     */

    private String ctybs;

    /**
     * 停用日期
     */

    private Long dtyrq;

    /**
     * 主机公司
     */

    private String czjgsdm;


    private Long warehouseCreatetime;


    private Long warehouseUpdatetime;


}
