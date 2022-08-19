package org.datamiddle.cdc.oracle;

import lombok.extern.slf4j.Slf4j;

/**
 * @Description: 用于异步解析OracleConnection拿到的数据，因为持久化动作要放在这里，避免服务kill -9 导致的数据不一致丢失问题。
 * @Author: WangYouzheng
 * @Date: 2022/8/8 15:46
 * @Version: V1.0
 */
@Slf4j
public class OracleCDCResolve {


}