package com.pzg.code.zipproject.mapper;

import com.pzg.code.zipproject.vo.ApiServiceDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ApiServiceMapper {


    List<ApiServiceDto> getAllAuthApiByApp(@Param("appId") Integer appId);

}
