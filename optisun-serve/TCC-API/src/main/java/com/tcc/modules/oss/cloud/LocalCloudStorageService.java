package com.tcc.modules.oss.cloud;

import cn.hutool.core.io.FileUtil;
import com.tcc.common.exception.WTDPException;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

/**
 * 本地云存储
 */
public class LocalCloudStorageService extends CloudStorageService {

    public LocalCloudStorageService(CloudStorageConfig config){
        this.config = config;
    }


    @Override
    public String upload(byte[] data, String path) {
        return upload(new ByteArrayInputStream(data), path);
    }

    @Override
    public String upload(InputStream inputStream, String path) {
        try {
            String savePath = config.getLocalDirName() + File.separator + path;
            File file = new File(savePath);
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }else if(!file.getParentFile().isDirectory()){
                file.getParentFile().mkdirs();
            }
            FileUtil.writeFromStream(inputStream, file);
        } catch (Exception e){
            throw new WTDPException("上传文件失败，请检查配置信息", e);
        }
        return config.getLocalDomain() + "/" + path;
    }

    @Override
    public String uploadSuffix(byte[] data, String suffix) {
        return upload(data, getPath(config.getLocalPrefix(), suffix));
    }

    @Override
    public String uploadSuffix(InputStream inputStream, String suffix) {
        return upload(inputStream, getPath(config.getLocalPrefix(), suffix));
    }
}
