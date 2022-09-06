package org.jeecg.yqwl.datamiddle.ads.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsDocs;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;

import java.util.List;

/**
 * 在MySQL中操作数据
 * @author dabao
 * @date 2022/8/29
 */
public interface IMysqlDwmVlmsSptb02Service extends IService<DwmVlmsDocs> {



    /**
     * 获取近30日的DOCS数据
     * @param queryCriteria 查询参数
     * @author dabao
     * @date 2022/8/29
     * @return {@link List< DwmVlmsDocs>}
     */
    List<DwmVlmsDocs> selectDocsCcxdlList(GetQueryCriteria queryCriteria);

}
