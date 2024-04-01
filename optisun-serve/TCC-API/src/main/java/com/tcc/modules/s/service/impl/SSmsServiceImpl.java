package com.tcc.modules.s.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcc.modules.cust.entity.CommissionEntity;
import com.tcc.modules.cust.entity.RechargeEntity;
import com.tcc.modules.cust.form.RechargeCheckForm;
import com.tcc.modules.cust.service.RechargeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.s.dao.SSmsDao;
import com.tcc.modules.s.entity.SSmsEntity;
import com.tcc.modules.s.service.SSmsService;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service("sSmsService")
public class SSmsServiceImpl extends ServiceImpl<SSmsDao, SSmsEntity> implements SSmsService {

    @Autowired
    private RechargeService rechargeService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SSmsEntity> page = this.page(
                new Query<SSmsEntity>().getPage(params),
                new QueryWrapper<SSmsEntity>()
        );

        return new PageUtils(page);
    }

    @Transactional
    @Override
    public void dealAudit(SSmsEntity entity) {
        // z钱包不判断重复 靠金额和账号判断
        if(!entity.getWallet().equals("Zamani")){
            int count = rechargeService.count(new LambdaQueryWrapper<RechargeEntity>()
                    .eq(RechargeEntity::getTransid, entity.getTransId())
                    .eq(RechargeEntity::getMoneyFront, entity.getMoney())
                    //.eq(entity.getWallet()=="EVCPlus",RechargeEntity::getTransactionNo, entity.getTransDate())
                    .eq(RechargeEntity::getMoneytypename, entity.getWallet())
                    .eq(RechargeEntity::getStatus, 1));
            if (count != 0) {
                entity.setIsDone(2);
                entity.setDoneTime(System.currentTimeMillis());
                this.updateById(entity);
                return;
            }
        }

        RechargeEntity recharge = rechargeService.getOne(new LambdaQueryWrapper<RechargeEntity>()
                .eq(RechargeEntity::getTransid, entity.getTransId())
                .eq(RechargeEntity::getMoneyFront, entity.getMoney())
                //.eq(entity.getWallet()=="EVCPlus",RechargeEntity::getTransactionNo, entity.getTransDate())
                .eq(RechargeEntity::getMoneytypename, entity.getWallet())
                .eq(RechargeEntity::getStatus, 0)
                .orderByDesc(RechargeEntity::getRechargeId)
                .last("limit 0, 1"));
        if(recharge != null && !"USDT".equalsIgnoreCase(recharge.getMoneytypename())) {
            RechargeCheckForm form1 = new RechargeCheckForm();
            form1.setRechargeId(recharge.getRechargeId());
            form1.setStatus(1);
            form1.setRemark("自动审核通过,ID:"+entity.getId());
            rechargeService.check(form1);
            entity.setIsDone(1);
            entity.setDoneTime(System.currentTimeMillis());
            this.updateById(entity);
        }
    }

}