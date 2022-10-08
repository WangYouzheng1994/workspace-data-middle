package org.datamiddle.cdc.oracle.bean;

import lombok.Data;

/**
 *   memcache 对象配置类
 */
@Data
public class MemcacheConfEntity {
    private String scn;
    private String querydata;
    private int flags;
    private long cas;
    private int expire;
    private long timeing;

}
