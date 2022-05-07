package com.yqwl.datamiddle.realtime.bean;
import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Objects;

@Data
public class Spti30 {
    private String cddy;
    private String cqwh;
    private String cczydm;
    private Long dczrq;
    private String vbz;
    private String czjgsdm;
    private Timestamp createTime;
    private Timestamp updateTime;


}
