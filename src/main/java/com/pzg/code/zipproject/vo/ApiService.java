package com.pzg.code.zipproject.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 能力api服务实体类，面向网关访问
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ApiService {

    /**
     * 能力服务id
     */
    private Integer apiServiceId;

    /**
     * 能力分组id
     */
    @NotNull(message = "分组id不为空")
    private Long apiGroupId;

    /**
     * api服务名称
     */
    @NotBlank(message = "api服务名称不为空")
    @Size(max = 100, message = "api服务名称最高100位")
    private String apiServiceName;

    /**
     * 服务类型，公开或私有
     */
    @NotBlank(message = "api服务类型不为空")
    private String type;

    /**
     * 描述
     */
    @NotBlank(message = "描述必填")
    @Size(max = 255, message = "描述最高255位")
    private String description;

    /**
     * 请求协议
     */
    @NotBlank(message = "请求协议必填")
    @Size(max = 20, message = "请求协议最高20位")
    private String protocol;

    /**
     * 请求方法类型
     */
    @NotBlank(message = "请求方法类型必填")
    @Size(max = 20, message = "请求方法类型最高20位")
    private String method;

    /**
     * 请求路径
     */
    @NotBlank(message = "请求路径必填")
    @Size(max = 500, message = "请求路径最高500位")
    private String requestPath;

    /**
     * 是否缓存
     */
    @NotNull(message = "是否缓存必填")
    private Integer isCached;

    /**
     * 后端服务id
     */
    @NotNull(message = "后端服务id必填")
    private Long serviceId;

    /**
     * 参数集合
     */
    @Size(max = 500, message = "参数最高500位")
    private String paramJson;

    /**
     * 是否删除
     */
    private Integer isDeleted;

}

