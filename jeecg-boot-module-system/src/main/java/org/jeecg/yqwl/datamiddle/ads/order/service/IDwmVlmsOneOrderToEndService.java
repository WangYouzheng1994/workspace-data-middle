package org.jeecg.yqwl.datamiddle.ads.order.service;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsDocs;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsOneOrderToEnd;
import org.jeecg.yqwl.datamiddle.ads.order.vo.GetQueryCriteria;
import org.jeecg.yqwl.datamiddle.ads.order.vo.VvinGroupQuery;

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
    List<DwmVlmsOneOrderToEnd> selectOneOrderToEndList2(GetQueryCriteria queryCriteria);

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
     *  优化查询
     * @param queryCriteria  查询参数
     * @author dabao
     * @date 2022/8/25
     * @return {@link List< DwmVlmsDocs>}
     */
    List<DwmVlmsDocs> selectDocsList2(GetQueryCriteria queryCriteria);

    /**
     * 分片查总数
     * @param queryCriteria
     * @author dabao
     * @date 2022/9/21
     * @return {@link Integer}
     */
    Integer selectDocsCount(GetQueryCriteria queryCriteria);



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

    /**
     * 分页查询DOCS
     * @param queryCriteria 查询参数
     * @author dabao
     * @date 2022/8/25
     * @return {@link Page<DwmVlmsDocs>}
     */
    Page<DwmVlmsDocs> selectDocsPage(GetQueryCriteria queryCriteria);

    /**
     * 一单到底查询
     * @param queryCriteria
     * @author dabao
     * @date 2022/8/25
     * @return {@link Page< DwmVlmsOneOrderToEnd>}
     */
    Page<DwmVlmsOneOrderToEnd> selectOneOrderToEndPage(GetQueryCriteria queryCriteria);

    Integer selectCountDocs(GetQueryCriteria queryCriteria);
}
