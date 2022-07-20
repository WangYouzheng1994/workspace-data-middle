package com.yqwl.datamiddle.realtime.bean;

import com.yqwl.datamiddle.realtime.enums.CamelUnderline;
import com.yqwl.datamiddle.realtime.enums.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import java.math.BigDecimal;


@Data
@TableName("ods_vlms_sptb22_dq")
@EqualsAndHashCode(callSuper = false)
@CamelUnderline(isChange = false)
public class OdsVlmsSptb22Dq implements Serializable  {
    /**idnum*/
   
    private Integer idnum;
    /**id*/
   
    private Integer id;
    /**cpzh*/
   
    private String cpzh;
    /**vwz*/
    
    private String vwz;
    /**dcjsj*/
   
    private Long dcjsj;
    /**vsd*/
    
    private String vsd;
    /**cjsdbh*/
    
    private String cjsdbh;
    /**csqdm*/
    
    private String csqdm;
    /**csxdm*/
    
    private String csxdm;
    /**dczrq*/
    
    private Long dczrq;
    /**cdhbs*/
    
    private String cdhbs;
    /**0 gps/ 1 lbs*/
    
    private String clybs;
    /**离长时间*/
    
    private Long dlcsj;
    /**csq*/
    
    private String csq;
    /**csx*/
    
    private String csx;
    /**ddksj*/
    
    private Long ddksj;
    /**车辆滞留标识（两次取得数据相同认为是滞留）*/
    
    private String czlbs;
    /**上次位置*/
   
    private String vywz;
    /**cbz*/
   
    private String cbz;
    /**csjh*/
    
    private String csjh;
    /**dlbssj*/
    
    private Long dlbssj;
    /**vlbswz*/
    
    private String vlbswz;
    /**采点时段形如'20091004 02'*/
    
    private String csd;
    /**滞留小时*/
   
    private Integer nljzt;
    /**经度*/
    
    private BigDecimal ccbjd;
    /**纬度*/
    
    private BigDecimal ccbwd;
    /**创建时间*/
    
    private Long warehouseCreatetime;
    /**更新时间*/
    
    private Long warehouseUpdatetime;

}
