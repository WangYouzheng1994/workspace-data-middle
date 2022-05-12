package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 此类用于 sptb02表 dwd层使用
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DwdSptb02 extends Sptb02 {

    /**
     * dwd新增-运输方式 公路 G 水路 S 铁路 T
     */
    private String trafficType;

    /**
     * dwd新增-起运时间 公路-取DTVSCFSJ(TVS出发时间)的值;铁水-取DSJCFSJ(实际离长时间)的值
     */
    private Long shipmentTime;

    /**
     * dwd新增-计划下达时间
     */
    private Long planReleaseTime;

    /**
     * dwd新增-运单指派时间
     */
    private Long assignTime;

    /**
     * dwd新增-打点到货时间
     */
    private Long dotSiteTime;

    /**
     * dwd新增-最终到货时间
     */
    private Long finalSiteTime;

    /**
     * dwd新增-运单生成时间
     */
    private Long orderCreateTime;


    /**
     * 适配 lc_spec_config
     * 基地代码转换
     * 区位号。
     * 0431、 -> 1
     * 022、  -> 5
     * 027、
     * 028、  -> 2
     * 0757   -> 3
     *
     * 表示生产的基地（2013-10-12储运部会议上确定）
     */
    private String baseCode;

    /**
     *  运输方式 适配 lc_spec_config
      公路 1
      铁路 2
      水运 3
     */
    private String transModeCode;

    /**
     *  主机公司代码 适配 lc_spec_config
         1  一汽大众
         2  一汽红旗
         3   一汽马自达
     */
    private String hostComCode;



    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

}
