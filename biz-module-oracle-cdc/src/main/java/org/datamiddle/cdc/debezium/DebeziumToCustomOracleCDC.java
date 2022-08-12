package org.datamiddle.cdc.debezium;

import io.debezium.engine.ChangeEvent;
import io.debezium.engine.DebeziumEngine;
import org.apache.kafka.connect.source.SourceRecord;

import java.util.List;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/7/22 13:40
 * @Version: V1.0
 */
public class DebeziumToCustomOracleCDC implements DebeziumEngine.ChangeConsumer<ChangeEvent<SourceRecord, SourceRecord>>  {

    private static final String READ_TYPE = "read";
    private static final String CREATE_TYPE = "create";
    private static final String INSERT_TYPE = "insert";
    private static final String UPDATE_TYPE = "update";
    private static final String QUERY_TYPE = "query";

    // 22062286790Y3 51905346
    @Override
    public void handleBatch(List<ChangeEvent<SourceRecord, SourceRecord>> records, DebeziumEngine.RecordCommitter<ChangeEvent<SourceRecord, SourceRecord>> committer) throws InterruptedException {
        System.out.println("啦啦啦啦啦");
/*
        for (Object sourceRecord : records) {
            // 1. 创建JSON对象用于存储最终数据
            JSONObject result = new JSONObject();

            // 2. 获取库名&表名
            String topic = ((SourceRecord)sourceRecord).topic();
            String[] fields = topic.split("\\.");
            String database = fields[1];
            String tableName = fields[2];

            Struct value = (Struct) ((SourceRecord)sourceRecord).value();
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
            Envelope.Operation operation = Envelope.operationFor(((SourceRecord)sourceRecord));
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
            result.put("ts", tsMs);*/
        // }
    }
}