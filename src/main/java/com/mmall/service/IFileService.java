package com.mmall.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author wb
 * @create 2019-10-10 19:51
 */
public interface IFileService {
    /**上传文件*/
    String upload(MultipartFile file, String path);
}
