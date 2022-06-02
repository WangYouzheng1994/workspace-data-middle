package com.yqwl.datamiddle.realtime.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.api.java.tuple.Tuple2;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Author: Felix
 * Date: 2021/2/5
 * Desc: 用于维度查询的工具类  底层调用的是PhoenixUtil
 * select * from dim_base_trademark where id=10 and name=zs;
 */
@Slf4j
public class DimUtil {

    public static final String MYSQL_DB_TYPE = "mysql";
    public static final String CLICKHOUSE_DB_TYPE = "clickhouse";
    public static final String HBASE_DB_TYPE = "hbase";

    /**
     * 从Phoenix中查询数据，没有使用缓存
     */
    public static JSONObject getDimInfoNoCache(String tableName, Tuple2<String, String>... cloNameAndValue) {
        //拼接查询条件
        String whereSql = " where ";
        for (int i = 0; i < cloNameAndValue.length; i++) {
            Tuple2<String, String> tuple2 = cloNameAndValue[i];
            String filedName = tuple2.f0;
            String fieldValue = tuple2.f1;
            if (i > 0) {
                whereSql += " and ";
            }
            whereSql += filedName + "='" + fieldValue + "'";
        }

        String sql = "select * from " + tableName + whereSql;
        //System.out.println("查询维度的SQL:" + sql);
        log.info("查询维度的SQL:{}", sql);
//        List<JSONObject> dimList = PhoenixUtil.queryList(sql, JSONObject.class);
        List<JSONObject> dimList = MysqlUtil.queryList(sql, JSONObject.class, false);
        JSONObject dimJsonObj = null;
        //对于维度查询来讲，一般都是根据主键进行查询，不可能返回多条记录，只会有一条
        if (dimList != null && dimList.size() > 0) {
            dimJsonObj = dimList.get(0);
        } else {
            //System.out.println("维度数据没有找到:" + sql);
            log.info("维度数据没有找到:{}", sql);
        }
        return dimJsonObj;
    }

    //在做维度关联的时候，大部分场景都是通过id进行关联，所以提供一个方法，只需要将id的值作为参数传进来即可
    @SuppressWarnings("unchecked")
    public static JSONObject getDimInfo(String dbType, String tableName, String columnName, Object value, String andSql) {
        // 判定是否有多个列值,约定用,号分割
        //System.out.println("获取的value值:" + value);
        log.info("获取的查询where条件里value值:{}", value);
        if (Objects.nonNull(value)) {
            if (value instanceof List) {
                List list = (List) value;
                if (CollectionUtils.isNotEmpty(list)) {
                    //获取查询的列名
                    String[] columnArr = StringUtils.split(columnName, ",");
                    List<Tuple2<String, Object>> tupleList = new ArrayList<>();
                    for (int i = 0; i < columnArr.length; i++) {
                        //列名和对应值包装成一个对象
                        tupleList.add(Tuple2.of(columnArr[i], ((List) value).get(i)));
                    }
                    return getDimInfo(dbType, tableName, andSql, tupleList.toArray(new Tuple2[]{}));
                }
            } else {
                return getDimInfo(dbType, tableName, andSql, Tuple2.of(StringUtils.isBlank(columnName) ? "id" : columnName, value));
            }
        }
        return null;
    }

    /*
        优化：从Phoenix中查询数据，加入了旁路缓存
             先从缓存查询，如果缓存没有查到数据，再到Phoenix查询，并将查询结果放到缓存中

        redis
            类型：    string
            Key:     dim:表名:值       例如：dim:DIM_BASE_TRADEMARK:10_xxx
            value：  通过PhoenixUtil到维度表中查询数据，取出第一条并将其转换为json字符串
            失效时间:  24*3600

        //"DIM_BASE_TRADEMARK", Tuple2.of("id", "13"),Tuple2.of("tm_name","zz"))

        redisKey= "dim:dim_base_trademark:"
        where id='13'  and tm_name='zz'


        dim:dim_base_trademark:13_zz ----->Json

        dim:dim_base_trademark:13_zz
    */

