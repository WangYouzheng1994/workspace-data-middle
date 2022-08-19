package com.yqwl.datamiddle.realtime.util;

import com.yqwl.datamiddle.realtime.bean.DwdBaseStationDataEpc;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.MapState;
import org.apache.flink.api.common.state.MapStateDescriptor;
import org.apache.flink.configuration.Configuration;

/**
 * @Description: 状态后端 给Cp9专用的
 * @Author: XiaoFeng
 * @Date: 2022/8/10 14:37
 * @Version: V1.0
 */
public class BackStationUtilCp9 extends RichMapFunction<DwdBaseStationDataEpc, DwdBaseStationDataEpc> {

        // 声明Map类型的状态后端
        private transient MapState<String, Long> myMapState;

        @Override
        public void open(Configuration parameters) throws Exception {
            MapStateDescriptor<String, Long> mapStateDescriptor = new MapStateDescriptor<>("vin码和操作时间", String.class, Long.class);
            myMapState = getRuntimeContext().getMapState(mapStateDescriptor);
        }

        @Override
        public DwdBaseStationDataEpc map(DwdBaseStationDataEpc dwdBaseStationDataEpc) throws Exception {
            /**
             * 1.当前状态后端的状态为Map类型,key为String,也就是汽车的Vin码,value为vin码所对应的数据,vin所对应的操作时间
             * 2.每次流到了到了这里,就会调用这里的map:
             */
            String vin = dwdBaseStationDataEpc.getVIN();                    //车架号
            Long nowOperatetime = dwdBaseStationDataEpc.getOPERATETIME();   //操作时间
            // 1):判断状态后端有无当前数据的vin码的key所对应的对象,没有就添加上
            if (myMapState.get(vin) == null) {
                myMapState.put(vin, nowOperatetime);
                dwdBaseStationDataEpc.setCP9_OFFLINE_TIME(nowOperatetime);
                return dwdBaseStationDataEpc;
            } else {
                // 2):当前'状态后端'有vin码对应的value就会判断操作时间,
                //    如果'状态后端'已有的操作时间大于'当前流数据'的操作时间则删除'状态后端'中的key(因为取的是第一条下线时间的数据).
                //    然后再把更早的下线时间存到'状态后端'中.
                Long oldOperatetime = myMapState.get(vin);
                //  Long oldOperatetime = oldDataEpc.getOPERATETIME();
                if (oldOperatetime > nowOperatetime) {
                    myMapState.remove(vin);
                    myMapState.put(vin, nowOperatetime);
                    dwdBaseStationDataEpc.setCP9_OFFLINE_TIME(nowOperatetime);
                    return dwdBaseStationDataEpc;
                } else {
                    // 3):如果'状态后端'已有的操作时间小于当前流的操作时间,就会保留当前状态后端的操作时间,且设置为DwdBaseStationDataEpc的第一次下线时间.
                    dwdBaseStationDataEpc.setCP9_OFFLINE_TIME(oldOperatetime);
                    return dwdBaseStationDataEpc;
                }
            }
        }
}
