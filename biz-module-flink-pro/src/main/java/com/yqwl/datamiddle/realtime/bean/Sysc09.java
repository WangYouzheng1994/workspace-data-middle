package com.yqwl.datamiddle.realtime.bean;


import lombok.Data;
import java.io.Serializable;

/**
 * <p>
 * 地市级代码表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
public class Sysc09 implements Serializable {

    private static final long serialVersionUID = 1L;


      private Long idnum;

      /**
     * 地市代码
     */
    private String cdsdm;

      /**
     * 省区代码
     */
    private String csqdm;

      /**
     * 地市名称 20 - 50
     */
    private String vdsmc;

      /**
     * 备注 20 50
     */
    private String vbz;

      /**
     * 时间戳。BI提数据
     */
    private Long dstamp;

      /**
     * "物流标准编码。选择，来源于M平台
     */
    private String cwlbm;

      /**
     * "物流标准名称。来源于M平台
"
     */
    private String cwlmc;

      /**
     * 创建时间
     */
    private Long warehouseCreatetime;

      /**
     * 更新时间
     */
    private Long warehouseUpdatetime;


}
