package org.datamiddle.cdc.oracle;

import com.alibaba.fastjson2.JSON;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.datamiddle.cdc.oracle.bean.QueueData;
import org.datamiddle.cdc.oracle.bean.TransactionManager;
import org.datamiddle.cdc.oracle.converter.oracle.LogminerConverter;
import org.datamiddle.cdc.util.KafkaProduceUtil;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @Description: Oracle Logminer CDC  一个CDC的任务
 * @Author: WangYouzheng
 * @Date: 2022/8/3 13:08
 * @Version: V1.0
 */
@Slf4j
public class OracleSource {
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
     * logminer 步进
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
        this.startScn = new BigInteger("0");
        this.endScn = new BigInteger("0");
        this.logminerStep = new BigInteger("2000");
        // 初始化连接
        OracleCDCConnection oracleCDCConnect = init(oracleCDCConfig);
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
        // 读取Log日志

        // 一直沿用这个流程去处理
        while (runningFlag) {
            running(oracleCDCConnect);
        }
    }

    /**
     * 运转
     *
     * @param oracleCDCConnect
     */
    public void running(OracleCDCConnection oracleCDCConnect) {
        log.info("Running Current startSCN:{}, endSCN:{}", this.startScn, this.endScn);
        // 设置任務持续运转状态下的偏移量~
        this.endScn = oracleCDCConnect.getEndScn();
        payLoad(oracleCDCConnect);

        log.info("Running Current startSCN:{}, endSCN:{}", this.startScn, this.endScn);
    }

    /**
     * 多线程提交读取信息的任务
     *
     * @param connection
     * @param sql
     */
    public void loadData(OracleCDCConnection connection, String sql) {
        connectionExecutor.submit(() -> {
            connection.queryData(sql);
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

        currentMaxScn = connecUtil.getCurrentScn(connection);

        if (Objects.isNull(connection) || currentMaxScn.subtract(this.endScn).compareTo(logminerStep) > 0) {

            // 按照加载日志文件大小限制，根据endScn作为起点找到对应的一组加载范围
            BigInteger currentStartScn = Objects.nonNull(this.endScn) ? this.endScn : startScn;

            // 如果加载了redo日志，则起点不能是上一次记载的日志的结束位点，而是上次消费的最后一条数据的位点
            if (loadRedo) {
                // 需要加1  因为logminer查找数据是左闭右开，如果不加1  会导致最后一条数据重新消费
                currentStartScn = currentSinkPosition.add(BigInteger.ONE);
            }

            Pair<BigInteger, Boolean> endScn = null;
            try {
                endScn = connecUtil.getEndScn(connection, currentStartScn, new ArrayList<>(32));
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
                // e.printStackTrace();
            }
            // 开始日志挖掘
            connecUtil.startOrUpdateLogMiner(connecUtil, currentStartScn, endScn.getLeft(), false);
            // 读取v$logmnr_contents 数据由线程池加载
            String logMinerSelectSql = ""; // 这个地方需要根据指定的表名
            // 动态组装SQL，摄取update insert delete
            logMinerSelectSql = connecUtil.buildSelectSql("UPDATE,INSERT,DELETE", StringUtils.join(oracleCDCConfig.getTable().toArray(), ","), false);

            // 请求日志挖掘结果logmnr_contents查看。 结果会放到 connection的logminerData中 (ResultSet)。 TODO：此操作响应快慢与数据量反比，需要用多线程异步获取结果。
            boolean selectResult = connecUtil.queryData(logMinerSelectSql);
            if (selectResult) {
                // jdbc请求成功
                try {
                    // 解析
                    if (connecUtil.hasNext()) {
                        // 获取解析结果
                        QueueData result = connecUtil.getResult();
                        // 格式化数据 after before
                        if (result != null) {
                            producer.send(new ProducerRecord<>("test_oracle_cdc_custom", JSON.toJSONString(LogminerHandler.parse(result, logminerConverter))));
                            // System.out.println(JSON.toJSONString(LogminerHandler.parse(result, logminerConverter)));
                            // TODO: 推送到Kafka，这里需要做Kafka推送记录了~
                        }
                    }
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }

                // 格式化数据
                //logminerHandler.parse(result, );

                // TODO: 格式化完了以后，更新任务的开始和结束
            }

            this.endScn = endScn.getLeft();
            this.loadRedo = endScn.getRight();
            // if (Objects.isNull(currentConnection)) {
            //     updateCurrentConnection(logMinerConnection);
            // }
            // 如果已经加载了redoLog就不需要多线程加载了
            if (endScn.getRight()) {
                // break
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

    private static BigInteger a;

    public static void main(String[] args) {
        /*BigInteger bigInteger = new BigInteger("1");
        System.out.println(bigInteger.subtract(a));
        System.out.println(a);*/
        // new OracleCDCConfig();
        OracleCDCConfig build = OracleCDCConfig.builder()
                .readPosition("ALL")
                .driverClass("oracle.jdbc.OracleDriver")
                .table(Arrays.asList("TDS_LJ.TEST_SCN"))
                .username("flinkuser")
                .password("flinkpw").jdbcUrl("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(LOAD_BALANCE=OFF)(FAILOVER=OFF)(ADDRESS=(PROTOCOL=tcp)(HOST=" +
                        "192.168.3.95" + ")(PORT=" +
                        "1521" + ")))(CONNECT_DATA=(SID=" +
                        "ORCL" + ")))").build();
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
}