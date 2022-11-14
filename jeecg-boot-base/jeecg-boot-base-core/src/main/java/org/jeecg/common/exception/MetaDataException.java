package org.jeecg.common.exception;

/**
 * 元数据错误异常
 * @Author: XiaoKai
 * @Date: 2022-011-11
 * @Version: V2.0
 */
public class MetaDataException extends RuntimeException {

    public MetaDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public MetaDataException(String message) {
        super(message);
    }
}