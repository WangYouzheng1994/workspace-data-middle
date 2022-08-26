package org.datamiddle.cdc.oracle.bean;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.math.BigInteger;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/8/24 15:38
 * @Version: V1.0
 */
@Data
public class LogData {
    /**
     * 数据库大写名字 Oracle 为Schema，Mysql为database
     */
    private String database;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 更新前数据，如果是没有before的 为空对象 不是null
     */
    private JSONObject before;

    /**
     * 更新后数据，如果是没有after的为空对象 不是null
     */
    private JSONObject after;

    /**
     * 操作类型: insert, update, delete
     */
    private String type;

    /**
     * 抽取挖掘采集到的时间
     */
    private Long ts;

    /**
     * 操作时间:
     */
    private Long opTs;

    /**
     * 日志偏移量
     */
    private BigInteger scn;
}