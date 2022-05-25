package org.jeecg.yqwl.datamiddle.ads.order.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Description: 插入Clickhouse的"dwm_vlms_sptb02"表的数据
 * @Author: XiaoFeng
 * @Date: 2022/5/24 19:50
 * @Version: V1.0
 */
@Data
public class DwmSptb02VO {
   /**
    * 结算单编号
    */
   @JsonProperty("CJSDBH")
   private String CJSDBH;

   /**
    * 主机厂计划下达时间   ## 计划量指标
    */
   @JsonProperty("DDJRQ")
   private Long DDJRQ;

   /**
    * 实际"出库"时间## 出库量指标
    */
   @JsonProperty("ACTUAL_OUT_TIME")
   private Long ACTUAL_OUT_TIME;
   /**
    * 理论"出库"时间 ## 待发量指标
    */
   @JsonProperty("THEORY_OUT_TIME")
   private Long THEORY_OUT_TIME;   //--
   /**
    * 实际"起运"时间 ## |在途量指标	**起运量指标
    */
   @JsonProperty("SHIPMENT_TIME")
   private Long SHIPMENT_TIME;
   /**
    * 最终"到货"时间 ## |在途量指标  **到货量指标
    */
   @JsonProperty("FINAL_SITE_TIME")
   private Long FINAL_SITE_TIME;
   /**
    * 主机公司名称  ->对应主机公司代码
    */
   @JsonProperty("CUSTOMER_NAME")
   private String CUSTOMER_NAME;
   /**
    * 基地名称 	  ->对应区位号
    */
   @JsonProperty("BASE_NAME")
   private String BASE_NAME;
   /**
    * 主机公司代码   品牌(大众,红旗,奔腾,马自达)
    */
   @JsonProperty("CZJGSDM")
   private String CZJGSDM;
   /**
    * 区位号        基地(长春,成都,佛山,青岛,天津)
    */
   @JsonProperty("CQWH")
   private String CQWH;
}
