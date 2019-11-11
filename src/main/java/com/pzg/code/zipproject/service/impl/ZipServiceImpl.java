package com.pzg.code.zipproject.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.pzg.code.zipproject.mapper.ApiServiceMapper;
import com.pzg.code.zipproject.service.ZipService;
import com.pzg.code.zipproject.utils.CommonUtils;
import com.pzg.code.zipproject.utils.FastDFSUtils;
import com.pzg.code.zipproject.utils.FileUtils;
import com.pzg.code.zipproject.utils.FreemarkerConfigUtils;
import com.pzg.code.zipproject.vo.ApiGeneratorDto;
import com.pzg.code.zipproject.vo.ApiParamJsonDto;
import com.pzg.code.zipproject.vo.ApiServiceDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipServiceImpl implements ZipService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipServiceImpl.class);

    @Autowired
    private ApiServiceMapper apiServiceMapper;

    private String apiDebugUrl = "http://localhost:8105";

    /**
     * sdk生成路径
     */
    private String writeServerPath = "E:/test/code/";

    /**
     * 源jar包路径
     */
    private String originalJarPath = "E:/test/sdk-core-java-0.0.5.jar";

    /**
     * 源文档路径
     */
    private String originalDocPath = "E:/test/Readme.zh.md";

    /**
     * 用户下载到本地路径
     */
    private String downloadPath = "E:/test/zip";

    private String zipPath = "http://10.0.91.222:8999/";

    /**
     * 下载sdk
     *
     * @param appId
     * @return
     */
    @Override
    public Object downloadExecuteCode(Integer appId, String appName, HttpServletResponse response) {
        try {
            //获取信息生成代码
            List<ApiServiceDto> apiServiceDtos = apiServiceMapper.getAllAuthApiByApp(appId);
            if (apiServiceDtos.isEmpty()) {
                LOGGER.error("该app不存在发布线上，且授权有效的api");
            }
            List<ApiGeneratorDto> httpApiGeneratorList = new ArrayList<>();
            List<ApiGeneratorDto> httpsApiGeneratorList = new ArrayList<>();
            apiServiceDtos.forEach(apiServiceDto -> {
                ApiGeneratorDto apiGeneratorDto = new ApiGeneratorDto();
                apiGeneratorDto.setProtocol(apiServiceDto.getProtocol().toUpperCase());
                apiGeneratorDto.setApiServiceName(apiServiceDto.getApiServiceName());
                apiGeneratorDto.setMethod(apiServiceDto.getMethod().toUpperCase());
                apiGeneratorDto.setRequestPath(apiServiceDto.getPathPrefix() + apiServiceDto.getRequestPath());
                apiGeneratorDto.setAppKey(apiServiceDto.getAppKey());
                apiGeneratorDto.setAppSecret(apiServiceDto.getAppSecret());
                apiGeneratorDto.setFuncName(CommonUtils.randomLowString(6));
                List<Map<String, String>> paramList = new ArrayList<>();
                List<ApiParamJsonDto> apiParamJsonDtos = JSONObject.parseArray(apiServiceDto.getParamJson(), ApiParamJsonDto.class);
                if (apiParamJsonDtos != null && apiParamJsonDtos.size() > 0) {
                    apiParamJsonDtos.forEach(apiParamJson -> {
                        Map<String, String> paramMap = new HashMap<>(16);
                        paramMap.put("paramName", apiParamJson.getApi_param_name());
                        paramMap.put("paramType", apiParamJson.getApi_param_type());
                        paramMap.put("paramPosition", apiParamJson.getApi_param_position().toUpperCase());
                        paramList.add(paramMap);
                    });
                }
                apiGeneratorDto.setParamList(paramList);
                String protocol = apiServiceDto.getProtocol().toUpperCase();
                switch (protocol) {
                    case "HTTP":
                        httpApiGeneratorList.add(apiGeneratorDto);
                        break;
                    case "HTTPS":
                        httpsApiGeneratorList.add(apiGeneratorDto);
                        break;
                    default:
                        return;
                }
            });
            apiDebugUrl = apiDebugUrl.substring(apiDebugUrl.lastIndexOf("/") + 1);
            Map<String, Object> dataMap = new HashMap<>(16);
            dataMap.put("packageName", "com.hiynn.sdk");
            dataMap.put("appName", appName);
            dataMap.put("apiDebugUrl", apiDebugUrl);
            dataMap.put("httpApiGeneratorList", httpApiGeneratorList);
            dataMap.put("httpsApiGeneratorList", httpsApiGeneratorList);
            //下载路径，写文件到服务器上，所有文件都写入完成后打包
            String commonPath = "SDK_" + appName + "_" + UUID.randomUUID().toString();
            String codePath = writeServerPath + commonPath + "/com/hiynn/sdk";
            String docPath = writeServerPath + commonPath + "/com/hiynn/doc";
            String jarPath = writeServerPath + commonPath + "/com/hiynn/lib";
            try {
                if (!httpApiGeneratorList.isEmpty()) {
                    //生成http调用api
                    FileUtils.generatorToJava(FreemarkerConfigUtils.TYPE_HTTP, dataMap, "HttpApiClient_" + appName + ".java", codePath);
                }
                if (!httpsApiGeneratorList.isEmpty()) {
                    //生成https调用api
                    FileUtils.generatorToJava(FreemarkerConfigUtils.TYPE_HTTPS, dataMap, "HttpsApiClient_" + appName + ".java", codePath);
                }
                //生成Demo
                FileUtils.generatorToJava(FreemarkerConfigUtils.TYPE_DEMO, dataMap, "Demo_" + appName + ".java", codePath);
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
            //将jar包和文档都写入到相应位置
            String jarFilename = "sdk-core-java-0.0.1.jar";
            String docFilename = "Readme.zh.md";
            writeFile(originalJarPath, jarPath, jarFilename);
            writeFile(originalDocPath, docPath, docFilename);
            //将文档和代码以及jar包一起打zip包
            String zipFilename = commonPath + ".zip";
            //压缩文件
            compressToZip(writeServerPath + commonPath, downloadPath, zipFilename, response);

            // TODO: 2019/5/23 直接使用流写到控制台前端无法获取，先使用FastDFS上传返回路径，然后将路径返回前端
            File zip = new File(downloadPath + File.separator + zipFilename);
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(zip));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int len;
            byte[] bytes = new byte[1024 * 10];
            while ((len = bis.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
                bos.flush();
            }
            String ext = zipFilename.substring(zipFilename.lastIndexOf(".") + 1);
            String path = FastDFSUtils.uploadFile(bos.toByteArray(), ext);
            String zipUri = zipPath + path;
            LOGGER.error("下载成功");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        }
        return null;
    }

    /**
     * 写文件
     *
     * @param readPath  读取文件路径
     * @param writePath 写入文件路径
     * @param fileName  文件名
     */
    public void writeFile(String readPath, String writePath, String fileName) {
        File writeFile = new File(writePath);
        if (!writeFile.exists()) {
            writeFile.mkdirs();
        }
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(readPath));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(writePath + File.separator + fileName))) {
            byte[] bytes = new byte[1024 * 10];
            int len;
            while ((len = bis.read(bytes, 0, bytes.length)) != -1) {
                bos.write(bytes, 0, len);
                bos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }


    /**
     * 压缩文件
     *
     * @param sourceFilePath 源文件路径
     * @param zipFilePath    压缩文件路径
     * @param zipFilename    压缩文件名
     */
    public void compressToZip(String sourceFilePath, String zipFilePath, String zipFilename, HttpServletResponse response) throws UnsupportedEncodingException {
        File sourceFile = new File(sourceFilePath);
        File file = new File(zipFilePath + File.separator + zipFilename);
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(zipFilename, "UTF-8"));
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(file))) {
            writeZip(sourceFile, "", zos);
            //文件压缩完成后，删除被压缩文件
            boolean flag = FileUtils.deleteDir(sourceFile);
            LOGGER.info("删除被压缩文件[" + sourceFile + "]标志：{}", flag);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    /**
     * 遍历所有文件，压缩
     *
     * @param file       源文件目录
     * @param parentPath 压缩文件目录
     * @param zos        文件流
     */
    public void writeZip(File file, String parentPath, ZipOutputStream zos) {
        if (file.isDirectory()) {
            //目录
            parentPath += file.getName() + File.separator;
            File[] files = file.listFiles();
            for (File f : files) {
                writeZip(f, parentPath, zos);
            }
        } else {
            //文件
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
                //指定zip文件夹
                ZipEntry zipEntry = new ZipEntry(parentPath + file.getName());
                zos.putNextEntry(zipEntry);
                int len;
                byte[] buffer = new byte[1024 * 10];
                while ((len = bis.read(buffer, 0, buffer.length)) != -1) {
                    zos.write(buffer, 0, len);
                    zos.flush();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getMessage(), e.getCause());
            }
        }

    }


}
