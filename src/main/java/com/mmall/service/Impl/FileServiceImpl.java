package com.mmall.service.Impl;

import com.google.common.collect.Lists;
import com.mmall.service.IFileService;
import com.mmall.util.FTPUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * @author wb
 * @create 2019-10-10 19:51
 */
@Service("iFileUploadService")
public class FileServiceImpl implements IFileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    /**上传文件*/
    @Override
    public String upload(MultipartFile file, String path){
        String fileName = file.getOriginalFilename();
        //获取图片的扩展名
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        //若A上传abc.jpg,B也上传abc.jpg，则A的会被覆盖，所以uploadFileName必须使用一个不会重复的  获取一个新的文件名
        String uploadFileName = UUID.randomUUID().toString()+"."+fileExtensionName;
        logger.info("开始上传文件，上传文件的文件名：{}，上传的路径：{}，新文件名：{}",fileName,path,uploadFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()){
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }
        File targetFile = new File(path, uploadFileName);

        try {
            file.transferTo(targetFile);
            //文件已经上传成功

            //将targetFile上传到FTP服务器上
            FTPUtil.uploadFile(Lists.<File>newArrayList(targetFile));
            //上传完之后，删除upload下面的文件
            targetFile.delete();

        } catch (IOException e) {
            logger.error("上传文件异常",e);
            return null;
        }
        return targetFile.getName();
    }
}
