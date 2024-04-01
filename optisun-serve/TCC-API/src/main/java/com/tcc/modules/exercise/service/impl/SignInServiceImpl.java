package com.tcc.modules.exercise.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.Constant;
import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.ResultCode;
import com.tcc.common.utils.StringUtils;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.service.CustScoreLogService;
import com.tcc.modules.cust.service.CustService;
import com.tcc.modules.exercise.dao.SignInDao;
import com.tcc.modules.exercise.entity.SignInEntity;
import com.tcc.modules.exercise.service.SignInService;
import com.tcc.modules.sys.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service("SignInService")
public class SignInServiceImpl extends ServiceImpl<SignInDao, SignInEntity> implements SignInService {

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private CustService custService;
    @Autowired
    private CustScoreLogService custScoreLogService;

    /**
     * 签到
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void excute(Long custId) {
        //1.校验每天只能签到一次
        Date now = new Date();
        Date beginTime = DateUtil.beginOfDay(now);
        Date endTime = DateUtil.endOfDay(now);
        int count = this.count(new LambdaQueryWrapper<SignInEntity>()
                .eq(SignInEntity::getCustId, custId)
                .ge(SignInEntity::getSignInTime, beginTime)
                .le(SignInEntity::getSignInTime, endTime)
                .last("limit 0, 1")
        );
        if (count > 0) {
            throw new WTDPException(ResultCode.RESULT_100019);
        }
        Date yes = DateUtil.offsetDay(now, -1);
        Date yesBeginTime = DateUtil.beginOfDay(yes);
        Date yesEndTime = DateUtil.endOfDay(yes);
        int yesCount = this.count(new LambdaQueryWrapper<SignInEntity>().eq(SignInEntity::getCustId, custId)
                .ge(SignInEntity::getSignInTime, yesBeginTime)
                .le(SignInEntity::getSignInTime, yesEndTime)
                .last("limit 0, 1")
        );
        // 计算连续签到天数及最大连续签到天数
        CustEntity cust = custService.getById(custId);
        int signCount = 0;
        int maxSignCount = 0;
        int totalSignCount = cust.getTotalSignIn() + 1;
        if (yesCount > 0) {
            signCount = cust.getContinuousSignIn() + 1;
            maxSignCount = cust.getMaxContinuousSignIn();
            if (signCount > maxSignCount) {
                maxSignCount = signCount;
            }
        } else {
            signCount = 1;
            maxSignCount = cust.getMaxContinuousSignIn();
        }
        custService.update(new LambdaUpdateWrapper<CustEntity>()
                .eq(CustEntity::getCustId, custId)
                .set(CustEntity::getContinuousSignIn, signCount)
                .set(CustEntity::getMaxContinuousSignIn, maxSignCount)
                .set(CustEntity::getTotalSignIn, totalSignCount)
        );

        //3.解析config表,获取赠送金额
        String configStr = sysConfigService.getValue(Constant.SUPER_ORG, "SIGN_IN_SETTING");
        //奖励积分
        Integer usualAward = 0;
        BigDecimal giftAmount = BigDecimal.ZERO;
        if (StringUtils.isNotBlank(configStr)) {
            JSONObject config = JSONUtil.parseObj(configStr);
            int usualDays = config.getInt("usualDays", 1);
            if(totalSignCount % usualDays == 0){
                usualAward = config.getInt("usualAward", 0);
            }
            if (config.containsKey("domains")) {
                JSONArray domains = JSONUtil.parseArray(config.get("domains"));
                for (int i = 0; i < domains.size(); i++) {
                    JSONObject item = domains.getJSONObject(i);
                    Integer days = item.getInt("days", -1);
                    BigDecimal num = item.getBigDecimal("num");
                    if (totalSignCount == days) {
                        giftAmount = num;
                        break;
                    }
                }
            }
        }
        //4.写签到记录表
        SignInEntity signIn = new SignInEntity();
        signIn.setCustId(custId);
        signIn.setSignInTime(now);
        signIn.setPointNum(usualAward);
        signIn.setGiftAmount(giftAmount);
        signIn.setCumulativeDays(signCount);
        signIn.setCreateTime(DateUtils.getCurrentTime());
        signIn.setUpdateTime(DateUtils.getCurrentTime());
        signIn.setDelFlag("0");
        this.save(signIn);

        Map<String, Object> params = new HashMap<>();
        params.put("custId", custId);
        boolean updateFlag = false;
        //5. 更新用户账户信息
        if(giftAmount.compareTo(BigDecimal.ZERO) > 0){
            updateFlag = true;
//            params.put("leftCommissionMoney", giftAmount);
//            params.put("totalCommissionMoney", giftAmount);
            params.put("otherCommissionMoney", giftAmount);
            custScoreLogService.scoreChange(custId,1, giftAmount,3,"签到赠送金额:"+giftAmount);
        }
        if(usualAward > 0){
            updateFlag = true;
            params.put("pointLeft", usualAward);
            params.put("pointTotal", usualAward);
        }
        if(updateFlag){
            custService.updateMoney(params);
        }
    }

}
