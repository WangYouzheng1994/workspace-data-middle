package org.datamiddle;

import io.debezium.connector.oracle.OracleConnector;
import io.debezium.embedded.Connect;
import io.debezium.engine.DebeziumEngine;
import io.debezium.relational.history.FileDatabaseHistory;
import org.apache.kafka.connect.storage.FileOffsetBackingStore;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2022/7/22 10:29
 * @Version: V1.0
 */
public class DebeziumTest {
    public static void main(String[] args) {
        // 配置
        Properties props = new Properties();
        props.setProperty("name", "oracle-engine-0036"); //唯一实例
        props.setProperty("connector.class", OracleConnector.class.getName());
        props.setProperty("offset.storage", FileOffsetBackingStore.class.getName());
        // 指定 offset 存储目录
        props.setProperty("offset.storage.file.filename", "D:/data/oracledebezium/oracle45.txt");
        // 指定 Topic offset 写入磁盘的间隔时间
        props.setProperty("offset.flush.interval.ms", "60000");
        //设置数据库连接信息
        props.setProperty("database.hostname", "192.168.3.95");
        props.setProperty("database.port", "1521");
        props.setProperty("database.user", "flinkuser");
        props.setProperty("database.password", "flinkpw");
        props.setProperty("database.server.id", "85701");
        //指定 CDB 模式的实例名
        props.setProperty("database.dbname", "ORCL");
        //C##DBZUSER.STU可以用正则
        // props.setProperty("table.include.list", "STUDENT1");
        // props.setProperty("snapshot.include.collection.list", "TDS_LJ.SPTB02");
        // props.setProperty("schema.include.list", "TDS_LJ");
        // props.setProperty("table.include.list", "SPTB02");
        // props.setProperty("schema.whitelist","TDS_LJ");
        props.setProperty("table.include.list", "TDS_LJ.TEST_SCN");
        props.setProperty("database.history",
                FileDatabaseHistory.class.getCanonicalName());
        props.setProperty("database.history.file.filename",
                "D:/data/oracledebezium/oracle55.txt");
        //每次运行需要对此参数进行修改，因为此参数唯一
        props.setProperty("database.server.name", "my-oracle-connector-0029");

        //是否输出 schema 信息
        // props.setProperty("key.converter.schemas.enable", "false");
        // props.setProperty("value.converter.schemas.enable", "false");
        props.setProperty("database.serverTimezone", "UTC"); // 时区
        props.setProperty("database.connection.adapter", "logminer"); // 模式
        // props.put("database.tablename.case.insensitive", "false");
        // props.put("include.schema.changes", "false");
        props.put("database.history.skip.unparseable.ddl", "true");
         props.put("snapshot.mode", "initial");
        // props.put("snapshot.mode", "schema_only");
        // props.put("log.mining.strategy", "redo_log_catalog"); //redo_log_catalog
        props.put("log.mining.strategy", "online_catalog");
        props.put("log.mining.continuous.mine", "true");
        // props.put("log.mining.archive.log.only.mode", "true");
        // props.setProperty("database.history.skip.unparseable.ddl", String.valueOf(true));

        props.put("database.url", "jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(LOAD_BALANCE=OFF)(FAILOVER=OFF)(ADDRESS=(PROTOCOL=tcp)(HOST=" +
                "192.168.3.95" + ")(PORT=" +
                "1521" + ")))(CONNECT_DATA=(SID=" +
               "ORCL" + ")))");

        // 配置菁高级别
        props.put("event.processing.failure.handling.mode", "warn");

        // 2. 构建 DebeziumEngine
        // 使用 Json 格式
        DebeziumEngine engine = DebeziumEngine
                .create(Connect.class)
                .using(props)
                .notifying(record -> {
                    // record中会有操作的类型（增、删、改）和具体的数据
                    System.out.println(record);
                    System.out.println("record.key() = " + record.key());
                    System.out.println("record.value() = " + record.value());
                }).notifying(new DebeziumToCustomOracleCDC())
                .using((success, message, error) -> {
                    //查看错误信息
                    if (!success && error != null) {
                        // 报错回调
                        System.out.println("----------error------");
                        System.out.println(message);
                        System.out.println(error);
                        error.printStackTrace();
                    }
                })
                .build();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(engine);

    }
}
