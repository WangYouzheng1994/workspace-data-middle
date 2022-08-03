package org.test.cdc.oracle;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigInteger;
import java.util.concurrent.ExecutorService;

/**
 * @Description: Oracle Logminer CDC
 * @Author: WangYouzheng
 * @Date: 2022/8/3 13:08
 * @Version: V1.0
 */
@Slf4j
public class OracleSource {
    private ExecutorService executor;
    private ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("debezium-engine").build();
    private OracleCDCConfig oracleCDCConfig;

    /**
     * 概要设计：
     * 1. 建立连接测试
     * 2. 读取日志 获取每个日志的范围
     * 3. 过滤指定的表
     * 4. 循环取值 幂等过滤 把信息 推给传入的handler
     */

    public void start() {

    }

    /**
     * 初始化 线程池 保证任务的后台运行
     * 1. 测试连接
     * 2. 测试权限
     * 3. 测试用户权限
     */
    public void init(OracleCDCConfig config) {
        this.oracleCDCConfig = config;
        // 单任务哈 后续如果中台建设完毕 可以考虑动态新增任务的时候才这样处理。
        this.executor = Executors.newSingleThreadExecutor(threadFactory);
        OracleCDCConnecUtil oracleCDCConnecUtil = new OracleCDCConnecUtil(config);

        if (!oracleCDCConnecUtil.getConnection()) {
            log.error("初始化oracle 连接失败");
        }
    }

    /**
     * 获取起始SCN
     */
    public void getStartSCN(BigInteger startScn) {
        // 如果从保存点模式开始 并且不是0 证明保存点是ok的
        if (startScn != null && startScn.compareTo(BigInteger.ZERO) != 0) {
            return startScn;
        }

        // 恢复位置为0，则根据配置项进行处理
        if (SCNReadType.ALL.name().equalsIgnoreCase(oracleCDCConfig.getReadPosition())) {
            // 获取最开始的scn
            startScn = getMinScn();
        } else if (SCNReadType.CURRENT.name().equalsIgnoreCase(oracleCDCConfig.getReadPosition())) {
            startScn = OracleDBUtil.getCurrentScn();
        } else if (SCNReadType.TIME.name().equalsIgnoreCase(oracleCDCConfig.getReadPosition())) {
            // 根据指定的时间获取对应时间段的日志文件的起始位置
            if (oracleCDCConfig.getStartTime() == 0) {
                throw new IllegalArgumentException(
                        "[startTime] must not be null or empty when readMode is [time]");
            }
            startScn = OracleDBUtil.getLogFileStartPositionByTime(oracleCDCConfig.getStartTime());
        } else if (SCNReadType.SCN.name().equalsIgnoreCase(oracleCDCConfig.getReadPosition())) {
            // 根据指定的scn获取对应日志文件的起始位置
            if (StringUtils.isEmpty(logMinerConfig.getStartScn())) {
                throw new IllegalArgumentException(
                        "[startSCN] must not be null or empty when readMode is [scn]");
            }

            startScn = new BigInteger(oracleCDCConfig.getStartSCN());
        } else {
            throw new IllegalArgumentException(
                    "unsupported readMode : " + logMinerConfig.getReadPosition());
        }
    }

    /**
     * 获取结束SCN
     */
    public void getEndSCN() {

    }
}