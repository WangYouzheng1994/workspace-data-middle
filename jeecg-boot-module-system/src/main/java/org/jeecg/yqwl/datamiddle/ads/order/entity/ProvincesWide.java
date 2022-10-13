package org.jeecg.yqwl.datamiddle.ads.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@ToString
@TableName("dim_vlms_provinces")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class ProvincesWide {

    private Long IDNUM;

    private String csqdm;

    private String cdsdm;

    private String csxdm;

    private String sqsxdm;

    private String vsqmc;

    private String vsqjc;

    private String vdsmc;

    private String vsxmc;

    private String cjc;

    private String cdqdm;

    private String vdqmc;

    private String cwlbm3;

    private String cwlmc3;

    private BigDecimal njd;

    private BigDecimal nwd;

    private String cwlmc;

    private String cwlbmSq;

    private Long warehouseCreatetime;

    private Long warehouseUpdatetime;



}
