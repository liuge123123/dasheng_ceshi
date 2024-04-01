package com.tcc.common.utils;

import cn.hutool.core.io.FileUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class PosterUtils {

    /**
     * 将图片加载到缓冲区
     * @param img
     * @return
     */
    public static BufferedImage loadImageLocal(File img) {
        try {
            return ImageIO.read(img);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * 将图片加载到缓冲区
     * @param imgName
     * @return
     */
    public static BufferedImage loadImageLocal(String imgName) {
        try {
            return ImageIO.read(new File(imgName));
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * 海报生成
     * @param poster
     * @param qrcode
     * @return
     */
    public static BufferedImage createPoster(BufferedImage poster, BufferedImage qrcode) {
        try {
            int w = qrcode.getWidth();
            int h = qrcode.getHeight();
            Graphics2D g = poster.createGraphics();
            g.drawImage(qrcode, 321, 830, 200, 200, null);
            g.dispose();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return poster;
    }

    /**
     * 将缓冲区的图片保存到本地
     * @param newImage
     * @param img
     */
    public static void writeImageLocal(String newImage, BufferedImage img) {
        if (newImage != null && img != null) {
            try {
                File outputfile = FileUtil.file(newImage);
                if(!outputfile.getParentFile().exists() || !outputfile.getParentFile().isDirectory()){
                    outputfile.getParentFile().mkdirs();
                }
                ImageIO.write(img, "jpg", outputfile);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
