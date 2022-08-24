package org.jeecg.yqwl.datamiddle.ads.order.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsDocs;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsOneOrderToEnd;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;

import java.util.List;


/**
 * @Description: 一单到底
 * @Author: jeecg-boot
 * @Date:   2022-06-06
 * @Version: V1.0
 */
@DS("slave")
public interface IDwmVlmsOneOrderToEndService extends IService<DwmVlmsOneOrderToEnd> {
    /**
     * 一单到底 count计数
     *
     * @param queryCriteria
     * @return
     */
    Integer countOneOrderToEndList(GetQueryCriteria queryCriteria);

    /**
     * 按条件分页查询
     * @param queryCriteria
     * @return
     */
    List<DwmVlmsOneOrderToEnd> selectOneOrderToEndList(GetQueryCriteria queryCriteria);

    /**
     * DOCS count计数
     *
     * @param queryCriteria
     * @return
     */
    Integer countDocsList(GetQueryCriteria queryCriteria);

    /**
     * DOCS 列表页查询
     *
     * @param queryCriteria
     * @return
     */
    List<DwmVlmsDocs> selectDocsList(GetQueryCriteria queryCriteria);



    /**
     * DOCSCcxdl count计数
     *
     * @param queryCriteria
     * @return
     */
    Integer countDocsCcxdlList(GetQueryCriteria queryCriteria);

    /**
     * DOCSCcxdl 列表页查询
     *
     * @param queryCriteria
     * @return
     */
    List<DwmVlmsDocs> selectDocsCcxdlList(GetQueryCriteria queryCriteria);

    /**
     * 按照Vin码去查询总数
     * @param vvin
     * @return
     */
    Integer countClickhouseVin(String vvin);

    /**
     * 按照Vin码去查询有Vin码的值并返回
     * @param vvin
     * @return
     */
    List<String> getOneOrderToEndVin(List<String> vvin);

    Page<DwmVlmsDocs> selectDocsPage(GetQueryCriteria queryCriteria);
}
