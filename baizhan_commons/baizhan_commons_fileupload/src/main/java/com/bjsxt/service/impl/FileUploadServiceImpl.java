package com.bjsxt.service.impl;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.service.FileUploadService;
import com.bjsxt.utils.FastDFSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 文件上传服务实现
 */
@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${baizhan.fastdfs.nginx}")
    private String nginxServer;

    /**
     * 上传文件，通过工具类FastDFSClient实现文件上传
     *
     * @param file
     * @return
     */
    @Override
    public BaizhanResult uploadFile(MultipartFile file) {

        try {
            //文件输入流
            InputStream inputStream = file.getInputStream();
            //文件原始名称
            String filename = file.getOriginalFilename();
            String[] result = FastDFSClient.uploadFile(inputStream, filename);
            if (result == null) {
                //上传失败
                throw new RuntimeException("上传图片失败");
            }

            //拼接可访问路径
            String path = nginxServer + result[0] + "/" + result[1];
            return BaizhanResult.ok(path);
        } catch (IOException e) {
            e.printStackTrace();
            return BaizhanResult.error("服务器繁忙，请稍后重试");
        }
    }

}
