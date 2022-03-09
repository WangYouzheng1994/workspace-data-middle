package com.yqwl.datamiddle.realtime.enums;

/**
 * kafka topic 主题枚举类
 */
public enum KafkaTopicEnum {

    MYSQL_TOPIC("mysql数据库主题名称"),
    ORACLE_TOPIC("oracle数据库主题名称"),
    MONGODB_TOPIC("mongodb数据库主题名称");

    /**
     * 主题名称描述
     */
    private final String desc;

    KafkaTopicEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
