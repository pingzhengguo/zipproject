package com.pzg.code.zipproject.service;

import javax.servlet.http.HttpServletResponse;

/**
 * @ClassName :  ZipService
 * @Author : PZG
 * @Date : 2019-11-11   上午 10:55
 * @Description :
 */
public interface ZipService {


    Object downloadExecuteCode(Integer appId, String appName, HttpServletResponse response);

}
