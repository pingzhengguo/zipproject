package com.pzg.code.zipproject.controller;

import com.pzg.code.zipproject.entity.FileSave;
import com.pzg.code.zipproject.entity.FtpProperty;
import com.pzg.code.zipproject.mapper.FileSaveMapper;
import com.pzg.code.zipproject.utils.FtpUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Date;

/**
 * @ClassName :  FileController
 * @Author : PZG
 * @Date : 2019-11-08   下午 03:05
 * @Description :
 */
@RestController
@RequestMapping("/file")
public class FileController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileSaveMapper fileSaveMapper;

    private String mode = "local";

    private String folder = "/home/webapp/haiyun/uploadfile";

    private String host = "192.168.170.131";

    private int port = 8080;

    private String username = "username";

    private String password = "password";

    private String filepath = "/pub";

    /**
     * @param file
     * @return
     * @throws Exception
     * @Title: update
     * @Description: TODO(上传文件)
     */
    @PostMapping(headers = "content-type=multipart/form-data")
    @ApiOperation(value = "上传文件", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiImplicitParam(name = "file", value = "文件流对象,接收数组格式", required = true, dataType = "MultipartFile", allowMultiple = true)
    public Object update(@ApiParam(value = "world文件", required = true) MultipartFile file)
            throws Exception {
        String timeStampName = new Date().getTime() + "." + StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
        if ("local".equals(mode)) {
            File localFile = new File(folder, timeStampName);
            // 把传入的文件写到本地文件
            InputStream in = null;
            OutputStream out = null;
            try {
                in = file.getInputStream();
                out = new FileOutputStream(localFile);
                byte[] b = new byte[1024];
                int len;
                while ((len = in.read(b)) != -1) {
                    out.write(b, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                in.close();
                out.close();
            }

        } else {
            // 把传入的文件写到指定的ftp服务器
            FtpProperty ftpProperty = new FtpProperty(host, port, username, password, filepath);
            boolean result = FtpUtils.uploadToFtp(file.getInputStream(), timeStampName, ftpProperty, false);
            if (!result) {
                LOGGER.error("上传失败！");
            }
        }
        // 文件上传信息保存到数据库
        FileSave fileSave = new FileSave();
        fileSave.setTimeStampFileName(timeStampName);
        fileSave.setOriginFileName(file.getOriginalFilename());
        fileSaveMapper.insert(fileSave);
        return fileSave;
    }

    /**
     * @param timeStampFileName
     * @param response
     * @return void    返回类型
     * @Title: download
     * @Description: TODO(文件的下载)
     */
    @GetMapping("/{timeStampFileName:.+}") // id为时间戳
    @ApiOperation(value = "下载文件", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void download(@PathVariable("timeStampFileName") String timeStampFileName, HttpServletResponse response) {
        InputStream inputStream = null;
        FtpProperty ftpProperty = new FtpProperty(host, port, username, password, filepath);
        try {
            if ("local".equals(mode)) {
                inputStream = new FileInputStream(new File(folder, timeStampFileName));
            } else {
                inputStream = FtpUtils.downloadFile(timeStampFileName, ftpProperty);
            }
            OutputStream outputStream = response.getOutputStream();
            // 指明为下载
            response.setContentType("application/x-download");
            String originFileName = fileSaveMapper.selectByPrimaryKey(timeStampFileName).getOriginFileName();
            response.addHeader("Content-Disposition",
                    "attachment;fileName=" + new String(originFileName.getBytes("UTF-8"), "iso-8859-1")); // 设置文件名
            // 把输入流copy到输出流
            IOUtils.copy(inputStream, outputStream);
            outputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if ("ftp".equals(mode)) {
                FtpUtils.closeConnect();
            }
        }

    }

}
