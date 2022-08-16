package org.jeecg.yqwl.datamiddle.job.common.excel;



import org.jeecg.yqwl.datamiddle.util.custom.GetterUtil;

import java.util.ArrayList;

import static org.jeecg.yqwl.datamiddle.job.common.excel.ExcelReadUtil.getMessage;


/**
 * @author WangYouzheng
 * @version 1.0
 * @since: 2020/2/12 12:42
 * @Description:
 */
public class ExcelErrorList<E> extends ArrayList<E> {
    public ExcelErrorList() {
        super();
    }

    public void add(E e, String... str) {
        super.add((E) getMessage(GetterUtil.getString(e), str));
    }
}
