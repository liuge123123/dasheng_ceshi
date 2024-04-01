package com.tcc.modules.third.tencent;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONException;
import com.github.qcloudsms.*;
import com.github.qcloudsms.httpclient.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
public class TencentUtils {

    private static String appId = "";

    private static String appKey = "";

    private static String pullsendstatusUrl = "https://yun.tim.qq.com/v5/tlssmssvr/pullsendstatus";
    private static String addTemplateUrl = "https://yun.tim.qq.com/v5/tlssmssvr/add_template";
    private static String modTemplateUrl = "https://yun.tim.qq.com/v5/tlssmssvr/mod_template";
    private static String delTemplateUrl = "https://yun.tim.qq.com/v5/tlssmssvr/del_template";
    private static String getTemplateUrl = "https://yun.tim.qq.com/v5/tlssmssvr/get_template";

    public static void init(String appId, String appKey){
        TencentUtils.appId = appId;
        TencentUtils.appKey = appKey;
    }

    /**
     * 发送单条短信
     * @param sign
     * @param templateId
     * @param params
     * @param mobile
     * @throws HTTPException
     * @throws IOException
     */
    public static void sendSms(String sign, String templateId, ArrayList<String> params, String mobile) throws HTTPException, IOException {
        SmsSingleSender ssender = new SmsSingleSender(Convert.toInt(appId), appKey);
        SmsSingleSenderResult result = ssender.sendWithParam("86", mobile, Convert.toInt(templateId), params, sign, "", "");
        log.debug("短信>>>>>>>>> " + JSONUtil.toJsonStr(result));

    }

    /**
     * 群发短信，不超过200条
     * @param sign
     * @param templateId
     * @param params
     * @param mobiles
     */
    public static void sendSms(String sign, String templateId, ArrayList<String> params, ArrayList<String> mobiles){
        try {
            SmsMultiSender msender = new SmsMultiSender(Convert.toInt(appId), appKey);
            SmsMultiSenderResult result =  msender.sendWithParam("86", mobiles, Convert.toInt(templateId), params, sign, "", "");
            System.out.println(result);
        } catch (HTTPException e) {
            // HTTP 响应码错误
            e.printStackTrace();
        } catch (JSONException e) {
            // JSON 解析错误
            e.printStackTrace();
        } catch (IOException e) {
            // 网络 IO 错误
            e.printStackTrace();
        }
    }

    /**
     * 获取统计数据
     * @param beginDate
     * @param endDate
     * @return
     * @throws HTTPException
     * @throws org.json.JSONException
     * @throws IOException
     */
    public static JSONObject pullsendstatus(int beginDate, int endDate) throws HTTPException, org.json.JSONException, IOException {
        JSONObject body = (new JSONObject()).put("begin_date", beginDate).put("end_date", endDate);
        return exec(pullsendstatusUrl, body);
    }

    /**
     * 添加模版
     * @param text
     * @param title
     * @param type
     * @param remark
     * @return
     * @throws HTTPException
     * @throws org.json.JSONException
     * @throws IOException
     */
    public static JSONObject addTemplate(String text, String title, int type, String remark) throws HTTPException, org.json.JSONException, IOException {
        JSONObject body = (new JSONObject()).put("text", text).put("title", title).put("type", type).put("remark", remark == null ? "" : remark );
        return exec(addTemplateUrl, body);
    }

    /**
     * 修改模版
     * @param tplId
     * @param text
     * @param title
     * @param type
     * @param remark
     * @return
     * @throws HTTPException
     * @throws org.json.JSONException
     * @throws IOException
     */
    public static JSONObject modTemplate(int tplId, String text, String title, int type, String remark) throws HTTPException, org.json.JSONException, IOException {
        JSONObject body = (new JSONObject()).put("tpl_id", tplId).put("text", text).put("title", title).put("type", type).put("remark", remark == null ? "" : remark );
        return exec(modTemplateUrl, body);
    }

