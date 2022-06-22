package com.yqwl.datamiddle.realtime.common;

/**
 * redis 相关配置
 */
public class JedisConfig {

    public static final String HOSTNAME = "192.168.3.95";
    public static final String PASSWORD = "fqwl!123";
    public static final int PORT = 6379;
    public static final int TIMEOUT = 10000;
    public static final int MAX_TOTAL = 1000;
    public static final int MAX_WAIT_MILLIS = 2000;
    public static final int MAX_IDLE = 50;
    public static final int MIN_IDLE = 5;


    //线上正式
   // public static final String HOSTNAME = "10.123.175.197";
   // public static final String PASSWORD = "+5BhrREAnCh1ORZn";


}