    /**
     * 获取 维度数据 先去缓存找，缓存找不到 就去hbase找。 随后把得到的结果放入到redis中。 有效期24小时。
     *
     * @param tableName       表名
     * @param cloNameAndValue
     * @return
     */
    @SuppressWarnings("unchecked")
    public static JSONObject getDimInfo(String dbType, String tableName, String andSql, Tuple2<String, Object>... cloNameAndValue) {
        //拼接查询条件
        String whereSql = " where ";
        String redisKey = "dwm:vlms:" + tableName.toLowerCase() + ":";
        for (int i = 0; i < cloNameAndValue.length; i++) {
            Tuple2<String, Object> tuple2 = cloNameAndValue[i];
            String filedName = tuple2.f0;
            Object fieldValue = tuple2.f1;
            //判断null
            if (Objects.isNull(fieldValue)) {
                continue;
            }
            if (i > 0) {
                whereSql += " and ";
                redisKey += "_";
            }
            if (fieldValue instanceof Integer) {
                whereSql += filedName + "=" + fieldValue;
            } else {
                whereSql += filedName + "='" + fieldValue + "'";
            }
            redisKey += fieldValue;
        }
        //System.out.println("redisKey:" + redisKey);
        log.info("redisKey:{}", redisKey);
        //拼接额外添加的and sql
        if (StringUtils.isNotEmpty(andSql)) {
            whereSql += andSql;
        }
        //System.out.println("组装生成where sql:{}" , whereSql);
        log.info("组装生成where sql:{}", whereSql);
        //从Redis中获取数据
        Jedis jedis = null;
        //维度数据的json字符串形式
        String dimJsonStr = null;
        //维度数据的json对象形式
        JSONObject dimJsonObj = null;
        try {
            //获取jedis客户端
            //jedis = RedisUtil.getJedis();
            //根据key到Redis中查询
            //dimJsonStr = jedis.get(redisKey);
            //判断是否从Redis中查询到了数据
            //if (dimJsonStr != null && dimJsonStr.length() > 0) {
            //dimJsonObj = JSON.parseObject(dimJsonStr);
            //} else {
            //如果在Redis中没有查到数据，需要到Phoenix中查询
            String sql = "select * from " + tableName + whereSql;
            //System.out.println("查询维度的SQL:" + sql);
            log.info("查询维度的SQL:{}", sql);
            List<JSONObject> dimList = null;
            if (MYSQL_DB_TYPE.equals(dbType)) {
                dimList = DbUtil.queryList(sql, JSONObject.class, false);
            } else if (HBASE_DB_TYPE.equals(dbType)) {
                dimList = PhoenixUtil.queryList(sql, JSONObject.class);
            }
            //对于维度查询来讲，一般都是根据主键进行查询，不可能返回多条记录，只会有一条
            if (dimList != null && dimList.size() > 0) {
                dimJsonObj = dimList.get(0);
                //将查询出来的数据放到Redis中缓存起来
                //if (jedis != null) {
                //   jedis.setex(redisKey, 3600 * 24, dimJsonObj.toJSONString());
                // }
            } else {
                //System.out.println("维度数据没有找到:" + sql);
                log.info("维度数据没有找到:{}", sql);
            }

            //}
        } catch (Exception e) {
            //System.out.println("维度查询异常信息:" + e.getMessage());
            log.info("维度查询异常信息:{}", e.getMessage());
            // throw new RuntimeException("从redis中查询维度失败");
        } finally {
            //关闭Jedis
            if (jedis != null) {
                jedis.close();
            }
        }
        return dimJsonObj;
    }


    /**
     * 根据key让Redis中的缓存失效
     */
    public static void deleteCached(String tableName, String id) {
        String key = "dim:" + tableName.toLowerCase() + ":" + id;
        try {
            Jedis jedis = RedisUtil.getJedis();
            // 通过key清除缓存
            jedis.del(key);
            jedis.close();
        } catch (Exception e) {
            System.out.println("缓存异常！");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
    }
}
