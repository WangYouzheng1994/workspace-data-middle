package org.jeecg.yqwl.datamiddle.ads.order.vo;

import lombok.Data;

/**
 * top10列表数据
 * @author dabao
 * @date 2022/10/19
 */
@Data
public class TopTenDataVo {

    private String startCityName;

    private String endCityName;

    private Integer value;
}
