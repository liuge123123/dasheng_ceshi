package com.tcc.modules.third.tencent;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tcc.common.utils.R;
import com.tencentcloudapi.cms.v20190321.CmsClient;
import com.tencentcloudapi.cms.v20190321.models.TextModerationRequest;
import com.tencentcloudapi.cms.v20190321.models.TextModerationResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import org.apache.commons.codec.binary.Base64;

import java.util.HashMap;
import java.util.Map;

public class TencentValidate {

    private TencentApiConfig tencentApiConfig;

    public TencentValidate(TencentApiConfig tencentApiConfig){
        this.tencentApiConfig=tencentApiConfig;
    }

    /**
     * 腾讯文本内容安全验证
     * @param contents
     * @return
     */
    public R contentsValidate(String contents){
        try{
            Credential cred = new Credential(this.tencentApiConfig.getSecretId(), this.tencentApiConfig.getSecretKey());

            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("cms.tencentcloudapi.com");

            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);

            CmsClient client = new CmsClient(cred, "ap-guangzhou", clientProfile);

            Map<String,Object> paramsList=new HashMap<>();
            paramsList.put("Content", Base64.encodeBase64String(contents.getBytes()));
            String params= JSONUtil.parseFromMap(paramsList).toString();

            //String params = "{\"Content\":\"5Lmg6L+b5bmz\"}";
            TextModerationRequest req = TextModerationRequest.fromJsonString(params, TextModerationRequest.class);

            TextModerationResponse resp = client.TextModeration(req);
            JSONObject jsonObject=JSONUtil.parseObj(resp);
            Object  dataItem=jsonObject.get("Data");
            TextData textData=new TextData();
            textData=	JSONUtil.toBean(dataItem.toString(),TextData.class);
            if(textData.getEvilFlag()==0)
            {
                return  R.ok();
            }
            else{
                if(textData.getEvilFlag()==1){
                    String [] list=textData.getKeywords();
                    String msgstr="";
                    for(Integer i=0;i<list.length;i++){
                        msgstr+=list[i]+",";
                    }
                    Integer index=msgstr.lastIndexOf(',');
                    if(index>0&&index==msgstr.length()-1)
                    {
                        msgstr=msgstr.substring(0,msgstr.lastIndexOf(','));
                    }
                    return  R.error(10008,"含有非法关键字,关键字为:"+msgstr);
                }
            }
            //System.out.println(TextModerationRequest.toJsonString(resp));
        } catch (TencentCloudSDKException e) {
            System.out.println(e.toString());
            return  R.ok();
        }
        return  R.ok();
    }
}
