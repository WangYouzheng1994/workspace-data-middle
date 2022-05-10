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
 * 操作员对应基地授权
 * </p>
 *
 * @author yiqi
 * @since 2022-05-10
 */
@Data
  @EqualsAndHashCode(callSuper = false)
    @TableName("ods_vlms_spti30")
public class Spti30 implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "IDNUM", type = IdType.AUTO)
      private Long idnum;

    @TableField("CDDY")
    private String cddy;

      /**
     * 区位号。0431、022、027、028、0757表示生产的基地（2013-10-12储运部会议上确定）czddm = 'SPTDZJD'
     */
      @TableField("CQWH")
    private String cqwh;

    @TableField("CCZYDM")
    private String cczydm;

    @TableField("DCZRQ")
    private Long dczrq;

    @TableField("VBZ")
    private String vbz;

      /**
     * 主机公司代码。字典：WTDW 必输。权限来自SPTI33
     */
      @TableField("CZJGSDM")
    private String czjgsdm;

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
