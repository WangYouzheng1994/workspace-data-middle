package com.yqwl.datamiddle.realtime.bean;

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
 * @since 2022-05-30
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class Sptb02d1 implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 移动计划
     */

    private String CJSDBH;

    /**
     * 产品代码
     */

    private String CCPDM;

    /**
     * VIN
     */

    private String VVIN;

    /**
     * 数量 20180412 modify by dtf 由6改为8,1 保密车的补位数有小数！
     */

    private BigDecimal NSL;

    /**
     * 备车数量
     */

    private BigDecimal NBCSL;

    /**
     * 发动机号
     */

    private String VFDJH;

    /**
     * 0/1/2 车辆属性 （暂不用）
     */

    private String CCLSX;

    /**
     * 运费单价
     */

    private BigDecimal NYFDJ;

    /**
     * 管理费单价
     */

    private BigDecimal NGLFDJ;

    /**
     * 针对解放来讲，是总体的支付金额，而不是单价了
     */

    private BigDecimal NZFJG;

    /**
     * 支付价格，主要用于华北基地，算法复杂见21319附件
     */

    private BigDecimal NYFBYJ;

    /**
     * 审批表ID。来自支付标准
     */

    private Integer NSPBID;

    /**
     * 支付标准。委托价格X浮动率。
     */

    private BigDecimal NZFBZ;

    /**
     * 支付备注 20180605(600-1000)
     */

    private String CZFBZ;

    /**
     * 主机公司的单价
     */

    private BigDecimal NYFDJ_Z;

    /**
     * 运费备注
     */

    private String CYFBZ;

    /**
     * 支付给基地
     */

    private Integer NZFJG_JD;

    /**
     * 支付备注
     */

    private String CZFBZ_JD;

    /**
     * 价格标识。1手动修改，其它，自动计算    21 买断车；22 买断车板里的非买断车；41 保密车；
     */

    private String CJGBS;

    /**
     * 扣款单价。扣款额=nkkdj*nsl
     */

    private BigDecimal NKKDJ;

    /**
     * 结算补偿单价。支付给基地时，按nzfjg_jd/nyfdj的比例生成
     */

    private BigDecimal NJSBCJ;

    /**
     * 支付补偿单价
     */

    private BigDecimal NZFBCJ;

    /**
     * 计算补偿价标识；1,2,3为按转网车价、对流价、区域价计算成功了。X表示有异常（详见SPTB87），21表示买断车
     */

    private String CZFBCBS;

    /**
     * 吉轻等 收入补偿价 （特殊报表）
     */

    private BigDecimal NJSBCJ_TS;

    /**
     * 吉轻等 奖励支付价 （特殊报表）
     */

    private BigDecimal NJLZFJ_TS;

    /**
     * 吉轻等 支付补偿价 （特殊报表）
     */

    private BigDecimal NZFBCJ_TS;


    private String VJLZFJ_TS;

    /**
     * 20180129 比率支付价格 大众公路中标运输商 出库日期大于20180101
     */

    private BigDecimal NZFJG_BL;

    /**
     * 解放支付类型（比率或招标价）
     */

    private String VZFBZ_JF;

    /**
     * 解放 20171201-20180317 招标价 新价格 备注 留痕价 解放补偿运输商（一次性）
     */

    private BigDecimal NZFBCJ_JFBZ;

    /**
     * 20180516 解放长春补偿报表 支付价备注 20180711(600-1000) 20190216 1000-2000
     */

    private String CZFBZ_JFBC;

    /**
     * 20180619 add by dtf 解放车型名称(J6牵引类；载货；自卸；委改库)
     */

    private String VCXMC_JF;

    /**
     * 20180619 add by dtf 解放轴数（两轴）
     */

    private String CZS_JF;

    /**
     * 20180619 add by dtf 解放马力（200）
     */

    private String CML_JF;

    /**
     * 大众18年7月补偿 结算补偿价
     */

    private BigDecimal NJSBCJ_7;

    /**
     * 大众18年7月补偿 支付补偿价
     */

    private BigDecimal NZFBCJ_7;

    /**
     * 运费备注 补偿 20180909 DTF 大众提价补偿报表
     */

    private String CYFBZ_BC;

    /**
     * 支付补偿备注 20180910 DTF 大众提价补偿报表
     */

    private String CZFBCJ_7;

    /**
     * 20181009 add by dtf 解放类型（牵引、载货、自卸）等
     */

    private String VCXMC2_JF;

    /**
     * 20181018 add by dtf 价格文件代码 支付报表与录大数检验用
     */

    private String PRICE_CODE;

    /**
     * 20181022 add by dtf 合同号（主公司） 生成支付报表校验用 20210205 50-100
     */

    private String CONTRACT_NO;

    /**
     * 2018年7月第一次比例补偿 基地支付、收入
     */

    private BigDecimal NJSBCJ_7_BL;

    /**
     * 2018年7月第一次比例补偿 普通支付
     */

    private BigDecimal NZFBCJ_7_BL;

    /**
     * 20181115 add by dtf 3地问题处理 新算价格
     */

    private BigDecimal NZFJG_NEW;

    /**
     * 20181115 add by dtf 3地问题处理 新算基地价格
     */

    private BigDecimal NZFJG_JD_NEW;

    /**
     * 20181119 add by dtf 大众3地同城支付差值 补偿列
     */

    private BigDecimal NZFBCJ_8;

    /**
     * 20181120 add by dtf 解放接口 公告车型
     */

    private String CGGCX;

    /**
     * 20190121 add by dtf 18年长久支付40%补偿
     */

    private BigDecimal NZFBCJ_9;

    /**
     * 20190131 add by dtf 2018大众公路运输补偿
     */

    private BigDecimal NJSBCJ_10;

    /**
     * 大众干线补偿支付价格
     */

    private BigDecimal NZFBCJ_11;

    /**
     * 大众分拨补偿支付价格
     */

    private BigDecimal NZFBCJ_12;

    /**
     * 20190809 B平台2次支付补偿
     */

    private BigDecimal NZFBCJ_13;

    /**
     * 20190926 2019年大众运费补偿
     */

    private BigDecimal NJSBCJ_2019;

    /**
     * 20191010 2019年大众支付补偿
     */

    private BigDecimal NZFBCJ_2019;

    /**
     * 20191012 2019年大众支付补偿 通过核算项目取到的原价格！
     */

    private BigDecimal NZFBCJ_2019_Y;

    /**
     * 20190926 2019年大众运费补偿 通过核算项目取到的原价格！
     */

    private BigDecimal NJSBCJ_2019_Y;


    private Long DDATE_LASTMOFIFY_YF;


    private Long DDATE_LASTMOFIFY_ZF;

    /**
     * 20200620 DTF 解放支付比例（实际）
     */

    private BigDecimal NZFBL_JF;

    /**
     * 20200621 DTF 解放支付单价（含税）
     */

    private BigDecimal NZFBZ_JF;

    /**
     * 20200703 DTF 单据日期200401开始接收TDS运费价格*0.99
     */

    private BigDecimal NYFDJ_JF;

    /**
     * 20200711 DTF 高速扣款
     */

    private BigDecimal NKK_GSF;

    /**
     * 20200725 DTF 佛山铁路支付补偿价
     */

    private BigDecimal NZFBCJ_14;

    /**
     * 20200911 DTF 核算项目代码
     */

    private String CHSXMDM;

    /**
     * 2019支付补偿备注
     */

    private String CBZ_ZFBC2019;

    /**
     * 保密车补位结算价格
     */

    private BigDecimal NJSJG_BMCBW;

    /**
     * 保密车补位支付价格
     */

    private BigDecimal NZFJG_BMCBW;

    /**
     * 20201110 奔腾铁路　２次运费价格　固定值
     */

    private BigDecimal NYF_BT_T;

    /**
     * 20210107 特发车支付补偿价格
     */

    private BigDecimal NZFBCJ_15;

    /**
     * 20210224 大众铁水结算比例
     */

    private BigDecimal NJSBL_TS;

    /**
     * 20210224 大众铁水支付比例
     */

    private BigDecimal NZFBL_TS;

    /**
     * 20210224 大众铁水结算正确价格
     */

    private BigDecimal NYFDJ_SJ_TS;

    /**
     * 20210224 大众铁水支付正确价格
     */

    private BigDecimal NZFJG_SJ_TS;

    /**
     * 20210303 大众铁水基地报表比例
     */

    private BigDecimal NJDBL_TS;

    /**
     * 20211029 解放支付扣款2 财务
     */

    private BigDecimal NKK_JF2;

    /**
     * 20211103 大众佛山支付补偿价格
     */

    private BigDecimal NZFBCJ_16;

    /**
     * 20211116 解放长春基地 扣款后的里程
     */

    private BigDecimal NLC_JF;

    /**
     * 创建时间
     */

    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */

    private Long WAREHOUSE_UPDATETIME;


}
