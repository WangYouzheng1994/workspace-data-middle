package com.yqwl.datamiddle.realtime.app.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 铁水运单溯源接口入库时间
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
public class Sptb02RailseaRk implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

      /**
     * 结算单编号 主键
     */
          
    private String cjsdbh;

      /**
     * VIN 
     */
          
    private String vvin;

      /**
     * 物理仓库代码（站台代码）
     */
          
    private String vwlckdm;

      /**
     * 物理仓库名称 增加长度：由20增至50
     */
          
    private String vwlckmc;

      /**
     * 溯源库房ID
     */
          
    private Integer warehouseId;

      /**
     * 溯源库房代码
     */
          
    private String warehouseCode;

      /**
     * 溯源库房名称
     */
          
    private String warehouseName;

      /**
     * 入库日期
     */
          
    private Long drkrq;

      /**
     * 创建时间
     */
          
    private Long warehouseCreatetime;

      /**
     * 更新时间
     */
          
    private Long warehouseUpdatetime;


}
