package com.yqwl.datamiddle.realtime.app.ods.scn.handle;

/**
 * @Description: 幂等处理器
 * @Author: WangYouzheng
 * @Date: 2022/7/29 14:36
 * @Version: V1.0
 */
public class OracleCDCSCNHandle {
    /**
     * 处理过程：
     * 1. 先判定是否符合日期要求, 要求是2020年以后的scn 否则不予过滤
     * 2. 随后判定此scn 是否已经处理
     * 2.1 读取redis
     * 2.2 读取mysql
     * 2.3 写入mysql表 config_vlms_scn（此处分表处理）
     * 2.4 写入redis (此处无需做缓存DB强一致写入保证)
     *
     * @return
     */
    public static boolean handle() {
        boolean handleResult = true;



        return handleResult;
    }
}