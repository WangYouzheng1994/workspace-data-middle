package org.test.cdc.oracle;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.test.cdc.oracle.constants.OracleCDCConnecUtil;

import java.math.BigInteger;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

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
     * 概要设计：
     * 1. 建立连接测试
     * 2. 读取日志 获取每个日志的范围
     * 3. 过滤指定的表
     * 4. 循环取值 幂等过滤 把信息 推给传入的handler
     */

    public void start(OracleCDCConfig oracleCDCConfig) {
        // 初始化连接
        OracleCDCConnecUtil oracleCDCConnecUtil = init(oracleCDCConfig);
        // 获取开始偏移量 根据模式判定
        startScn = getStartSCN(oracleCDCConnecUtil, new BigInteger("0"));

        // 初始化Logminer

        // 读取Log日志
    }

    /**
     * 初始化connection的初始化加载 logminer
     * 此阶段主要负责 确定好实际开始的scn 与 结束的 scn 并且把此信息同步到任务层面。
     * （此方案后续如果需要断点恢复任务需要持久化到mysql，目前人工处理从 #start 方法进入的时候写死一个scn的值）
     *
     * @param connecUtil
     */
    public void payLoad(OracleCDCConnecUtil connecUtil) {

/*
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
        BigInteger bigInteger = new BigInteger("1");
        System.out.println(bigInteger.subtract(a));
        System.out.println(a);
    }

    /**
     * 初始化 线程池 保证任务的后台运行
     * 1. 测试连接
     * 2. 测试权限
     * 3. 测试用户权限
     */
    public OracleCDCConnecUtil init(OracleCDCConfig config) {
        this.oracleCDCConfig = config;
        // 单任务哈 后续如果中台建设完毕 可以考虑动态新增任务的时候才这样处理。
        this.executor = Executors.newSingleThreadExecutor(threadFactory);
        OracleCDCConnecUtil oracleCDCConnecUtil = new OracleCDCConnecUtil(config);

        if (!oracleCDCConnecUtil.getConnection(oracleCDCConfig)) {
            log.error("初始化oracle 连接失败");
        }

        // TODO: 判定当前的模式 查看核心参数是否存在

        return oracleCDCConnecUtil;
    }

    /**
     * 获取起始SCN
     */
    public BigInteger getStartSCN(OracleCDCConnecUtil oracleCDCConnecUtil, BigInteger startScn) {
        Connection connection = oracleCDCConnecUtil.getConnection();

        // 如果从保存点模式开始 并且不是0 证明保存点是ok的
        if (startScn != null && startScn.compareTo(BigInteger.ZERO) != 0) {
            return startScn;
        }

        // 恢复位置为0，则根据配置项进行处理
        if (SCNReadType.ALL.name().equalsIgnoreCase(oracleCDCConfig.getReadPosition())) {
            // 获取最开始的scn
            startScn = OracleDBUtil.getMinScn(connection);
        } else if (SCNReadType.CURRENT.name().equalsIgnoreCase(oracleCDCConfig.getReadPosition())) {
            startScn = OracleDBUtil.getCurrentScn(connection);
        } else if (SCNReadType.TIME.name().equalsIgnoreCase(oracleCDCConfig.getReadPosition())) {
            // 根据指定的时间获取对应时间段的日志文件的起始位置
            if (oracleCDCConfig.getStartTime() == 0) {
                throw new IllegalArgumentException("[startTime] must not be null or empty when readMode is [time]");
            }
            startScn = OracleDBUtil.getLogFileStartPositionByTime(connection, oracleCDCConfig.getStartTime());
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