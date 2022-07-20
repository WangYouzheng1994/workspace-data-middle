package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description: 动态分流表
 * @Author: WangYouzheng
 * @Date: 2021/12/28 10:59
 * @Version: V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TableProcess {
    //动态分流 Sink 常量 改为小写和脚本一致
    public static final String SINK_TYPE_MYSQL = "mysql";
    public static final String SINK_TYPE_HBASE = "hbase";
    public static final String SINK_TYPE_KAFKA = "kafka";
    public static final String SINK_TYPE_CK = "clickhouse";

    /**
     * 来源表
     */
    String sourceTable;

    /**
     * 操作类型 insert,update,delete
     */
    String operateType;

    /**
     * 输出类型 hbase kafka
     */
    String sinkType;

    /**
     * 输出表(主题)
     */
    String sinkTable;

    /**
     * 输出字段
     */
    String sinkColumns;

    /**
     * 主键字段
     */
    String sinkPk;

    /**
     * 建表扩展
     */
    String sinkExtend;

    /**
     * 源表数据对应类全名
     */
    String className;

    /**
     * 所在层次
     */
    String levelName;
    /**
     * 是否扫描使用 1是0否
     */
    Integer isUse;

    /**
     * Bean类类型，根据全路径类名转化而来
     */
    private Class clazz;
}