package com.yqwl.datamiddle.realtime.bean;


import lombok.Data;


import java.io.Serializable;

/**
 * <p>
 * 司机信息	职位：主驾和副驾
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data

public class Sptc03 implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

      /**
     * 司机代码
     */
      
    private String vsjdm;

      /**
     * 姓名
     */
      
    private String vxm;

      /**
     * 性别
     */
      
    private String cxb;

      /**
     * 驾驶证号码，身份证
     */
      
    private String vjszhm;

      /**
     * 领证日期
     */
      
    private Long dlzrq;

      /**
     * 准驾车型
     */
      
    private String vzjcx;

      /**
     * 电话
     */
      
    private String vdh;

      /**
     * 移动电话
     */
      
    private String vyddh;

      /**
     * 地址
     */
      
    private String vdz;

      /**
     * 邮编
     */
      
    private String vyb;

      /**
     * EMAIL
     */
      
    private String vemail;

      /**
     * 主驾、副驾
     */
      
    private String vzw;

      /**
     * 学历，SYSC09C‘VXL’
     */
      
    private String vxl;

      /**
     * 职业，SYSC09C'VZY'
     */
      
    private String vzy;

      /**
     * 0、可用 1、停用
     */
      
    private String vtybs;

      /**
     * 停用标识
     */
      
    private Long vtyrq;

      /**
     * 备注
     */
      
    private String vbz;

      /**
     * 0、空闲 1、运输 2、锁定（事假等）
     */
      
    private String czt;

      /**
     * 运输商代码
     */
      
    private String cyssdm;

      /**
     * LBS手机号
     */
      
    private String clbs;

      /**
     * 说明
     */
      
    private String csm;

      /**
     * 1表示是特殊人物维护或修改的信息
     */
      
    private String ctsbs;

      /**
     * 调度员
     */
      
    private String cddy;

      /**
     * 核算员
     */
      
    private String chsy;

      /**
     * 属性 SYSC09D.CZDDM = 'SJSX'
     */
      
    private String csx;

      /**
     * 运营模式 SYSC09D.CZDDM = 'SJYYMS'
     */
      
    private String cyyms;

      /**
     * 司机卡号
     */
      
    private String csjkh;

      /**
     * 变更日期
     */
      
    private Long dbgrq;

      /**
     * 确认日期
     */
      
    private Long dqrrq;

      /**
     * 是否国企司机
     */
      
    private String ckqsj;

      /**
     * 是否开发票。由车辆表中转至此处
     */
      
    private String ckfp;

      /**
     * 序号
     */
      
    private String cxh;

      /**
     * 逻辑删除标识
     */
      
    private String cdel;

      /**
     * 旧代码
     */
      
    private String cdmO;

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
     * 验证自有车辆和司机关系。0验证，其它 不验证
     */
      
    private String cyzbs;

      /**
     * 时间戳。BI提数据
     */
      
    private Long dstamp;

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


}
