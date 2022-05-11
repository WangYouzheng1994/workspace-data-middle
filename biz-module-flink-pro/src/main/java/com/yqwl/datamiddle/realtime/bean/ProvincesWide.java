package com.yqwl.datamiddle.realtime.bean;

import com.yqwl.datamiddle.realtime.enums.TransientSink;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.sql.Timestamp;
@Data
@ToString
public class ProvincesWide {

    private Long idnum;
    private String csqdm;
    private String cdsdm;

    private String csxdm;

//在原视图的基础上新增的一条联合的数据:省区市县代码
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
    //新加kafka的ts时间戳
    @TransientSink
    private Timestamp ts;
    //新加合并表的字段
    @TransientSink
    private String cdsdm08;


    /*     sysc07 :  csqdm  vsqmc cjc2 cjc cdqdm  cwlmc cwlbm  a.cwlbm cwlbm_sq
       b: csxdm,  vsxmc, cwlbm cwlbm3 ,cwlmc cwlmc3, njd,nwd
 * 合并sysc07和sysc08的数据
 */
    public ProvincesWide(Sysc07 sysc07Info,Sysc08 sysc08Info){
        mergeSysc07(sysc07Info);
        mergeSysc08(sysc08Info);
    }
    public ProvincesWide(ProvincesWide provincesWide,Sysc09 sysc09){

        mergeSysc09(sysc09);
    }
    public ProvincesWide(ProvincesWide provincesWide,Mdac01 mdac01){
//        System.out.println("Wide+mdac01");
        mergeProvincesWide(provincesWide);
        mergeMdac01(mdac01);
    }

    public void mergeProvincesWide(ProvincesWide provincesWide2){
        System.out.println("Wide:"+provincesWide2.toString());
        if (provincesWide2!=null){

            if (provincesWide2.getTs() !=null){
                this.ts=provincesWide2.getTs();
                Timestamp ts = provincesWide2.getTs();
                Long time = ts.getTime();
                this.warehouseCreatetime=time;
            }
//            提前给nvl( d.cdsdm, b.csxdm ) cdsdm 中的cdsdm给赋值
            if (provincesWide2.getCdsdm() !=null){
                this.cdsdm=provincesWide2.getCdsdm();
            }
//             提前给nvl( d.vdsmc, b.vsxmc ) vdsmc 中的vdsmc给赋值
            if (provincesWide2.getVdsmc() !=null){
                this.vdsmc=provincesWide2.getVdsmc();
            }
//            给新加的字段给赋值:
            if (provincesWide2.getCsqdm() !=null && provincesWide2.getCsxdm() !=null){
                this.sqsxdm=provincesWide2.getCsqdm()+provincesWide2.getCsxdm();
            }

            if (provincesWide2.getCsqdm() !=null){
                this.csqdm=provincesWide2.getCsqdm();
            }
            if (provincesWide2.getVsqmc() !=null){
                this.vsqmc=provincesWide2.getVsqmc();
            }
            if (provincesWide2.getVsqjc() !=null){
                this.vsqjc=provincesWide2.getVsqjc();
            }
            if (provincesWide2.getCjc() !=null){
                this.cjc=provincesWide2.getCjc();
            }
            if (provincesWide2.getCdqdm() !=null){
                this.cdqdm=provincesWide2.getCdqdm();
            }
            if (provincesWide2.getCwlmc() !=null){
                this.cwlmc=provincesWide2.getCwlmc();
            }
            if (provincesWide2.getCwlbmSq() !=null){
                this.cwlbmSq=provincesWide2.getCwlbmSq();
            }
            if (provincesWide2.getCsxdm() !=null){
                this.csxdm=provincesWide2.getCsxdm();
            }
            if (provincesWide2.getVsxmc() !=null){
                this.vsxmc=provincesWide2.getVsxmc();
            }
            if (provincesWide2.getCwlbm3() !=null){
                this.cwlbm3=provincesWide2.getCwlbm3();
            }
            if (provincesWide2.getCwlmc() !=null){
                this.cwlmc3=provincesWide2.getCwlmc();
            }
            if (provincesWide2.getNjd() !=null){
                this.njd=provincesWide2.getNjd();
            }
            if (provincesWide2.getNwd() !=null){
                this.nwd=provincesWide2.getNwd();
            }
            if (provincesWide2.getCdsdm08() !=null){
                this.cdsdm08=provincesWide2.getCdsdm08();
            }

        }
    }

    /*
        合并Sysc07
     */
    public void mergeSysc07(Sysc07 sysc07Info){
        if (sysc07Info !=null){
            this.ts=sysc07Info.getTs();
            if (sysc07Info.getCsqdm() !=null){
                this.csqdm=sysc07Info.getCsqdm();
            }
            if (sysc07Info.getVsqmc() !=null){
                this.vsqmc=sysc07Info.getVsqmc();
            }
            if (sysc07Info.getCjc2() !=null){
                this.vsqjc=sysc07Info.getCjc2();
            }
            if (sysc07Info.getCjc() !=null){
                this.cjc=sysc07Info.getCjc();
            }
            if (sysc07Info.getCdqdm() !=null){
                this.cdqdm=sysc07Info.getCdqdm();
            }
            if (sysc07Info.getCwlmc() !=null){
                this.cwlmc=sysc07Info.getCwlmc();
            }
            if (sysc07Info.getCwlbm() !=null){
                this.cwlbmSq=sysc07Info.getCwlbm();
            }

        }
    }
    /*
        合并Sysc08
     */
    public void mergeSysc08(Sysc08 sysc08Info){

        if (sysc08Info != null){
//            提前给nvl( d.cdsdm, b.csxdm ) cdsdm 中的cdsdm给赋值
            if (sysc08Info.getCsxdm() !=null){
                this.cdsdm=sysc08Info.getCsxdm();
            }
            if (sysc08Info.getCsxdm() !=null){
                this.csxdm=sysc08Info.getCsxdm();
            }
            if (sysc08Info.getVsxmc() !=null){
                this.vsxmc=sysc08Info.getVsxmc();
//             提前给nvl( d.vdsmc, b.vsxmc ) vdsmc 中的vdsmc给赋值
                this.vdsmc=sysc08Info.getVsxmc();
            }
            if (sysc08Info.getCwlbm() !=null){
                this.cwlbm3=sysc08Info.getCwlbm();
            }
            if (sysc08Info.getCwlmc() !=null){
                this.cwlmc3=sysc08Info.getCwlmc();
            }
            if (sysc08Info.getNjd() !=null){
                this.njd=sysc08Info.getNjd();
            }
            if (sysc08Info.getNwd() !=null){
                this.nwd=sysc08Info.getNwd();
            }
            if (sysc08Info.getCdsdm() !=null){
                this.cdsdm08=sysc08Info.getCdsdm();
            }
        }
    }
    public void mergeSysc09(Sysc09 sysc09Info){
        if (sysc09Info !=null){
            this.cdsdm=sysc09Info.getCdsdm();
            this.vdsmc=sysc09Info.getVdsmc();
        }
    }
    public void mergeMdac01(Mdac01 mdac01Info){
        if (mdac01Info !=null){
            this.vdqmc=mdac01Info.getVdqmc();
        }
    }

}
