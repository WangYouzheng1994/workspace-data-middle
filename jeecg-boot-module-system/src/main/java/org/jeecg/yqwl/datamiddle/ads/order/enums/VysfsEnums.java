package org.jeecg.yqwl.datamiddle.ads.order.enums;

public enum VysfsEnums {


    /**
     * 公路运输
     */
    SHIPPING_METHOD_G("G","公路"),
    /**
     * 铁路运输
     */
    SHIPPING_METHOD_T("T","铁路"),
    /**
     * 水路运输
     */
    SHIPPING_METHOD_S("S","水路"),
    /**
     * 水路运输
     */
    SHIPPING_METHOD_L1("L1","铁路"),
    /**
     * 水路运输
     */
    SHIPPING_METHOD_SD("SD","水路短驳"),
    /**
     * 水路运输
     */
    SHIPPING_METHOD_TD("TD","铁路短驳"),
    /**
     * 水路运输
     */
    SHIPPING_METHOD_J("J","集港集站"),
    /**
     * 水路运输
     */
    SHIPPING_METHOD_G1("G1","公路");

    private final String code;
    private final String name;

    VysfsEnums(String code, String name){
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static String getNameByCode(String code){
        for (VysfsEnums e: values()) {
            if (e.code.equals(code)){
                return e.name;
            }
        }
        return null;

    }
}
