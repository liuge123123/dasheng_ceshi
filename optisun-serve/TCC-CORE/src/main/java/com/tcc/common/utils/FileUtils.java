package com.tcc.common.utils;

import cn.hutool.core.io.FileUtil;
import it.sauronsoftware.jave.Encoder;
import it.sauronsoftware.jave.EncoderException;
import it.sauronsoftware.jave.MultimediaInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

public class FileUtils {

    /**
     * 是否为图片，根据后缀名称判断
     * @param suffix
     * @return
     */
    public static boolean isImgBySuffix(String suffix){
        String suffixs = "|jpg|jpeg|png|gif|ico|bmp|webp|";
        suffix = "|" + suffix.toLowerCase() + "|";
        if(suffixs.indexOf(suffix) != -1){
            return true;
        }
        return false;
    }

    /**
     * 是否为音频，根据后缀名称判断
     * @param suffix
     * @return
     */
    public static boolean isAudioBySuffix(String suffix){
        String suffixs = "|mid|midi|mp3|wav|wma|ogg|amr|aac|";
        suffix = "|" + suffix.toLowerCase() + "|";
        if(suffixs.indexOf(suffix) != -1){
            return true;
        }
        return false;
    }

    /**
     * 是否为视频，根据后缀名称判断
     * @param suffix
     * @return
     */
    public static boolean isVideoBySuffix(String suffix){
        String suffixs = "|mp4|flv|mov|3gp|rmvb|mpg|mpeg|mkt|swf|rm|m4v|";
        suffix = "|" + suffix.toLowerCase() + "|";
        if(suffixs.indexOf(suffix) != -1){
            return true;
        }
        return false;
    }

    /**
     * 获取媒体文件的时长，毫秒
     * @param file
     * @return
     * @throws EncoderException
     */
    public static long getMidiaDuration(MultipartFile file) {
        File temFile = new File(file.getOriginalFilename());
        try {
            FileUtil.writeFromStream(file.getInputStream(), temFile);
            Encoder encoder = new Encoder();
            MultimediaInfo info = encoder.getInfo(temFile);
            long duration = info.getDuration();
            temFile.delete();
            return duration;
        }catch (Exception e){
            e.printStackTrace();
            if(temFile.exists()){
                temFile.delete();
            }
        }
        return 0;
    }
}
