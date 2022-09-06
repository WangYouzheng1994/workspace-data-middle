package org.jeecg.yqwl.datamiddle.ads.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DataRetrieveDetail;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsDocs;
import org.jeecg.yqwl.datamiddle.ads.order.vo.DataRetrieveQuery;

import java.util.List;

/**
* @author Administrator
* @description 针对表【data_retrieve_detail】的数据库操作Service
* @createDate 2022-08-29 13:53:06
*/
public interface DataRetrieveDetailService extends IService<DataRetrieveDetail> {

    Integer selectCountByCount(DataRetrieveQuery query);

    List<DwmVlmsDocs> selectDocsList(DataRetrieveQuery query);
}
