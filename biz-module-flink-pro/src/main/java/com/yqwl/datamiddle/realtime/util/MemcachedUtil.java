package com.yqwl.datamiddle.realtime.util;

import lombok.extern.slf4j.Slf4j;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.ConnectionFactoryBuilder;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.auth.AuthDescriptor;
import net.spy.memcached.auth.PlainCallbackHandler;
import net.spy.memcached.internal.OperationFuture;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @Description: MemcacheUtil
 * @Author: WangYouzheng
 * @Date: 2022/8/1 13:26
 * @Version: V1.0
 */
@Slf4j
public class MemcachedUtil {
    private static final MemcachedClient connect = connect();

    public static void main(String[] args) {
        final String host = "192.168.3.96";//控制台上的“内网地址”
        final String port ="11211"; //默认端口 11211，不用改
        final String username = "root";//控制台上的“实例ID”，新版ocs的username可以置空
        final String password = "fqwl@123!";//邮件中提供的“密码”
        MemcachedClient cache = null;
        try {
            AuthDescriptor ad = new AuthDescriptor(new String[]{"PLAIN"}, new PlainCallbackHandler(username, password));
            cache = new MemcachedClient(
                    new ConnectionFactoryBuilder().setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
                            // .setAuthDescriptor(ad)
                            .build(),
                    AddrUtil.getAddresses(host + ":" + port));
            System.out.println("OCS Sample Code");
            //向OCS中存一个key为"ocs"的数据，便于后面验证读取数据
            String key = "ocs";
            String value = "Open Cache Service,  from www.Aliyun.com";
            int expireTime = 1000; // 过期时间，单位s; 从写入时刻开始计时，超过expireTime s后，该数据过期失效，无法再读出；
            OperationFuture<Boolean> future = cache.set(key, expireTime, value);
            future.get();  // spymemcached set()是异步的，future.get() 等待cache.set()操作结束，也可以不等待，用户根据自己需求选择
            //向OCS中存若干个数据，随后可以在OCS控制台监控上看到统计信息
            for(int i=0;i<100;i++){
                key="key-"+i;
                value="value-"+i;
                //执行set操作，向缓存中存数据
                expireTime = 1000; // 过期时间，单位s
                future = cache.set(key, expireTime, value);
                future.get();  //  确保之前(cache.set())操作已经结束
            }
            System.out.println("Set操作完成!");
            //执行get操作，从缓存中读数据,读取key为"ocs"的数据
            System.out.println("Get操作:"+cache.get(key));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        if (cache != null) {
            cache.shutdown();
        }
    }

    public static void loginWithAuth() {

    }
    public static void loginNoAuth() {

    }

    /**
     * 建立Memcache 连接
     *
     * @return
     */
    public static MemcachedClient connect() {
        MemcachedClient cache = null;
        final String host = "192.168.3.96";//控制台上的“内网地址”
        final String port ="11211"; //默认端口 11211，不用改
        try {
            cache = new MemcachedClient(
                    new ConnectionFactoryBuilder().setProtocol(ConnectionFactoryBuilder.Protocol.BINARY)
                            // .setAuthDescriptor(ad)
                            .build(),
                    AddrUtil.getAddresses(host + ":" + port));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        return cache;
    }

    /**
     * 同步设置值，成功返回true,否则false
     *
     * tips: 会通过future阻塞
     *
     * @param key
     * @param exp
     * @param value
     * @return
     */
    public static Boolean setValue(String key, int exp, Object value) {

        OperationFuture<Boolean> future = connect.set(key, exp, value);
        try {
            return future.get();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    /**
     * 异步设置值
     *
     * @param key
     * @param exp
     * @param value
     * @return
     */
    public static OperationFuture<Boolean> setValueAsync(String key, int exp, Object value) {
        return connect.set(key, exp, value);
    }
}
