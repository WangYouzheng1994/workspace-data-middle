package com.yqwl.datamiddle.realtime.app.ods.scn.shard;

/**
 * @Description: 分表
 * @Author: WangYouzheng
 * @Date: 2022/7/29 14:46
 * @Version: V1.0
 */
public abstract class AbstractShardMySql {
    /**
     * 获取分表名称
     *
     * @return
     */
    public String getShardName() {


        return "";
    }

    public static enum ShardType {
        /**
         * hash最后位数
         */
        HASH_NUM,
        /**
         * hash整个名称
         */
        HASH_NAME
    }
}