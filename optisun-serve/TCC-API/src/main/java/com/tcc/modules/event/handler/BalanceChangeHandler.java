package com.tcc.modules.event.handler;

import cn.hutool.core.convert.Convert;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcc.common.utils.ConfigConstant;
import com.tcc.common.utils.DateUtils;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.entity.CustScoreLogEntity;
import com.tcc.modules.cust.service.CustScoreLogService;
import com.tcc.modules.cust.service.CustService;
import com.tcc.modules.event.Event;
import com.tcc.modules.event.EventConstant;
import com.tcc.modules.event.EventHandler;
import com.tcc.modules.event.data.BalanceChangeData;
import com.tcc.modules.sys.service.SysConfigService;
import com.tcc.modules.t.entity.TGoodsGradeEntity;
import com.tcc.modules.t.service.TGoodsGradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static com.tcc.modules.event.EventConstant.BalanceChangeType.REFUND_BUY;
import static com.tcc.modules.event.EventConstant.BalanceChangeType.REFUND_PROFIT;

@Slf4j
@Component
public class BalanceChangeHandler implements EventHandler {

    @Autowired
    private CustService custService;

    @Autowired
    private CustScoreLogService custScoreLogService;

    @Autowired
    private TGoodsGradeService tGoodsGradeService;

    @Autowired
    private SysConfigService sysConfigService;
//    ReentrantLock lock = new ReentrantLock(true);

    @Transactional(rollbackFor = Exception.class)
    @Override
    public <T> void handler(Event<T> event) {
        log.debug(JSONUtil.toJsonStr(event));
        scoreChagne((BalanceChangeData) event.getData());
    }

    public void scoreChagne(BalanceChangeData data) {
//        // BalanceChangeData data = (BalanceChangeData) event.getData();
//        CustEntity cust = custService.getOne(new LambdaQueryWrapper<CustEntity>()
//                .eq(CustEntity::getCustId, data.getCustId())
//                .last("for update"));
//        // 写明细
//        CustScoreLogEntity log = new CustScoreLogEntity();
//        log.setCustId(data.getCustId());
//        log.setDirect(data.getDirect().ordinal() + 1);
//        log.setType(data.getType().ordinal());
//        log.setScore(data.getChangeMoney());
//        log.setCreateTime(DateUtils.getCurrentTime());
//        log.setOrgId(cust.getOrgId());
//        log.setBeforeScore(cust.getBalance());
//        log.setSalesmanId(cust.getSalesmanId());
//        log.setIsNb(cust.getIsNb());
//        log.setSourceId(data.getSourceId());
//        if (EventConstant.Direct.ADD.equals(data.getDirect())) {
//            log.setAfterScore(cust.getBalance().add(data.getChangeMoney()));
//        } else {
//            log.setAfterScore(cust.getBalance().subtract(data.getChangeMoney()));
//        }
//        if (REFUND_BUY.equals(data.getType())) {
//            //TODO 退税订单冻结
//            log.setScore(data.getFrozenMoney());
//            log.setAfterScore(cust.getBalance().subtract(data.getFrozenMoney()));
//        } else if (REFUND_PROFIT.equals(data.getType())) {
//            //TODO 个人退税佣金
//            cust.setBalance(log.getAfterScore());
//            log.setScore(log.getScore().add(data.getFrozenMoney()));
//            log.setAfterScore(log.getAfterScore().add(data.getFrozenMoney()));
//            log.setRemark("退税佣金：" + data.getChangeMoney() + "," + "冻结金额返还：" + data.getFrozenMoney());
//        } else {
//            // 写账户余额
//            cust.setBalance(log.getAfterScore());
//        }
//        custScoreLogService.save(log);
//        // 处理冻结金额
//        if (data.getFrozenMoney().compareTo(BigDecimal.ZERO) > 0) {
//            if (EventConstant.Direct.ADD.equals(data.getDirect())) {
//                BigDecimal fMoney = cust.getFrozenAmount().subtract(data.getFrozenMoney());
//                if (fMoney.compareTo(BigDecimal.ZERO) < 0) {
//                    fMoney = BigDecimal.ZERO;
//                }
//                cust.setFrozenAmount(fMoney);
//                cust.setBalance(cust.getBalance().add(data.getFrozenMoney()));
//            } else {
//                cust.setFrozenAmount(cust.getFrozenAmount().add(data.getFrozenMoney()));
//                cust.setBalance(cust.getBalance().subtract(data.getFrozenMoney()));
//            }
//        }
//        // 处理会员等级
//        List<TGoodsGradeEntity> list = tGoodsGradeService.list(new LambdaQueryWrapper<TGoodsGradeEntity>()
//                .eq(TGoodsGradeEntity::getOrgId, cust.getOrgId())
//                .eq(TGoodsGradeEntity::getDelFlag, "0")
//                .orderByAsc(TGoodsGradeEntity::getMoney));
//        if (list != null && list.size() > 0) {
//            BigDecimal balance = cust.getBalance().add(cust.getFrozenAmount());
//            for (int i = 0; i < list.size(); i++) {
//                TGoodsGradeEntity level = list.get(i);
//                if (balance.compareTo(level.getMoney()) >= 0) {
//                    if (cust.getLevel() != level.getId()) {
//                        // 升级需要判定下级VIP人数是否符合要求
//                        if (level.getId() > cust.getLevel()) {
//                            String configStr = sysConfigService.getValue(cust.getOrgId(), ConfigConstant.UPGRADE_VIP_CONFIG_KEY);
//                            int config = Convert.toInt(configStr, 0);
//                            if (config > 0 && level.getId() > 6) {
//                                int count = custService.count(new LambdaQueryWrapper<CustEntity>()
//                                        .eq(CustEntity::getParentId, cust.getCustId())
//                                        .ge(CustEntity::getLevel, cust.getLevel())
//                                        .eq(CustEntity::getDelFlag, "0"));
//
//                                if (count < config) {
//                                    break;
//                                }
//                            }
//                        }
//                        cust.setLastLevel(cust.getLevel());
//                        cust.setLevel(level.getId());
//                        cust.setLevelUpTime(DateUtils.getCurrentTime());
//                        if (level.getMoney().compareTo(BigDecimal.ZERO) > 0) {
//                            cust.setIsVip(1);
//                        } else {
//                            cust.setIsVip(0);
//                        }
//                    }
//                }
//            }
//        }
//        // 其他处理
//        if (EventConstant.BalanceChangeType.FIRST_REEBATE.equals(data.getType())
//                || EventConstant.BalanceChangeType.SECOND_REEBATE.equals(data.getType())
//                || EventConstant.BalanceChangeType.THIRD_REEBATE.equals(data.getType())) {
//            // 分销返佣处理
//            cust.setNextMoney(cust.getNextMoney().add(data.getChangeMoney()));
//        } else if (EventConstant.BalanceChangeType.REFUND_PROFIT.equals(data.getType())) {
//            // 退税佣金处理
//            cust.setPersonBackMoney(cust.getPersonBackMoney().add(data.getChangeMoney()));
//        } else if (EventConstant.BalanceChangeType.GRABBING_PROFIT.equals(data.getType())) {
//            cust.setPersonRobMoney(cust.getPersonRobMoney().add(data.getChangeMoney()));
//        }
//        custService.updateById(cust);
    }
}
