package com.tcc.modules.pay.ff;

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
public class FfPaymentService implements IPaymentService {
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
        int timestamp = DateUtils.getCurrentTime();
        String order_date =DateUtils.format(new Date(timestamp* 1000L), DateUtils.DATE_TIME_PATTERN);
        Map<String,Object> paydata = new TreeMap<>();
        paydata.put("goods_name","NURO");
        paydata.put("mch_id",sPlatformBankEntity.getMerchant());
        paydata.put("mch_order_no",orderId);
        paydata.put("notify_url",getNotifyUrl(sPlatformBankEntity.getName(),orderId));
        paydata.put("order_date",order_date);
        paydata.put("page_url",getCallBack());
        paydata.put("pay_type",sPlatformBankEntity.getPayChannel());
        paydata.put("trade_amount",amount);
        paydata.put("version","1.0");
        paydata.put("sign",map2Md5(paydata, "=", "&", "&key=" + sPlatformBankEntity.getMerchantKey()));
        paydata.put("sign_type","MD5");
        log.info("ff请求URL:"+sPlatformBankEntity.getPayUrl());
        log.info("ff请求报文:"+paydata);
        String result = HttpRequest.post(sPlatformBankEntity.getPayUrl()).form(paydata)
                .timeout(5000)
                .execute()
                .body();
        log.info("ff请求返回报文:"+result);

        JSONObject object = JSONUtil.parseObj(result);
        String code = (String) object.get("respCode");
        if(code.equals("SUCCESS")){
            payResult.setStatus(1);
            payResult.setPayUrl((String) object.get("payInfo"));
            payResult.setMsg("success");
        }else{
            //{"code":"400","msg":"No less than 100 per transaction","data":[],"time":1696327002}
            payResult.setStatus(0);
            payResult.setMsg((String) object.get("tradeMsg"));
        }
        return payResult;
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

    @Override
    public String notify(HttpServletRequest request, String urlOrderId) {
        com.alibaba.fastjson.JSONObject params = requestKitBean.getReqParamJSON();
        log.info("ff回调收到参数:"+params.toString());
        String tradeResult = (String) params.get("tradeResult");
        String mchOrderNo = (String) params.get("mchOrderNo");

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
        map.remove("sign");map.remove("signType");
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
        log.info("ff代付回调收到参数:"+params.toString());
        String tradeResult = (String) params.get("tradeResult");
        String orderId = (String) params.get("merTransferId");


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
        map.remove("sign");map.remove("signType");
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
            form.setRemark("ff回调");
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
        paydata.put("apply_date",order_date);
        paydata.put("back_url",getTransferNotifyUrl(sPlatformBankEntity.getName(),commission.getOrderId()));
        paydata.put("bank_code",sPlatformBankEntity.getTransferChannel());
        paydata.put("mch_id",sPlatformBankEntity.getMerchant());
        paydata.put("mch_transferId",commission.getOrderId());
        paydata.put("receive_account",commission.getAccount());
        paydata.put("receive_name",commission.getName());
        paydata.put("remark",commission.getCode());
        paydata.put("transfer_amount",amount);
        paydata.put("sign",map2Md5(paydata, "=", "&", "&key=" + sPlatformBankEntity.getTransferKey()));
        paydata.put("sign_type","MD5");

        log.info("ff代付请求URL:"+sPlatformBankEntity.getTransferUrl());
        log.info("ff代付请求报文:"+paydata);
//        String result = postForm(sPlatformBankEntity.getTransferUrl(),paydata);
        String result = HttpRequest.post(sPlatformBankEntity.getTransferUrl()).form(paydata)
                .timeout(5000)
                .execute()
                .body();
        log.info("ff代付请求返回报文:"+result);
        JSONObject object = JSONUtil.parseObj(result);
        String code = (String) object.get("respCode");
        if(code.equals("SUCCESS")){
            transferResult.setStatus(1);
            transferResult.setMsg("success");
        }else{
            transferResult.setStatus(0);
            transferResult.setMsg((String) object.get("errorMsg"));
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
