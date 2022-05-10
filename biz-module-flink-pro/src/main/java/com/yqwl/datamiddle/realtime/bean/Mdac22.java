package com.yqwl.datamiddle.realtime.bean;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 经销商档案
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_mdac22")
public class Mdac22 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 经销商代码
     */
      @TableField("CJXSDM")
    private String cjxsdm;

      /**
     * 经销商名称 20181112 modify by dtf 50-100
     */
      @TableField("VJXSMC")
    private String vjxsmc;

      /**
     * 经销商简称 20181112 modify by dtf 50-100
     */
      @TableField("VJXSJC")
    private String vjxsjc;

      /**
     * 省区代码
     */
      @TableField("CSQDM")
    private String csqdm;

      /**
     * 市县代码
     */
      @TableField("CSXDM")
    private String csxdm;

      /**
     * 商务代表处
     */
      @TableField("CSWDBC")
    private String cswdbc;

      /**
     * 地址 20181112 modify by dtf 100-400 与TDS一致
     */
      @TableField("VDZ")
    private String vdz;

      /**
     * 邮编
     */
      @TableField("CYB")
    private String cyb;

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
     * 传真
     */
      @TableField("VCZ")
    private String vcz;

      /**
     * 网址
     */
      @TableField("VWZ")
    private String vwz;

      /**
     * e_mail
     */
      @TableField("VE_MAIL")
    private String veMail;

      /**
     * 联系人
     */
      @TableField("CLXR")
    private String clxr;

      /**
     * 销售总监
     */
      @TableField("CXSZJ")
    private String cxszj;

      /**
     * 服务总监
     */
      @TableField("CFWZJ")
    private String cfwzj;

      /**
     * 开户行
     */
      @TableField("VKHH")
    private String vkhh;

      /**
     * 银行账号，现在表示银行监管项目中的保障金帐户 19位工商银行卡号
     */
      @TableField("VYHZH")
    private String vyhzh;

      /**
     * 纳税登记号
     */
      @TableField("VNSDJH")
    private String vnsdjh;

      /**
     * 经销商类型.01 /三位一体,02 /单一销售,03 /单一服务
     */
      @TableField("CJXSLX")
    private String cjxslx;

      /**
     * 经销商分组
     */
      @TableField("CJXSFZ")
    private String cjxsfz;

      /**
     * 经销商级别
     */
      @TableField("CJXSJB")
    private String cjxsjb;

      /**
     * 所有制代码
     */
      @TableField("CSYZDM")
    private String csyzdm;

      /**
     * 隶属系统代码
     */
      @TableField("CLSXTDM")
    private String clsxtdm;

      /**
     * 建档日期
     */
      @TableField("DJDRQ")
    private Long djdrq;

      /**
     * 法人代表
     */
      @TableField("CFRDB")
    private String cfrdb;

      /**
     * 信用检查。0、不检查，1、检查
     */
      @TableField("CXYJC")
    private String cxyjc;

      /**
     * 信用额度
     */
      @TableField("NXYED")
    private BigDecimal nxyed;

      /**
     * 付款期限
     */
      @TableField("NFKQX")
    private Long nfkqx;

      /**
     * 提货仓库
     */
      @TableField("CCKDM")
    private String cckdm;

      /**
     * 到货地代码
     */
      @TableField("CDHDDM")
    private String cdhddm;

      /**
     * 价格类型
     */
      @TableField("CJGLX")
    private String cjglx;

      /**
     * 备注
     */
      @TableField("VBZ")
    private String vbz;

      /**
     * 停用标识。0、在用，1、停用
     */
      @TableField("CTYBS")
    private String ctybs;

      /**
     * 停用日期
     */
      @TableField("DTYRQ")
    private Long dtyrq;

      /**
     * 销售订单确认后，占用的信用额度，只读、不可以手工修改。暂不用
     */
      @TableField("NZYXYED")
    private BigDecimal nzyxyed;

      /**
     * 发运提前期
     */
      @TableField("NFYTQQ")
    private BigDecimal nfytqq;

      /**
     * 发运距离(公里)
     */
      @TableField("NFYJL")
    private BigDecimal nfyjl;

      /**
     * 计划标识。‘0’可作计划。1不能做计划
     */
      @TableField("CJHBS")
    private String cjhbs;

      /**
     * 考核分数
     */
      @TableField("NKHFS")
    private Long nkhfs;

      /**
     * 使用本地库资源(0为不使用1为使用)
     */
      @TableField("CSYBDK")
    private String csybdk;

    @TableField("CDXJXS")
    private String cdxjxs;

      /**
     * (暂时无用)查询计划完成情况标识
     */
      @TableField("CCXBS")
    private String ccxbs;

      /**
     * 备件价格类型
     */
      @TableField("CBJJGLX")
    private String cbjjglx;

      /**
     * 备件交货地点
     */
      @TableField("CBJJHDD")
    private String cbjjhdd;

      /**
     * 备件结算方式
     */
      @TableField("CBJJSFS")
    private String cbjjsfs;

      /**
     * 对应备件仓库
     */
      @TableField("CDYBJCK")
    private String cdybjck;

      /**
     * 对应索赔件库
     */
      @TableField("CDYSPCK")
    private String cdyspck;

      /**
     * 默认发票税率
     */
      @TableField("NFPSL")
    private BigDecimal nfpsl;

      /**
     * 服务停用
     */
      @TableField("CFWTY")
    private String cfwty;

      /**
     * 备件信用额度
     */
      @TableField("NBJXYED")
    private BigDecimal nbjxyed;

      /**
     * 结算经销商代码
     */
      @TableField("CJSJXSDM")
    private String cjsjxsdm;

      /**
     * 经营类别
     */
      @TableField("CJYLB")
    private String cjylb;

      /**
     * '备件二级销售标识(0/不是，1/是)'
     */
      @TableField("CBJEJXS")
    private String cbjejxs;

      /**
     * '管理备件库存标识(0/不管理，1/管理)（如果二级销售标识＝1，必须管理库存）'
     */
      @TableField("CBJGLKC")
    private String cbjglkc;

      /**
     * 接口标识　已转出/1
     */
      @TableField("CJKBS")
    private String cjkbs;

      /**
     * FFM码
     */
      @TableField("VFFM")
    private String vffm;

      /**
     * ID
     */
      @TableField("ID")
    private Integer id;

      /**
     * 销售停用
     */
      @TableField("CXSTY")
    private String cxsty;

      /**
     * 服务暂时停用
     */
      @TableField("CFWZSTY")
    private String cfwzsty;

      /**
     * 24小时电话
     */
      @TableField("VYWDH")
    private String vywdh;

      /**
     * 开户名称
     */
      @TableField("VKHMC")
    private String vkhmc;

      /**
     * 开业日期
     */
      @TableField("DKYRQ")
    private Long dkyrq;

      /**
     * 销售业务电话
     */
      @TableField("VXSYWDH")
    private String vxsywdh;

      /**
     * 备件业务电话
     */
      @TableField("VBJYWDH")
    private String vbjywdh;

      /**
     * 服务业务电话，现在表示   委托监管协议编号
     */
      @TableField("VFWYWDH")
    private String vfwywdh;

      /**
     * 备件停用
     */
      @TableField("CBJTY")
    private String cbjty;

      /**
     * 服务站使用，发票限额
     */
      @TableField("NFPXE")
    private BigDecimal nfpxe;

      /**
     * 0/未安装DMS,1/安装DMS
     */
      @TableField("CDMS")
    private String cdms;

    @TableField("CFWDBC")
    private String cfwdbc;

      /**
     * 财务类型
     */
      @TableField("CCWLX")
    private String ccwlx;

      /**
     * 总经理移动电话
     */
      @TableField("CZJLYDDH")
    private String czjlyddh;

      /**
     * 经销商类型1
     */
      @TableField("CJXSLX1")
    private String cjxslx1;

      /**
     * ????????
     */
      @TableField("NSPFPSL")
    private Integer nspfpsl;

      /**
     * 大客户标识
     */
      @TableField("CDKHBS")
    private String cdkhbs;

      /**
     * 多公司模式下的公司-SPTC60
     */
      @TableField("CGS")
    private String cgs;

      /**
     * 主机公司代码。字典WTDW
     */
      @TableField("CZJGSDM")
    private String czjgsdm;

      /**
     * 时间戳。BI提数据
     */
      @TableField("DSTAMP")
    private Long dstamp;

      /**
     * 线路代码：默认为线路起点-线路终点代码 add by dtf 20171220 用于解放长春 匹配线路
     */
      @TableField("CXLDM")
    private String cxldm;

      /**
     * 线路代码：默认为线路起点-线路终点代码 add by dtf 20180511 用于解放成都 匹配线路
     */
      @TableField("CXLDM_CD")
    private String cxldmCd;

      /**
     * 线路代码：默认为线路起点-线路终点代码 add by dtf 20180511 用于解放青岛 匹配线路
     */
      @TableField("CXLDM_QD")
    private String cxldmQd;

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
     * 大区代码
     */
      @TableField("CDQDM")
    private String cdqdm;

      /**
     * 大区名称
     */
      @TableField("CDQMC")
    private String cdqmc;

      /**
     * 小区代码
     */
      @TableField("CXQDM")
    private String cxqdm;

      /**
     * 小区名称
     */
      @TableField("CXQMC")
    private String cxqmc;

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
     * 实际到货地省区代码
     */
      @TableField("SJCSQDM")
    private String sjcsqdm;

      /**
     * 实际到货地市县代码
     */
      @TableField("SJCSXDM")
    private String sjcsxdm;

      /**
     * 经度
     */
      @TableField("NJD")
    private BigDecimal njd;

      /**
     * 纬度
     */
      @TableField("NWD")
    private BigDecimal nwd;

      /**
     * 经纬度同步标识
     */
      @TableField("NFLAG")
    private String nflag;

    @TableField("WAREHOUSE_CREATETIME")
    private Long warehouseCreatetime;

    @TableField("WAREHOUSE_UPDATETIME")
    private Long warehouseUpdatetime;


}
