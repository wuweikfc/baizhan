package com.bjsxt.controller;

import com.bjsxt.commons.pojo.BaizhanResult;
import com.bjsxt.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    /**
     * 上传文件
     *
     * @param file
     * @return {
     * "data":"http://ip:8888/M00/00/00/xxx.xxx"
     * }
     */
    public BaizhanResult uploadFile(MultipartFile file) {

        return fileUploadService.uploadFile(file);
    }

}
