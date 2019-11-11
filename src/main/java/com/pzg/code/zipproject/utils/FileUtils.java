package com.pzg.code.zipproject.utils;

import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;

public class FileUtils {

    private static final String HTTPS_FTL = "HttpsApiClient.ftl";
    private static final String HTTP_FTL = "HttpApiClient.ftl";
    private static final String WEBSERVICE_FTL = "WebServiceClient.ftl";
    private static final String DEMO_FTL = "DemoApi.ftl";

    /**
     * 根据相应模板生成java代码
     *
     * @param type
     * @param data
     * @param fileName
     * @param PATH
     * @throws IOException
     * @throws TemplateException
     */
    public static void generatorToJava(int type, Object data, String fileName, String PATH) throws IOException, TemplateException {
        String path = PATH + File.separator + fileName;
        File file = new File(PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
        Template template = getTemplate(type);
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)), 2048)) {
            //写文件
            template.process(data, writer);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取模板
     *
     * @param type
     * @return
     * @throws IOException
     */
    public static Template getTemplate(int type) throws IOException {
        switch (type) {
            case FreemarkerConfigUtils.TYPE_HTTP:
                return FreemarkerConfigUtils.getInstance().getTemplate(HTTP_FTL);
            case FreemarkerConfigUtils.TYPE_HTTPS:
                return FreemarkerConfigUtils.getInstance().getTemplate(HTTPS_FTL);
            case FreemarkerConfigUtils.TYPE_WEBSERVICE:
                return FreemarkerConfigUtils.getInstance().getTemplate(WEBSERVICE_FTL);
            case FreemarkerConfigUtils.TYPE_DEMO:
                return FreemarkerConfigUtils.getInstance().getTemplate(DEMO_FTL);
            default:
                return null;
        }
    }


    /**
     * 删除文件夹
     *
     * @param dir
     * @return
     */
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        //删除空文件夹
        return dir.delete();
    }

    /**
     * 字节数组转16进制字符串
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    public static byte[] fileToContent(File file) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            int len;
            byte[] buffer = new byte[1204 * 10];
            while ((len = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
                bos.flush();
            }
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}

