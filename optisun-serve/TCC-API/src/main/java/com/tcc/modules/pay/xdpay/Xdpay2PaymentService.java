package com.tcc.modules.pay.xdpay;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcc.common.bean.RequestKitBean;
import com.tcc.common.utils.MD5Util;
import com.tcc.modules.cust.entity.CommissionEntity;
import com.tcc.modules.cust.entity.RechargeEntity;
import com.tcc.modules.cust.form.CommissionCheckForm;
import com.tcc.modules.cust.form.RechargeCheckForm;
import com.tcc.modules.cust.service.CommissionService;
import com.tcc.modules.cust.service.RechargeService;
import com.tcc.modules.pay.IPaymentService;
import com.tcc.modules.pay.PayResult;
import com.tcc.modules.pay.TransferResult;
import com.tcc.modules.s.entity.SPlatformBankEntity;
import com.tcc.modules.s.service.SPlatformBankService;
import com.tcc.modules.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class Xdpay2PaymentService implements IPaymentService {
    @Autowired
    private SPlatformBankService platformBankService;
    @Autowired
    private RequestKitBean requestKitBean;

    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private SysConfigService sysConfigService;



    private String getNotifyUrl(String paycode,String orderId){
        String notifyUrl = sysConfigService.getValue(0, "NOTIFY_URL");
        return notifyUrl+"/app/account/notify/"+paycode+"/"+orderId;
    }

    private String getTransferNotifyUrl(String paycode,String orderId){
        String notifyUrl = sysConfigService.getValue(0, "NOTIFY_URL");
        return notifyUrl+"/cust/commission/transfernotify/"+paycode+"/"+orderId;
    }

    private String getCallBack(){
        return sysConfigService.getValue(0, "CALLBACK_URL");
    }

    @Override
    public PayResult pay(String orderId, BigDecimal amount,SPlatformBankEntity sPlatformBankEntity) {
        PayResult payResult = new PayResult();

        String signStr = "";
        signStr += "amount=" +amount+ "&";
        signStr = signStr + "callbackUrl=" + getCallBack() + "&";
        signStr = signStr + "merchant=" + sPlatformBankEntity.getMerchant() + "&";
        signStr = signStr + "notifyUrl=" + getNotifyUrl(sPlatformBankEntity.getName(),orderId) + "&";
        signStr = signStr + "orderId=" + orderId + "&";
        signStr = signStr + "payCode="+sPlatformBankEntity.getPayChannel();
        signStr = md5Sign(signStr,sPlatformBankEntity.getMerchantKey());

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("amount",amount);
        paramsMap.put("callbackUrl",getCallBack());
        paramsMap.put("merchant",sPlatformBankEntity.getMerchant());
        paramsMap.put("notifyUrl",getNotifyUrl(sPlatformBankEntity.getName(),orderId));
        paramsMap.put("orderId",orderId);
        paramsMap.put("payCode",sPlatformBankEntity.getPayChannel());
        paramsMap.put("sign",signStr);
        log.info("xdpay请求URL:"+sPlatformBankEntity.getPayUrl());
        log.info("xdpay请求报文:"+paramsMap.toString());
        String result = postForm(sPlatformBankEntity.getPayUrl(),paramsMap);
        log.info("xdpay请求返回报文:"+result);
        //{"code":400,"data":null,"desc":"business error","msg":"the channel is not open","success":false}
        JSONObject object = JSONUtil.parseObj(result);
        Boolean isSuccess = (Boolean) object.get("success");
        Integer code = (Integer) object.get("code");
        if(isSuccess&&code==200){
            JSONObject respData = JSONUtil.parseObj(object.get("data"));//应答信息
            payResult.setStatus(1);
            payResult.setPayUrl((String) respData.get("url"));
            payResult.setMsg("success");

        }else{
            payResult.setStatus(0);
            payResult.setMsg((String) object.get("msg"));
        }
        return payResult;
    }

    @Override
    public String notify(HttpServletRequest request, String urlOrderId) {
        com.alibaba.fastjson.JSONObject params = requestKitBean.getReqParamJSON();
        log.info("xdpay回调收到参数:"+params.toString());
        BigDecimal amount = new BigDecimal(String.valueOf(params.get("amount")));
        String orderId = (String) params.get("orderId");
        String platOrderId = (String) params.get("platOrderId");
        Integer status = (Integer) params.get("status");
        String sign = (String) params.get("sign");

        RechargeEntity rechargeEntity = rechargeService.getOne(new LambdaQueryWrapper<RechargeEntity>()
                .eq(RechargeEntity::getOrderCode, orderId)
                .eq(RechargeEntity::getStatus,0)
                .last("limit 0,1"));
        if(rechargeEntity==null){
            log.info("order not found");
            return "order not found";
        }
        SPlatformBankEntity sPlatformBankEntity = platformBankService.getOne(new LambdaQueryWrapper<SPlatformBankEntity>()
                .eq(SPlatformBankEntity::getName, rechargeEntity.getMoneytypename())
                .eq(SPlatformBankEntity::getDelFlag, 0)
                .last("limit 0,1"));
        String signStr = "";
        signStr = signStr + "amount=" + amount + "&";
        signStr = signStr + "orderId=" +orderId + "&";
        signStr = signStr + "platOrderId=" + platOrderId + "&";
        signStr = signStr + "status=" + status;
        log.info("xdpay回调收到参数sign:"+signStr);
        if(validateSign(signStr,sign,sPlatformBankEntity.getMerchantKey())){
            log.info("sign success");
            RechargeCheckForm rechargeCheckForm = new RechargeCheckForm();
            rechargeCheckForm.setRechargeId(rechargeEntity.getRechargeId());
            rechargeCheckForm.setStatus(status);
            rechargeCheckForm.setRemark("三方回调");
//            rechargeService.check(rechargeCheckForm);
            return "success";
        }else{
            log.info("sign error");
            return "sign error";
        }

    }

    @Override
    public String transfernotify(HttpServletRequest request, String urlOrderId,String ifCode) {
        com.alibaba.fastjson.JSONObject params = requestKitBean.getReqParamJSON();
        log.info("xdpay代付回调收到参数:"+params.toString());
        BigDecimal amount = new BigDecimal(String.valueOf(params.get("amount")));
        String orderId = (String) params.get("orderId");
        String platOrderId = (String) params.get("platOrderId");
        Integer status = (Integer) params.get("status"); // 1 成功 2失败
        String sign = (String) params.get("sign");

        CommissionEntity commissionEntity = commissionService.getOne(new LambdaQueryWrapper<CommissionEntity>()
                .eq(CommissionEntity::getOrderId, orderId)
                .eq(CommissionEntity::getStatus,3)
                .last("limit 0,1"));
        if(commissionEntity==null){
            log.info("order not found");
            return "order not found";
        }
        SPlatformBankEntity sPlatformBankEntity = platformBankService.getOne(new LambdaQueryWrapper<SPlatformBankEntity>()
                .eq(SPlatformBankEntity::getName, ifCode)
                .eq(SPlatformBankEntity::getDelFlag, 0)
                .last("limit 0,1"));
        String signStr = "";
        signStr = signStr + "amount=" + amount + "&";
        signStr = signStr + "orderId=" +orderId + "&";
        signStr = signStr + "platOrderId=" + platOrderId + "&";
        signStr = signStr + "status=" + status;
        log.info("xdpay回调收到参数sign:"+signStr);
        if(validateSign(signStr,sign,sPlatformBankEntity.getMerchantKey())){
            log.info("sign success");
            CommissionCheckForm form = new CommissionCheckForm();
            form.setId(commissionEntity.getId());
            form.setStatus(status);
            form.setRemark("xdpay回调");
//            commissionService.check(form,1L);
            return "success";
        }else{
            log.info("sign error");
            return "sign error";
        }

    }

    @Override
    public TransferResult transfer(CommissionEntity commission, BigDecimal amount, SPlatformBankEntity sPlatformBankEntity) {
        TransferResult transferResult = new TransferResult();
        String signStr = "";
        signStr = signStr + "amount=" + amount + "&";
        signStr = signStr + "bankAccount=" + commission.getAccount() + "&";
        signStr = signStr + "customName=" + commission.getName() + "&";
        signStr = signStr + "merchant=" + sPlatformBankEntity.getMerchant() + "&";
        signStr = signStr + "notifyUrl=" + getTransferNotifyUrl(sPlatformBankEntity.getName(),commission.getOrderId()) + "&";
        signStr = signStr + "orderId=" + commission.getOrderId() + "&";
        signStr = signStr + "payCode=" + sPlatformBankEntity.getTransferChannel() + "&";
        signStr = signStr + "remark=" + commission.getCode();
        signStr = md5Sign(signStr,sPlatformBankEntity.getMerchantKey());

        Map<String, Object> paramsMap = new HashMap<>();
        paramsMap.put("amount",amount);
        paramsMap.put("bankAccount",commission.getAccount());
        paramsMap.put("customName",commission.getName());
        paramsMap.put("merchant",sPlatformBankEntity.getMerchant());
        paramsMap.put("notifyUrl", getTransferNotifyUrl(sPlatformBankEntity.getName(),commission.getOrderId()));
        paramsMap.put("orderId",commission.getOrderId());
        paramsMap.put("payCode",sPlatformBankEntity.getTransferChannel());
        paramsMap.put("remark",commission.getCode());
        paramsMap.put("sign",signStr);
        log.info("xdpay代付请求URL:"+sPlatformBankEntity.getPayUrl());
        log.info("xdpay代付请求报文:"+paramsMap.toString());
        String result = postForm(sPlatformBankEntity.getTransferUrl(),paramsMap);
        log.info("xdpay代付请求返回报文:"+result);
        //{"code":400,"data":null,"desc":"business error","msg":"the channel is not open","success":false}
        JSONObject object = JSONUtil.parseObj(result);
        Boolean isSuccess = (Boolean) object.get("success");
        Integer code = (Integer) object.get("code");
        if(isSuccess&&code==200){
            JSONObject respData = JSONUtil.parseObj(object.get("data"));//应答信息
            transferResult.setStatus(1);
            transferResult.setMsg("success");

        }else{
            transferResult.setStatus(0);
            transferResult.setMsg((String) object.get("msg"));
        }
        return transferResult;
    }

    /**
     * 签名验证
     * 8/20/22
     */
    private Boolean validateSign(String signSource, String retsign,String md5Key)
    {
        signSource = signSource + "&key=" +md5Key;
        String signKey = MD5Util.md5(signSource);
        if (signKey.equals(retsign)) {
            return true;
        }
        return false;
    }

    /**
     * 签名
     * 8/20/22
     */
    private String md5Sign(String signStr,String md5key)
    {
        signStr = signStr + "&key=" +md5key;
        return MD5Util.md5(signStr);
    }
    private String postForm(String url,Map<String, Object> paramsMap){
        // 1. 创建HttpRequest对象 - 指定好 url 地址
        HttpRequest httpRequest = new HttpRequest(url);
        // 2. 设置请求方式，默认是GET请求
        httpRequest.setMethod(Method.POST);
        // 3. 设置请求参数   可通过 form表单方法 设置
        //    form方法有很多重载方法,可以一个一个参数设置，也可以将参数封装进一个map集合然后一块儿传

        httpRequest.form(paramsMap);
        // 4. 设置请求头
        //    请求头同样可以逐一设置，也可以封装到map中再统一设置
        //    设置的请求头是否可以覆盖等信息具体请看源码关于重载方法的说明
//        httpRequest.header("x-c-authorization","yourToken");
        // 5. 执行请求，得到http响应类
        HttpResponse execute = httpRequest.execute();

        // 6. 解析这个http响应类，可以获取到响应主体、cookie、是否请求成功等信息
        boolean ok = execute.isOk(); // 是否请求成功 判断依据为：状态码范围在200~299内

        if(ok){
            String body = execute.body();
            return body;
        }else{
            return "";
        }
    }
}
