package com.pzg.code.zipproject.controller;

import com.pzg.code.zipproject.service.ZipService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Map;

/**
 * @ClassName :  SdkController
 * @Author : PZG
 * @Date : 2019-11-11   上午 10:47
 * @Description :
 */
@RestController
@RequestMapping("/sdkController")
public class SdkController {

    @Autowired
    private ZipService zipService;

    /**
     * #自动生成代码存储路径
     */
    private String writeServerPath = "E:/test/code/";

    /**
     * 下载sdk
     *
     * @param appId
     * @param appName
     * @param response
     */
    @ApiOperation(value = "下载sdk")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "APP主键", paramType = "path", dataType = "Integer"),
            @ApiImplicitParam(name = "appName", value = "APP名称", paramType = "path", dataType = "String"),
    })
    @GetMapping(value = "/download/{appId}/{appName}")
    public Object download(@PathVariable Integer appId, @PathVariable String appName, HttpServletResponse response) {
        return zipService.downloadExecuteCode(appId, appName, response);
    }

    /**
     * 下载sdk
     *
     * @param map
     * @param response
     * @return
     */
    @ApiOperation(value = "下载sdk-POST请求")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "appId", value = "APP主键", paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "appName", value = "APP名称", paramType = "query", dataType = "String"),
    })
    @RequestMapping(value = "/download", method = RequestMethod.POST)
    public Object download(@RequestBody Map<String, Object> map, HttpServletResponse response) {
        return zipService.downloadExecuteCode((Integer) map.get("appId"), (String) map.get("appName"), response);
    }

    /**
     * 将文件转化为base64编码
     *
     * @param file
     * @return
     */
    @PostMapping("/encode")
    @ApiOperation(value = "下载sdk-将文件转化为base64编码")
    public String fileToBase64(@ApiParam(name = "file", value = "文件") @RequestParam("file") MultipartFile file) {
        try (InputStream ins = file.getInputStream();
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            int len;
            byte[] bytes = new byte[1024 * 10];
            while ((len = ins.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
                bos.flush();
            }
            return Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 测试图片接口
     *
     * @param response
     */
    @PostMapping("/write")
    public void writeFileTest(HttpServletResponse response) {
        File file = new File("E:/test/test.png");
        response.setContentType("image/png");
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
             BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())) {
            int len;
            byte[] buffer = new byte[1024 * 10];
            while ((len = bis.read(buffer, 0, buffer.length)) != -1) {
                bos.write(buffer, 0, len);
                bos.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试上传文件
     *
     * @param file
     */
    @PostMapping("/upload")
    public String uploadFileTest(@RequestParam("file") MultipartFile file) throws IOException {
        File dir = new File(writeServerPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try (BufferedInputStream bis = new BufferedInputStream(file.getInputStream())) {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(dir + File.separator + file.getOriginalFilename())));
            byte[] bytes = new byte[1024 * 10];
            int len;
            while ((len = bis.read(bytes)) != -1) {
                bos.write(bytes, 0, len);
                bos.flush();
            }
            return "上传成功，请在[" + dir.getAbsolutePath() + "]文件下夹下查看上传文件";
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
//        String filename = file.getOriginalFilename();
//        String path = FastDFSUtils.uploadFile(file.getBytes(), filename.substring(filename.lastIndexOf(".") + 1, filename.length()));
//        return "上传成功，文件路径为：[" + path + "]。";

    }

    @PostMapping("/testHttp")
    public String testHttpClient(HttpServletRequest request) {
        Enumeration<String> enums = request.getParameterNames();
        while (enums.hasMoreElements()) {
            String paramName = enums.nextElement();
            System.out.println(paramName + "--" + request.getParameter(paramName));
        }
        Map<String, String[]> map = request.getParameterMap();
        System.out.println(map.toString());
        return null;
    }
}
