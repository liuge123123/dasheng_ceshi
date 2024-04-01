package com.tcc.modules.third.tencent;

import lombok.Data;

@Data
public class TextData {

    /*
    是否恶意 0：正常 1：可疑
     */
    private Integer EvilFlag;

    /*
    恶意类型
    100：正常
    20001：政治
    20002：色情
    20006：涉毒违法
    20007：谩骂
    20105：广告引流
    24001：暴恐
    21000：综合
     */
    private Integer EvilType;

    /*
    命中的关键词
     */
    private String[] Keywords;
}
