package org.jeecg.yqwl.datamiddle.job.common.excel.exception;

/**
 * @author WangYouzheng
 * @version 1.0
 * @since: 2020/2/12 16:38
 * @Description: Excel读取异常 (poi层面)
 */
public class ExcelReadException extends RuntimeException {
    public ExcelReadException() {
        super();
    }

    public ExcelReadException(String msg) {
        super(msg);
    }
}
