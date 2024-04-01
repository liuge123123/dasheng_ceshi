package com.tcc.modules.third.mail;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailAccount;
import cn.hutool.extra.mail.MailException;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.Constant;
import com.tcc.common.utils.SpringContextUtils;
import com.tcc.modules.sys.service.SysConfigService;

import java.io.File;

public class MailService {

    public static JSONObject sendMail(Long  orgId, String mailTo, String mailTitle, String mailContent, File... file){
        SysConfigService sysConfigService = (SysConfigService) SpringContextUtils.getBean("sysConfigService");
        String configStr = sysConfigService.getValue(orgId, Constant.CONFIG_KEY.MAIL_SETTING);
        if(StrUtil.isBlank(configStr)){
            throw new WTDPException("邮件未配置");
        }
        JSONObject config = JSONUtil.parseObj(configStr);
        return sendMail(config, mailTo, mailTitle, mailContent, file);
    }

    public static JSONObject sendMail(JSONObject config, String mailTo, String mailTitle, String mailContent, File... file){
        MailAccount mailAccount = new MailAccount();
        mailAccount.setHost(config.getStr("host"));
        mailAccount.setPort(config.getInt("port"));
        mailAccount.setFrom(config.getStr("from"));
        mailAccount.setPass(config.getStr("pass"));
        mailAccount.setSslEnable(config.getBool("sslEnable"));
        JSONObject resObj = new JSONObject();
        try {
            String resStr = MailUtil.send(mailAccount, mailTo, mailTitle, mailContent, true, file );
            resObj.putOpt("code", 0);
            resObj.putOpt("msg", "success");
        }catch (MailException e){
            resObj.putOpt("code", 500);
            resObj.putOpt("msg", "邮箱配置错误：" + e.getMessage());
        }
        return resObj;
    }

}
