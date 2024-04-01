package com.tcc.modules.third.aliyun;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.Constant;
import com.tcc.common.utils.SpringContextUtils;
import com.tcc.modules.sys.service.SysConfigService;

public class SmsService {


    public static Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                // 您的AccessKey ID
                .setAccessKeyId(accessKeyId)
                // 您的AccessKey Secret
                .setAccessKeySecret(accessKeySecret);
        // 访问的域名
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new com.aliyun.dysmsapi20170525.Client(config);
    }

    public static JSONObject sendMsg(Long  orgId, String mobiles, String tplCode, String params, String outId) throws Exception {
        SysConfigService sysConfigService = (SysConfigService) SpringContextUtils.getBean("sysConfigService");
        String smsConfig = sysConfigService.getValue(orgId, Constant.CONFIG_KEY.SMS_SETTING);
        if(StrUtil.isBlank(smsConfig)){
            throw new WTDPException("短信未配置");
        }
        JSONObject config = JSONUtil.parseObj(smsConfig);
        return sendMsg(orgId, mobiles, config.getStr("sign"), tplCode, params, outId);
    }

    /**
     * 发送短信
     * @param orgId
     * @param mobiles
     * @param sign
     * @param tplCode
     * @param params
     * @param outId
     * @return
     * @throws Exception
     */
    public static JSONObject sendMsg(Long  orgId, String mobiles, String sign, String tplCode, String params, String outId) throws Exception {
        SysConfigService sysConfigService = (SysConfigService) SpringContextUtils.getBean("sysConfigService");
        String smsConfig = sysConfigService.getValue(orgId, Constant.CONFIG_KEY.SMS_SETTING);
        if(StrUtil.isBlank(smsConfig)){
            throw new WTDPException("短信未配置");
        }
        JSONObject config = JSONUtil.parseObj(smsConfig);
        return sendMsg(config, mobiles, sign, tplCode, params, outId);
    }

    /**
     * 发送短信
     * @param config
     * @param mobiles
     * @param sign
     * @param tplCode
     * @param params
     * @param outId
     * @return
     * @throws Exception
     */
    public static JSONObject sendMsg(JSONObject config, String mobiles, String sign, String tplCode, String params, String outId) throws Exception {
        String appId = config.getStr("appId");
        String appSecret = config.getStr("appSecret");
        Client client = SmsService.createClient(appId, appSecret);
        SendSmsRequest sendSmsRequest = new SendSmsRequest();
        sendSmsRequest.setPhoneNumbers(mobiles)
                .setSignName(sign)
                .setTemplateCode(tplCode)
                .setTemplateParam(params)
                .setOutId(outId);
        // 复制代码运行请自行打印 API 的返回值
        SendSmsResponse response = client.sendSms(sendSmsRequest);
        JSONObject res = new JSONObject();
        res.putOpt("code", "OK".equals(response.getBody().getCode()) ? 0 : -1);
        res.putOpt("msg", response.getBody().getMessage());
        res.putOpt("data", response.getBody().getBizId());
        return res;
    }

}
