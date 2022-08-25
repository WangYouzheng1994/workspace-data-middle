package com.yqwl.datamiddle.realtime.bean;

import com.yqwl.datamiddle.realtime.enums.CamelUnderline;
import com.yqwl.datamiddle.realtime.enums.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * dim_vlms_mdac1210实体类
 * @author dabao
 * @date 2022/8/25
 */
@CamelUnderline(isChange = false)
@TableName("dim_vlms_mdac1210")
@Data
@EqualsAndHashCode(callSuper = false)
public class DimMdac1210 implements Serializable {
    /**
     * id自增
     */
    private Long IDNUM;

    /**
     * 
     */
    private String CPP;

    /**
     * 
     */
    private String VPPSM;

    /**
     * 来自于mdac10
     */
    private String CTYBS10;

    /**
     * 来自于mdac10
     */
    private Long DTYRQ10;

    /**
     * 对应委托单位.与字典表SYSC09D对应CZDDM = 'WTDW'
     */
    private Long NGSDJ;

    /**
     * 排序
     */
    private String CPX;

    /**
     * 计划标识.0不检查，1/检查
     */
    private String CFHBS;

    /**
     * ID（来自于mdac10）
     */
    private Long ID10;

    /**
     * 
     */
    private String CCQCK;

    /**
     * 
     */
    private String CWLWZ;

    /**
     * 主机厂公司（来自于mdac10）
     */
    private String CGS10;

    /**
     * 审批标识：0  未审批  1：已审批（来自于mdac10）
     */
    private String APPROVALFLAG10;

    /**
     * 审批人（来自于mdac10）
     */
    private String APPROVALUSER10;

    /**
     * 审批日期（来自于mdac10）
     */
    private Long APPROVALDATE10;

    /**
     * 终审审批标识：0  未审批  1：已审批（来自于mdac10）
     */
    private String FINALAPPROVALFLAG10;

    /**
     * 终审审批人（来自于mdac10）
     */
    private String FINALAPPROVALUSER10;

    /**
     * 终审审批日期（来自于mdac10）
     */
    private Long FINALAPPROVALDATE10;

    /**
     * 产品代码
     */
    private String CCPDM;

    /**
     * 产品名称
     */
    private String VCPMC;

    /**
     * 
     */
    private String VCPJC;

    /**
     * 
     */
    private Integer NAQKC;

    /**
     * 
     */
    private Integer NPL;

    /**
     * 
     */
    private Integer NPLZL;

    /**
     * 来自于mdac12
     */
    private String CTYBS12;

    /**
     * 来自于mdac12
     */
    private Long DTYRQ12;

    /**
     * 识别是否为新能源 (E7,E6)
     */
    private String CCXDL;

    /**
     * 20190404 30-40 红旗车型33  40-100 20210528
     */
    private String CCXDM;

    /**
     * 
     */
    private String CJHTY;

    /**
     * 
     */
    private Long DJHTYRQ;

    /**
     * 
     */
    private String CDDTY;

    /**
     * 
     */
    private Long DDDTYRQ;

    /**
     * 
     */
    private String CLSTY;

    /**
     * 
     */
    private Long DLSTYRQ;

    /**
     * 
     */
    private BigDecimal NCCFDJ;

    /**
     * 
     */
    private String CPHBS;

    /**
     * 
     */
    private String CJHDBBS;

    /**
     * 
     */
    private String VBZ;

    /**
     * 
     */
    private String CJKBS;

    /**
     * 来自于mdac12
     */
    private Long ID12;

    /**
     * 描述
     */
    private String VMS;

    /**
     * 
     */
    private Integer NFWFDJ;

    /**
     * 库龄设置，单位为天
     */
    private Integer NKLSZ;

    /**
     * 
     */
    private String CXSTY;

    /**
     * 多公司模式下的公司-SPTC60（来自于mdac12）
     */
    private String CGS12;

    /**
     * 审批标识：0  未审批  1：已审批（来自于mdac12）
     */
    private String APPROVALFLAG12;

    /**
     * 审批人（来自于mdac12）
     */
    private String approvaluser12;

    /**
     * 审批日期（来自于mdac12）
     */
    private Long APPROVALDATE12;

    /**
     * 来自于mdac12
     */
    private String FINALAPPROVALFLAG12;

    /**
     * 来自于mdac12
     */
    private String FINALAPPROVALUSER12;

    /**
     * 来自于mdac12
     */
    private Long FINALAPPROVALDATE12;

    /**
     * 创建时间
     */
    private Long WAREHOUSECREATETIME;

    /**
     * 更新时间
     */
    private Long WAREHOUSEUPDATETIME;

}