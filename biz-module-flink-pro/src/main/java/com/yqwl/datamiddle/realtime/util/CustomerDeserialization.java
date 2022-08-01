package com.yqwl.datamiddle.realtime.util;

import com.alibaba.fastjson2.JSONObject;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import io.debezium.data.Envelope;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.streaming.api.transformations.SideOutputTransformation;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

public class CustomerDeserialization implements DebeziumDeserializationSchema<String> {

    private static final String READ_TYPE = "read";
    private static final String CREATE_TYPE = "create";
    private static final String INSERT_TYPE = "insert";
    private static final String UPDATE_TYPE = "update";
    private static final String QUERY_TYPE = "query";
    private static final ThreadPoolExecutor instance = ThreadPoolUtil.getInstance();

    /**
     * 序列化后封装的统一数据结构格式
     * {
     * "database":"",
     * "tableName":"",
     * "type":"c u d",
     * "before":{"":"","":""....},
     * "after":{"":"","":""....},
     * "ts": timestamp
     * }
     */
    /**
     * read:
     * {
     * "database":"datasource_kafka",
     * "before":{},
     * "after":{"order_no":"20220303911728","create_time":1649412632000,"product_count":1,"product_id":434,"id":297118,"product_amount":3426},
     * "type":"read",  query
     * "tableName":"orders_detail",
     * "ts":1651830021955
     * }
     * <p>
     * insert:
     * {
     * "database":"datasource_kafka",
     * "before":{},
     * "after":{"order_no":"20220303855787","create_time":1647859623000,"product_count":2,"product_id":39,"id":300007,"product_amount":4453},
     * "type":"insert",
     * "tableName":"orders_detail",
     * "ts":1651830458870
     * }
     * <p>
     * update:
     * {
     * "database":"datasource_kafka",
     * "before":{"order_no":"20220303855786","create_time":1647859623000,"product_count":1,"product_id":38,"id":1008,"product_amount":4443},
     * "after":{"order_no":"20220303855786","create_time":1647859623000,"product_count":1,"product_id":3878,"id":1008,"product_amount":4443},
     * "type":"update",
     * "tableName":"orders_detail",
     * "ts":1651830576944
     * }
     * <p>
     * delete:
     * {
     * "database":"datasource_kafka",
     * "before":{"order_no":"20220303855786","create_time":1647859623000,"product_count":1,"product_id":3878,"id":1008,"product_amount":4443},
     * "after":{},
     * "type":"delete",
     * "tableName":"orders_detail",
     * "ts":1651830662880
     * }
     */
    @Override
    public void deserialize(SourceRecord sourceRecord, Collector<String> collector) throws Exception {
        // 1. 创建JSON对象用于存储最终数据
        JSONObject result = new JSONObject();

        // 2. 获取库名&表名
        String topic = sourceRecord.topic();
        String[] fields = topic.split("\\.");
        String database = fields[1];
        String tableName = fields[2];

        Struct value = (Struct) sourceRecord.value();
        // 3. 获取“before”数据
        Struct before = value.getStruct("before");
        JSONObject beforeJson = new JSONObject();
        if (before != null) {
            Schema beforeSchema = before.schema();
            List<Field> beforeFields = beforeSchema.fields();
            for (Field field : beforeFields) {
                Object beforeValue = before.get(field);
                beforeJson.put(field.name(), beforeValue);
            }
        }
//cdc读取的原始数据结构格式
//Struct{after=Struct{ID=Struct{scale=0,value=[B@1da5996c},NAME=222,AGE=Struct{scale=0,value=[B@7b5fa09d},CREATE_TIME=1650015242000},
//source=Struct{version=1.5.4.Final,connector=oracle,name=oracle_logminer,ts_ms=1650000249859,snapshot=true,db=ORCL,schema=FLINKUSER,table=TEST_A,scn=9011397},op=r,ts_ms=1650000249862}

        // 4. 获取“after”数据
        Struct after = value.getStruct("after");
        JSONObject afterJson = new JSONObject();
        if (after != null) {
            Schema afterSchema = after.schema();
            List<Field> afterFields = afterSchema.fields();
            for (Field field : afterFields) {
                Object afterValue = after.get(field);
                afterJson.put(field.name(), afterValue);
            }
        }

        // 5. 获取操作类型  read create update delete
        Envelope.Operation operation = Envelope.operationFor(sourceRecord);
        String type = operation.toString().toLowerCase();
        if (READ_TYPE.equals(type)) {
            type = INSERT_TYPE;
        } else if (CREATE_TYPE.equals(type)) {
            type = INSERT_TYPE;
        } else if (UPDATE_TYPE.equals(type)) {
            type = INSERT_TYPE;
        }

        // 6. 获取进入cdc时间
        Long tsMs = value.getInt64("ts_ms");

        // 7. 将字段写入JSON对象
        result.put("database", database);
        result.put("tableName", tableName);
        result.put("before", beforeJson);
        result.put("after", afterJson);
        result.put("type", type);
        result.put("ts", tsMs);
        //System.out.println("序列化统一数据格式：" + result.toJSONString());

        // 8. 输出数据
        // TODO:1. 如果scn存在，就跳过。3. 同时对于每个小时的scn进行记录，方便下游的数据回放，保证下游数据的释放是正常的. 2. 跳过大于2020年1月1号以前的数据。
        /*if () {




        }*/

        collector.collect(result.toJSONString());
    }

    @Override
    public TypeInformation<String> getProducedType() {
        return BasicTypeInfo.STRING_TYPE_INFO;
    }
}
