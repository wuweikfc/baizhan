package com.bjsxt.service;

import com.bjsxt.commons.pojo.BaizhanResult;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploadService {

    BaizhanResult uploadFile(MultipartFile file);

}
