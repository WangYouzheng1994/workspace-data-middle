package com.yqwl.datamiddle.realtime.util;

import cn.hutool.setting.dialect.Props;
import com.google.common.base.CaseFormat;
import com.yqwl.datamiddle.realtime.bean.TableProcess;
import com.yqwl.datamiddle.realtime.common.MysqlConfig;
import org.apache.commons.beanutils.BeanUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2021/12/28 11:09
 * @Version: V1.0
 */
public class MysqlUtil {
    /**
     * mysql 查询方法，根据给定的 class 类型 返回对应类型的元素列表
     *
     * @param sql
     * @param clazz
     * @param underScoreToCamel 是否把对应字段的下划线名转为驼峰名
     * @param <T>
     * @return
     */
    public static <T> List<T> queryList(String sql, Class<T> clazz, Boolean underScoreToCamel) {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {

            //注册驱动
            Class.forName(MysqlConfig.DRIVER);
            Props props = PropertiesUtil.getProps();
            //建立连接
            conn = DriverManager.getConnection(props.getStr("mysql.url"), props.getStr("mysql.username"), props.getStr("mysql.password"));
            //创建数据库操作对象
            ps = conn.prepareStatement(sql);
            //执行 SQL 语句
            rs = ps.executeQuery();
            //处理结果集
            ResultSetMetaData md = rs.getMetaData();
            //声明集合对象，用于封装返回结果
            List<T> resultList = new ArrayList<T>();
            //每循环一次，获取一条查询结果
            while (rs.next()) {
                //通过反射创建要将查询结果转换为目标类型的对象
                T obj = clazz.newInstance();
                //对查询出的列进行遍历，每遍历一次得到一个列名
                for (int i = 1; i <= md.getColumnCount(); i++) {
                    String propertyName = md.getColumnName(i);
                    //如果开启了下划线转驼峰的映射，那么将列名里的下划线转换为属性的打
                    if (underScoreToCamel) {
                        //直接调用 Google 的 guava 的 CaseFormat LOWER_UNDERSCORE 小写开头+下划线->LOWER_CAMEL 小写开头+驼峰
                        propertyName = CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, propertyName);
                    }
                    //调用 apache 的 commons-bean 中的工具类，给 Bean 的属性赋值
                    BeanUtils.setProperty(obj, propertyName, rs.getObject(i));
                }
                resultList.add(obj);
            }
            return resultList;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("查询 mysql 失败！");
        } finally {
            //释放资源
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 测试验证
     *
     * @param args
     */
    public static void main(String[] args) {
        List<TableProcess> tableProcesses = queryList("select * from table_process",
                TableProcess.class, true);
        for (TableProcess tableProcess : tableProcesses) {
            System.out.println(tableProcess);
        }
    }
}
