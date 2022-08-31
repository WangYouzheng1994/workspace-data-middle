package org.jeecg.yqwl.datamiddle.ads.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.yqwl.datamiddle.ads.order.entity.DwmVlmsDocs;
import org.jeecg.yqwl.datamiddle.ads.order.entity.Sptb02;
import java.util.List;


/**
 * @Description: 用来去Oracle查询数据库
 * @Author: jeecg-boot
 * @Date:   2022-05-12
 * @Version: V1.0
 */
public interface IOracleSptb02Service extends IService<Sptb02> {
    /**
     * 查询是否有vin码对应的值
     * @param vvin
     * @return
     */
    Integer countOracleVinOfSptb02AndSptb02d1(String vvin);

    /**
     * 根据vin码查询数据
     * @param vinList
     * @author dabao
     * @date 2022/8/29
     * @return {@link List< DwmVlmsDocs>}
     */
    List<DwmVlmsDocs> selectListByVin(List<String> vinList);
}
