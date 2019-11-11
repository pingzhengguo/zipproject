package com.pzg.code.zipproject.vo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ApiServiceDto extends ApiService {

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 服务分组名
     */
    private String groupName;

    /**
     * path前缀
     */
    private String pathPrefix;

    /**
     * 授权app名
     */
    private String appName;

    /**
     * key
     */
    private String appKey;

    /**
     * secret
     */
    private String appSecret;

    /**
     * 结果说明
     */
    private String resultParamExplain;
}

