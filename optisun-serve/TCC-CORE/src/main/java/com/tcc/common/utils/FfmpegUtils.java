package com.tcc.common.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Configuration
@Slf4j
public class FfmpegUtils {

    private static String FFMPEG_PATH = "E:\\\\soft\\\\ffmpeg\\\\bin\\\\ffmpeg.exe";
    private static float MAX_VOLUME = -24;
    private static float MIN_VOLUME = -28;

//    @Value("${common.ffmpeg.path}")
    public void setPath(String ffmpegPath) {
        FfmpegUtils.FFMPEG_PATH = ffmpegPath;
    }

//    @Value("${common.ffmpeg.maxVolume}")
    public void setMaxVolume(float maxVolume) {
        FfmpegUtils.MAX_VOLUME = maxVolume;
    }

//    @Value("${common.ffmpeg.minVolume}")
    public void setMinVolume(float minVolume) {
        FfmpegUtils.MIN_VOLUME = minVolume;
    }

    /**
     * 音量标准化，根据平均音量整体调整
     * @param inputFilePath
     * @param outputFilePath
     * @return
     */
    public static boolean volumeNormalizer(String inputFilePath, String outputFilePath) throws Exception {
        float meanVolume = getMeanVolume(inputFilePath);
        if(meanVolume >= MIN_VOLUME && meanVolume <= MAX_VOLUME){ //在标准音量范围内
            FileUtil.copy(inputFilePath, outputFilePath, true);
            return true;
        }else{
            float subVolume = (MAX_VOLUME + MIN_VOLUME)/2 - meanVolume;
            return changeVolume(inputFilePath, outputFilePath, subVolume);
        }

    }

