package com.yqwl.datamiddle.realtime.app.dim;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.yqwl.datamiddle.realtime.bean.DwdSptb02;
import com.yqwl.datamiddle.realtime.bean.Sptb02;
import com.yqwl.datamiddle.realtime.bean.Sysc07;
import com.yqwl.datamiddle.realtime.util.JsonPartUtil;

import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Test {

    public static void main(String[] args) {
        Sptb02 sptb02 = new Sptb02();
        Sptb02 bean = JsonPartUtil.getBean(sptb02);
        System.out.println(bean);
        System.out.println("=============================");

        DwdSptb02 dwdSptb02 = new DwdSptb02();

        Class<? extends DwdSptb02> aClass = dwdSptb02.getClass();
        Class<?>[] declaredClasses = aClass.getDeclaredClasses();
        System.out.println("declaredClasses:" + declaredClasses.length);
        Field[] declaredFields = aClass.getDeclaredFields();
        System.out.println("declaredFields的数量:" + declaredFields.length);


        BeanUtil.copyProperties(sptb02, dwdSptb02);
        dwdSptb02.setCARTYPE("dsfadsf");
        dwdSptb02.setTRAFFIC_TYPE("G");

        System.out.println(sptb02);
        System.out.println(dwdSptb02);

        //格式化时间
        //1651830021955
        //1651935684693477
        Long time = 1651830021955L;
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String millisecondStrings = formatter.format(date);
        System.out.println(millisecondStrings);
        DateTime parse = DateUtil.parse(millisecondStrings);
        System.out.println(parse);
        DateTime newDate3 = DateUtil.offsetHour(parse, Integer.parseInt("2"));
        System.out.println(newDate3);

        String SHIPMENT_WAREHOUSE = "Shipping warehouse";
        String TRANSPORT_NAME = "Shipping warehouse";

        System.out.println("===============================");
        Sysc07 sysc07 = new Sysc07();
        sysc07.setBATCHNO(1);
        sysc07.setCJC("adfadf");
        sysc07.setCSHDM("adfadf");
        sysc07.setTs(new Timestamp(System.currentTimeMillis()));
        String s = JSON.toJSONString(sysc07);
        System.out.println(s);


    }

}
