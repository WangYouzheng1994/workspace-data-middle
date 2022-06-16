package org.jeecg.yqwl.datamiddle.ads.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.yqwl.datamiddle.ads.order.enums.CamelUnderline;

import java.io.Serializable;

/**
 * <p>
 * rfid商品车车接口数据
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ods_vlms_base_station_data_epc")
@CamelUnderline(isChange = false)
public class BaseStationDataEpc implements Serializable {

//    private static final long serialVersionUID = 1L;

    /**
     * 车架号
     */

    private String VIN;

    /**
     * 操作员
     */

    private String OPERATOR;

    /**
     * 操作时间
     */

    private Long OPERATETIME;

    /**
     * Epc码
     */

    private String EPC;

    /**
     * 操作类型  1001新增   1002更新
     */

    private String OPERTYPE;

    /**
     * 工厂下线口
     */

    private String CP;

    /**
     * 站点编号
     */

    private String SHOPNO;

    /**
     * 增加时间
     */

    private Long INSERT_DATE;


    private Long WAREHOUSE_CREATETIME;


    private Long WAREHOUSE_UPDATETIME;

    /**
     * 代码逻辑需要,新加字段
     * RowNum
     */
    private Integer Rn;


}
