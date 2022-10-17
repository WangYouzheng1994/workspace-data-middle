package org.jeecg.yqwl.datamiddle.ads.order.enums;

public enum ShippingMethodEnum {

    SHIPPING_METHOD_G("G","公路"),
    SHIPPING_METHOD_T("T","铁路"),
    SHIPPING_METHOD_S("S","水路");

    private final String code;
    private final String name;

    ShippingMethodEnum(String code, String name){
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
        for (ShippingMethodEnum e: values()) {
            if (e.code.equals(code)){
                return e.name;
            }
        }
        return null;

    }
}
