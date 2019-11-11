package com.pzg.code.zipproject.utils;

import com.pzg.code.zipproject.entity.FtpProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.net.ftp.*;

import java.io.IOException;
import java.io.InputStream;

/**
 * 描述：向制定的服务器上传和下载文件
 *
 * @since
 */

@Slf4j
public class FtpUtils {

    public static final int imageCutSize = 300;

    private static String ip;

    private static int port;

    private static String userName;

    private static String password;

    private static String filepath;

    private static FTPClient ftpClient = new FTPClient();

    /**
     * 功能：上传文件附件到文件服务器
     *
     * @return
     * @throws IOException
     */
    public static boolean uploadToFtp(InputStream buffIn, String fileName, FtpProperty ftpProperty, boolean needDelete)
            throws FTPConnectionClosedException, IOException, Exception {
        boolean returnValue = false;
        // 上传文件
        try {
            ip = ftpProperty.getHost();
            port = ftpProperty.getPort();
            userName = ftpProperty.getUsername();
            password = ftpProperty.getPassword();
            filepath = ftpProperty.getFilepath();
            // 建立连接
            connectToServer();
            // 设置传输二进制文件
            setFileType(FTP.BINARY_FILE_TYPE);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new IOException("failed to connect to the FTP Server:" + ip);
            }
            ftpClient.enterLocalPassiveMode();
            ftpClient.changeWorkingDirectory(filepath);
            // 上传文件到ftp
            returnValue = ftpClient.storeFile(fileName, buffIn);
            if (needDelete) {
                ftpClient.deleteFile(fileName);
            }
            // 输出操作结果信息
            if (returnValue) {
                log.info("uploadToFtp INFO: upload file  to ftp : succeed!");
            } else {
                log.info("uploadToFtp INFO: upload file  to ftp : failed!");
            }
            buffIn.close();
            // 关闭连接
            closeConnect();
        } catch (FTPConnectionClosedException e) {
            log.error("ftp连接被关闭！", e);
            throw e;
        } catch (Exception e) {
            returnValue = false;
            log.error("ERR : upload file  to ftp : failed! ", e);
            throw e;
        } finally {
            try {
                if (buffIn != null) {
                    buffIn.close();
                }
            } catch (Exception e) {
                log.error("ftp关闭输入流时失败！", e);
            }
            if (ftpClient.isConnected()) {
                closeConnect();
            }
        }
        return returnValue;
    }

    /**
     * 功能：根据文件名称，下载文件流
     *
     * @param filename
     * @param ftpProperty
     * @return
     * @throws IOException
     */
    public static InputStream downloadFile(String filename, FtpProperty ftpProperty) throws IOException {
        InputStream in = null;
        try {
            ip = ftpProperty.getHost();
            port = ftpProperty.getPort();
            userName = ftpProperty.getUsername();
            password = ftpProperty.getPassword();
            filepath = ftpProperty.getFilepath();
            // 建立连接
            connectToServer();
            ftpClient.enterLocalPassiveMode();
            // 设置传输二进制文件
            setFileType(FTP.BINARY_FILE_TYPE);
            int reply = ftpClient.getReplyCode();
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                throw new IOException("failed to connect to the FTP Server:" + ip);
            }
            ftpClient.changeWorkingDirectory("/pub");

            // ftp文件获取文件
            in = ftpClient.retrieveFileStream(filename);

        } catch (FTPConnectionClosedException e) {
            log.error("ftp连接被关闭！", e);
            throw e;
        } catch (Exception e) {
            log.error("ERR : upload file " + filename + " from ftp : failed!", e);
        }
        return in;
    }

    /**
     * 设置传输文件的类型[文本文件或者二进制文件]
     *
     * @param fileType --BINARY_FILE_TYPE、ASCII_FILE_TYPE
     */
    private static void setFileType(int fileType) {
        try {
            ftpClient.setFileType(fileType);
        } catch (Exception e) {
            log.error("ftp设置传输文件的类型时失败！", e);
        }
    }

    /**
     * 功能：关闭连接
     */
    public static void closeConnect() {
        try {
            if (ftpClient != null) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (Exception e) {
            log.error("ftp连接关闭失败！", e);
        }
    }

    /**
     * 连接到ftp服务器
     */
    private static void connectToServer() throws FTPConnectionClosedException, Exception {
        if (!ftpClient.isConnected()) {
            int reply;
            try {
                ftpClient = new FTPClient();
                ftpClient.connect(ip, port);
                ftpClient.login(userName, password);
                reply = ftpClient.getReplyCode();

                if (!FTPReply.isPositiveCompletion(reply)) {
                    ftpClient.disconnect();
                    log.info("connectToServer FTP server refused connection.");
                }

            } catch (FTPConnectionClosedException ex) {
                log.error("服务器:IP：" + ip + "没有连接数！there are too many connected users,please try later", ex);
                throw ex;
            } catch (Exception e) {
                log.error("登录ftp服务器【" + ip + "】失败", e);
                throw e;
            }
        }
    }

    // Check the path is exist; exist return true, else false.
    public static boolean existDirectory(String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        for (FTPFile ftpFile : ftpFileArr) {
            if (ftpFile.isDirectory() && ftpFile.getName().equalsIgnoreCase(path)) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    /**
     * 创建FTP文件夹目录
     *
     * @param pathName
     * @return
     * @throws IOException
     */
    public static boolean createDirectory(String pathName) throws IOException {
        boolean isSuccess = false;
        try {
            isSuccess = ftpClient.makeDirectory(pathName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

}
