package com.pzg.code.zipproject.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ApiParamJsonDto {

    /**
     * api参数名
     */
    private String api_param_name;

    /**
     * api参数位置，query,path,body
     */
    private String api_param_position;

    /**
     * api参数类型，String,int...
     */
    private String api_param_type;

    /**
     * api参数描述
     */
    private String api_param_description;

    /**
     * 是否必须
     */
    private boolean isMust;

    /**
     * 参数默认值
     */
    private String default_value;

    /**
     * back参数名
     */
    private String back_param_name;

    /**
     * back参数位置
     */
    private String back_param_position;

    /**
     * back参数类型
     */
    private String back_param_type;

    /**
     * back参数描述
     */
    private String back_param_description;

    /**
     * 参数顺序
     */
    private Integer order;
}

