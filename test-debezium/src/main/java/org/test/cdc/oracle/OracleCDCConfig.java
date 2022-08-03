package org.test.cdc.oracle;

import lombok.Data;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/8/3 13:19
 * @Version: V1.0
 */
@Data
public class OracleCDCConfig {
    // 默认是增量抽取：除此以外的模式为：all， time, scn
    private String readPosition = "current";

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