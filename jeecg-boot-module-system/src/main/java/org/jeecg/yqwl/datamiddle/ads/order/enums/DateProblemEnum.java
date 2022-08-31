package org.jeecg.yqwl.datamiddle.ads.order.enums;

/**
 * 时间问题枚举类
 * 正常的业务流程： 计划下达 -》 指派 -》 出库 -》 起运 -》 到货
 */
public enum DateProblemEnum {

    PROBLEM_00(0,"指派时间早于计划下达时间"),
    PROBLEM_01(1,"出库时间早于指派时间"),
    PROBLEM_02(2,"起运时间早于出库时间"),
    PROBLEM_03(3,"到货时间早于起运时间");

    /**
     * 问题编号
     */
    private Integer problemCode;
    /**
     * 主题名称描述
     */
    private final String desc;

    DateProblemEnum(Integer problemCode, String desc) {
        this.problemCode = problemCode;
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getProblemCode() {
        return problemCode;
    }
}
