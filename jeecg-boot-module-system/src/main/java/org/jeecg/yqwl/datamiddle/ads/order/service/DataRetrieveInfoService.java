package org.jeecg.yqwl.datamiddle.ads.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DataRetrieveInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsDocs;
import org.jeecg.yqwl.datamiddle.ads.order.vo.DataRetrieveQuery;

import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【data_retrieve_info(每日检索数据信息表
)】的数据库操作Service
* @createDate 2022-08-29 13:53:06
*/
public interface DataRetrieveInfoService extends IService<DataRetrieveInfo> {



    /**
     * 保存数据检索的结果
     * @param dataRetrieveInfo 检索信息主表
     * @param vinMap 本库异常信息数据的vin码
     * @param vinMapFromOracle 源库异常信息数据的vin码
     * @author dabao
     * @date 2022/8/29
     */
    void saveInfo(DataRetrieveInfo dataRetrieveInfo, Map<Integer, List<String>> vinMap, Map<Integer, List<String>> vinMapFromOracle);

    /**
     * 分页查询
     * @param query 参数
     * @author dabao
     * @date 2022/8/30
     * @return {@link Page<DataRetrieveInfo>}
     */
    Page<DataRetrieveInfo> selectDataRetrieveInfoPage(DataRetrieveQuery query);


    /**
     *
     * @param query 参数
     * @author dabao
     * @date 2022/8/30
     * @return {@link Page<DwmVlmsDocs>}
     */
    Page<DwmVlmsDocs> selectDataRetrieveDetail(DataRetrieveQuery query);
}
