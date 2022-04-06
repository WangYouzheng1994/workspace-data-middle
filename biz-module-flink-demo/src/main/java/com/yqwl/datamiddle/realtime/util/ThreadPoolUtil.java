package com.yqwl.datamiddle.realtime.util;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description: DCL单例线程池
 * @Author: WangYouzheng
 * @Date: 2022/1/7 9:55
 * @Version: V1.0
 */
public class ThreadPoolUtil {
    private static volatile ThreadPoolExecutor pool;

    public static ThreadPoolExecutor getInstance() {
        if (pool == null) {
            synchronized (ThreadPoolUtil.class) {
                if (pool == null) {
                    pool = new ThreadPoolExecutor(
                            20, 20, 300, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(Integer.MAX_VALUE)
                    );
                }
            }
        }
        return pool;
    }
}
