package com.yqwl.datamiddle.realtime.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * <p>
 * 司机信息	职位：主驾和副驾
 * </p>
 *
 * @author yiqi
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Sptc03 implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 司机代码
     */

    private String VSJDM;

    /**
     * 姓名
     */

    private String VXM;

    /**
     * 性别
     */

    private String CXB;

    /**
     * 驾驶证号码，身份证
     */

    private String VJSZHM;

    /**
     * 领证日期
     */

    private Long DLZRQ;

    /**
     * 准驾车型
     */

    private String VZJCX;

    /**
     * 电话
     */

    private String VDH;

    /**
     * 移动电话
     */

    private String VYDDH;

    /**
     * 地址
     */

    private String VDZ;

    /**
     * 邮编
     */

    private String VYB;

    /**
     * EMAIL
     */

    private String VEMAIL;

    /**
     * 主驾、副驾
     */

    private String VZW;

    /**
     * 学历，SYSC09C‘VXL’
     */

    private String VXL;

    /**
     * 职业，SYSC09C'VZY'
     */

    private String VZY;

    /**
     * 0、可用 1、停用
     */

    private String VTYBS;

    /**
     * 停用标识
     */

    private Long VTYRQ;

    /**
     * 备注
     */

    private String VBZ;

    /**
     * 0、空闲 1、运输 2、锁定（事假等）
     */

    private String CZT;

    /**
     * 运输商代码
     */

    private String CYSSDM;

    /**
     * LBS手机号
     */

    private String CLBS;

    /**
     * 说明
     */

    private String CSM;

    /**
     * 1表示是特殊人物维护或修改的信息
     */

    private String CTSBS;

    /**
     * 调度员
     */

    private String CDDY;

    /**
     * 核算员
     */

    private String CHSY;

    /**
     * 属性 SYSC09D.CZDDM = 'SJSX'
     */

    private String CSX;

    /**
     * 运营模式 SYSC09D.CZDDM = 'SJYYMS'
     */

    private String CYYMS;

    /**
     * 司机卡号
     */

    private String CSJKH;

    /**
     * 变更日期
     */

    private Long DBGRQ;

    /**
     * 确认日期
     */

    private Long DQRRQ;

    /**
     * 是否国企司机
     */

    private String CKQSJ;

    /**
     * 是否开发票。由车辆表中转至此处
     */

    private String CKFP;

    /**
     * 序号
     */

    private String CXH;

    /**
     * 逻辑删除标识
     */

    private String CDEL;

    /**
     * 旧代码
     */

    private String CDM_O;

    /**
     * 物流标准编码。选择，来源于M平台
     */

    private String CWLBM;

    /**
     * 物流标准名称。来源于M平台
     */

    private String CWLMC;

    /**
     * 同步日期
     */

    private Long DTBRQ;

    /**
     * 版本号
     */

    private Integer BATCHNO;

    /**
     * 验证自有车辆和司机关系。0验证，其它 不验证
     */

    private String CYZBS;

    /**
     * 时间戳。BI提数据
     */

    private Long DSTAMP;

    /**
     * 审批标识：0  未审批  1：已审批
     */

    private String APPROVAL_FLAG;

    /**
     * 审批人
     */

    private String APPROVAL_USER;

    /**
     * 审批日期
     */

    private Long APPROVAL_DATE;

    /**
     * 终审审批标识：0  未审批  1：已审批
     */

    private String FINAL_APPROVAL_FLAG;

    /**
     * 终审审批人
     */

    private String FINAL_APPROVAL_USER;

    /**
     * 终审审批日期
     */

    private Long FINAL_APPROVAL_DATE;

    /**
     * 创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */

    private Long WAREHOUSE_UPDATETIME;


}
