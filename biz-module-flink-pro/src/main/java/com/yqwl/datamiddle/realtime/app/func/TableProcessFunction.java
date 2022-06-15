package com.yqwl.datamiddle.realtime.app.func;

import com.alibaba.fastjson2.JSONObject;
import com.yqwl.datamiddle.realtime.bean.TableProcess;
import com.yqwl.datamiddle.realtime.common.PhoenixConfig;
import com.yqwl.datamiddle.realtime.util.MysqlUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * @Description:
 * @Author: WangYouzheng
 * @Date: 2021/12/28 11:18
 * @Version: V1.0
 */
public class TableProcessFunction extends ProcessFunction<JSONObject, JSONObject> {

    private static final Logger LOGGER = LogManager.getLogger(TableProcessFunction.class);

    //因为要将维度数据写到侧输出流，所以定义一个侧输出流标签
    private OutputTag<JSONObject> outputTag;

    public TableProcessFunction(OutputTag<JSONObject> outputTag) {
        this.outputTag = outputTag;
    }

    //用于在内存中存储表配置对象 [表名,表配置信息]
    private Map<String, TableProcess> tableProcessMap = new HashMap<>();
    //表示目前内存中已经存在的要放入mysql中的表
    private Set<String> existsTables = new HashSet<>();
    //声明 Phoenix 连接
    private Connection connection = null;

    @Override
    public void open(Configuration parameters) throws Exception {
        //初始化 Phoenix 连接
        Class.forName(PhoenixConfig.PHOENIX_DRIVER);
        connection = DriverManager.getConnection(PhoenixConfig.PHOENIX_SERVER);
        //初始化配置表信息
        initTableProcessMap();
        //开启定时任务,用于不断读取配置表信息 从现在起过 delay 毫秒以后，每隔 period 更新一次
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                initTableProcessMap();
            }
        }, 5000, 5000);
    }

    /**
     * 初始化分流配置集合
     */
    private void initTableProcessMap() {
        LOGGER.info("更新配置的处理信息");
        //查询 MySQL 中的配置表数据
        List<TableProcess> tableProcessList = MysqlUtil.queryList("select * from table_process", TableProcess.class, true);
        //遍历查询结果,将数据存入结果集合
        for (TableProcess tableProcess : tableProcessList) {
            //获取源表表名
            String sourceTable = tableProcess.getSourceTable();
            //获取数据操作类型
            String operateType = tableProcess.getOperateType();
            //获取结果表表名
            String sinkTable = tableProcess.getSinkTable();
            //获取 sink 类型
            String sinkType = tableProcess.getSinkType();
            //拼接字段创建主键
            String key = sourceTable + ":" + operateType;
            //将数据存入结果集合
            tableProcessMap.put(key, tableProcess);
            //如果是向 Hbase 中保存的表，那么判断在内存中维护的 Set 集合中是否存在
            if ("insert".equals(operateType) && "hbase".equals(sinkType)) {
                // 通过add方法的返回值判定是否存在该表
                boolean notExist = existsTables.add(sourceTable);
                //如果表信息数据不存在内存,则在 Phoenix 中创建新的表
                if (notExist) {
                    checkTable(sinkTable, tableProcess.getSinkColumns(), tableProcess.getSinkPk(),
                            tableProcess.getSinkExtend());
                }
            }
        }
        if (tableProcessMap == null || tableProcessMap.size() == 0) {
            throw new RuntimeException("缺少处理信息");
        }
    }

    /**
     * 判定hbase是否存在此表，不存在进行pheonix建表语句的拼接，进行建表动作。
     *
     * @param tableName
     * @param fields
     * @param pk
     * @param ext
     */
    private void checkTable(String tableName, String fields, String pk, String ext) {
        //主键不存在,则给定默认值
        if (pk == null) {
            pk = "id";
        }
        //扩展字段不存在,则给定默认值
        if (ext == null) {
            ext = "";
        }
        //创建字符串拼接对象,用于拼接建表语句 SQL
        StringBuilder createSql = new StringBuilder("create table if not exists " +
                PhoenixConfig.HBASE_SCHEMA + "." + tableName + "(");
        //将列做切分,并拼接至建表语句 SQL 中
        String[] fieldsArr = fields.split(",");
        for (int i = 0; i < fieldsArr.length; i++) {
            String field = fieldsArr[i];
            if (pk.equals(field)) {
                createSql.append(field).append(" varchar primary key ");
            } else {
                createSql.append("info.").append(field).append(" varchar");
            }
            if (i < fieldsArr.length - 1) {
                createSql.append(",");
            }
        }
        createSql.append(")");
        createSql.append(ext);
        try {
            //执行建表语句在 Phoenix 中创建表
            System.out.println(createSql);
            PreparedStatement ps = connection.prepareStatement(createSql.toString());
            ps.execute();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("建表失败！！！");
        }
    }

    /**
     * Process one element from the input stream.
     */
    @Override
    public void processElement(JSONObject jsonObj, Context ctx, Collector<JSONObject> out) throws Exception {
        String table = jsonObj.getString("table");
        String type = jsonObj.getString("type");
        JSONObject dataJsonObj = jsonObj.getJSONObject("data");
        //如果是使用 Maxwell 的初始化功能，那么 type 类型为 bootstrap-insert,我们这里也标记为 insert，方便后续处理
        if (type.equals("bootstrap-insert")) {
            type = "insert";
            jsonObj.put("type", type);
        }
        //获取配置表的信息
        if (tableProcessMap != null && tableProcessMap.size() > 0) {
            String key = table + ":" + type;
            TableProcess tableProcess = tableProcessMap.get(key);
            if (tableProcess != null) {
                jsonObj.put("sink_table", tableProcess.getSinkTable());
                if (tableProcess.getSinkColumns() != null && tableProcess.getSinkColumns().length() > 0) {
                    filterColumn(jsonObj.getJSONObject("data"), tableProcess.getSinkColumns());
                }
            } else {
                LOGGER.info("No This Key: {}", key);
            }
            if (tableProcess != null && TableProcess.SINK_TYPE_HBASE.equalsIgnoreCase(tableProcess.getSinkType())) {
                // 如果是hbase的 那么把这个数据和outputTag打标挂钩。
                ctx.output(outputTag, jsonObj);
            } else if (tableProcess != null && TableProcess.SINK_TYPE_KAFKA.equalsIgnoreCase(tableProcess.getSinkType())) {
                out.collect(jsonObj);
            }
        }
    }

    /**
     * 根据配置过滤要抽取的字段
     *
     * @param data
     * @param sinkColumns
     */
    private void filterColumn(JSONObject data, String sinkColumns) {
        String[] cols = StringUtils.split(sinkColumns, ",");
        Set<Map.Entry<String, Object>> entries = data.entrySet();
        List<String> columnList = Arrays.asList(cols);
        for (Iterator<Map.Entry<String, Object>> iterator = entries.iterator(); iterator.hasNext(); ) {
            Map.Entry<String, Object> entry = iterator.next();
            if (!columnList.contains(entry.getKey())) {
                iterator.remove();
            }
        }
    }
}
