package com.tcc.modules.pay.pp;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcc.common.bean.RequestKitBean;
import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.MD5Util;
import com.tcc.common.utils.StringUtils;
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
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
@Service
public class PpPaymentService implements IPaymentService {
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

    private String map2String(Map<String, ?> reqFieldMap, String c, String a) {
        StringBuilder fieldStringBuffer = new StringBuilder();
        reqFieldMap.forEach((s, o) -> {
            if (!StringUtils.isBlank(o.toString())) {
                fieldStringBuffer.append(s).append(c).append(o).append(a);
            }
        });
        return reqFieldMap.isEmpty() ? "" : fieldStringBuffer.substring(0, fieldStringBuffer.length() - 1);
    }
    protected String map2Md5(Map<String, ?> reqFieldMap, String c, String a, String key) {
        log.info("ff加密字符串参数:"+map2String(reqFieldMap, c, a) + key);
        return MD5Util.md5(map2String(reqFieldMap, c, a) + key);
    }

    @Override
    public PayResult pay(String orderId, BigDecimal amount,SPlatformBankEntity sPlatformBankEntity) {
        PayResult payResult = new PayResult();
        Map<String,Object> paydata = new TreeMap<>();
        paydata.put("amount",amount);
        paydata.put("callbakUrl",getCallBack());
        paydata.put("currency","INR");
        paydata.put("goodsName","NURO");
        paydata.put("merchantOrderNo",orderId);
        paydata.put("merNo",sPlatformBankEntity.getMerchant());
        paydata.put("notifyUrl",getNotifyUrl(sPlatformBankEntity.getName(),orderId));
        paydata.put("payCode",sPlatformBankEntity.getPayChannel());
        paydata.put("sign",map2Md5(paydata, "=", "&", "&key=" + sPlatformBankEntity.getMerchantKey()));
        log.info("pp请求URL:"+sPlatformBankEntity.getPayUrl());
        log.info("pp请求报文:"+paydata);
        String result = HttpRequest.post(sPlatformBankEntity.getPayUrl()).form(paydata)
                .timeout(5000)
                .execute()
                .body();
        log.info("pp请求返回报文:"+result);

        JSONObject object = JSONUtil.parseObj(result);
        String code = (String) object.get("code");
        if(code.equals("SUCCESS")){
            payResult.setStatus(1);
            payResult.setPayUrl((String) object.get("payLink"));
            payResult.setMsg("success");
        }else{
            //{"code":"400","msg":"No less than 100 per transaction","data":[],"time":1696327002}
            payResult.setStatus(0);
            payResult.setMsg((String) object.get("msg"));
        }
        return payResult;
    }

    @Override
    public String notify(HttpServletRequest request, String urlOrderId) {
        com.alibaba.fastjson.JSONObject params = requestKitBean.getReqParamJSON();
        log.info("pp回调收到参数:"+params.toString());
        String tradeResult = (String) params.get("result");
        String mchOrderNo = (String) params.get("merchantOrderNo");

        RechargeEntity rechargeEntity = rechargeService.getOne(new LambdaQueryWrapper<RechargeEntity>()
                .eq(RechargeEntity::getOrderCode, mchOrderNo)
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

        String sign = (String) params.get("sign");
        Map<String,Object> map = (Map<String,Object>)params;
        map.remove("sign");
        Map<String, Object> sortedMap = new TreeMap<String, Object>(map);
        if(validateSign(map2Md5(sortedMap, "=", "&", "&key=" + sPlatformBankEntity.getMerchantKey()),sign)){
            log.info("sign success");
            if(tradeResult.equals("1")){
                RechargeCheckForm rechargeCheckForm = new RechargeCheckForm();
                rechargeCheckForm.setRechargeId(rechargeEntity.getRechargeId());
                rechargeCheckForm.setStatus(1);
                rechargeCheckForm.setRemark("三方回调");
                rechargeService.check(rechargeCheckForm);
            }
            return "success";
        }else{
            log.info("sign error");
            return "sign error";
        }

    }

    @Override
    public String transfernotify(HttpServletRequest request, String urlOrderId,String ifCode) {
        com.alibaba.fastjson.JSONObject params = requestKitBean.getReqParamJSON();
        log.info("pp代付回调收到参数:"+params.toString());
        String tradeResult = (String) params.get("result");
        String orderId = (String) params.get("merchantOrderNo");


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


        String sign = (String) params.get("sign");
        Map<String,Object> map = (Map<String,Object>)params;
        map.remove("sign");
        Map<String, Object> sortedMap = new TreeMap<String, Object>(map);
        if(validateSign(map2Md5(sortedMap, "=", "&", "&key=" + sPlatformBankEntity.getTransferKey()),sign)){
            log.info("sign success");
            CommissionCheckForm form = new CommissionCheckForm();
            form.setId(commissionEntity.getId());
            if(tradeResult.equals("1")){
                form.setStatus(1);
            }else{
                form.setStatus(2);
            }
            form.setRemark("pp回调");
            commissionService.check(form,1L);
            return "success";
        }else{
            log.info("sign error");
            return "sign error";
        }
    }

    @Override
    public TransferResult transfer(CommissionEntity commission, BigDecimal amount, SPlatformBankEntity sPlatformBankEntity) {
        TransferResult transferResult = new TransferResult();
        int timestamp = DateUtils.getCurrentTime();
        String order_date =DateUtils.format(new Date(timestamp*1000L), DateUtils.DATE_TIME_PATTERN);
        Map<String,Object> paydata = new TreeMap<>();
        paydata.put("accth",commission.getCode());
        paydata.put("amount",amount);
        paydata.put("bankCode",sPlatformBankEntity.getTransferChannel());
        paydata.put("currency","INR");
        paydata.put("customerAccount",commission.getAccount());
        paydata.put("customerName",commission.getName());
        paydata.put("merNo",sPlatformBankEntity.getMerchant());
        paydata.put("merchantOrderNo",commission.getOrderId());
        paydata.put("notifyUrl",getTransferNotifyUrl(sPlatformBankEntity.getName(),commission.getOrderId()));
        paydata.put("sign",map2Md5(paydata, "=", "&", "&key=" + sPlatformBankEntity.getTransferKey()));

        log.info("pp代付请求URL:"+sPlatformBankEntity.getTransferUrl());
        log.info("pp代付请求报文:"+paydata);
        String result = HttpRequest.post(sPlatformBankEntity.getTransferUrl()).form(paydata)
                .timeout(5000)
                .execute()
                .body();
        log.info("pp代付请求返回报文:"+result);
        JSONObject object = JSONUtil.parseObj(result);
        String code = (String) object.get("code");
        if(code.equals("SUCCESS")){
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
    private Boolean validateSign(String signSource, String retsign)
    {
        if (signSource.equals(retsign)) {
            return true;
        }
        return false;
    }
    
}
