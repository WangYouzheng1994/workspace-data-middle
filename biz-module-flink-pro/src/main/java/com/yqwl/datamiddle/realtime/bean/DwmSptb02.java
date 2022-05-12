package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 此类用于 sptb02表 dwd层使用
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DwmSptb02 extends DwdSptb02 {

    /**
     * dwm新增-理论起运时间
     */
    private Long theoryShipmentTime;

    /**
     * dwm新增-理论出库时间
     */
    private Long theoryOutTime;

    /**
     * dwm新增-理论到货时间
     */
    private Long theorySiteTime;

    /**
     * dwm新增-实际出库时间
     */
    private Long actualOutTime;

    /**
     * dwm新增-入目标库时间
     */
    private Long enterTargetTime;

    /**
     * dwm新增-车架号
     */
    private String vvin;

    /**
     * dwm新增 运单状态-1待出库2已出库3待起运(待离港)4干线在途5已到待卸6末端配送-同城直发(配送中)7异地直发or移库(入库中)8已到库9已到店
     */
    private Integer trafficStatus;

    /**
     * dwm新增 运单状态名称 1待出库2已出库3待起运(待离港)4干线在途5已到待卸6末端配送-同城直发(配送中)7异地直发or移库(入库中)8已到库9已到店
     */
    private String trafficStatusName;


    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
