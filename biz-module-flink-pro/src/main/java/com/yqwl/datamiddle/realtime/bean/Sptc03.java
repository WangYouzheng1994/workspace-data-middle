package com.yqwl.datamiddle.realtime.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_sptc03")
public class Sptc03 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 司机代码
     */
      @TableField("VSJDM")
    private String vsjdm;

      /**
     * 姓名
     */
      @TableField("VXM")
    private String vxm;

      /**
     * 性别
     */
      @TableField("CXB")
    private String cxb;

      /**
     * 驾驶证号码，身份证
     */
      @TableField("VJSZHM")
    private String vjszhm;

      /**
     * 领证日期
     */
      @TableField("DLZRQ")
    private Long dlzrq;

      /**
     * 准驾车型
     */
      @TableField("VZJCX")
    private String vzjcx;

      /**
     * 电话
     */
      @TableField("VDH")
    private String vdh;

      /**
     * 移动电话
     */
      @TableField("VYDDH")
    private String vyddh;

      /**
     * 地址
     */
      @TableField("VDZ")
    private String vdz;

      /**
     * 邮编
     */
      @TableField("VYB")
    private String vyb;

      /**
     * EMAIL
     */
      @TableField("VEMAIL")
    private String vemail;

      /**
     * 主驾、副驾
     */
      @TableField("VZW")
    private String vzw;

      /**
     * 学历，SYSC09C‘VXL’
     */
      @TableField("VXL")
    private String vxl;

      /**
     * 职业，SYSC09C'VZY'
     */
      @TableField("VZY")
    private String vzy;

      /**
     * 0、可用 1、停用
     */
      @TableField("VTYBS")
    private String vtybs;

      /**
     * 停用标识
     */
      @TableField("VTYRQ")
    private Long vtyrq;

      /**
     * 备注
     */
      @TableField("VBZ")
    private String vbz;

      /**
     * 0、空闲 1、运输 2、锁定（事假等）
     */
      @TableField("CZT")
    private String czt;

      /**
     * 运输商代码
     */
      @TableField("CYSSDM")
    private String cyssdm;

      /**
     * LBS手机号
     */
      @TableField("CLBS")
    private String clbs;

      /**
     * 说明
     */
      @TableField("CSM")
    private String csm;

      /**
     * 1表示是特殊人物维护或修改的信息
     */
      @TableField("CTSBS")
    private String ctsbs;

      /**
     * 调度员
     */
      @TableField("CDDY")
    private String cddy;

      /**
     * 核算员
     */
      @TableField("CHSY")
    private String chsy;

      /**
     * 属性 SYSC09D.CZDDM = 'SJSX'
     */
      @TableField("CSX")
    private String csx;

      /**
     * 运营模式 SYSC09D.CZDDM = 'SJYYMS'
     */
      @TableField("CYYMS")
    private String cyyms;

      /**
     * 司机卡号
     */
      @TableField("CSJKH")
    private String csjkh;

      /**
     * 变更日期
     */
      @TableField("DBGRQ")
    private Long dbgrq;

      /**
     * 确认日期
     */
      @TableField("DQRRQ")
    private Long dqrrq;

      /**
     * 是否国企司机
     */
      @TableField("CKQSJ")
    private String ckqsj;

      /**
     * 是否开发票。由车辆表中转至此处
     */
      @TableField("CKFP")
    private String ckfp;

      /**
     * 序号
     */
      @TableField("CXH")
    private String cxh;

      /**
     * 逻辑删除标识
     */
      @TableField("CDEL")
    private String cdel;

      /**
     * 旧代码
     */
      @TableField("CDM_O")
    private String cdmO;

      /**
     * 物流标准编码。选择，来源于M平台
     */
      @TableField("CWLBM")
    private String cwlbm;

      /**
     * 物流标准名称。来源于M平台
     */
      @TableField("CWLMC")
    private String cwlmc;

      /**
     * 同步日期
     */
      @TableField("DTBRQ")
    private Long dtbrq;

      /**
     * 版本号
     */
      @TableField("BATCHNO")
    private Integer batchno;

      /**
     * 验证自有车辆和司机关系。0验证，其它 不验证
     */
      @TableField("CYZBS")
    private String cyzbs;

      /**
     * 时间戳。BI提数据
     */
      @TableField("DSTAMP")
    private Long dstamp;

      /**
     * 审批标识：0  未审批  1：已审批
     */
      @TableField("APPROVAL_FLAG")
    private String approvalFlag;

      /**
     * 审批人
     */
      @TableField("APPROVAL_USER")
    private String approvalUser;

      /**
     * 审批日期
     */
      @TableField("APPROVAL_DATE")
    private Long approvalDate;

      /**
     * 终审审批标识：0  未审批  1：已审批
     */
      @TableField("FINAL_APPROVAL_FLAG")
    private String finalApprovalFlag;

      /**
     * 终审审批人
     */
      @TableField("FINAL_APPROVAL_USER")
    private String finalApprovalUser;

      /**
     * 终审审批日期
     */
      @TableField("FINAL_APPROVAL_DATE")
    private Long finalApprovalDate;

      /**
     * 创建时间
     */
      @TableField("WAREHOUSE_CREATETIME")
    private Long warehouseCreatetime;

      /**
     * 更新时间
     */
      @TableField("WAREHOUSE_UPDATETIME")
    private Long warehouseUpdatetime;


}
