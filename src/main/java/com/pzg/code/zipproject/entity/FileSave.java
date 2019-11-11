package com.pzg.code.zipproject.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName :  FileSave
 * @Author : PZG
 * @Date : 2019-11-08   下午 03:08
 * @Description :
 */
@Data
public class FileSave {
    /**
     * 时间戳的文件名
     */
    @ApiModelProperty(value="时间戳的文件名")
    private String timeStampFileName;

    /**
     * 原始文件名
     */
    @ApiModelProperty(value="原始文件名")
    private String originFileName;
}
