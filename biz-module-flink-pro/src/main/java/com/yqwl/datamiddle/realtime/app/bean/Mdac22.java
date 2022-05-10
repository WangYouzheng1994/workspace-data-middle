package com.yqwl.datamiddle.realtime.app.bean;

import lombok.Data;
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
public class Mdac22 implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

      /**
     * 经销商代码
     */
          
    private String cjxsdm;

      /**
     * 经销商名称 20181112 modify by dtf 50-100
     */
          
    private String vjxsmc;

      /**
     * 经销商简称 20181112 modify by dtf 50-100
     */
          
    private String vjxsjc;

      /**
     * 省区代码
     */
          
    private String csqdm;

      /**
     * 市县代码
     */
          
    private String csxdm;

      /**
     * 商务代表处
     */
          
    private String cswdbc;

      /**
     * 地址 20181112 modify by dtf 100-400 与TDS一致
     */
          
    private String vdz;

      /**
     * 邮编
     */
          
    private String cyb;

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

      /**
     * 网址
     */
          
    private String vwz;

      /**
     * e_mail
     */
          
    private String veMail;

      /**
     * 联系人
     */
          
    private String clxr;

      /**
     * 销售总监
     */
          
    private String cxszj;

      /**
     * 服务总监
     */
          
    private String cfwzj;

      /**
     * 开户行
     */
          
    private String vkhh;

      /**
     * 银行账号，现在表示银行监管项目中的保障金帐户 19位工商银行卡号
     */
          
    private String vyhzh;

      /**
     * 纳税登记号
     */
          
    private String vnsdjh;

      /**
     * 经销商类型.01 /三位一体,02 /单一销售,03 /单一服务
     */
          
    private String cjxslx;

      /**
     * 经销商分组
     */
          
    private String cjxsfz;

      /**
     * 经销商级别
     */
          
    private String cjxsjb;

      /**
     * 所有制代码
     */
          
    private String csyzdm;

      /**
     * 隶属系统代码
     */
          
    private String clsxtdm;

      /**
     * 建档日期
     */
          
    private Long djdrq;

      /**
     * 法人代表
     */
          
    private String cfrdb;

      /**
     * 信用检查。0、不检查，1、检查
     */
          
    private String cxyjc;

      /**
     * 信用额度
     */
          
    private BigDecimal nxyed;

      /**
     * 付款期限
     */
          
    private Long nfkqx;

      /**
     * 提货仓库
     */
          
    private String cckdm;

      /**
     * 到货地代码
     */
          
    private String cdhddm;

      /**
     * 价格类型
     */
          
    private String cjglx;

      /**
     * 备注
     */
          
    private String vbz;

      /**
     * 停用标识。0、在用，1、停用
     */
          
    private String ctybs;

      /**
     * 停用日期
     */
          
    private Long dtyrq;

      /**
     * 销售订单确认后，占用的信用额度，只读、不可以手工修改。暂不用
     */
          
    private BigDecimal nzyxyed;

      /**
     * 发运提前期
     */
          
    private BigDecimal nfytqq;

      /**
     * 发运距离(公里)
     */
          
    private BigDecimal nfyjl;

      /**
     * 计划标识。‘0’可作计划。1不能做计划
     */
          
    private String cjhbs;

      /**
     * 考核分数
     */
          
    private Long nkhfs;

      /**
     * 使用本地库资源(0为不使用1为使用)
     */
          
    private String csybdk;

        
    private String cdxjxs;

      /**
     * (暂时无用)查询计划完成情况标识
     */
          
    private String ccxbs;

      /**
     * 备件价格类型
     */
          
    private String cbjjglx;

      /**
     * 备件交货地点
     */
          
    private String cbjjhdd;

      /**
     * 备件结算方式
     */
          
    private String cbjjsfs;

      /**
     * 对应备件仓库
     */
          
    private String cdybjck;

      /**
     * 对应索赔件库
     */
          
    private String cdyspck;

      /**
     * 默认发票税率
     */
          
    private BigDecimal nfpsl;

      /**
     * 服务停用
     */
          
    private String cfwty;

      /**
     * 备件信用额度
     */
          
    private BigDecimal nbjxyed;

      /**
     * 结算经销商代码
     */
          
    private String cjsjxsdm;

      /**
     * 经营类别
     */
          
    private String cjylb;

      /**
     * '备件二级销售标识(0/不是，1/是)'
     */
          
    private String cbjejxs;

      /**
     * '管理备件库存标识(0/不管理，1/管理)（如果二级销售标识＝1，必须管理库存）'
     */
          
    private String cbjglkc;

      /**
     * 接口标识　已转出/1
     */
          
    private String cjkbs;

      /**
     * FFM码
     */
          
    private String vffm;

      /**
     * ID
     */
          
    private Integer id;

      /**
     * 销售停用
     */
          
    private String cxsty;

      /**
     * 服务暂时停用
     */
          
    private String cfwzsty;

      /**
     * 24小时电话
     */
          
    private String vywdh;

      /**
     * 开户名称
     */
          
    private String vkhmc;

      /**
     * 开业日期
     */
          
    private Long dkyrq;

      /**
     * 销售业务电话
     */
          
    private String vxsywdh;

      /**
     * 备件业务电话
     */
          
    private String vbjywdh;

      /**
     * 服务业务电话，现在表示   委托监管协议编号
     */
          
    private String vfwywdh;

      /**
     * 备件停用
     */
          
    private String cbjty;

      /**
     * 服务站使用，发票限额
     */
          
    private BigDecimal nfpxe;

      /**
     * 0/未安装DMS,1/安装DMS
     */
          
    private String cdms;

        
    private String cfwdbc;

      /**
     * 财务类型
     */
          
    private String ccwlx;

      /**
     * 总经理移动电话
     */
          
    private String czjlyddh;

      /**
     * 经销商类型1
     */
          
    private String cjxslx1;

      /**
     * ????????
     */
          
    private Integer nspfpsl;

      /**
     * 大客户标识
     */
          
    private String cdkhbs;

      /**
     * 多公司模式下的公司-SPTC60
     */
          
    private String cgs;

      /**
     * 主机公司代码。字典WTDW
     */
          
    private String czjgsdm;

      /**
     * 时间戳。BI提数据
     */
          
    private Long dstamp;

      /**
     * 线路代码：默认为线路起点-线路终点代码 add by dtf 20171220 用于解放长春 匹配线路
     */
          
    private String cxldm;

      /**
     * 线路代码：默认为线路起点-线路终点代码 add by dtf 20180511 用于解放成都 匹配线路
     */
          
    private String cxldmCd;

      /**
     * 线路代码：默认为线路起点-线路终点代码 add by dtf 20180511 用于解放青岛 匹配线路
     */
          
    private String cxldmQd;

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
     * 大区代码
     */
          
    private String cdqdm;

      /**
     * 大区名称
     */
          
    private String cdqmc;

      /**
     * 小区代码
     */
          
    private String cxqdm;

      /**
     * 小区名称
     */
          
    private String cxqmc;

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
     * 实际到货地省区代码
     */
          
    private String sjcsqdm;

      /**
     * 实际到货地市县代码
     */
          
    private String sjcsxdm;

      /**
     * 经度
     */
          
    private BigDecimal njd;

      /**
     * 纬度
     */
          
    private BigDecimal nwd;

      /**
     * 经纬度同步标识
     */
          
    private String nflag;

        
    private Long warehouseCreatetime;

        
    private Long warehouseUpdatetime;


}
