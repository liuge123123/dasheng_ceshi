package com.tcc.common.utils;

import java.security.MessageDigest;

//微信Md5加密工具
public class MD5Util {

    private static String byteArrayToHexString(byte b[]) {
        StringBuffer resultSb = new StringBuffer();
        for (int i = 0; i < b.length; i++)
            resultSb.append(byteToHexString(b[i]));

        return resultSb.toString();
    }

    private static String byteToHexString(byte b) {
        int n = b;
        if (n < 0)
            n += 256;
        int d1 = n / 16;
        int d2 = n % 16;
        return hexDigits[d1] + hexDigits[d2];
    }

    public static String MD5Encode(String origin, String charsetname) {
        String resultString = null;
        try {
            resultString = new String(origin);
            MessageDigest md = MessageDigest.getInstance("MD5");
            if (charsetname == null || "".equals(charsetname))
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes()));
            else
                resultString = byteArrayToHexString(md.digest(resultString
                        .getBytes(charsetname)));
        } catch (Exception exception) {
        }
        return resultString;
    }

    private static final String hexDigits[] = { "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

    /**
     * 密码加密
     * @param pwd 密码
     * @param salt 随机盐
     * @return
     */
    public static String pwd(String pwd, String salt){
        String pss = MD5Encode(MD5Encode(pwd, "UTF-8") + salt, "UTF-8");
        return pss;
    }

    /**
     * 密码加密
     * @param pwd 密码
     * @return
     */
    public static String md5(String pwd){
        String pss = MD5Encode(pwd, "UTF-8");
        return pss;
    }

}
