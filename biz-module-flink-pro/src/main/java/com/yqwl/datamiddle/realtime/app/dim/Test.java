package com.yqwl.datamiddle.realtime.app.dim;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
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

        System.out.println("================================");

        String json = "{\"database\":\"TDS_LJ\",\"before\":{},\"after\":{\"CJHBS\":\"0\",\"VYDDH\":\"18335478582\",\"WAREHOUSE_UPDATETIME\":\"1653362665966\",\"CCXBS\":\"1\",\"CJKBS\":\"0\",\"CFWTY\":\"0\",\"FINAL_APPROVAL_FLAG\":\"0\",\"CTYBS\":\"0\",\"NFYJL\":\"0.00\",\"CSYBDK\":\"0\",\"CDHDDM\":\"H05297\",\"WAREHOUSE_CREATETIME\":\"1653362665966\",\"VJXSJC\":\" \",\"CXLDM_QD\":\"0\",\"NFKQX\":0,\"NKHFS\":0,\"CDMS\":\"0\",\"NXYED\":\"0.00\",\"CXSTY\":\"0\",\"ID\":\"0\",\"VDH\":\"18335478582\",\"NFPSL\":\"0.00\",\"DSTAMP\":1572723546095082,\"CBJGLKC\":\"0\",\"CZJGSDM\":\"2\",\"APPROVAL_FLAG\":\"0\",\"NZYXYED\":\"0.00\",\"CXYJC\":\"0\",\"CSXDM\":\"08\",\"CXLDM_CD\":\"0\",\"NFPXE\":\"0.00\",\"NFYTQQ\":\"0.00\",\"VDZ\":\"山西省晋中市介休市绿城华府工地\",\"CGS\":\"CC\",\"NBJXYED\":\"0.00\",\"VJXSMC\":\"江建军\",\"CDKHBS\":\"0\",\"CFWZSTY\":\"0\",\"CJXSDM\":\"341227197304289010\",\"CSQDM\":\"JN\",\"CBJEJXS\":\"0\"},\"type\":\"insert\",\"tableName\":\"MDAC22\",\"ts\":1653362665966}";
        JSONObject jsonObject = JSON.parseObject(json);
        String after = jsonObject.getString("after");
        System.out.println(after);

    }

}
