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
 * 运输车辆档案
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_mdac33")
public class Mdac33 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

      /**
     * 运输车代码
     */
      @TableField("VYSCDM")
    private String vyscdm;

      /**
     * 牌照号
     */
      @TableField("VPZH")
    private String vpzh;

      /**
     * 电话
     */
      @TableField("VDH")
    private String vdh;

      /**
     * 联系人
     */
      @TableField("CLXR")
    private String clxr;

      /**
     * 联系电话
     */
      @TableField("VLXDH")
    private String vlxdh;

      /**
     * 移动电话
     */
      @TableField("VYDDH")
    private String vyddh;

      /**
     * 运输车型代码
     */
      @TableField("VYSCX")
    private String vyscx;

      /**
     * 0、未停用，1、停用
     */
      @TableField("CTYBS")
    private String ctybs;

      /**
     * 停用日期
     */
      @TableField("DTYRQ")
    private Long dtyrq;

      /**
     * 备注
     */
      @TableField("VBZ")
    private String vbz;

      /**
     * 0/可用，1/已分配，2/在途，3/返程，G/锁定
     */
      @TableField("CZT")
    private String czt;

      /**
     * 副司机代码（T）
     */
      @TableField("VFSJDM")
    private String vfsjdm;

      /**
     * 主司机代码(T)
     */
      @TableField("VZSJDM")
    private String vzsjdm;

      /**
     * 承运定额(J)
     */
      @TableField("NCYDE")
    private Integer ncyde;

      /**
     * 认证标识。0/未认证，1/认证(J)
     */
      @TableField("CRZBS")
    private String crzbs;

      /**
     * 承运商代码(J)
     */
      @TableField("CCYSDM")
    private String ccysdm;

      /**
     * 承运队代码(J)
     */
      @TableField("CCYDDM")
    private String ccyddm;

      /**
     * 挂车牌照号
     */
      @TableField("VGCPZH")
    private String vgcpzh;

      /**
     * 运输车规格型号
     */
      @TableField("VGGXH")
    private String vggxh;

    @TableField("VCLQK")
    private String vclqk;

      /**
     * 0/正常,1/临时
     */
      @TableField("CCLSX")
    private String cclsx;

      /**
     * 专线说明
     */
      @TableField("CZXDM")
    private String czxdm;

      /**
     * GPS锁定时间
     */
      @TableField("DGPSSDSJ")
    private Long dgpssdsj;

      /**
     * GPS解锁时间
     */
      @TableField("DGPSJSSJ")
    private Long dgpsjssj;

      /**
     * 管理费标识 1收管理费 0不收管理费
     */
      @TableField("CGLFBS")
    private String cglfbs;

      /**
     * 运费标识 0不收运费  1正常收运费 2挂靠运费 3对流运费(返单登记306和业务接口tr_sptb02_yfcl用到)
     */
      @TableField("CYFBS")
    private String cyfbs;

      /**
     * 新车标识。0旧车，1新车
     */
      @TableField("CCYGB")
    private String ccygb;

      /**
     * 车头登记日期   日期型有日期控件
     */
      @TableField("DCTDJRQ")
    private Long dctdjrq;

      /**
     * 制造厂家    下拉框，下拉内容为 一汽和其他厂
     */
      @TableField("CZZCJ")
    private String czzcj;

      /**
     * 发动机马力  下拉框，下拉内容为 180及以下、180~220、220~260、260~320、320及以上
     */
      @TableField("CFDJML")
    private String cfdjml;

      /**
     * 发动机维修   下拉框，下拉内容为 无和更换
     */
      @TableField("CFDJWX")
    private String cfdjwx;

      /**
     * 挂车登记日期   日期型有日期控件
     */
      @TableField("DGCDJRQ")
    private Long dgcdjrq;

      /**
     * 桥（含牵引头） 下拉框，下拉内容为 三桥、四桥、五桥、六桥
     */
      @TableField("CQ")
    private String cq;

      /**
     * 车辆属性      下拉框，下拉内容为 公司、个人
     */
      @TableField("CCLSX1")
    private String cclsx1;

      /**
     * 车辆状态      下拉框，下拉内容为 内网车、外网车、加盟商
     */
      @TableField("CCLZT")
    private String cclzt;

      /**
     * 调度员        字符型，50字符长度
     */
      @TableField("CDDYXM")
    private String cddyxm;

      /**
     * 核算员        字符型，50字符长度
     */
      @TableField("CHSYXM")
    private String chsyxm;

      /**
     * 属性   下拉框，下拉内容为站队、一汽物流
     */
      @TableField("CSX")
    private String csx;

      /**
     * 运营模式 下拉框，下拉内容为半运费、对流1、对流2、对流3、挂靠(卓成)、挂靠1、挂靠2、挂靠3、挂靠4、挂靠5、管理费1、管理费2、管理费3、管理费4、里程工资制、买断、内大包、内大包1、外大包
     */
      @TableField("CYYMS")
    private String cyyms;

      /**
     *  支付标准
     */
      @TableField("CZFBS")
    private String czfbs;

      /**
     * 捆绑费 下拉框  下拉内容 是、否
     */
      @TableField("CKBF")
    private String ckbf;

      /**
     * 车厢牌照号
     */
      @TableField("CCXPZH")
    private String ccxpzh;

      /**
     * 车型        下拉菜单 下拉内容为 正常、A2、宽体、宽体+A2
     */
      @TableField("CCX")
    private String ccx;

      /**
     * 发动机号    字符型  50字符长度
     */
      @TableField("CFDJH")
    private String cfdjh;

      /**
     * 车辆厂牌型号 字符型 50字符长度
     */
      @TableField("CCLCPXH")
    private String cclcpxh;

      /**
     * 车厢品牌     下拉菜单 下拉内容为  解放牌、通华牌、金鸽牌、万荣牌、环达牌、汇达牌、神行牌、冀骏牌、东堡牌、劳尔牌、其他
     */
      @TableField("CCXPP")
    private String ccxpp;

      /**
     * 连接全长（实际尺寸）字符型  50字符长度
     */
      @TableField("CLJQC")
    private String cljqc;

      /**
     * 挂车全长（行车证尺寸）
     */
      @TableField("CGCQC1")
    private String cgcqc1;

      /**
     *  挂车全长（实际尺寸）
     */
      @TableField("CGCQC2")
    private String cgcqc2;

      /**
     *  车厢宽度（行车证尺寸）
     */
      @TableField("CCXKD1")
    private String ccxkd1;

      /**
     *  车厢宽度（实际尺寸）
     */
      @TableField("CCXKD2")
    private String ccxkd2;

      /**
     * 车厢高度（行车证尺寸）
     */
      @TableField("CCXGD1")
    private String ccxgd1;

      /**
     * 车厢高度（实际尺寸）
     */
      @TableField("CCXGD2")
    private String ccxgd2;

      /**
     * 上梯实际尺寸
     */
      @TableField("CSTSJCC")
    private String cstsjcc;

      /**
     * 下梯实际尺寸
     */
      @TableField("CXTSJCC")
    private String cxtsjcc;

      /**
     * 检车日期
     */
      @TableField("DJCRQ")
    private Long djcrq;

      /**
     * 入网日期
     */
      @TableField("DRWRQ")
    private Long drwrq;

      /**
     * 0、在网，1、清网
     */
      @TableField("CQWBS")
    private String cqwbs;

      /**
     * 清网日期
     */
      @TableField("DQWRQ")
    private Long dqwrq;

      /**
     * 运力状态
     */
      @TableField("CYLZT")
    private String cylzt;

      /**
     * 区域费率
     */
      @TableField("NFL_QY")
    private Integer nflQy;

      /**
     * 历史运输商简称
     */
      @TableField("CYSSJC_LS")
    private String cyssjcLs;

      /**
     * VW卡号
     */
      @TableField("CVW")
    private String cvw;

      /**
     * A2卡号
     */
      @TableField("CA2")
    private String ca2;

      /**
     * 得分
     */
      @TableField("NDF")
    private Integer ndf;

      /**
     * GPS状态
     */
      @TableField("CGPSZT")
    private String cgpszt;

      /**
     * 不合格原因
     */
      @TableField("CBHGYY")
    private String cbhgyy;

    @TableField("CCLSX2")
    private String cclsx2;

      /**
     * 检车结果
     */
      @TableField("CJCJG")
    private String cjcjg;

      /**
     * 是否大循环
     */
      @TableField("CSFDXH")
    private String csfdxh;

      /**
     * 费率
     */
      @TableField("NFL")
    private Integer nfl;

      /**
     * 车辆归属：SYSC09D.CZDDM='CLDA-CLGS'
     */
      @TableField("CCLGS")
    private String cclgs;

      /**
     * 1表示非系统车辆
     */
      @TableField("CFXTCL")
    private String cfxtcl;

      /**
     * 停用原因
     */
      @TableField("CTYYY")
    private String ctyyy;

      /**
     * 路线代码。来源于MDAC37C.LXDM
     */
      @TableField("CLXDM")
    private String clxdm;

      /**
     * 检车标识。0未通过，1通过
     */
      @TableField("CKFP")
    private String ckfp;

      /**
     * 序号
     */
      @TableField("CXH")
    private String cxh;

      /**
     * 牵引车识别代码
     */
      @TableField("CQYCSBDM")
    private String cqycsbdm;

      /**
     * 挂车识别代码
     */
      @TableField("CGCSBDM")
    private String cgcsbdm;

      /**
     * 连接全长（行车证尺寸）字符型  50字符长度
     */
      @TableField("CLJQC1")
    private String cljqc1;

      /**
     * 结算单位代码
     */
      @TableField("CLJSDM")
    private String cljsdm;

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
     * 当后轴保养系统中的车，没有按时进行保养则在V-LMS中进行锁车
     */
      @TableField("CBYSC")
    private String cbysc;

      /**
     * 路线 （长春运力明细字段 统计）
     */
      @TableField("VLX")
    private String vlx;

      /**
     * 组合 （长春运力明细字段 统计）
     */
      @TableField("VZH")
    private String vzh;

      /**
     * 路线代码。来源于MDAC37C1.CLXDM
     */
      @TableField("CLXDM1")
    private String clxdm1;

      /**
     * 是否上TVS标识：默认0 未上；1已上 
     */
      @TableField("CTVSBS")
    private String ctvsbs;

      /**
     * 合规不合规标识 (默认不合规0；合规1)
     */
      @TableField("CSFHG")
    private String csfhg;

      /**
     * 安装稽查队
     */
      @TableField("VAZJCD")
    private String vazjcd;

      /**
     * 区域监控人员
     */
      @TableField("VQYJKRY")
    private String vqyjkry;

      /**
     * 安装登记负责人
     */
      @TableField("VAZDJFZR")
    private String vazdjfzr;

      /**
     * 时间戳。BI提数据
     */
      @TableField("DSTAMP")
    private Long dstamp;

      /**
     * 绑定TVS日期
     */
      @TableField("DTVSBSBDRQ")
    private Long dtvsbsbdrq;

      /**
     * 20181215 add by dtf 是否分拨 标识 
     */
      @TableField("CSFFB")
    private String csffb;

      /**
     * 空闲定额
     */
      @TableField("NCYDE_D")
    private Integer ncydeD;

      /**
     * 牵引车审批标识：0  未审批  1：已审批
     */
      @TableField("APPROVAL_FLAG")
    private String approvalFlag;

      /**
     * 牵引车审批人
     */
      @TableField("APPROVAL_USER")
    private String approvalUser;

      /**
     * 牵引车审批日期
     */
      @TableField("APPROVAL_DATE")
    private Long approvalDate;

      /**
     * 牵引车终审审批标识：0  未审批  1：已审批
     */
      @TableField("FINAL_APPROVAL_FLAG")
    private String finalApprovalFlag;

      /**
     * 牵引车终审审批人
     */
      @TableField("FINAL_APPROVAL_USER")
    private String finalApprovalUser;

      /**
     * 牵引车终审审批日期
     */
      @TableField("FINAL_APPROVAL_DATE")
    private Long finalApprovalDate;

      /**
     * 初审是否合格
     */
      @TableField("CCSYJ")
    private String ccsyj;

      /**
     * 初审时间
     */
      @TableField("DCSSJ")
    private Long dcssj;

      /**
     * 初审人代码
     */
      @TableField("CCSRDM")
    private String ccsrdm;

      /**
     * 头车车籍
     */
      @TableField("CTCCJ")
    private String ctccj;

      /**
     * 挂车车籍
     */
      @TableField("CGCCJ")
    private String cgccj;

      /**
     * 牵引车小组名称
     */
      @TableField("CXZMC")
    private String cxzmc;

      /**
     * 牵引车组长名称
     */
      @TableField("CZZMC")
    private String czzmc;

      /**
     * 挂车审批标识：0  未审批  1：已审批
     */
      @TableField("TRAILER_APPROVAL_FLAG")
    private String trailerApprovalFlag;

      /**
     * 挂车审批人
     */
      @TableField("TRAILER_APPROVAL_USER")
    private String trailerApprovalUser;

      /**
     * 挂车审批日期
     */
      @TableField("TRAILER_APPROVAL_DATE")
    private Long trailerApprovalDate;

      /**
     * 挂车终审审批标识：0  未审批  1：已审批
     */
      @TableField("TRAILER_FINAL_APPROVAL_FLAG")
    private String trailerFinalApprovalFlag;

      /**
     * 挂车终审审批人
     */
      @TableField("TRAILER_FINAL_APPROVAL_USER")
    private String trailerFinalApprovalUser;

      /**
     * 挂车终审审批日期
     */
      @TableField("TRAILER_FINAL_APPROVAL_DATE")
    private Long trailerFinalApprovalDate;

      /**
     * 挂车小组名称
     */
      @TableField("TRAILER_CXZMC")
    private String trailerCxzmc;

      /**
     * 挂车组长名称
     */
      @TableField("TRAILER_CZZMC")
    private String trailerCzzmc;

    @TableField("WAREHOUSE_CREATETIME")
    private Long warehouseCreatetime;

    @TableField("WAREHOUSE_UPDATETIME")
    private Long warehouseUpdatetime;


}
