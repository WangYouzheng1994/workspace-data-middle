package org.datamiddle.cdc.util;

import org.datamiddle.cdc.oracle.bean.MemcacheConfEntity;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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
            InputStream inputStream = MysqlTools.class.getClassLoader().getResourceAsStream("application.properties");
            Properties properties = new Properties();
            properties.load(inputStream);

            String user = properties.getProperty("user");
            String password = properties.getProperty("password");
            String url = properties.getProperty("url");
            String driverClass = properties.getProperty("driverClass");
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

    }

}
