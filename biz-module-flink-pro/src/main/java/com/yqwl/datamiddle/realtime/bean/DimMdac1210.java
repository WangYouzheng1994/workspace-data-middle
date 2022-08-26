package com.yqwl.datamiddle.realtime.bean;

import com.yqwl.datamiddle.realtime.enums.CamelUnderline;
import com.yqwl.datamiddle.realtime.enums.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

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
    private String APPROVAL_FLAG_12;

    /**
     * 审批人（来自于mdac12）
     */
    private String APPROVAL_USER_12;

    /**
     * 审批日期（来自于mdac12）
     */
    private Long APPROVAL_DATE_12;

    /**
     * 来自于mdac12
     */
    private String FINAL_APPROVAL_FLAG_12;

    /**
     * 来自于mdac12
     */
    private String FINAL_APPROVAL_USER_12;

    /**
     * 来自于mdac12
     */
    private Long FINAL_APPROVAL_DATE_12;

    /**
     * 创建时间
     */
    private Long WAREHOUSE_CREATETIME;

    /**
     * 更新时间
     */
    private Long WAREHOUSE_UPDATETIME;

    public DimMdac1210(Mdac12 mdac12, Mdac10 mdac10){
        mergeMdac12(mdac12);
        mergeMdac10(mdac10);
    }

    public void mergeMdac12(Mdac12 mdac12){
        if (mdac12 != null){
            if (StringUtils.isNotBlank(mdac12.getCCPDM())){
                this.CCPDM = mdac12.getCCPDM();
            }
            if (StringUtils.isNotBlank(mdac12.getVCPMC())){
                this.VCPMC = mdac12.getVCPMC();
            }
            if (StringUtils.isNotBlank(mdac12.getVCPJC())){
                this.VCPJC = mdac12.getVCPJC();
            }
            if (mdac12.getNAQKC() != null){
                this.NAQKC=mdac12.getNAQKC();
            }
            if (mdac12.getNPL() != null){
                this.NPL=mdac12.getNPL();
            }
            if (mdac12.getNPLZL() != null){
                this.NPLZL=mdac12.getNPLZL();
            }
            if (StringUtils.isNotBlank(mdac12.getCTYBS())){
                this.CTYBS12 = mdac12.getCTYBS();
            }
            if (mdac12.getDTYRQ() != null){
                this.DTYRQ12=mdac12.getDTYRQ();
            }
            if (StringUtils.isNotBlank(mdac12.getCPP())){
                this.CPP = mdac12.getCPP();
            }
            if (StringUtils.isNotBlank(mdac12.getCCXDL())){
                this.CCXDL = mdac12.getCCXDL();
            }
            if (StringUtils.isNotBlank(mdac12.getCCXDM())){
                this.CCXDM = mdac12.getCCXDM();
            }
            if (StringUtils.isNotBlank(mdac12.getCJHTY())){
                this.CJHTY = mdac12.getCJHTY();
            }
            if (mdac12.getDJHTYRQ() != null){
                this.DJHTYRQ=mdac12.getDJHTYRQ();
            }
            if (StringUtils.isNotBlank(mdac12.getCDDTY())){
                this.CDDTY = mdac12.getCDDTY();
            }
            if (mdac12.getDDDTYRQ() != null){
                this.DDDTYRQ=mdac12.getDDDTYRQ();
            }
            if (StringUtils.isNotBlank(mdac12.getCLSTY())){
                this.CLSTY = mdac12.getCLSTY();
            }
            if (mdac12.getDLSTYRQ() != null){
                this.DLSTYRQ=mdac12.getDLSTYRQ();
            }
            if (mdac12.getNCCFDJ() != null){
                this.NCCFDJ=mdac12.getNCCFDJ();
            }
            if (StringUtils.isNotBlank(mdac12.getCPHBS())){
                this.CPHBS = mdac12.getCPHBS();
            }
            if (StringUtils.isNotBlank(mdac12.getCJHDBBS())){
                this.CJHDBBS = mdac12.getCJHDBBS();
            }
            if (StringUtils.isNotBlank(mdac12.getVBZ())){
                this.VBZ = mdac12.getVBZ();
            }
            if (StringUtils.isNotBlank(mdac12.getCJKBS())){
                this.CJKBS = mdac12.getCJKBS();
            }
            if (mdac12.getID() != null){
                this.ID12=mdac12.getID();
            }
            if (StringUtils.isNotBlank(mdac12.getVMS())){
                this.VMS = mdac12.getVMS();
            }
            if (mdac12.getNFWFDJ() != null){
                this.NFWFDJ=mdac12.getNFWFDJ();
            }
            if (mdac12.getNKLSZ() != null){
                this.NKLSZ=mdac12.getNKLSZ();
            }
            if (StringUtils.isNotBlank(mdac12.getCXSTY())){
                this.CXSTY = mdac12.getCXSTY();
            }
            if (StringUtils.isNotBlank(mdac12.getCGS())){
                this.CGS12 = mdac12.getCGS();
            }
            if (StringUtils.isNotBlank(mdac12.getAPPROVAL_FLAG())){
                this.APPROVAL_FLAG_12 = mdac12.getAPPROVAL_FLAG();
            }
            if (StringUtils.isNotBlank(mdac12.getAPPROVAL_USER())){
                this.APPROVAL_USER_12 = mdac12.getAPPROVAL_FLAG();
            }
            if (mdac12.getAPPROVAL_DATE() != null){
                this.APPROVAL_DATE_12=mdac12.getAPPROVAL_DATE();
            }
            if (StringUtils.isNotBlank(mdac12.getFINAL_APPROVAL_FLAG())){
                this.FINAL_APPROVAL_FLAG_12 = mdac12.getFINAL_APPROVAL_FLAG();
            }
            if (StringUtils.isNotBlank(mdac12.getFINAL_APPROVAL_USER())){
                this.FINAL_APPROVAL_USER_12 = mdac12.getFINAL_APPROVAL_USER();
            }
            if (mdac12.getFINAL_APPROVAL_DATE() != null){
                this.FINAL_APPROVAL_DATE_12=mdac12.getFINAL_APPROVAL_DATE();
            }
        }
    }

    public void mergeMdac10(Mdac10 mdac10){

    }

}