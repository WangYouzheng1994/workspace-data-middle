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
 * 运单STD导入
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_sptb02_std_import")
public class Sptb02StdImport implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

    @TableField("ID")
    private Integer id;

      /**
     * 结算单编号（作废了）
     */
      @TableField("CJSDBH")
    private String cjsdbh;

      /**
     * 配载单编号
     */
      @TableField("CPZDBH")
    private String cpzdbh;

      /**
     * 清单号
     */
      @TableField("VWXDWDM")
    private String vwxdwdm;

      /**
     * 任务单号
     */
      @TableField("CJHDH")
    private String cjhdh;

      /**
     * 运输方式  公路:G 铁路：T或L1  水路 :S  集港 ：J  水运短拨：SD 铁路短拨：TD （作废了）
     */
      @TableField("VYSFS")
    private String vysfs;

      /**
     * SD:收单  SCSJ:试乘试驾
     */
      @TableField("TYPE")
    private String type;

      /**
     * 创建人
     */
      @TableField("CREATE_BY")
    private String createBy;

      /**
     * 创建人姓名
     */
      @TableField("CREATE_BY_NAME")
    private String createByName;

      /**
     * 创建时间
     */
      @TableField("CREATE_DATE")
    private Long createDate;

      /**
     * 收单时间
     */
      @TableField("SD_DATE")
    private Long sdDate;

      /**
     * 监控理论到货时间
     */
      @TableField("VDHZSX_GPS")
    private Long vdhzsxGps;

      /**
     * 监控到货时间
     */
      @TableField("DGPSDHSJ")
    private Long dgpsdhsj;

      /**
     * 审核人
     */
      @TableField("APPROVER_USER")
    private String approverUser;

      /**
     * 大众审核时间 20210430 DTF
     */
      @TableField("DSHSJ_DZ")
    private Long dshsjDz;

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
