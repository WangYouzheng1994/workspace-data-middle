package org.jeecg.modules.dabao;

import org.jeecg.JeecgSystemApplication;
import org.jeecg.yqwl.datamiddle.ads.order.service.IMysqlDwmVlmsSptb02Service;
import org.jeecg.yqwl.datamiddle.ads.order.vo.DimProvinceVo;
import org.jeecg.yqwl.datamiddle.util.AddressLocationUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = JeecgSystemApplication.class)
public class ProvinceTest {
    @Resource
    IMysqlDwmVlmsSptb02Service mysqlDwmVlmsSptb02Service;

    @Test
    public void writeNjd(){
        List<DimProvinceVo> proVinceVos = mysqlDwmVlmsSptb02Service.getProVinceVo();

        proVinceVos.forEach(item -> {
            System.out.println(item);
            if (item.getVsxmc().equals("松岭区")){
                item.setVsxmc("黑龙江省大兴安岭地区松岭区");
            }
            if (item.getVsxmc().equals("改貌")){
                item.setVsxmc("改貌站");
            }
            String lonAndLatByAddress = AddressLocationUtil.getLonAndLatByAddress(item.getVsxmc());
            String[] split = lonAndLatByAddress.split(",");
            item.setNjd(new BigDecimal(split[0]));
            item.setNwd(new BigDecimal(split[1]));
            System.out.println(item);
        });
        mysqlDwmVlmsSptb02Service.updateProvince(proVinceVos);
    }
}
