package com.yqwl.datamiddle.realtime.beanmapper;

import com.yqwl.datamiddle.realtime.bean.DwdSptb02;
import com.yqwl.datamiddle.realtime.bean.Sptb02;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface Sptb02Mapper {

    Sptb02Mapper INSTANCT = Mappers.getMapper(Sptb02Mapper.class);

    DwdSptb02 conver(Sptb02 person);

}