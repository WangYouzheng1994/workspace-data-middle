package com.yqwl.datamiddle.realtime.app.bean;

import lombok.Data;
import java.io.Serializable;

/**
 * <p>
 * 承运队信息
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
public class Mdac52 implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

        
    private String ccysdm;

      /**
     * 代码
     */
          
    private String ccyddm;

      /**
     * 名称
     */
          
    private String vcydmc;

      /**
     * 0/未认证，1/认证
     */
          
    private String crzbs;

        
    private String ctybs;

        
    private Long dtyrq;

        
    private String vbz;

      /**
     * 简称
     */
          
    private String vcydjc;

      /**
     * 地址
     */
          
    private String vdz;

      /**
     * 法人代表
     */
          
    private String cfrdb;

      /**
     * 开户行
     */
          
    private String vkhh;

      /**
     * 帐号
     */
          
    private String vyhzh;

      /**
     * 税号
     */
          
    private String vnsdjh;

      /**
     * 负责人
     */
          
    private String vfzr;

      /**
     * 联系人
     */
          
    private String clxr;

      /**
     * 电话
     */
          
    private String vdh;

      /**
     * 移动电话
     */
          
    private String vyddh;

      /**
     * 传真
     */
          
    private String vcz;

        
    private String veMail;

      /**
     * 注册地址
     */
          
    private String vzcdz;

      /**
     * 运输车辆情况：比如：大/50，中/40，小/100等，既字符也可数字
     */
          
    private String vclqk;

      /**
     * dd
     */
          
    private String cyb;

      /**
     * 对应个人代码
     */
          
    private String cgrdm;

      /**
     * 收取管理费方式:D收取运费的5%,0正常收,即单价为运费3%+里程1%；该字段无前台界面支持按陆捷公司要求手工改
     */
          
    private String cbs;

      /**
     * 区域
     */
          
    private String vqy;

      /**
     * 所属
     */
          
    private String css;

      /**
     * 0 不收管理费，1收管理费 用于业务系统接口
     */
          
    private String cglfbs;

      /**
     * 多公司模式下的公司-SPTC60
     */
          
    private String cgs;

      /**
     * SPG码
     */
          
    private String cspgdm;

      /**
     * 2010-06-08才增加的对流标识本应该早发布的
     */
          
    private String cdlbs;

      /**
     * 运输商性质
     */
          
    private String cyssxz;

      /**
     * 运输商属性（自有、站队），用于卡车运输商。字典：YSSSX
     */
          
    private String cysssx;

      /**
     * 序号
     */
          
    private String cxh;

      /**
     * 历史SPG码
     */
          
    private String cspgdmH;

      /**
     * 结算单位代码
     */
          
    private String cljsdm;

      /**
     * 针对非长春有效
     */
          
    private String cyssdmO;

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
          
    private Long batchno;

        
    private String ccysdmO;

      /**
     * 运输商类型 全程控制标识
     */
          
    private String cysslx;

      /**
     * 轿车接口运输商代码。来源于tds_vlms_ccyddm
     */
          
    private String cyssdm2;

      /**
     * 时间戳。BI提数据
     */
          
    private Long dstamp;

      /**
     * 客户标准码
     */
          
    private String cwlbmK;

      /**
     * 轿车TDS的运输商名称
     */
          
    private String vcydmc2;

      /**
     * 红旗接口运输商代码。来源于tds_vlms_ccyddm
     */
          
    private String cyssdm3;

      /**
     * 红旗TDS的运输商名称
     */
          
    private String vcydmc3;

      /**
     * 马自达接口运输商代码。来源于tds_vlms_ccyddm
     */
          
    private String cyssdm4;

      /**
     * 马自达TDS的运输商名称
     */
          
    private String vcydmc4;

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
     * 解放青岛接口运输商代码。
     */
          
    private String cyssdm5;

      /**
     * 解放青岛TDS的运输商名称
     */
          
    private String vcydmc5;

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
     * G公路 T 铁路 S 水路
     */
          
    private String vyssnature;

      /**
     * 20200811统销 解放青岛接口运输商代码。
     */
          
    private String cyssdm6;

      /**
     * 20200811统销 解放青岛TDS的运输商名称
     */
          
    private String vcydmc6;

        
    private Long warehouseCreatetime;

        
    private Long warehouseUpdatetime;


}
