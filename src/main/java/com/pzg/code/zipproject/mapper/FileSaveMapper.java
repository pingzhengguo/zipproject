package com.pzg.code.zipproject.mapper;

import com.pzg.code.zipproject.entity.FileSave;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName :  FileSaveMapper
 * @Author : PZG
 * @Date : 2019-11-11   上午 10:32
 * @Description :
 */

@Mapper
public interface FileSaveMapper {

    int insert(FileSave record);

    FileSave selectByPrimaryKey(String timeStampFileName);

}
