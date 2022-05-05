package com.yqwl.datamiddle.realtime.util;

import com.alibaba.fastjson.JSONObject;
import com.ververica.cdc.debezium.DebeziumDeserializationSchema;
import io.debezium.data.Envelope;
import org.apache.flink.api.common.typeinfo.BasicTypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.util.Collector;
import org.apache.kafka.connect.data.Field;
import org.apache.kafka.connect.data.Schema;
import org.apache.kafka.connect.data.Struct;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.List;

public class CustomerDeserialization implements DebeziumDeserializationSchema<String> {
    /**
     * 封装的数据格式
     * {
     * "database":"",
     * "tableName":"",
     * "type":"c u d",
     * "before":{"":"","":""....},
     * "after":{"":"","":""....},
     * "ts": timestamp
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
//Struct{after=Struct{ID=Struct{scale=0,value=[B@1da5996c},NAME=222,AGE=Struct{scale=0,value=[B@7b5fa09d},CREATE_TIME=1650015242000},
// source=Struct{version=1.5.4.Final,connector=oracle,name=oracle_logminer,ts_ms=1650000249859,snapshot=true,db=ORCL,schema=FLINKUSER,table=TEST_A,scn=9011397},op=r,ts_ms=1650000249862}
//{"database":"FLINKUSER","before":{},"after":{"ID":{},"CREATE_TIME":1650015242000,"NAME":"222","AGE":{}},"type":"read","tableName":"TEST_A","ts":1649999915669}
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

        // 5. 获取操作类型    CREATE  UPDATE  DELETE
        Envelope.Operation operation = Envelope.operationFor(sourceRecord);
        String type = operation.toString().toLowerCase();
        if ("create".equals(type)) {
            type = "insert";
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



        // 8. 输出数据
        collector.collect(result.toJSONString());
    }

    @Override
    public TypeInformation<String> getProducedType() {
        return BasicTypeInfo.STRING_TYPE_INFO;
    }
}