    /**
     * 删除模版
     * @param tplId
     * @return
     * @throws HTTPException
     * @throws org.json.JSONException
     * @throws IOException
     */
    public static JSONObject delTemplate(int tplId) throws HTTPException, org.json.JSONException, IOException {
        JSONObject body = (new JSONObject()).put("tpl_id", new ArrayList<Integer>().add(tplId));
        return exec(delTemplateUrl, body);
    }

    /**
     * 删除模版
     * @param tplId
     * @return
     * @throws HTTPException
     * @throws org.json.JSONException
     * @throws IOException
     */
    public static JSONObject delTemplate(int[] tplId) throws HTTPException, org.json.JSONException, IOException {
        JSONObject body = (new JSONObject()).put("tpl_id", Arrays.asList(tplId));
        return exec(delTemplateUrl, body);
    }

    /**
     * 删除模版
     * @param tplId
     * @return
     * @throws HTTPException
     * @throws org.json.JSONException
     * @throws IOException
     */
    public static JSONObject delTemplate(ArrayList<Integer> tplId) throws HTTPException, org.json.JSONException, IOException {
        JSONObject body = (new JSONObject()).put("tpl_id", tplId);
        return exec(delTemplateUrl, body);
    }


    /**
     * 获取模版状态
     * @param tplId
     * @return
     * @throws HTTPException
     * @throws org.json.JSONException
     * @throws IOException
     */
    public static JSONObject getTemplate(int tplId) throws HTTPException, org.json.JSONException, IOException {
        JSONObject body = (new JSONObject()).put("tpl_id", new ArrayList<Integer>().add(tplId));
        return exec(getTemplateUrl, body);
    }


    /**
     * 获取模版状态
     * @param tplId
     * @return
     * @throws HTTPException
     * @throws org.json.JSONException
     * @throws IOException
     */
    public static JSONObject getTemplate(int[] tplId) throws HTTPException, org.json.JSONException, IOException {
        JSONObject body = (new JSONObject()).put("tpl_id", Arrays.asList(tplId));
        return exec(getTemplateUrl, body);
    }

    /**
     * 获取模版状态
     * @param tplId
     * @return
     * @throws HTTPException
     * @throws org.json.JSONException
     * @throws IOException
     */
    public static JSONObject getTemplate(ArrayList<Integer> tplId) throws HTTPException, org.json.JSONException, IOException {
        JSONObject body = (new JSONObject()).put("tpl_id", tplId);
        return exec(getTemplateUrl, body);
    }



    /**
     * 执行api
     * @param url
     * @param body
     * @return
     * @throws HTTPException
     * @throws IOException
     */
    public static JSONObject exec(String url, JSONObject body) throws HTTPException, IOException {
        long random = SmsSenderUtil.getRandom();
        long now = SmsSenderUtil.getCurrentTime();
        body.put("sig", SmsSenderUtil.calculateSignature(appKey, random, now)).put("time", now);
        HTTPRequest req = (new HTTPRequest(HTTPMethod.POST, url)).addHeader("Conetent-Type", "application/json").addQueryParameter("sdkappid", appId).addQueryParameter("random", random).setConnectionTimeout(60000).setRequestTimeout(60000).setBody(body.toString());
        try {
            DefaultHTTPClient httpclient = new DefaultHTTPClient();
            HTTPResponse res = httpclient.fetch(req);
            handleError(res);
            return new JSONObject(res.body);
        } catch (URISyntaxException var14) {
            throw new RuntimeException("API url has been modified, current url: " + url);
        }
    }

    /**
     * 调接口错误
     * @param response
     * @return
     * @throws HTTPException
     */
    public static HTTPResponse handleError(HTTPResponse response) throws HTTPException {
        if (response.statusCode >= 200 && response.statusCode < 300) {
            return response;
        } else {
            throw new HTTPException(response.statusCode, response.reason);
        }
    }



}
