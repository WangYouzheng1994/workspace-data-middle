package com.yqwl.datamiddle.realtime.bean;

import com.yqwl.datamiddle.realtime.enums.CamelUnderline;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 用于修数的bean
 * @Author: XiaoFeng
 * @Date: 2022/7/5 13:34
 * @Version: V1.0
 */
@CamelUnderline(isChange = false)
@Data
@EqualsAndHashCode(callSuper = false)
public class FixDwmsptb02Bean {
    /**
     * 结算单编号 主键
     */
    private String CJSDBH;

    /**
     * 运输车牌照号
     */
    private String VJSYDM;
    /**
     * dwd基地省区代码-起货地
     */
    private String START_PROVINCE_CODE;


    /**
     * dwm起货地市县代码
     */
    private String START_CITY_CODE;

    /**
     * dwm起货地市县名称
     */
    private String START_CITY_NAME;

    /**
     * dwd到货地省区代码
     */
    private String END_PROVINCE_CODE;

    /**
     * dwm到货地市县代码
     */
    private String END_CITY_CODE;

    /**
     * dwm到货地市县名称
     */
    private String END_CITY_NAME;
}
