package org.datamiddle.cdc.oracle;

import cn.hutool.core.date.DateUtil;
import cn.hutool.setting.dialect.Props;
import com.alibaba.fastjson2.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.debezium.connector.oracle.OracleConnection;
import io.debezium.connector.oracle.logminer.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.datamiddle.cdc.oracle.bean.QueueData;
import org.datamiddle.cdc.oracle.bean.TransactionManager;
import org.datamiddle.cdc.oracle.converter.oracle.LogminerConverter;
import org.datamiddle.cdc.util.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

/**
 * @Description: Oracle Logminer CDC  一个CDC的任务
 * @Author: WangYouzheng
 * @Date: 2022/8/3 13:08
 * @Version: V1.0
 */
@Slf4j
public class OracleSource {
    //数据库用户名
    private static String schema;
    /**
     * 执行任务的线程池
     */
    private ExecutorService executor;

    private ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("debezium-engine").build();
    private ExecutorService connectionExecutor;
    /**
     * 启动该任务的配置
     */
    private OracleCDCConfig oracleCDCConfig;
    /**
     * 开始偏移量
     */
    private BigInteger startScn;
    /**
     * 结束偏移量
     */
    private BigInteger endScn;

    /**
     * logminer 步进  默认 2000
     */
    private BigInteger logminerStep;

    /**
     * 是否加载了online实时日志
     */
    private Boolean loadRedo = false;

    /**
     * 最后一条数据的位点 这个位点是摄取转换后的位置
     */
    private BigInteger currentSinkPosition;

    /**
     * 挖掘点位， 这个是logminer的一次摄取后的点位，因为在循环读取，需要有中间值
     */
    private BigInteger currentLogReadEnd;

    /**
     * 缓存控制器，放在这一层 你可以认为任务后续会管控多线程模式下的抽取动作
     */
    private TransactionManager transactionManager;

    /**
     * 针对挖掘到的 logminer log数据进行处理
     */
    // private LogminerHandler logminerHandler;

    /**
     * 设置转换器，转换器用于把日志转换成可用的 flink 流数据。
     */
    private LogminerConverter logminerConverter = new LogminerConverter(false, true);

    /**
     * 控制任务的运转状态，此状态应该迁移到DB
     */
    private boolean runningFlag = true;

    //日志唯一id rs_id
    private String rs_id;
    //scn 间隔范围
    private static Integer scnScope;
    //.scn 最小的区间
    private static Integer scnScopeMin;
    //最终确定scn范围
    private static Integer lastScnScope;
    //kafka topic name
    private static String KafkaTopicName = "test_oracle_cdc_custom";
    //定义时间区间
    private static String startTimeAfter="00:00:00";
    private static String endTimeAfter="02:00:00";
    private static String startTime="11:59:00";
    private static String endTime="15:00:00";

    /**
     * 转换日志生产
     */
    KafkaProducer<Object, Object> producer = KafkaProduceUtil.getKafkaProductBySchema("test_oracle_cdc_custom");

    /**
     * 概要设计：
     * 1. 建立连接测试
     * 2. 读取日志 获取每个日志的范围
     * 3. 过滤指定的表
     * 4. 循环取值 幂等过滤 把信息 推给传入的handler，经由LogminerConverter对日志数据进行转换。
     */
    public void start(OracleCDCConfig oracleCDCConfig) {
        // 初始化连接池
        connectionExecutor = new ThreadPoolExecutor(
                20, 20, 300, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(Integer.MAX_VALUE));
        // 初始化日志缓存器， 底层是google的 lru缓存架构
        transactionManager = new TransactionManager(1000L, 20);

        this.oracleCDCConfig = oracleCDCConfig;
        this.startScn = new BigInteger(oracleCDCConfig.getStartSCN());
        this.endScn = new BigInteger(oracleCDCConfig.getEndSCN());
        this.logminerStep = new BigInteger("2000");
        // 初始化连接
        OracleCDCConnection oracleCDCConnect = init(oracleCDCConfig);
        // 获取开始偏移量 根据模式判定
        startScn = getStartSCN(oracleCDCConnect, startScn);
        // 读取的位置 从startSCN偏移量开始
        this.currentSinkPosition = startScn;

        //初始化全量
        initOracleAllData(oracleCDCConnect);

        // 给转换器设置一个Connection，查询metaData信息。
        String jdbcUrl = oracleCDCConfig.getJdbcUrl();
        String username = oracleCDCConfig.getUsername();
        String password = oracleCDCConfig.getPassword();
        // String driverClass = oracleCDCConfig.getDriverClass();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    jdbcUrl,
                    username,
                    password);
            logminerConverter.setConnection(connection);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        // 初始化Logminer
        payLoad(oracleCDCConnect);
        // 读取Log日志

