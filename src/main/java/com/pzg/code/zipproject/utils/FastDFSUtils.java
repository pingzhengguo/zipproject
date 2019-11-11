package com.pzg.code.zipproject.utils;

import org.csource.fastdfs.*;

public class FastDFSUtils {

    private static TrackerServer trackerServer;
    private static TrackerClient trackerClient;
    private static StorageClient1 storageClient;
    private static StorageServer storageServer;

    static {
        try {
            final String propertiesPath = Thread.currentThread().getContextClassLoader().getResource("").getPath() + "fastdfs_client.properties";
            ClientGlobal.initByProperties(propertiesPath);
            //跟踪器客户端对象
            trackerClient = new TrackerClient();
            //跟踪器服务端对象
            trackerServer = trackerClient.getConnection();
            //存储器服务端对象
            storageServer = trackerClient.getStoreStorage(trackerServer);
            //存储器客户端对象
            storageClient = new StorageClient1(trackerServer, storageServer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 上传文件
     *
     * @param content     文件content
     * @param suffix_name 后缀名
     * @return
     */
    public static String uploadFile(byte[] content, String suffix_name) {
        String path = null;
        try {
            path = storageClient.upload_file1(content, suffix_name, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 文件下载
     *
     * @param filePath 文件路径
     * @return
     */
    public static byte[] downloadFile(String filePath) {
        byte[] bytes = null;
        try {
            bytes = storageClient.download_file1(filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

}

