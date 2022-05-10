package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;

import java.io.Serializable;
/**
 * <p>
 * 
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
public class Mdac11 implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

      /**
     * 20190406 30-50
     */
          
    private String ccxdm;

        
    private String ccxdl;

        
    private String vcxsm;

        
    private String ctybs;

        
    private Long dtyrq;

        
    private String cpp;

        
    private String cjscx;

        
    private Integer id;

      /**
     * 创建时间
     */
          
    private Long warehouseCreatetime;

      /**
     * 更新时间
     */
          
    private Long warehouseUpdatetime;


}