    /**
     * 音量标准化，削峰填谷
     * ffmpeg -i a.mp3 --filter:a "loudnorm" -y a1.mp3
     * @param inputFilePath
     * @param outputFilePath
     * @return
     */
    public static boolean volumeNormalizer2(String inputFilePath, String outputFilePath) throws Exception {
        File inputFile = new File(inputFilePath);
        if(!inputFile.exists()){
            throw new IOException("文件不存在");
        }
        File outFile = new File(outputFilePath);
        if(!outFile.getParentFile().exists()){
            outFile.getParentFile().mkdirs();
        }
        List<String> command = new ArrayList<String>();
        // 音量标准化
        command.add(FFMPEG_PATH);
        command.add("-i");
        command.add(inputFilePath);
        command.add("-filter:a");
        command.add("\"loudnorm\"");
        command.add("-y");
        command.add(outputFilePath);
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);
        //正常信息和错误信息合并输出
        builder.redirectErrorStream(true);
        //开始执行命令
        Process process = builder.start();
        String line = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String lastLine = null;
        while ((line = br.readLine()) != null) {
            log.debug(line);
            lastLine = line;
        }
        br.close();
        process.destroy();
        String resultReg = "video:(\\d*)kB audio:(\\d*)kB subtitle:(\\d*)kB other streams:(\\d*)kB global headers:(\\d*)kB muxing overhead: (\\d*.\\d*)%";
        if(lastLine.matches(resultReg)){
            return true;
        }
        return false;
    }

    /**
     * 获取音频的最大音量
     * ffmpeg -i a.mp3 -filter_complex volumedetect -c:v copy -f null /dev/null
     * @param inputFilePath
     * @return
     */
    public static float getMaxVolume(String inputFilePath) throws Exception {
        File inputFile = new File(inputFilePath);
        if(!inputFile.exists()){
            throw new IOException("文件不存在");
        }
        List<String> command = new ArrayList<String>();
        // 音量标准化
        command.add(FFMPEG_PATH);
        command.add("-i");
        command.add(inputFilePath);
        command.add("-filter_complex");
        command.add("volumedetect");
        command.add("-c:v");
        command.add("copy");
        command.add("-f");
        command.add("null");
        command.add("/dev/null");
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);
        //正常信息和错误信息合并输出
        builder.redirectErrorStream(true);
        //开始执行命令
        Process process = builder.start();
        String line = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String max_volume = null;
        while ((line = br.readLine()) != null) {
            log.debug(line);
            if(line.indexOf("max_volume") != -1){
                max_volume = line.split(":")[1].replace("dB", "").trim();
            }
        }
        if(max_volume == null){
            throw new Exception("获取音频音量失败");
        }
        return Convert.toFloat(max_volume);
    }

    /**
     * 获取音频的最大音量
     * ffmpeg -i a.mp3 -filter_complex volumedetect -c:v copy -f null /dev/null
     * @param inputFilePath
     * @return
     */
    public static float getMeanVolume(String inputFilePath) throws Exception {
        File inputFile = new File(inputFilePath);
        if(!inputFile.exists()){
            throw new IOException("文件不存在");
        }
        List<String> command = new ArrayList<String>();
        // 音量标准化
        command.add(FFMPEG_PATH);
        command.add("-i");
        command.add(inputFilePath);
        command.add("-filter_complex");
        command.add("volumedetect");
        command.add("-c:v");
        command.add("copy");
        command.add("-f");
        command.add("null");
        command.add("/dev/null");
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(command);
        //正常信息和错误信息合并输出
        builder.redirectErrorStream(true);
        //开始执行命令
        Process process = builder.start();
        String line = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String mean_volume = null;
        while ((line = br.readLine()) != null) {
            log.debug(line);
            if(line.indexOf("mean_volume") != -1){
                mean_volume = line.split(":")[1].replace("dB", "").trim();
            }
        }
        if(mean_volume == null){
            throw new Exception("获取音频音量失败");
        }
        return Convert.toFloat(mean_volume);
    }

    /**
     * 增减音频音量
     * ffmpeg -i a.mp3 -filter:a "volume= 5dB" -y a1.mp3
     * @param inputFilePath
     * @param outputFilePath
     * @param VolumeDB
     * @return
     */
    public static boolean changeVolume(String inputFilePath, String outputFilePath, float VolumeDB) throws Exception {
        File inputFile = new File(inputFilePath);
        if(!inputFile.exists()){
            throw new IOException("文件不存在");
        }
        File outFile = new File(outputFilePath);
        if(!outFile.getParentFile().exists()){
            outFile.getParentFile().mkdirs();
        }
        List<String> command = new ArrayList<String>();
        command.add(FFMPEG_PATH);
        command.add("-i");
        command.add(inputFilePath);
        command.add("-filter:a");
        command.add("\"volume="+ VolumeDB +"dB\"");
        command.add("-y");
        command.add(outputFilePath);
        StringBuffer commondStr = new StringBuffer();
        for (int i = 0; i < command.size(); i++) {
            commondStr.append(command.get(i) + " ");
        }
        log.debug(">>>>>>>>>>>>>>>>>>>>>>>>ffmpeg命令：" + commondStr.toString());
        Properties props = System.getProperties();
        String os = props.getProperty("os.name").toLowerCase();
        Process process = null;
        if (os.indexOf("windows") >= 0) { //win 执行命令
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(command);
            //正常信息和错误信息合并输出
            builder.redirectErrorStream(false);
            process = builder.start();
        } else if (os.indexOf("linux") >= 0) { //linux执行命令
            Runtime rt = Runtime.getRuntime();
            process = rt.exec(new String[]{"sh", "-c", commondStr.toString()});
        }
        if(process == null){
            throw new Exception("未识别的操作系统");
        }
        String line = null;
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorBr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String lastLine = null;
        log.debug(">>>>>>>>>>>>>>>>>>>>错误信息");
        // 输出的错误信息
        while ((line = errorBr.readLine()) != null) {
            log.debug(line);
            lastLine = line;
        }
        //正确信息
        log.debug(">>>>>>>>>>>>>>>>>>>>输出信息");
        while ((line = br.readLine()) != null) {
            log.debug(line);
            lastLine = line;
        }
        br.close();
        errorBr.close();

        int i = process.waitFor();
        log.debug("Process waitFor 返回结果：{}",i);

        process.destroy();
        String resultReg = "video:(\\d*)kB audio:(\\d*)kB subtitle:(\\d*)kB other streams:(\\d*)kB global headers:(\\d*)kB muxing overhead: (\\d*.\\d*)%";
        if(lastLine.matches(resultReg)){
            return true;
        }
        return false;
    }

    public static void main(String[] args) {
        Object result = false;
        try {
//            float meanVolume = FfmpegUtils.getMeanVolume("D:/t.mp3");
//            float s = -25;
//            float sub = s - meanVolume;
//            FfmpegUtils.changeVolume("D:/t.mp3", "D:/t3.mp3", sub);

            result = FfmpegUtils.volumeNormalizer("D:/b.mp3", "D:/b11.mp3");
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.debug(result + "");
    }

}