        // 一直沿用这个流程去处理
        while (runningFlag) {
            running(oracleCDCConnect);
        }
    }

    /***
     *  初始化全量数据
     */
    private void initOracleAllData(OracleCDCConnection oracleCDCConnect) {
        //获取当前需要查询的数据表
        List<String> tableList = oracleCDCConfig.getTable();
        //获取当前的scn
        BigInteger currentMaxScn = null;
        Connection connection = oracleCDCConnect.getConnection();
        // 获取当前数据库的最大偏移量
        currentMaxScn = oracleCDCConnect.getCurrentScn(connection);
        //遍历
        for (String tableName:tableList) {
            log.info("初始化表："+tableName);
            oracleCDCConnect.initOracleAllDataConnect(connection,currentMaxScn,producer,KafkaTopicName,schema,tableName);
        }
        //赋值startscn 与endscn
        this.startScn =currentMaxScn;
        this.endScn =currentMaxScn;
        oracleCDCConnect.setEndScn(this.endScn);
        this.currentSinkPosition =this.endScn;
        //输出一下日志
        log.info("初始化init 闪回数据已完毕");
    }

    /**
     * 运转
     *
     * @param oracleCDCConnect
     */
    public void running(OracleCDCConnection oracleCDCConnect) {
        log.info("One Round Start， currentSinkPosition:{}, startSCN:{}, endSCN:{}", this.currentSinkPosition, this.startScn, this.endScn);
        // 设置任務持续运转状态下的偏移量~
        this.endScn = oracleCDCConnect.getEndScn();
        //错峰读取数据
        String format = "HH:mm:ss";
        //获取当前时间
        try{
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            String format1 = simpleDateFormat.format(new Date());
            Date nowDate = simpleDateFormat.parse(format1);
            Date date = new Date();
            SimpleDateFormat df = new SimpleDateFormat("HH");
            String str = df.format(date);
            int a = Integer.parseInt(str);
            if (a >= 0&&a<12 ) {
                //凌晨
                //范围开始时间
                Date startTimeDateAfter = new SimpleDateFormat(format).parse(startTimeAfter);
                //范围结束时间
                Date endTimeDateAfter = new SimpleDateFormat(format).parse(endTimeAfter);
                boolean inAfter = DateUtil.isIn(nowDate, startTimeDateAfter, endTimeDateAfter);
                if(inAfter){
                    lastScnScope=scnScopeMin;
                }else{
                    lastScnScope =scnScope;
                }
            }else{
                //范围开始时间
                Date startTimeDate = new SimpleDateFormat(format).parse(startTime);
                //范围结束时间
                Date endTimeDate = new SimpleDateFormat(format).parse(endTime);
                boolean in = DateUtil.isIn(nowDate, startTimeDate, endTimeDate);
                if(in){
                    lastScnScope=scnScopeMin;
                }else{
                    lastScnScope =scnScope;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        log.info("One Round， scn范围:{}", lastScnScope);
        payLoad(oracleCDCConnect);

        log.info("One Round End， currentSinkPosition:{}, startSCN:{}, endSCN:{}", this.currentSinkPosition, this.startScn, this.endScn);
    }

    /**
     * 多线程提交读取信息的任务
     *
     * @param connection
     * @param sql
     */
    public void loadData(OracleCDCConnection connection, String sql) {
        connectionExecutor.submit(() -> {
            //connection.queryData(sql);
        });
    }

    /**
     * 初始化connection的初始化加载 logminer
     * 此阶段主要负责 确定好实际开始的scn 与 结束的 scn 并且把此信息同步到任务层面。
     * （此方案后续如果需要断点恢复任务需要持久化到mysql，目前人工处理从 #start 方法进入的时候写死一个scn的值）
     *
     * @param connecUtil
     */
    public void payLoad(OracleCDCConnection connecUtil) {
        BigInteger currentMaxScn = null;

        Connection connection = connecUtil.getConnection();

        // 获取当前数据库的最大偏移量
        currentMaxScn = connecUtil.getCurrentScn(connection);

        log.info("payLoad CurrentMaxScn: {}, startSCN:{}, endSCN:{}", currentMaxScn, startScn, endScn);

        // 最大值 减掉 结束SCN 大于 步进  降低DB压力的一种方式
        if (Objects.isNull(connection) || currentMaxScn.subtract(this.endScn).compareTo(logminerStep) > 0) {

            // 按照加载日志文件大小限制，根据endScn作为起点找到对应的一组加载范围
            BigInteger currentStartScn = Objects.nonNull(this.endScn) ? this.endScn : startScn;

            //断点续传：
            //     需要记录当前的开始scn  并且记录消费的scn的值 为了解决当开始scn小于消费scn时 ，取开始scn会出现写入重复的情况
            //     此处如果startscn> currentStartScn 说明此区间内没有数据情况；startscn< currentStartScn 说明出现数据情况，此处要注意避免数据重复
            //writeTxtFG("断点续传记录SCN，开始SCN="+currentStartScn+",消费SCN="+currentSinkPosition+"Identification = 1"+"rs_id="+rs_id,"D://cdc/mysqlRecord.txt");

            // 如果加载了redo日志，则起点不能是上一次记载的日志的结束位点，而是上次消费的最后一条数据的位点
            if (loadRedo) {
                //writeTxt("进入实时日志查询时currentSinkPosition="+currentSinkPosition,"D://cdc/dd.txt");
                // 需要加1  因为logminer查找数据是左闭右开，如果不加1  会导致最后一条数据重新消费
                currentStartScn = currentSinkPosition.add(BigInteger.ONE);
            }
            //定义结束endScn
            Pair<BigInteger, Boolean> endScn = null;
            try {
                //获取结束SCN
                endScn = connecUtil.getEndScn(connection, currentStartScn, new ArrayList<>(32));
            } catch (SQLException e) {
                //获取结束的SCN失败时
                log.error(e.getMessage(), e);
            }
            if (endScn != null) {
                // 更新当前位点
                this.endScn = endScn.getLeft();
                this.loadRedo = endScn.getRight();
                //计算scn 间隔，scn过大 会增大查询时间，
                if(BigInteger.ZERO.compareTo(currentStartScn)==0){
                    currentStartScn = connecUtil.getLogFileExecute().getFirstChange();
                }
                BigInteger subtract = this.endScn.subtract(currentStartScn);
                //计算余数
                BigInteger remainder = this.endScn.remainder(BigInteger.valueOf(lastScnScope));
                //每次查询10w的数据量 循环次数
                BigInteger divide = subtract.divide(BigInteger.valueOf(lastScnScope));
                //循环计算startlogminer 的开始scn 和结束scn
                for (int i = 0; i <divide.intValue() ; i++) {
                    //开始scn
                    BigInteger startScn = currentStartScn.add(BigInteger.valueOf(lastScnScope).multiply(BigInteger.valueOf(i)));
                    //结束scn
                    BigInteger endScnLast = currentStartScn.add(BigInteger.valueOf(lastScnScope).multiply(BigInteger.valueOf(i + 1)));
                    log.info("单个日志中的scn： 开始scn{},结束scn{}",startScn,endScnLast);
                    //执行程序
                    executeSQL(connecUtil,startScn,endScnLast);
                }
                //如果是0 被整除了
                if(BigInteger.ZERO.compareTo(remainder)!=0) {
                    //计算
                    BigInteger startScn = currentStartScn.add(BigInteger.valueOf(lastScnScope).multiply(divide));
                    log.info("单个日志中的scn： 开始scn{},结束scn{}", startScn, this.endScn);
                    //执行程序
                    executeSQL(connecUtil, startScn, this.endScn);
                }

                //writeTxt("-----------------------------------------------------------------------------------------------------","D://cdc/dd.txt");
                // if (Objects.isNull(currentConnection)) {
                //     updateCurrentConnection(logMinerConnection);
                // }
                // 如果已经加载了redoLog就不需要多线程加载了
                //if (endScn.getRight()) {
                    // break
                //}
            }
        }
/* 多线程处理 屏蔽，目前用单线程执行。
        for (LogMinerConnection logMinerConnection : needLoadList) {
            logMinerConnection.checkAndResetConnection();
            if (Objects.isNull(currentMaxScn)) {
                currentMaxScn = logMinerConnection.getCurrentScn();
            }
            // currentReadEndScn为空（第一次加载 保证初始化时至少有一个线程加载日志文件）或者
            // 当前加载的日志范围比数据库最大SCN差距超过3000则再起一个connection进行加载
            if (Objects.isNull(currentConnection)
                    || currentMaxScn.subtract(this.endScn).compareTo(step) > 0) {

                // 按照加载日志文件大小限制，根据endScn作为起点找到对应的一组加载范围
                BigInteger currentStartScn = Objects.nonNull(this.endScn) ? this.endScn : startScn;

                // 如果加载了redo日志，则起点不能是上一次记载的日志的结束位点，而是上次消费的最后一条数据的位点
                if (loadRedo) {
                    // 需要加1  因为logminer查找数据是左闭右开，如果不加1  会导致最后一条数据重新消费
                    currentStartScn = currentSinkPosition.add(BigInteger.ONE);
                }

                Pair<BigInteger, Boolean> endScn =
                        logMinerConnection.getEndScn(currentStartScn, new ArrayList<>(32));
                logMinerConnection.startOrUpdateLogMiner(currentStartScn, endScn.getLeft());
                // 读取v$logmnr_contents 数据由线程池加载
                loadData(logMinerConnection, logMinerSelectSql);
                this.endScn = endScn.getLeft();
                this.loadRedo = endScn.getRight();
                if (Objects.isNull(currentConnection)) {
                    updateCurrentConnection(logMinerConnection);
                }
                // 如果已经加载了redoLog就不需要多线程加载了
                if (endScn.getRight()) {
                    break;
                }
            } else {
                break;
            }
        }*/
    }
    public void executeSQL(OracleCDCConnection connecUtil,BigInteger currentStartScn,BigInteger endeScn){
        // 开始日志挖掘
        connecUtil.startOrUpdateLogMiner(connecUtil, currentStartScn, endeScn, false,this.currentSinkPosition);
        //writeTxt("开始的scn:"+currentStartScn+"--结束scn:"+endScn.getLeft(),"D://cdc/dd.txt");

        // 读取v$logmnr_contents 数据由线程池加载
        String logMinerSelectSql = ""; // 这个地方需要根据指定的表名
        // 动态组装SQL，摄取update insert delete
        logMinerSelectSql = connecUtil.buildSelectSql("UPDATE,INSERT,DELETE", StringUtils.join(oracleCDCConfig.getTable().toArray(), ","), false);
        log.info("执行完拼接查询sql");
        // 请求日志挖掘结果logmnr_contents查看。 结果会放到 connection的logminerData中 (ResultSet)。 TODO：此操作响应快慢与数据量反比，需要用多线程异步获取结果。
        boolean selectResult = connecUtil.queryData(logMinerSelectSql,currentStartScn,this.currentSinkPosition);
        log.info("执行完日志视图");
        if (selectResult) {
            // jdbc请求成功
            try {
                // 解析
                if (connecUtil.hasNext()) {
                    // 获取解析Logminer结果
                    List<QueueData> result = connecUtil.getResult();
                    if(null!=result&&result.size()>0){
                        log.info("开始推送数据");
                        for (int i = 0; i <result.size() ; i++) {
                            QueueData queueData = result.get(i);

                            //发打印输出 此处该写入kafka,
                            //写入kafka时，写入成功后 出错，此时记录的scn是上一次的scn ，再次启动项目时候，会重复写入当前条
                            String querydata = JSON.toJSONString(LogminerHandler.parse(queueData, logminerConverter));
                            producer.send(new ProducerRecord<>(KafkaTopicName, querydata));
                            //log.info("写入memcache成功："+queueData);
                            this.currentSinkPosition = queueData.getScn();
                            this.rs_id = queueData.getRsId();
                            //写入memcached
                            Boolean aBoolean = MemcachedUtil.setValue(currentSinkPosition + "" + rs_id, 0, querydata+"||||"+ new Date().getTime());
                            //log.info("写入memcache成功："+aBoolean);
                           // writeTxt("内容："+queueData,"D://cdc/dd.txt");

                            //writeTxt("消费的CSN 记录下每次有数据的scn："+queueData+"rsid="+rs_id,"D://cdc/dd.txt");

                        }
                    }

                            /*if (result != null) {
                                // 格式化数据 after before，并推送到kafka
                                //producer.send(new ProducerRecord<>("test_oracle_cdc_custom", JSON.toJSONString(LogminerHandler.parse(result, logminerConverter))));
                                writeTxt(result+"");
                                this.currentSinkPosition = result.getScn();
                                // System.out.println(JSON.toJSONString(LogminerHandler.parse(result, logminerConverter)));
                                // TODO: 推送到Kafka，这里需要做Kafka推送记录了~
                            } */
                }

            } catch (Exception e) {
                //如果出错此处记录
                //writeTxtFG("断点续传记录SCN，开始SCN="+currentStartScn+",消费SCN="+currentSinkPosition+"Identification = 4,"+"rs_id="+rs_id,"D://cdc/mysqlRecord.txt");
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }

    /***
     *  测试 将内容写入到文件
     */
    private void writeTxt(String datastr,String diststr){
        FileWriter fw = null;
        try {
            //如果文件存在，则追加内容；如果文件不存在，则创建文件
            File f = new File(diststr);
            fw = new FileWriter(f, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw);
        pw.println(datastr);
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /***
     *  测试 将内容写入到文件
     */
    private void writeTxtFG(String datastr,String diststr){
        FileWriter fw = null;
        try {
            //如果文件存在，则追加内容；如果文件不存在，则创建文件
            File f = new File(diststr);

            fw = new FileWriter(f);
        } catch (IOException e) {
            e.printStackTrace();
        }
        PrintWriter pw = new PrintWriter(fw,false);
        pw.write(datastr);
        pw.flush();
        try {
            fw.flush();
            pw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static BigInteger a;

    public static void main(String[] args) {
        /*BigInteger bigInteger = new BigInteger("1");
        System.out.println(bigInteger.subtract(a));
        System.out.println(a);*/
        // new OracleCDCConfig();
        /***
         * 测试说明1. startscn 》消费scn时  再起启动时候的入参为starscn
         *         2.startscn < 消费scn时  断点续传的scn为 消费scn+1 (其中会出现相同事务提交的数据，出现异常时候的bug)
         */
        Props props = PropertiesUtil.getProps();
        String host = props.getStr("cdc.oracle.hostname");
        String port = props.getStr("cdc.oracle.port");
        String tableArray = props.getStr("cdc.oracle.table.list");
        String user = props.getStr("cdc.oracle.username");
        String pwd = props.getStr("cdc.oracle.password");
        String oracleServer = props.getStr("cdc.oracle.database");
        KafkaTopicName = props.getStr("kafka.topic");
        schema = props.getStr("cdc.oracle.schema.list");
        String scnstr = props.getStr("cdc.scnscope");
        String scnstrmin = props.getStr("cdc.scnscopemin");
        List<String> sourceTableList = null;
        startTime = props.getStr("starttime");
        endTime = props.getStr("endtime");
        startTimeAfter = props.getStr("startimeafter");
        endTimeAfter = props.getStr("endtimeafter");
        scnScope = Integer.valueOf(scnstr);
        scnScopeMin = Integer.valueOf(scnstrmin);
        lastScnScope = scnScope;
        try {
            sourceTableList = getSourceTableList();
            //sourceTableList = new ArrayList<>();
            //sourceTableList.add("TDS_LJ.SPTB02");
        }catch (Exception e){
            e.printStackTrace();
        }
        OracleCDCConfig build = OracleCDCConfig.builder().startSCN("0").endSCN("0")
                .readPosition("ALL")
                .driverClass("oracle.jdbc.OracleDriver")
                .table(sourceTableList)
                .username(user)
                .password(pwd).jdbcUrl("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(LOAD_BALANCE=OFF)(FAILOVER=OFF)(ADDRESS=(PROTOCOL=tcp)(HOST=" +
                        host + ")(PORT=" +
                        port + ")))(CONNECT_DATA=(SID=" +
                        oracleServer + ")))").build();
        new OracleSource().start(build);
    }

    /**
     * 初始化 线程池 保证任务的后台运行
     * 1. 测试连接
     * 2. 测试权限
     * 3. 测试用户权限
     */
    public OracleCDCConnection init(OracleCDCConfig config) {
        this.oracleCDCConfig = config;
        // 单任务哈 后续如果中台建设完毕 可以考虑动态新增任务的时候才这样处理。
        this.executor = Executors.newSingleThreadExecutor(threadFactory);
        OracleCDCConnection oracleCDCConnecUtil = new OracleCDCConnection(config, transactionManager);

        if (!oracleCDCConnecUtil.getConnection(oracleCDCConfig)) {
            log.error("初始化oracle 连接失败");
        }

        // TODO: 判定当前的模式 查看核心参数是否存在

        return oracleCDCConnecUtil;
    }

    /**
     * 获取起始SCN
     */
    public BigInteger getStartSCN(OracleCDCConnection oracleCDCConnecUtil, BigInteger startScn) {
        Connection connection = oracleCDCConnecUtil.getConnection();

        // 如果从保存点模式开始 并且不是0 证明保存点是ok的
        if (startScn != null && startScn.compareTo(BigInteger.ZERO) != 0) {
            return startScn;
        }

        // 恢复位置为0，则根据配置项进行处理
        if (SCNReadType.ALL.name().equalsIgnoreCase(oracleCDCConfig.getReadPosition())) {
            // 获取最开始的scn
            startScn = oracleCDCConnecUtil.getMinScn(connection);
        } else if (SCNReadType.CURRENT.name().equalsIgnoreCase(oracleCDCConfig.getReadPosition())) {
            startScn = oracleCDCConnecUtil.getCurrentScn(connection);
        } else if (SCNReadType.TIME.name().equalsIgnoreCase(oracleCDCConfig.getReadPosition())) {
            // 根据指定的时间获取对应时间段的日志文件的起始位置
            if (oracleCDCConfig.getStartTime() == 0) {
                throw new IllegalArgumentException("[startTime] must not be null or empty when readMode is [time]");
            }
            startScn = oracleCDCConnecUtil.getLogFileStartPositionByTime(connection, oracleCDCConfig.getStartTime());
        } else if (SCNReadType.SCN.name().equalsIgnoreCase(oracleCDCConfig.getReadPosition())) {
            // 根据指定的scn获取对应日志文件的起始位置
            if (StringUtils.isEmpty(oracleCDCConfig.getStartSCN())) {
                throw new IllegalArgumentException("[startSCN] must not be null or empty when readMode is [scn]");
            }
            startScn = new BigInteger(oracleCDCConfig.getStartSCN());
        } else {
            throw new IllegalArgumentException(
                    "unsupported readMode : " + oracleCDCConfig.getReadPosition());
        }
        return startScn;
    }

    /**
     * 获取结束SCN
     */
    public void getEndSCN() {

    }

    public void readLogInfo() {

    }

    /****
     *  断点续传重新启动
     * @param oracleCDCConfig
     */
    public void reStart(OracleCDCConfig oracleCDCConfig) {
        Props props = PropertiesUtil.getProps();
        KafkaTopicName = props.getStr("kafka.topic");

        // 初始化连接池
        connectionExecutor = new ThreadPoolExecutor(
                20, 20, 300, TimeUnit.SECONDS, new LinkedBlockingDeque<Runnable>(Integer.MAX_VALUE));
        // 初始化日志缓存器， 底层是google的 lru缓存架构
        transactionManager = new TransactionManager(1000L, 20);

        this.oracleCDCConfig = oracleCDCConfig;
        this.startScn = new BigInteger(oracleCDCConfig.getStartSCN());
        this.endScn = new BigInteger(oracleCDCConfig.getEndSCN());
        this.logminerStep = new BigInteger("2000");
        //获取异常标识
        int identification = oracleCDCConfig.getIdentification();
        //获取异常处理的最后一条消费数据的id
        String rs_id = oracleCDCConfig.getRs_id();
        // 初始化连接
        OracleCDCConnection oracleCDCConnect = init(oracleCDCConfig);
        //设置异常标识
        oracleCDCConnect.setIdentification(identification);
        oracleCDCConnect.setRs_id(rs_id);
        // 获取开始偏移量 根据模式判定
        startScn = getStartSCN(oracleCDCConnect, startScn);
        // 读取的位置 从startSCN偏移量开始
        this.currentSinkPosition = startScn;

        // 给转换器设置一个Connection，查询metaData信息。
        String jdbcUrl = oracleCDCConfig.getJdbcUrl();
        String username = oracleCDCConfig.getUsername();
        String password = oracleCDCConfig.getPassword();
        // String driverClass = oracleCDCConfig.getDriverClass();
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(
                    jdbcUrl,
                    username,
                    password);
            logminerConverter.setConnection(connection);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }

        // 初始化Logminer
        payLoad(oracleCDCConnect);
        // 如果当前的id = 4 ;需要将此数据修改为0
        oracleCDCConnect.setIdentification(0);
        // 一直沿用这个流程去处理
        while (runningFlag) {
            running(oracleCDCConnect);
        }
    }
    /**
     * 获取当前上下文环境下 数仓中的分流到clickhouse的表名，用以MySqlCDC抽取。
     * @return
     * @throws Exception
     */
    public static List<String> getSourceTableList() throws Exception {
        Props props = PropertiesUtil.getProps();
        List<Map<String, Object>> sourceTableList = DbUtil.executeQuery("select distinct(source_table) as source_table from table_process where  level_name='ods'" );
        List<String> sourceTable = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(sourceTableList)) {
            // sourceTableList
            for (Map<String, Object> sourceTableKV : sourceTableList) {
                String sourceTableName = GetterUtil.getString(sourceTableKV.get("source_table"));
                sourceTable.add(props.getStr("cdc.oracle.schema.list") + "." + sourceTableName.toUpperCase());
            }
        }
        return sourceTable;
    }
}