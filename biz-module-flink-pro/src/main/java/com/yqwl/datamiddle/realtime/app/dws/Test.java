package com.yqwl.datamiddle.realtime.app.dws;

import com.alibaba.fastjson.JSONObject;
import com.yqwl.datamiddle.realtime.util.JsonPartUtil;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

public class Test {


    public static void main(String[] args) throws Exception {
        long timestamp = System.currentTimeMillis();
        LocalDate localDate = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDate();
        System.out.println(localDate);
        LocalDateTime localDateTime = Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        System.out.println(localDateTime);



        String highwayWarehouseType =  "";

    }
}
