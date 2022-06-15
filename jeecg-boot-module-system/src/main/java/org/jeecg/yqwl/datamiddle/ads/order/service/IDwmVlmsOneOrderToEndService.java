package org.jeecg.yqwl.datamiddle.ads.order.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsOneOrderToEnd;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;
import org.jeecg.yqwl.datamiddle.ads.order.vo.SelectData;

import java.util.List;


/**
 * @Description: 一单到底
 * @Author: jeecg-boot
 * @Date:   2022-06-06
 * @Version: V1.0
 */
@DS("slave")
public interface IDwmVlmsOneOrderToEndService extends IService<DwmVlmsOneOrderToEnd> {
    Integer countOneOrderToEndList(GetQueryCriteria queryCriteria);

    /**
     * 按条件分页查询
     * @param queryCriteria
     * @return
     */
    List<DwmVlmsOneOrderToEnd> selectOneOrderToEndList(GetQueryCriteria queryCriteria);


    /**
     * 同板数量
     * @return
     */
    List<SelectData> selectTotal(String stowageNoteNo);
}
