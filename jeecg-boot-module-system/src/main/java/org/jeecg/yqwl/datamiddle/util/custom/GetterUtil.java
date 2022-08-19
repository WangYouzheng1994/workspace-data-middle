package org.jeecg.yqwl.datamiddle.util.custom;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @Description: GetterUtil
 * @Author: WangYouzheng
 * @Date: 2021/9/6 14:03
 * @Version: V1.0
 */
@Slf4j
public class GetterUtil {
    /**
     * 获取字符串长度，如果是空 返回1
     *
     * @param strs
     * @return
     */
    public static Integer getStrlen(String... strs) {
        int result = 0;

        for (String str : strs) {
            result += StringUtils.isNotBlank(str) ? str.length() : 1 ;
        }

        return result;
    }

    /**
     * 获取Map
     *
     * @param map
     * @return 如果是空，返回HashMap
     */
    public static Map getMap(Object map) {
        if (map != null) {
            return (Map) map;
        } else {
            return new HashMap();
        }
    }

    public static String getString(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    public static Long getLong(Object value, Long defaultVal) {
        if (value == null) {
            return defaultVal;
        } else {
            try {
                return new Long(value.toString());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return defaultVal;
            }
        }
    }

    /**
     * 获取 Long数据，默认值0L
     * @param value
     * @return
     */
    public static Long getLong(Object value ) {
        return getLong(value, 0L);
    }

    /**
     * uuid
     *
     * @return
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace(StringPool.DASH, StringPool.EMPTY);
    }

    /**
     * snowflake id
     * @return
     */
    public static Long workerId() {
        return IdWorker.getId();
    }

    /**
     * snowflake id
     * @return
     */
    public static String workerIdStr() {
        return IdWorker.getIdStr();
    }

    /**
     * 获取mongoDB分页参数Query
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    //public static Query getMongoLimitQuery(Integer pageNo, Integer pageSize) {
    //    return getMongoLimitQuery(pageNo, pageSize, new Query());
    //}

    /**
     * 获取mongodb 分页query
     *
     * @param pageNo
     * @param pageSize
     * @param query
     * @return
     */
    //public static Query getMongoLimitQuery(Integer pageNo, Integer pageSize, Query query) {
    //    if (query == null) {
    //        query = new Query();
    //    }
    //    query.limit(pageSize).skip((pageNo - 1)  * pageSize);
    //
    //    return query;
    //}

    /**
     * 获取整形
     *
     * @param value
     * @return 如果是null或者异常返回0
     */
    public static Integer getInteger(Object value) {
        if (value == null) {
            return 0;
        } else {
            try {
                return new Integer(value.toString());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return 0;
            }
        }
    }

    /**
     * 获取空分页返回值
     *
     * @return
     */
    public static IPage getEmptyPage() {
        IPage objectPage = new Page<>();
        objectPage.setRecords(Collections.emptyList());
        objectPage.setTotal(0L);
        return objectPage;
    }

    public static void main(String[] args) {
        System.out.println(uuid());
        System.out.println(workerIdStr());
    }
}
