package org.jeecg.yqwl.datamiddle.ads.order.vo;

import lombok.Data;

/**
 * top10列表数据
 * @author dabao
 * @date 2022/10/19
 */
@Data
public class TopTenDataVo {

    /**
     * 始发城市
     */
    private String startCityName;

    /**
     * 到达城市
     */
    private String endCityName;

    /**
     * 数量
     */
    private Integer value;
}
