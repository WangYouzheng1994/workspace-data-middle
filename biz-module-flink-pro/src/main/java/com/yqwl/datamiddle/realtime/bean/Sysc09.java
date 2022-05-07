package com.yqwl.datamiddle.realtime.bean;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Data
public class Sysc09 {
    private  int IDNUM;
    private String cdsdm;
    private String csqdm;
    private String vdsmc;
    private String vbz;
    private Long dstamp;
    private String cwlbm;
    private String cwlmc;
    private Timestamp createTime;
    private Timestamp updateTime;


}
