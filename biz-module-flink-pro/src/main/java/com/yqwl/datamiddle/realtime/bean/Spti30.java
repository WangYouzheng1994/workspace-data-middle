package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 操作员对应基地授权
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Spti30 implements Serializable {

    private static final long serialVersionUID = 1L;


    private String CDDY;

    /**
     * 区位号。0431、022、027、028、0757表示生产的基地（2013-10-12储运部会议上确定）czddm = 'SPTDZJD'
     */

    private String CQWH;


    private String CCZYDM;


    private Long DCZRQ;


    private String VBZ;

    /**
     * 主机公司代码。字典：WTDW 必输。权限来自SPTI33
     */

    private String CZJGSDM;

    /**
     * 创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */

    private Long WAREHOUSE_UPDATETIME;


}
