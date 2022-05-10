package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 运单STD导入
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
public class Sptb02StdImport implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

        
    private Integer id;

      /**
     * 结算单编号（作废了）
     */
          
    private String cjsdbh;

      /**
     * 配载单编号
     */
          
    private String cpzdbh;

      /**
     * 清单号
     */
          
    private String vwxdwdm;

      /**
     * 任务单号
     */
          
    private String cjhdh;

      /**
     * 运输方式  公路:G 铁路：T或L1  水路 :S  集港 ：J  水运短拨：SD 铁路短拨：TD （作废了）
     */
          
    private String vysfs;

      /**
     * SD:收单  SCSJ:试乘试驾
     */
          
    private String type;

      /**
     * 创建人
     */
          
    private String createBy;

      /**
     * 创建人姓名
     */
          
    private String createByName;

      /**
     * 创建时间
     */
          
    private Long createDate;

      /**
     * 收单时间
     */
          
    private Long sdDate;

      /**
     * 监控理论到货时间
     */
          
    private Long vdhzsxGps;

      /**
     * 监控到货时间
     */
          
    private Long dgpsdhsj;

      /**
     * 审核人
     */
          
    private String approverUser;

      /**
     * 大众审核时间 20210430 DTF
     */
          
    private Long dshsjDz;

      /**
     * 创建时间
     */
          
    private Long warehouseCreatetime;

      /**
     * 更新时间
     */
          
    private Long warehouseUpdatetime;


}
