package org.datamiddle.cdc.util;

import cn.hutool.setting.dialect.Props;
import org.datamiddle.cdc.oracle.bean.MemcacheConfEntity;
import org.jeecgframework.boot.DateUtil;
import org.jeecgframework.boot.IdWorker;

import java.io.InputStream;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;

/**
 *   获取mysql 中 memcache 配置表中最大值的数据
 */
public class MysqlTools {

    public static MemcacheConfEntity getConnection() throws Exception{
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        MemcacheConfEntity memcacheConfEntity = null;
        try {
            //1.读取配置文件中的4个基本信息
           /* InputStream inputStream = MysqlTools.class.getClassLoader().getResourceAsStream("application.properties");
            Properties properties = new Properties();
            properties.load(inputStream);*/
            Props props = PropertiesUtil.getProps();

            String user = props.getStr("user");
            String password = props.getStr("password");
            String url = props.getStr("url");
            String driverClass = props.getStr("driverClass");
            //2.加载驱动
            Class.forName(driverClass);
            //3.获取连接
            conn = DriverManager.getConnection(url, user, password);
            //2.预编译sql语句，返回PreparedStatement的实例
            String sql = "select scn,querydata,flags,cas,expire,timeing from memcached_conf where cas = (select max(cas) from memcached_conf )";
            preparedStatement = conn.prepareStatement(sql);
            //3.填充占位符
            //4.执行
            preparedStatement.execute();
            ResultSet resultSet = preparedStatement.executeQuery();
            memcacheConfEntity =new MemcacheConfEntity();
            //获取配置文件数据
            if(resultSet.next()){
                String scn = resultSet.getString("scn");
                String querydata = resultSet.getString("querydata");
                String flagsStr = resultSet.getString("flags");
                String casStr = resultSet.getString("cas");
                String expireStr = resultSet.getString("expire");
                String timeingStr = resultSet.getString("timeing");
                int flags = Integer.valueOf(flagsStr);
                long cas = Long.valueOf(casStr);
                int expire = Integer.valueOf(expireStr);
                long timeing = Long.valueOf(timeingStr);
                memcacheConfEntity.setScn(scn);
                memcacheConfEntity.setCas(cas);
                memcacheConfEntity.setExpire(expire);
                memcacheConfEntity.setFlags(flags);
                memcacheConfEntity.setQuerydata(querydata);
                memcacheConfEntity.setTimeing(timeing);
                //结束
                return memcacheConfEntity ;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            //5.资源的关闭
            preparedStatement.close();
            conn.close();
        }
        return memcacheConfEntity;
    }

    public static void main(String[] args) throws Exception {
      String scn = "98438100 0x003a24.00001687.0010 ";
        String[] split = scn.split(" ");
        for (int i = 0; i <split.length ; i++) {
            System.out.println(i+":"+split[i]);
        }
        //  MemcacheConfEntity connection = MysqlTools.getConnection();
       // System.out.println(connection.toString());
        //long time = new Date().getTime();
        //System.out.println(time);
        //System.out.println(System.currentTimeMillis());
        System.out.println(dateToStamp("1665309008610"));
        System.out.println(dateToStamp("1665313048135"));

        System.out.println(dateToStamp("1665316190522"));
        System.out.println(dateToStamp2("2021-12-19 18:07:06"));

        //IdWorker idWorker = new IdWorker(1, 1, 1);
        /*BigInteger currentStartScn =BigInteger.valueOf(16948905185114l);
        int scnScope = 100000;
        BigInteger endScn = BigInteger.valueOf(16948905194023l);
        BigInteger subtract = endScn.subtract(currentStartScn);
        //每次查询10w的数据量
        BigInteger divide = subtract.divide(BigInteger.valueOf(scnScope));
        System.out.println(divide);
        BigInteger remainder = endScn.remainder(currentStartScn);
        System.out.println(remainder);*/
    }
    public static String dateToStamp(String s) throws ParseException {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }
    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp2(String s) {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = simpleDateFormat.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

}
