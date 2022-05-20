package com.yqwl.datamiddle.realtime.bean;

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
 * @since 2022-05-18
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Mdac22 implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 经销商代码
     */

    private String CJXSDM;

    /**
     * 经销商名称 20181112 modify by dtf 50-100
     */

    private String VJXSMC;

    /**
     * 经销商简称 20181112 modify by dtf 50-100
     */

    private String VJXSJC;

    /**
     * 省区代码
     */

    private String CSQDM;

    /**
     * 市县代码
     */

    private String CSXDM;

    /**
     * 商务代表处
     */

    private String CSWDBC;

    /**
     * 地址 20181112 modify by dtf 100-400 与TDS一致
     */

    private String VDZ;

    /**
     * 邮编
     */

    private String CYB;

    /**
     * 电话
     */

    private String VDH;

    /**
     * 移动电话
     */

    private String VYDDH;

    /**
     * 传真
     */

    private String VCZ;

    /**
     * 网址
     */

    private String VWZ;

    /**
     * e_mail
     */

    private String VE_MAIL;

    /**
     * 联系人
     */

    private String CLXR;

    /**
     * 销售总监
     */

    private String CXSZJ;

    /**
     * 服务总监
     */

    private String CFWZJ;

    /**
     * 开户行
     */

    private String VKHH;

    /**
     * 银行账号，现在表示银行监管项目中的保障金帐户 19位工商银行卡号
     */

    private String VYHZH;

    /**
     * 纳税登记号
     */

    private String VNSDJH;

    /**
     * 经销商类型.01 /三位一体,02 /单一销售,03 /单一服务
     */

    private String CJXSLX;

    /**
     * 经销商分组
     */

    private String CJXSFZ;

    /**
     * 经销商级别
     */

    private String CJXSJB;

    /**
     * 所有制代码
     */

    private String CSYZDM;

    /**
     * 隶属系统代码
     */

    private String CLSXTDM;

    /**
     * 建档日期
     */

    private Long DJDRQ;

    /**
     * 法人代表
     */

    private String CFRDB;

    /**
     * 信用检查。0、不检查，1、检查
     */

    private String CXYJC;

    /**
     * 信用额度
     */

    private BigDecimal NXYED;

    /**
     * 付款期限
     */

    private Long NFKQX;

    /**
     * 提货仓库
     */

    private String CCKDM;

    /**
     * 到货地代码
     */

    private String CDHDDM;

    /**
     * 价格类型
     */

    private String CJGLX;

    /**
     * 备注
     */

    private String VBZ;

    /**
     * 停用标识。0、在用，1、停用
     */

    private String CTYBS;

    /**
     * 停用日期
     */

    private Long DTYRQ;

    /**
     * 销售订单确认后，占用的信用额度，只读、不可以手工修改。暂不用
     */

    private BigDecimal NZYXYED;

    /**
     * 发运提前期
     */

    private BigDecimal NFYTQQ;

    /**
     * 发运距离(公里)
     */

    private BigDecimal NFYJL;

    /**
     * 计划标识。‘0’可作计划。1不能做计划
     */

    private String CJHBS;

    /**
     * 考核分数
     */

    private Long NKHFS;

    /**
     * 使用本地库资源(0为不使用1为使用)
     */

    private String CSYBDK;


    private String CDXJXS;

    /**
     * (暂时无用)查询计划完成情况标识
     */

    private String CCXBS;

    /**
     * 备件价格类型
     */

    private String CBJJGLX;

    /**
     * 备件交货地点
     */

    private String CBJJHDD;

    /**
     * 备件结算方式
     */

    private String CBJJSFS;

    /**
     * 对应备件仓库
     */

    private String CDYBJCK;

    /**
     * 对应索赔件库
     */

    private String CDYSPCK;

    /**
     * 默认发票税率
     */

    private BigDecimal NFPSL;

    /**
     * 服务停用
     */

    private String CFWTY;

    /**
     * 备件信用额度
     */

    private BigDecimal NBJXYED;

    /**
     * 结算经销商代码
     */

    private String CJSJXSDM;

    /**
     * 经营类别
     */

    private String CJYLB;

    /**
     * '备件二级销售标识(0/不是，1/是)'
     */

    private String CBJEJXS;

    /**
     * '管理备件库存标识(0/不管理，1/管理)（如果二级销售标识＝1，必须管理库存）'
     */

    private String CBJGLKC;

    /**
     * 接口标识　已转出/1
     */

    private String CJKBS;

    /**
     * FFM码
     */

    private String VFFM;

    /**
     * ID
     */

    private Integer ID;

    /**
     * 销售停用
     */

    private String CXSTY;

    /**
     * 服务暂时停用
     */

    private String CFWZSTY;

    /**
     * 24小时电话
     */

    private String VYWDH;

    /**
     * 开户名称
     */

    private String VKHMC;

    /**
     * 开业日期
     */

    private Long DKYRQ;

    /**
     * 销售业务电话
     */

    private String VXSYWDH;

    /**
     * 备件业务电话
     */

    private String VBJYWDH;

    /**
     * 服务业务电话，现在表示   委托监管协议编号
     */

    private String VFWYWDH;

    /**
     * 备件停用
     */

    private String CBJTY;

    /**
     * 服务站使用，发票限额
     */

    private BigDecimal NFPXE;

    /**
     * 0/未安装DMS,1/安装DMS
     */

    private String CDMS;


    private String CFWDBC;

    /**
     * 财务类型
     */

    private String CCWLX;

    /**
     * 总经理移动电话
     */

    private String CZJLYDDH;

    /**
     * 经销商类型1
     */

    private String CJXSLX1;

    /**
     * ????????
     */

    private Integer NSPFPSL;

    /**
     * 大客户标识
     */

    private String CDKHBS;

    /**
     * 多公司模式下的公司-SPTC60
     */

    private String CGS;

    /**
     * 主机公司代码。字典WTDW
     */

    private String CZJGSDM;

    /**
     * 时间戳。BI提数据
     */

    private Long DSTAMP;

    /**
     * 线路代码：默认为线路起点-线路终点代码 add by dtf 20171220 用于解放长春 匹配线路
     */

    private String CXLDM;

    /**
     * 线路代码：默认为线路起点-线路终点代码 add by dtf 20180511 用于解放成都 匹配线路
     */

    private String CXLDM_CD;

    /**
     * 线路代码：默认为线路起点-线路终点代码 add by dtf 20180511 用于解放青岛 匹配线路
     */

    private String CXLDM_QD;

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
     * 大区代码
     */

    private String CDQDM;

    /**
     * 大区名称
     */

    private String CDQMC;

    /**
     * 小区代码
     */

    private String CXQDM;

    /**
     * 小区名称
     */

    private String CXQMC;

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
     * 实际到货地省区代码
     */

    private String SJCSQDM;

    /**
     * 实际到货地市县代码
     */

    private String SJCSXDM;

    /**
     * 经度
     */

    private BigDecimal NJD;

    /**
     * 纬度
     */

    private BigDecimal NWD;

    /**
     * 经纬度同步标识
     */

    private String NFLAG;


    private Long WAREHOUSE_CREATETIME;


    private Long WAREHOUSE_UPDATETIME;


}
