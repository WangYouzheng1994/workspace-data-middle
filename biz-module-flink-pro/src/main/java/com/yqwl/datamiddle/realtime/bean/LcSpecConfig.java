
package com.yqwl.datamiddle.realtime.bean;
import lombok.Data;
import java.sql.Timestamp;

@Data
public class LcSpecConfig {

    private Integer  idnum;
    private Integer versionCode;

    private String hostComCode;

    private String hostComName;

    private String specCode;

    private String specName;

    private String baseCode;

    private String baseName;

    private String distCode;

    private String distName;

    private String transModeCode;

    private String transModeName;

    private String startCalNodeCode;

    private String startCalNodeName;

    private String endCalNodeCode;

    private String endCalNodeName;

    private String standardHours;

    private Integer status;

    private String creator;

    private Long createTime;

    private Long isLastTime;

    private String csrxm;

    private Long csrq;

    private String zsrxm;

    private Long zsrq;

    private Timestamp createTime1;

    private Timestamp updateTime;


}
