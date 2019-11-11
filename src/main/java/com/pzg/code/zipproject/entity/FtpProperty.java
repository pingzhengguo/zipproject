package com.pzg.code.zipproject.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 描述：ftp服务器的信息
 */
@Data
public class FtpProperty {


    public FtpProperty() {
        super();
    }

    public FtpProperty(String host, int port, String username, String password, String filepath) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.filepath = filepath;
    }

    @ApiModelProperty(value = "主机名")
    private String host;

    @ApiModelProperty(value = "端口号")
    private int port;

    @ApiModelProperty(value = "用户名")
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    @ApiModelProperty(value = "文件夹路径")
    private String filepath;

}
