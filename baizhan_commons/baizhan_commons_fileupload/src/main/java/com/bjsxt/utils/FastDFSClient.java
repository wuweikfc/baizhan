package com.bjsxt.utils;

import org.apache.commons.lang3.StringUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;

import java.io.*;

/**
 * fastDFS分布式文件系统操作客户端
 */
public class FastDFSClient {

    private static final String CONF_FILENAME = "fdfs_client.conf";

    private static StorageClient storageClient = null;

    /**
     * 只加载一次
     */
    static {
        try {
            ClientGlobal.init(CONF_FILENAME);
            TrackerClient trackerClient = new TrackerClient(ClientGlobal.getG_tracker_group());
            TrackerServer trackerServer = trackerClient.getConnection();
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);
            storageClient = new StorageClient(trackerServer, storageServer);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param inputStream 上传的文件输入流
     * @param fileName    上传的文件原始名
     * @return
     */
    public static String[] uploadFile(InputStream inputStream, String fileName) {

        try {
            //文件的元数据
            NameValuePair[] meta_list = new NameValuePair[2];
            //第一组元数据，文件的原始名称
            meta_list[0] = new NameValuePair("file name", fileName);
            //第二组元数据
            meta_list[1] = new NameValuePair("file length", inputStream.available() + "");
            //准备字节数组
            byte[] file_buff = null;
            if (inputStream != null) {
                //查看文件长度
                int len = inputStream.available();
                //创建对应长度的字节数组
                file_buff = new byte[len];
                //将输入流中的字节内容，读到字节数组中
                inputStream.read(file_buff);
            }
            //上传文件，参数含义：要上传的文件内容（使用字节数组传递），上传的文件类型（扩展名），元数据
            String[] fields = storageClient.upload_file(file_buff, getFileExt(fileName), meta_list);
            return fields;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * @param file     文件
     * @param fileName 文件名
     * @return 返回null则为失败
     */
    public static String[] uploadFile(File file, String fileName) {

        FileInputStream fis = null;
        try {
            NameValuePair[] meta_list = null;
            fis = new FileInputStream(file);
            byte[] file_buff = null;
            if (fis != null) {
                int len = fis.available();
                file_buff = new byte[len];
                fis.read(file_buff);
            }
            String[] fields = storageClient.upload_file(file_buff, getFileExt(fileName), meta_list);
            return fields;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    /**
     * 根据组名和远程文件名来删除一个文件
     *
     * @param groupName      例如 "group1"，如果不指定该值，默认为group1
     * @param remoteFileName 例如"M00/00/00/wKgxgk5HbLvfP86RAAAAChd9X1Y736.jpg"
     * @return 0为成功，非0为失败，具体为错误代码
     */
    public static int deleteFile(String groupName, String remoteFileName) {

        try {
            int result = storageClient.delete_file(groupName == null ? "group1" : groupName, remoteFileName);
            return result;
        } catch (Exception e) {
            return 0;
        }

    }

    /**
     * 修改一个已经存在的文件
     *
     * @param oldGroupName 旧的组名
     * @param oldFileName  旧的文件名
     * @param file         新文件
     * @param fileName     新文件名
     * @return
     */
    public static String[] modifyFile(String oldGroupName, String oldFileName, File file, String fileName) {

        String[] fields = null;
        try {
            //先上传
            fields = uploadFile(file, fileName);
            if (fields == null) {
                return null;
            }
            //再删除
            int delResult = deleteFile(oldGroupName, oldFileName);
            if (delResult != 0) {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
        return fields;

    }

    /**
     * 文件下载
     *
     * @param groupName      卷名
     * @param remoteFileName 文件名
     * @return 返回一个流
     */
    public static InputStream downloadFile(String groupName, String remoteFileName) {

        try {
            byte[] bytes = storageClient.download_file(groupName, remoteFileName);
            InputStream inputStream = new ByteArrayInputStream(bytes);
            return inputStream;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 获取元数据信息
     *
     * @param groupName
     * @param remoteFileName
     * @return
     */
    public static NameValuePair[] getMetaData(String groupName, String remoteFileName) {

        try {
            NameValuePair[] nvp = storageClient.get_metadata(groupName, remoteFileName);
            return nvp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * 获取文件名后缀，不带点 .
     *
     * @param fileName
     * @return 如："jpg"或""
     */
    private static String getFileExt(String fileName) {

        if (StringUtils.isBlank(fileName) || !fileName.contains(".")) {
            return "";
        } else {
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        }

    }

}
