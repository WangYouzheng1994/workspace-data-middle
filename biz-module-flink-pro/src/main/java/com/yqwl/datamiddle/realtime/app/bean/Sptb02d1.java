package com.yqwl.datamiddle.realtime.app.bean;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * <p>
 * 派车单的待运车辆详细信息。由业务员生成
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
public class Sptb02d1 implements Serializable {

    private static final long serialVersionUID = 1L;

      private Long idnum;

      /**
     * 移动计划
     */
          
    private String cjsdbh;

      /**
     * 产品代码 
     */
          
    private String ccpdm;

      /**
     * VIN 
     */
          
    private String vvin;

      /**
     * 数量 20180412 modify by dtf 由6改为8,1 保密车的补位数有小数！
     */
          
    private BigDecimal nsl;

      /**
     * 备车数量 
     */
          
    private BigDecimal nbcsl;

      /**
     * 发动机号 
     */
          
    private String vfdjh;

      /**
     * 0/1/2 车辆属性 （暂不用）
     */
          
    private String cclsx;

      /**
     * 运费单价
     */
          
    private Integer nyfdj;

      /**
     * 管理费单价
     */
          
    private Integer nglfdj;

      /**
     * 针对解放来讲，是总体的支付金额，而不是单价了
     */
          
    private Integer nzfjg;

      /**
     * 支付价格，主要用于华北基地，算法复杂见21319附件
     */
          
    private Integer nyfbyj;

      /**
     * 审批表ID。来自支付标准
     */
          
    private Integer nspbid;

      /**
     * 支付标准。委托价格X浮动率。
     */
          
    private Integer nzfbz;

      /**
     * 支付备注 20180605(600-1000)
     */
          
    private String czfbz;

      /**
     * 主机公司的单价
     */
          
    private Integer nyfdjZ;

      /**
     * 运费备注
     */
          
    private String cyfbz;

      /**
     * 支付给基地
     */
          
    private Integer nzfjgJd;

      /**
     * 支付备注
     */
          
    private String czfbzJd;

      /**
     * 价格标识。1手动修改，其它，自动计算    21 买断车；22 买断车板里的非买断车；41 保密车； 
     */
          
    private String cjgbs;

      /**
     * 扣款单价。扣款额=nkkdj*nsl
     */
          
    private Integer nkkdj;

      /**
     * 结算补偿单价。支付给基地时，按nzfjg_jd/nyfdj的比例生成
     */
          
    private Integer njsbcj;

      /**
     * 支付补偿单价
     */
          
    private Integer nzfbcj;

      /**
     * 计算补偿价标识；1,2,3为按转网车价、对流价、区域价计算成功了。X表示有异常（详见SPTB87），21表示买断车 
     */
          
    private String czfbcbs;

      /**
     * 吉轻等 收入补偿价 （特殊报表）
     */
          
    private Integer njsbcjTs;

      /**
     * 吉轻等 奖励支付价 （特殊报表）
     */
          
    private Integer njlzfjTs;

      /**
     * 吉轻等 支付补偿价 （特殊报表）
     */
          
    private Integer nzfbcjTs;

        
    private String vjlzfjTs;

      /**
     * 20180129 比率支付价格 大众公路中标运输商 出库日期大于20180101
     */
          
    private Integer nzfjgBl;

      /**
     * 解放支付类型（比率或招标价）
     */
          
    private String vzfbzJf;

      /**
     * 解放 20171201-20180317 招标价 新价格 备注 留痕价 解放补偿运输商（一次性）
     */
          
    private Integer nzfbcjJfbz;

      /**
     * 20180516 解放长春补偿报表 支付价备注 20180711(600-1000) 20190216 1000-2000
     */
          
    private String czfbzJfbc;

      /**
     * 20180619 add by dtf 解放车型名称(J6牵引类；载货；自卸；委改库) 
     */
          
    private String vcxmcJf;

      /**
     * 20180619 add by dtf 解放轴数（两轴）
     */
          
    private String czsJf;

      /**
     * 20180619 add by dtf 解放马力（200）
     */
          
    private String cmlJf;

      /**
     * 大众18年7月补偿 结算补偿价
     */
          
    private Integer njsbcj7;

      /**
     * 大众18年7月补偿 支付补偿价
     */
          
    private Integer nzfbcj7;

      /**
     * 运费备注 补偿 20180909 DTF 大众提价补偿报表
     */
          
    private String cyfbzBc;

      /**
     * 支付补偿备注 20180910 DTF 大众提价补偿报表
     */
          
