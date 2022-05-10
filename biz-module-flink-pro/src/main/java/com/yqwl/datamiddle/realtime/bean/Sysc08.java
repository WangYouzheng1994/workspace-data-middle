package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * <p>
 * 市县代码表
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data

public class Sysc08 implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

      /**
     * 市县代码
     */
    private String csxdm;

      /**
     * 省区代码
     */
    private String csqdm;

      /**
     * 市县名称
     */
    private String vsxmc;

      /**
     * 备注
     */
    private String vbz;

    
    private Integer id;

      /**
     * 物流标准编码。选择，来源于M平台
     */
    private String cwlbm;

      /**
     * 物流标准名称。来源于M平台
     */
    private String cwlmc;

      /**
     * 同步日期
     */
    private Long dtbrq;

      /**
     * 版本号
     */
    private Integer batchno;

      /**
     * 地市代码。SYSC09.CDSDM
     */
    private String cdsdm;

      /**
     * 经度
     */
    private BigDecimal njd;

      /**
     * 纬度
     */
    private BigDecimal nwd;

      /**
     * 时间戳。BI提数据
     */
    private Long dstamp;

      /**
     * 20171206 红旗市县代码乱码 处理
     */
    private String csxdmHq;

      /**
     * 审批标识：0  未审批  1：已审批
     */
    private String approvalFlag;

      /**
     * 审批人
     */
    private String approvalUser;

      /**
     * 审批日期
     */
    private Long approvalDate;

      /**
     * 半径
     */
    private BigDecimal nradius;

      /**
     * 终审审批标识：0  未审批  1：已审批
     */
    private String finalApprovalFlag;

      /**
     * 终审审批人
     */
    private String finalApprovalUser;

      /**
     * 终审审批日期
     */
    private Long finalApprovalDate;

      /**
     * 创建时间
     */
    private Long warehouseCreatetime;

      /**
     * 更新时间
     */
    private Long warehouseUpdatetime;


    //新加kafka的ts时间戳
    private Timestamp ts;

}
