package com.yqwl.datamiddle.realtime.app.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 站台-仓库对照表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
public class SiteWarehouse implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

      /**
     * 站点代码
     */
          
    private String vwlckdm;

      /**
     * 站点名称
     */
          
    private String vwlckmc;

      /**
     * 库房ID
     */
          
    private Integer warehouseId;

      /**
     * 库房代码
     */
          
    private String warehouseCode;

      /**
     * 库房名称
     */
          
    private String warehouseName;

      /**
     * 备注
     */
          
    private String cbz;

      /**
     * 操作日期
     */
          
    private Long dczrq;

      /**
     * 操作员
     */
          
    private String cczydm;

      /**
     * 类型：对照contrast；仓库：warehouse
     */
          
    private String type;

        
    private Long warehouseCreatetime;

        
    private Long warehouseUpdatetime;


}
