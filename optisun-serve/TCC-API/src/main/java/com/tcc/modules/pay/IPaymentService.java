package com.tcc.modules.pay;

import com.tcc.modules.cust.entity.CommissionEntity;
import com.tcc.modules.s.entity.SPlatformBankEntity;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

public interface IPaymentService {

    /** 调起支付接口，并响应数据；  内部处理普通商户和服务商模式  **/
    PayResult pay(String orderId, BigDecimal amount,SPlatformBankEntity sPlatformBankEntity);

    String notify(HttpServletRequest request, String urlOrderId);

    TransferResult transfer(CommissionEntity commission, BigDecimal amount, SPlatformBankEntity sPlatformBankEntity);

    String transfernotify(HttpServletRequest request, String urlOrderId,String ifCode);
}