    private String czfbcj7;

      /**
     * 20181009 add by dtf 解放类型（牵引、载货、自卸）等
     */
          
    private String vcxmc2Jf;

      /**
     * 20181018 add by dtf 价格文件代码 支付报表与录大数检验用
     */
          
    private String priceCode;

      /**
     * 20181022 add by dtf 合同号（主公司） 生成支付报表校验用 20210205 50-100
     */
          
    private String contractNo;

      /**
     * 2018年7月第一次比例补偿 基地支付、收入
     */
          
    private Integer njsbcj7Bl;

      /**
     * 2018年7月第一次比例补偿 普通支付
     */
          
    private Integer nzfbcj7Bl;

      /**
     * 20181115 add by dtf 3地问题处理 新算价格
     */
          
    private Integer nzfjgNew;

      /**
     * 20181115 add by dtf 3地问题处理 新算基地价格
     */
          
    private Integer nzfjgJdNew;

      /**
     * 20181119 add by dtf 大众3地同城支付差值 补偿列
     */
          
    private Integer nzfbcj8;

      /**
     * 20181120 add by dtf 解放接口 公告车型
     */
          
    private Integer cggcx;

      /**
     * 20190121 add by dtf 18年长久支付40%补偿
     */
          
    private Integer nzfbcj9;

      /**
     * 20190131 add by dtf 2018大众公路运输补偿
     */
          
    private Integer njsbcj10;

      /**
     * 大众干线补偿支付价格
     */
          
    private Integer nzfbcj11;

      /**
     * 大众分拨补偿支付价格
     */
          
    private Integer nzfbcj12;

      /**
     * 20190809 B平台2次支付补偿
     */
          
    private Integer nzfbcj13;

      /**
     * 20190926 2019年大众运费补偿 
     */
          
    private Integer njsbcj2019;

      /**
     * 20191010 2019年大众支付补偿 
     */
          
    private Integer nzfbcj2019;

      /**
     * 20191012 2019年大众支付补偿 通过核算项目取到的原价格！
     */
          
    private Integer nzfbcj2019Y;

      /**
     * 20190926 2019年大众运费补偿 通过核算项目取到的原价格！
     */
          
    private Integer njsbcj2019Y;

        
    private Long ddateLastmofifyYf;

        
    private Long ddateLastmofifyZf;

      /**
     * 20200620 DTF 解放支付比例（实际）
     */
          
    private Integer nzfblJf;

      /**
     * 20200621 DTF 解放支付单价（含税）
     */
          
    private Integer nzfbzJf;

      /**
     * 20200703 DTF 单据日期200401开始接收TDS运费价格*0.99
     */
          
    private Integer nyfdjJf;

      /**
     * 20200711 DTF 高速扣款
     */
          
    private Integer nkkGsf;

      /**
     * 20200725 DTF 佛山铁路支付补偿价
     */
          
    private Integer nzfbcj14;

      /**
     * 20200911 DTF 核算项目代码
     */
          
    private String chsxmdm;

      /**
     * 2019支付补偿备注
     */
          
    private String cbzZfbc2019;

      /**
     * 保密车补位结算价格
     */
          
    private Integer njsjgBmcbw;

      /**
     * 保密车补位支付价格
     */
          
    private Integer nzfjgBmcbw;

      /**
     * 20201110 奔腾铁路　２次运费价格　固定值
     */
          
    private Integer nyfBtT;

      /**
     * 20210107 特发车支付补偿价格
     */
          
    private Integer nzfbcj15;

      /**
     * 20210224 大众铁水结算比例
     */
          
    private Integer njsblTs;

      /**
     * 20210224 大众铁水支付比例
     */
          
    private Integer nzfblTs;

      /**
     * 20210224 大众铁水结算正确价格
     */
          
    private Integer nyfdjSjTs;

      /**
     * 20210224 大众铁水支付正确价格
     */
          
    private Integer nzfjgSjTs;

      /**
     * 20210303 大众铁水基地报表比例
     */
          
    private BigDecimal njdblTs;

      /**
     * 20211029 解放支付扣款2 财务
     */
          
    private Integer nkkJf2;

      /**
     * 20211103 大众佛山支付补偿价格
     */
          
    private Integer nzfbcj16;

      /**
     * 20211116 解放长春基地 扣款后的里程 
     */
          
    private Integer nlcJf;

      /**
     * 创建时间
     */
          
    private Long warehouseCreatetime;

      /**
     * 更新时间
     */
          
    private Long warehouseUpdatetime;


}
