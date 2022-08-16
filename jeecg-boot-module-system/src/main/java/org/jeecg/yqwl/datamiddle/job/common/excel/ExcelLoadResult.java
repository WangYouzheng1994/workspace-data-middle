package org.jeecg.yqwl.datamiddle.job.common.excel;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @Author: WangYouzheng
 * @Date: 2021/7/9 17:13
 * @Description: 表格读取
 */
@Data
public class ExcelLoadResult<T> {
	private List<T> dataList;
	private Map<String, String> paramsMap;

	// ....精彩未完待续~
}
