package com.pzg.code.zipproject.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@ToString
public class ApiGeneratorDto {

    private String apiServiceName;

    private List<Map<String, String>> paramList;

    private String requestPath;

    private String method;

    private String protocol;

    private String appKey;

    private String appSecret;

    private String funcName;

}

