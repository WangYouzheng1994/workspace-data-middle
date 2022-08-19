package org.datamiddle.cdc.oracle;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @Description: 开启一个CDC 任务的参数，此类需要考虑持久化到数据库，从数据库加载。
 * @Author: WangYouzheng
 * @Date: 2022/8/3 13:19
 * @Version: V1.0
 */
@Data
@Builder
public class OracleCDCConfig {
    // 默认是增量抽取：除此以外的模式为：all， time, scn
    private String readPosition = "all";

    // 指定时间抽取  time模式
    private Long startTime;

    // 指定时间抽取  time模式
    private String endTime;

    // 指定SCN抽取  SCN模式
    private String startSCN;

    // 指定SCN结束  SCN模式
    private String endSCN;

    // 查询logminer 解析结果 v$logmnr_contents的数量
    private int fetchSize = 1000;

    // 要过滤的表 schema.TableName 大写
    private List<String> table;

    /**
     * jdbc 驱动连接
     */
    private String jdbcUrl;

    /**
     * username
     */
    private String username;

    /**
     * password
     */
    private String password;

    /**
     * 驱动类名
     */
    private String driverClass;
}