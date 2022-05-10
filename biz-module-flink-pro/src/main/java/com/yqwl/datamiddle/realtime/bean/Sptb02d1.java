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
 * 派车单的待运车辆详细信息。由业务员生成
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_sptb02d1")
public class Sptb02d1 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 移动计划
     */
      @TableField("CJSDBH")
    private String cjsdbh;

      /**
     * 产品代码 
     */
      @TableField("CCPDM")
    private String ccpdm;

      /**
     * VIN 
     */
      @TableField("VVIN")
    private String vvin;

      /**
     * 数量 20180412 modify by dtf 由6改为8,1 保密车的补位数有小数！
     */
      @TableField("NSL")
    private BigDecimal nsl;

      /**
     * 备车数量 
     */
      @TableField("NBCSL")
    private BigDecimal nbcsl;

      /**
     * 发动机号 
     */
      @TableField("VFDJH")
    private String vfdjh;

      /**
     * 0/1/2 车辆属性 （暂不用）
     */
      @TableField("CCLSX")
    private String cclsx;

      /**
     * 运费单价
     */
      @TableField("NYFDJ")
    private Integer nyfdj;

      /**
     * 管理费单价
     */
      @TableField("NGLFDJ")
    private Integer nglfdj;

      /**
     * 针对解放来讲，是总体的支付金额，而不是单价了
     */
      @TableField("NZFJG")
    private Integer nzfjg;

      /**
     * 支付价格，主要用于华北基地，算法复杂见21319附件
     */
      @TableField("NYFBYJ")
    private Integer nyfbyj;

      /**
     * 审批表ID。来自支付标准
     */
      @TableField("NSPBID")
    private Integer nspbid;

      /**
     * 支付标准。委托价格X浮动率。
     */
      @TableField("NZFBZ")
    private Integer nzfbz;

      /**
     * 支付备注 20180605(600-1000)
     */
      @TableField("CZFBZ")
    private String czfbz;

      /**
     * 主机公司的单价
     */
      @TableField("NYFDJ_Z")
    private Integer nyfdjZ;

      /**
     * 运费备注
     */
      @TableField("CYFBZ")
    private String cyfbz;

      /**
     * 支付给基地
     */
      @TableField("NZFJG_JD")
    private Integer nzfjgJd;

      /**
     * 支付备注
     */
      @TableField("CZFBZ_JD")
    private String czfbzJd;

      /**
     * 价格标识。1手动修改，其它，自动计算    21 买断车；22 买断车板里的非买断车；41 保密车； 
     */
      @TableField("CJGBS")
    private String cjgbs;

      /**
     * 扣款单价。扣款额=nkkdj*nsl
     */
      @TableField("NKKDJ")
    private Integer nkkdj;

      /**
     * 结算补偿单价。支付给基地时，按nzfjg_jd/nyfdj的比例生成
     */
      @TableField("NJSBCJ")
    private Integer njsbcj;

      /**
     * 支付补偿单价
     */
      @TableField("NZFBCJ")
    private Integer nzfbcj;

      /**
     * 计算补偿价标识；1,2,3为按转网车价、对流价、区域价计算成功了。X表示有异常（详见SPTB87），21表示买断车 
     */
      @TableField("CZFBCBS")
    private String czfbcbs;

      /**
     * 吉轻等 收入补偿价 （特殊报表）
     */
      @TableField("NJSBCJ_TS")
    private Integer njsbcjTs;

      /**
     * 吉轻等 奖励支付价 （特殊报表）
     */
      @TableField("NJLZFJ_TS")
    private Integer njlzfjTs;

      /**
     * 吉轻等 支付补偿价 （特殊报表）
     */
      @TableField("NZFBCJ_TS")
    private Integer nzfbcjTs;

    @TableField("VJLZFJ_TS")
    private String vjlzfjTs;

      /**
     * 20180129 比率支付价格 大众公路中标运输商 出库日期大于20180101
     */
      @TableField("NZFJG_BL")
    private Integer nzfjgBl;

      /**
     * 解放支付类型（比率或招标价）
     */
      @TableField("VZFBZ_JF")
    private String vzfbzJf;

      /**
     * 解放 20171201-20180317 招标价 新价格 备注 留痕价 解放补偿运输商（一次性）
     */
      @TableField("NZFBCJ_JFBZ")
    private Integer nzfbcjJfbz;

      /**
     * 20180516 解放长春补偿报表 支付价备注 20180711(600-1000) 20190216 1000-2000
     */
      @TableField("CZFBZ_JFBC")
    private String czfbzJfbc;

      /**
     * 20180619 add by dtf 解放车型名称(J6牵引类；载货；自卸；委改库) 
     */
      @TableField("VCXMC_JF")
    private String vcxmcJf;

      /**
     * 20180619 add by dtf 解放轴数（两轴）
     */
      @TableField("CZS_JF")
    private String czsJf;

      /**
     * 20180619 add by dtf 解放马力（200）
     */
      @TableField("CML_JF")
    private String cmlJf;

      /**
     * 大众18年7月补偿 结算补偿价
     */
      @TableField("NJSBCJ_7")
    private Integer njsbcj7;

      /**
     * 大众18年7月补偿 支付补偿价
     */
      @TableField("NZFBCJ_7")
    private Integer nzfbcj7;

      /**
     * 运费备注 补偿 20180909 DTF 大众提价补偿报表
     */
      @TableField("CYFBZ_BC")
    private String cyfbzBc;

      /**
     * 支付补偿备注 20180910 DTF 大众提价补偿报表
     */
      @TableField("CZFBCJ_7")
    private String czfbcj7;

      /**
     * 20181009 add by dtf 解放类型（牵引、载货、自卸）等
     */
      @TableField("VCXMC2_JF")
    private String vcxmc2Jf;

      /**
     * 20181018 add by dtf 价格文件代码 支付报表与录大数检验用
     */
      @TableField("PRICE_CODE")
    private String priceCode;

      /**
     * 20181022 add by dtf 合同号（主公司） 生成支付报表校验用 20210205 50-100
     */
      @TableField("CONTRACT_NO")
    private String contractNo;

      /**
     * 2018年7月第一次比例补偿 基地支付、收入
     */
      @TableField("NJSBCJ_7_BL")
    private Integer njsbcj7Bl;

      /**
     * 2018年7月第一次比例补偿 普通支付
     */
      @TableField("NZFBCJ_7_BL")
    private Integer nzfbcj7Bl;

      /**
     * 20181115 add by dtf 3地问题处理 新算价格
     */
      @TableField("NZFJG_NEW")
    private Integer nzfjgNew;

      /**
     * 20181115 add by dtf 3地问题处理 新算基地价格
     */
      @TableField("NZFJG_JD_NEW")
    private Integer nzfjgJdNew;

      /**
     * 20181119 add by dtf 大众3地同城支付差值 补偿列
     */
      @TableField("NZFBCJ_8")
    private Integer nzfbcj8;

      /**
     * 20181120 add by dtf 解放接口 公告车型
     */
      @TableField("CGGCX")
    private Integer cggcx;

      /**
     * 20190121 add by dtf 18年长久支付40%补偿
     */
      @TableField("NZFBCJ_9")
    private Integer nzfbcj9;

      /**
     * 20190131 add by dtf 2018大众公路运输补偿
     */
      @TableField("NJSBCJ_10")
    private Integer njsbcj10;

      /**
     * 大众干线补偿支付价格
     */
      @TableField("NZFBCJ_11")
    private Integer nzfbcj11;

      /**
     * 大众分拨补偿支付价格
     */
      @TableField("NZFBCJ_12")
    private Integer nzfbcj12;

      /**
     * 20190809 B平台2次支付补偿
     */
      @TableField("NZFBCJ_13")
    private Integer nzfbcj13;

      /**
     * 20190926 2019年大众运费补偿 
     */
      @TableField("NJSBCJ_2019")
    private Integer njsbcj2019;

      /**
     * 20191010 2019年大众支付补偿 
     */
      @TableField("NZFBCJ_2019")
    private Integer nzfbcj2019;

      /**
     * 20191012 2019年大众支付补偿 通过核算项目取到的原价格！
     */
      @TableField("NZFBCJ_2019_Y")
    private Integer nzfbcj2019Y;

      /**
     * 20190926 2019年大众运费补偿 通过核算项目取到的原价格！
     */
      @TableField("NJSBCJ_2019_Y")
    private Integer njsbcj2019Y;

    @TableField("DDATE_LASTMOFIFY_YF")
    private Long ddateLastmofifyYf;

    @TableField("DDATE_LASTMOFIFY_ZF")
    private Long ddateLastmofifyZf;

      /**
     * 20200620 DTF 解放支付比例（实际）
     */
      @TableField("NZFBL_JF")
    private Integer nzfblJf;

      /**
     * 20200621 DTF 解放支付单价（含税）
     */
      @TableField("NZFBZ_JF")
    private Integer nzfbzJf;

      /**
     * 20200703 DTF 单据日期200401开始接收TDS运费价格*0.99
     */
      @TableField("NYFDJ_JF")
    private Integer nyfdjJf;

      /**
     * 20200711 DTF 高速扣款
     */
      @TableField("NKK_GSF")
    private Integer nkkGsf;

      /**
     * 20200725 DTF 佛山铁路支付补偿价
     */
      @TableField("NZFBCJ_14")
    private Integer nzfbcj14;

      /**
     * 20200911 DTF 核算项目代码
     */
      @TableField("CHSXMDM")
    private String chsxmdm;

      /**
     * 2019支付补偿备注
     */
      @TableField("CBZ_ZFBC2019")
    private String cbzZfbc2019;

      /**
     * 保密车补位结算价格
     */
      @TableField("NJSJG_BMCBW")
    private Integer njsjgBmcbw;

      /**
     * 保密车补位支付价格
     */
      @TableField("NZFJG_BMCBW")
    private Integer nzfjgBmcbw;

      /**
     * 20201110 奔腾铁路　２次运费价格　固定值
     */
      @TableField("NYF_BT_T")
    private Integer nyfBtT;

      /**
     * 20210107 特发车支付补偿价格
     */
      @TableField("NZFBCJ_15")
    private Integer nzfbcj15;

      /**
     * 20210224 大众铁水结算比例
     */
      @TableField("NJSBL_TS")
    private Integer njsblTs;

      /**
     * 20210224 大众铁水支付比例
     */
      @TableField("NZFBL_TS")
    private Integer nzfblTs;

      /**
     * 20210224 大众铁水结算正确价格
     */
      @TableField("NYFDJ_SJ_TS")
    private Integer nyfdjSjTs;

      /**
     * 20210224 大众铁水支付正确价格
     */
      @TableField("NZFJG_SJ_TS")
    private Integer nzfjgSjTs;

      /**
     * 20210303 大众铁水基地报表比例
     */
      @TableField("NJDBL_TS")
    private BigDecimal njdblTs;

      /**
     * 20211029 解放支付扣款2 财务
     */
      @TableField("NKK_JF2")
    private Integer nkkJf2;

      /**
     * 20211103 大众佛山支付补偿价格
     */
      @TableField("NZFBCJ_16")
    private Integer nzfbcj16;

      /**
     * 20211116 解放长春基地 扣款后的里程 
     */
      @TableField("NLC_JF")
    private Integer nlcJf;

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
