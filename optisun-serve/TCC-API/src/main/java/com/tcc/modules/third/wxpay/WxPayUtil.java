package com.tcc.modules.third.wxpay;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.Constant;
import com.tcc.common.utils.R;
import com.tcc.config.CommonConfig;
import com.tcc.modules.sys.service.SysConfigService;
import com.github.binarywang.wxpay.bean.request.WxPayRefundRequest;
import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayRefundResult;
import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.security.NoSuchAlgorithmException;
import java.util.*;

@Slf4j
@Component
public class WxPayUtil {

    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private SysConfigService sysConfigService;

    private WxPayConfigProperties config;

    public void init(Long  orgId){
        String configStr = sysConfigService.getValue(orgId, "PAY.CONFIG.WX");
        if(StrUtil.isNotBlank(configStr)){
            config = JSONUtil.toBean(configStr, WxPayConfigProperties.class);
            WxPayConfig payConfig = new WxPayConfig();
            payConfig.setAppId(StringUtils.trimToNull(config.getAppId()));
            payConfig.setMchId(StringUtils.trimToNull(config.getMchId()));
            payConfig.setMchKey(StringUtils.trimToNull(config.getMchKey()));
            payConfig.setSubAppId(StringUtils.trimToNull(config.getSubAppId()));
            payConfig.setSubMchId(StringUtils.trimToNull(config.getSubMchId()));
            payConfig.setKeyPath(StringUtils.trimToNull(config.getKeyPath()));
            // 可以指定是否使用沙箱环境
            payConfig.setUseSandboxEnv(false);
            wxPayService.setConfig(payConfig);
        }
    }

    /**
     * 微信App订单支付
     * @param map
     * @param payType  JSAPI 微信小程序支付， APP  app支付
     * @return
     * @throws WxPayException
     */
    public Object orderWeChatPay(Map<String, Object> map, String payType) throws WxPayException, NoSuchAlgorithmException {
        CommonConfig commonConfig = CommonConfig.getConfig(Constant.SUPER_ORG);
        String orderNum = Convert.toStr(map.get("orderNo"));
        String money = Convert.toStr(map.get("money"));
        String openId= Convert.toStr(map.get("openid"));
        String attach= Convert.toStr(map.get("attach")); //支付类型 nomal 一般订单,sharing 拼团订单
        //设置签名
        SortedMap<String, String> parameters = new TreeMap<String, String>();
        parameters.put("appid", config.getAppId());
        parameters.put("mch_id", config.getMchId());
        parameters.put("nonce_str",WxPayCommonUtil.create_nonce_str());
        parameters.put("body", "订单支付");
        parameters.put("out_trade_no",orderNum); //订单id
        parameters.put("fee_type", "CNY");
        parameters.put("total_fee", money);
        parameters.put("spbill_create_ip", commonConfig.getServerIp());
        parameters.put("notify_url",config.getNotifyUrl());
        parameters.put("trade_type", payType);
        parameters.put("attach",attach);
        //设置签名
        String sign = WxPayCommonUtil.createSign(config.getMchKey(),"UTF-8", parameters);
        //微信支付统一下单
        Date now = new Date();
        String startTime = DateUtil.format(now, DatePattern.PURE_DATETIME_FORMAT);
        String endTime = DateUtil.format(DateUtil.offsetMinute(now, 30), DatePattern.PURE_DATETIME_FORMAT);
        WxPayUnifiedOrderRequest orderRequest = new WxPayUnifiedOrderRequest();
        orderRequest.setAppid(config.getAppId());
        orderRequest.setMchId(config.getMchId());
        orderRequest.setDeviceInfo("WEB");
        orderRequest.setNonceStr(WxPayCommonUtil.create_nonce_str());
        orderRequest.setSign(sign);
        orderRequest.setBody("订单支付");
        orderRequest.setOutTradeNo(orderNum);
        orderRequest.setTotalFee(Convert.toInt(money));
        orderRequest.setSpbillCreateIp(commonConfig.getServerIp());
        orderRequest.setNotifyUrl(config.getNotifyUrl());
        orderRequest.setTradeType(payType);
        orderRequest.setOpenid(openId);
        orderRequest.setAttach(attach);
        Object s = wxPayService.createOrder(orderRequest);
        Map<String,Object> result=new HashMap<>();
        result.put("payment",s);
        return result;
    }

    /***
     * 退款
     * @param orderNo 订单号
     * @param totalMoney 总金额
     * @param amount  退款金额(单位:元)
     * @param refundReason 退款原因
     * @return
     */
    public Object refund(String orderNo,double totalMoney, double amount, String refundReason){
        if(StrUtil.isBlank(orderNo)){
            throw new WTDPException("订单编号不能为空");
        }
        if(amount <= 0){
            throw new WTDPException("退款金额必须大于0");
        }
        WxPayRefundResult refundResult = new WxPayRefundResult();
        WxPayRefundRequest wxPayRefundRequest=new WxPayRefundRequest();
        wxPayRefundRequest.setOutTradeNo(orderNo);
        wxPayRefundRequest.setOutRefundNo(orderNo);
        wxPayRefundRequest.setTotalFee(Convert.toInt(totalMoney*100));
        wxPayRefundRequest.setRefundFee(Convert.toInt(amount*100));
        wxPayRefundRequest.setRefundDesc(refundReason);

        try {
            refundResult = wxPayService.refund(wxPayRefundRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String return_code = refundResult.getReturnCode();// //返回状态码
        String return_msg = refundResult.getReturnMsg();//     //返回信息
        if ("SUCCESS".equals(return_code)) {
            String result_code =refundResult.getResultCode();//     //业务结果
            String err_code_des =refundResult.getErrCodeDes();//     //错误代码描述
            if ("SUCCESS".equals(result_code)) {
                //表示退款申请接受成功，结果通过退款查询接口查询
                //修改用户订单状态为退款申请中或已退款。退款异步通知根据需求，可选
                //
                return R.ok().put("data","退款申请成功");
            } else {
                log.info("订单号:{}错误信息:{}", orderNo, err_code_des);
                return R.error().put("data",err_code_des);
            }
        } else {
            log.info("订单号:{}错误信息:{}", orderNo, return_msg);
            return R.error().put("data",return_msg);
        }
    }

}
