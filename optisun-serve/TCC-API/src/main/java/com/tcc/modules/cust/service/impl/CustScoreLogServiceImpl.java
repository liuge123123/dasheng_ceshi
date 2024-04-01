package com.tcc.modules.cust.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.ConfigConstant;
import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;
import com.tcc.modules.cust.dao.CustDao;
import com.tcc.modules.cust.dao.CustScoreLogDao;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.entity.CustScoreLogEntity;
import com.tcc.modules.cust.service.CustScoreLogService;
import com.tcc.modules.cust.service.CustService;
import com.tcc.modules.event.EventPublisher;
import com.tcc.modules.sys.service.SysConfigService;
import com.tcc.modules.t.entity.TGoodsGradeEntity;
import com.tcc.modules.t.service.TGoodsGradeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service("custScoreLogService")
public class CustScoreLogServiceImpl extends ServiceImpl<CustScoreLogDao, CustScoreLogEntity> implements CustScoreLogService {

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private CustService custService;

    @Autowired
    private TGoodsGradeService tGoodsGradeService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private CustDao custDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Long custId = Convert.toLong(params.get("custId"));
        Integer[] types = Convert.toIntArray(params.get("types"));
        Integer type = Convert.toInt(params.get("type"));
        String remark = Convert.toStr(params.get("remark"));
        IPage<CustScoreLogEntity> page = this.page(
                new Query<CustScoreLogEntity>().getPage(params),
                new LambdaQueryWrapper<CustScoreLogEntity>()
                        .eq(custId != null, CustScoreLogEntity::getCustId, custId)
                        .in(types != null && types.length > 0, CustScoreLogEntity::getType, types)
                        .eq(type != null, CustScoreLogEntity::getType, type)
                        .likeRight(StrUtil.isNotBlank(remark), CustScoreLogEntity::getRemark, remark)
                        .orderByDesc(CustScoreLogEntity::getId)
        );
        return new PageUtils(page);
    }

    @Override
    public PageUtils queryPageByBack(Map<String, Object> params) {
        IPage page = new Query<>().getPage(params);
        List<Map<String, Object>> custList = this.baseMapper.getScorelist(page, params);
        page.setRecords(custList);
        return new PageUtils(page);
    }


    @Transactional(rollbackFor = Exception.class)
    @Override
    public void scoreChange(Long custId, Integer direct, BigDecimal money, Integer type, String remark,Long sourceId) {
        if (direct == 2) {
            money = money.multiply(new BigDecimal(-1));
        }
        CustEntity custEntity = custService.getById(custId);
        //TODO 客户充值余额变动记录
        CustScoreLogEntity scoreLogEntity = new CustScoreLogEntity();
        scoreLogEntity.setCustId(custId);
        scoreLogEntity.setDirect(direct);
        scoreLogEntity.setType(type);
        scoreLogEntity.setScore(money);
        //====lxy=====
        /*if(type == 13 || type == 10){ //退还 处理充值余额
            scoreLogEntity.setBeforeScore(custEntity.getLeftRechargeMoney());
            scoreLogEntity.setAfterScore(custEntity.getLeftRechargeMoney().add(money));
        }else{          //其它处理可提现佣金 余额
            scoreLogEntity.setBeforeScore(custEntity.getLeftCommissionMoney());
            scoreLogEntity.setAfterScore(custEntity.getLeftCommissionMoney().add(money));
        }*/
        scoreLogEntity.setBeforeScore(custEntity.getLeftCommissionMoney());
        scoreLogEntity.setAfterScore(custEntity.getLeftCommissionMoney().add(money));
        //====lxy=====
        scoreLogEntity.setRemark(remark);
        scoreLogEntity.setCreateTime(DateUtils.getCurrentTime());
        scoreLogEntity.setOrgId(custEntity.getOrgId());
        scoreLogEntity.setIsNb(custEntity.getIsNb());
        scoreLogEntity.setSalesmanId(custEntity.getSalesmanId());
        scoreLogEntity.setSourceId(sourceId);
        this.save(scoreLogEntity);
        //更新客户佣金余额及总佣金
        Map<String, Object> params = new HashMap<>();
        params.put("custId", custId);
        if (direct == 1 && type != 11 && type != 13 && type != 10) {
            // 提现驳回不加累计，理财退还本金不加累计
            params.put("totalCommissionMoney", money);
        }
        if (type == 12) {
            // 基金分红累计
            params.put("fundCommissionMoney", money);
        }
        /*if(type == 13 || type == 10 || type == 4){
            params.put("LeftRechargeMoney", money);
        }else{
            params.put("leftCommissionMoney", money);
        }*/
        params.put("leftCommissionMoney", money);
        custDao.updateMoney(params);
    }

    /**
     * 客户佣金余额变动
     *
     * @param custId
     * @param direct 方向，1：增加，2：扣减
     * @param money
     * @param type   类型：1:系统赠送；2系统扣减;3签到赠送；4首冲赠送 5vip0 过期扣减注册金 6个人任务返佣7团队佣金8基金佣金9抽奖奖金10充值 11提现，12基金分红 13退单(下单？)
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void scoreChange(Long custId, Integer direct, BigDecimal money, Integer type, String remark) {
        if (direct == 2) {
            money = money.multiply(new BigDecimal(-1));
        }
        CustEntity custEntity = custService.getById(custId);
        //TODO 客户充值余额变动记录
        CustScoreLogEntity scoreLogEntity = new CustScoreLogEntity();
        scoreLogEntity.setCustId(custId);
        scoreLogEntity.setDirect(direct);
        scoreLogEntity.setType(type);
        scoreLogEntity.setScore(money);
        //====lxy=====
        /*if(type == 13 || type == 10 || type == 1 || type == 2 || type == 4){//13退本金 10充值 20240226修改钱包余额
            scoreLogEntity.setBeforeScore(custEntity.getLeftRechargeMoney());
            scoreLogEntity.setAfterScore(custEntity.getLeftRechargeMoney().add(money));
        }else{          //其它处理可提现佣金 余额
            scoreLogEntity.setBeforeScore(custEntity.getLeftCommissionMoney());
            scoreLogEntity.setAfterScore(custEntity.getLeftCommissionMoney().add(money));
        }*/

        scoreLogEntity.setBeforeScore(custEntity.getLeftCommissionMoney());
        scoreLogEntity.setAfterScore(custEntity.getLeftCommissionMoney().add(money));

        //====lxy=====
        scoreLogEntity.setRemark(remark);
        scoreLogEntity.setCreateTime(DateUtils.getCurrentTime());
        scoreLogEntity.setOrgId(custEntity.getOrgId());
        scoreLogEntity.setIsNb(custEntity.getIsNb());
        scoreLogEntity.setSalesmanId(custEntity.getSalesmanId());
        this.save(scoreLogEntity);
        //更新客户佣金余额及总佣金
        Map<String, Object> params = new HashMap<>();
        params.put("custId", custId);
        if (direct == 1 && type != 11 && type != 13 && type != 1 && type != 10 &&type != 4) {
            // 提现驳回不加累计，理财退还本金不加累计
            params.put("totalCommissionMoney", money);
        }
        if (direct == 2 && type == 19) {
            //扣减提现余额 对应扣掉累计收益
            params.put("totalCommissionMoney", money);
        }

        if (type == 12 || type ==19) {
            // 基金分红累计
            params.put("fundCommissionMoney", money);
        }
        /* 20240226修改钱包余额
        if(type == 13 || type == 10 || type == 1 || type == 2 || type == 4){
            params.put("LeftRechargeMoney", money);
        }else{
            params.put("leftCommissionMoney", money);
        }*/
        params.put("leftCommissionMoney", money);

        custDao.updateMoney(params);
        //处理会员等级
        //vip2升级到vip3下级必须有5个vip同级 或高于他等级
        //setCustLevel(custEntity);
    }

    @Override
    public TGoodsGradeEntity setCustLevel(CustEntity cust) {
//         处理会员等级
//        List<TGoodsGradeEntity> list = tGoodsGradeService.list(new LambdaQueryWrapper<TGoodsGradeEntity>()
//                .eq(TGoodsGradeEntity::getOrgId, cust.getOrgId())
//                .eq(TGoodsGradeEntity::getDelFlag, "0")
//                .orderByAsc(TGoodsGradeEntity::getMoney));
        long newLevel = cust.getLevel();
//        if (list != null && list.size() > 0) {
//            BigDecimal balance = cust.getLeftCommissionMoney();
//            for (int i = 0; i < list.size(); i++) {
//                TGoodsGradeEntity level = list.get(i);
//                if (balance.compareTo(level.getMoney()) >= 0) {
//                    // 升级需要判定下级VIP人数是否符合要求
//                    if (level.getId() > cust.getLevel()) {
//                        // 人数
//                        String numStr = sysConfigService.getValue(cust.getOrgId(), ConfigConstant.UPGRADE_VIP_CONFIG_KEY);
//                        int num = Convert.toInt(numStr, 0);
//                        String levelConfig = sysConfigService.getValue(cust.getOrgId(), ConfigConstant.UPGRADE_VIP_LEVEL_KEY);
//                        int levelId = Convert.toInt(levelConfig, 4);
//                        if (num > 0 && level.getId() > levelId) {
//                            int count = custService.count(new LambdaQueryWrapper<CustEntity>()
//                                    .eq(CustEntity::getParentId, cust.getCustId())
//                                    .ge(CustEntity::getLevel, newLevel)
//                                    .eq(CustEntity::getDelFlag, "0"));
//
//                            if (count < num) {
//                                break;
//                            }
//                        }
//                    }
//                    newLevel = level.getId();
//                }
//            }
//        }
//        if (newLevel != cust.getLevel()) {
//            custService.update(new LambdaUpdateWrapper<CustEntity>()
//                    .eq(CustEntity::getCustId, cust.getCustId())
//                    .set(CustEntity::getLevel, newLevel)
//                    .set(CustEntity::getLastLevel, cust.getLevel())
//                    .set(CustEntity::getLevelUpTime, DateUtils.getCurrentTime())
//            );
//        }
        TGoodsGradeEntity tGoodsGradeEntity = tGoodsGradeService.getOne(new LambdaQueryWrapper<TGoodsGradeEntity>()
                .eq(TGoodsGradeEntity::getGradeLevel, cust.getLevel())
                .eq(TGoodsGradeEntity::getDelFlag, "0")
                .last("limit 0, 1"));
        return tGoodsGradeEntity;
    }

    @Override
    public BigDecimal custRewardTotal(Long custId) {
        return baseMapper.custRewardTotal(custId);
    }

    @Override
    public BigDecimal custRewardToday(Long custId, Integer type) {
        Long currentTime = System.currentTimeMillis();
        return baseMapper.custRewardToday(custId,type,DateUtils.getDailyStartTime(currentTime)/1000,DateUtils.getDailyEndTime(currentTime)/1000);
    }
//    /**
//     * 系统赠送与扣减、
//     */
//    @Transactional
//    @Override
//    public void  scorceChageAdd(Long custId, Integer direct, BigDecimal money) {
//        //TODO 增加
//       if(direct==1){
//           CustEntity  custEntity =  custService.getById(custId);
//            //TODO 客户充值余额变动记录
//           CustScoreLogEntity scoreLogEntity = new CustScoreLogEntity();
//           scoreLogEntity.setCustId(custId);
//           scoreLogEntity.setDirect(direct);
//           scoreLogEntity.setType(1);
//           scoreLogEntity.setScore(money);
//           scoreLogEntity.setBeforeScore(custEntity.getLeftRechargeMoney());
//           scoreLogEntity.setAfterScore(custEntity.getLeftRechargeMoney().add(money));
//           scoreLogEntity.setRemark("系统赠送金额："+money);
//           scoreLogEntity.setCreateTime(DateUtils.getCurrentTime());
//           scoreLogEntity.setOrgId(custEntity.getOrgId());
//           scoreLogEntity.setIsNb(custEntity.getIsNb());
//           scoreLogEntity.setSalesmanId(custEntity.getSalesmanId());
//           this.save(scoreLogEntity);
//           //TODO 客户充值余额增加
//           custEntity.setLeftRechargeMoney(custEntity.getLeftRechargeMoney().add(money));
//           custEntity.setUpdateTime(DateUtils.getCurrentTime());
//           custService.updateById(custEntity);
//       }
//       else{
//           CustEntity  custEntity =  custService.getById(custId);
//           if(custEntity.getLeftRechargeMoney().compareTo(money)==-1){
//               throw new WTDPException("余额不足，不能扣减");
//           }
//           //TODO 客户充值余额变动记录
//           CustScoreLogEntity scoreLogEntity = new CustScoreLogEntity();
//           scoreLogEntity.setCustId(custId);
//           scoreLogEntity.setDirect(direct);
//           scoreLogEntity.setType(2);
//           scoreLogEntity.setScore(money);
//           scoreLogEntity.setBeforeScore(custEntity.getLeftRechargeMoney());
//           scoreLogEntity.setAfterScore(custEntity.getLeftRechargeMoney().subtract(money));
//           scoreLogEntity.setRemark("系统扣减金额："+money);
//           scoreLogEntity.setCreateTime(DateUtils.getCurrentTime());
//           scoreLogEntity.setOrgId(custEntity.getOrgId());
//           scoreLogEntity.setIsNb(custEntity.getIsNb());
//           scoreLogEntity.setSalesmanId(custEntity.getSalesmanId());
//           this.save(scoreLogEntity);
//           //TODO 客户充值余额减少
//           custEntity.setLeftRechargeMoney(custEntity.getLeftRechargeMoney().subtract(money));
//           custEntity.setUpdateTime(DateUtils.getCurrentTime());
//           custService.updateById(custEntity);
//       }
//    }
//
//    @Override
//    public CustEntity change(CustEntity cust, Event<BalanceChangeData> event) {
//        log.debug(JSONUtil.toJsonStr(event));
//        BalanceChangeData data = event.getData();
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
//        if(EventConstant.Direct.ADD.equals(data.getDirect())){
//            log.setAfterScore(cust.getBalance().add(data.getChangeMoney()));
//        }else{
//            log.setAfterScore(cust.getBalance().subtract(data.getChangeMoney()));
//        }
//        this.save(log);
//        // 写账户余额
//        cust.setBalance(log.getAfterScore());
//        // 处理冻结金额
//        if(data.getFrozenMoney().compareTo(BigDecimal.ZERO) > 0){
//            if(EventConstant.Direct.ADD.equals(data.getDirect())){
//                BigDecimal fMoney = cust.getFrozenAmount().subtract(data.getFrozenMoney());
//                if(fMoney.compareTo(BigDecimal.ZERO) < 0){
//                    fMoney = BigDecimal.ZERO;
//                }
//                cust.setFrozenAmount(fMoney);
//                cust.setBalance(cust.getBalance().add(data.getFrozenMoney()));
//            }else{
//                cust.setFrozenAmount(cust.getFrozenAmount().add(data.getFrozenMoney()));
//                cust.setBalance(cust.getBalance().subtract(data.getFrozenMoney()));
//            }
//        }
//        // 处理会员等级
//        List<TGoodsGradeEntity> list = tGoodsGradeService.list(new LambdaQueryWrapper<TGoodsGradeEntity>()
//                .eq(TGoodsGradeEntity::getOrgId, cust.getOrgId())
//                .eq(TGoodsGradeEntity::getDelFlag, "0")
//                .orderByAsc(TGoodsGradeEntity::getMoney));
//        if(list != null && list.size() > 0){
//            BigDecimal balance = cust.getBalance().add(cust.getFrozenAmount());
//            for (int i = 0; i < list.size(); i++) {
//                TGoodsGradeEntity level = list.get(i);
//                if(balance.compareTo(level.getMoney()) >= 0){
//                    if(cust.getLevel() != level.getId()) {
//                        // 升级需要判定下级VIP人数是否符合要求
//                        if(level.getId() > cust.getLevel()) {
//                            String configStr = sysConfigService.getValue(cust.getOrgId(), ConfigConstant.UPGRADE_VIP_CONFIG_KEY);
//                            int config = Convert.toInt(configStr, 0);
//                            if (config > 0 && level.getId() > 6 ) {
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
//        if(EventConstant.BalanceChangeType.FIRST_REEBATE.equals(data.getType())
//                ||  EventConstant.BalanceChangeType.SECOND_REEBATE.equals(data.getType())
//                ||  EventConstant.BalanceChangeType.THIRD_REEBATE.equals(data.getType())){
//            // 分销返佣处理
//            cust.setNextMoney(cust.getNextMoney().add(data.getChangeMoney()));
//        }else if(EventConstant.BalanceChangeType.REFUND_PROFIT.equals(data.getType())){
//            // 退税佣金处理
//            cust.setPersonBackMoney(cust.getPersonBackMoney().add(data.getChangeMoney()));
//        }else if(EventConstant.BalanceChangeType.GRABBING_PROFIT.equals(data.getType())){
//            cust.setPersonRobMoney(cust.getPersonRobMoney().add(data.getChangeMoney()));
//        }
//        custService.updateById(cust);
//        return cust;
//    }
//
//    @Override
//    public CustEntity change(Event<BalanceChangeData> event) {
//        CustEntity cust = custService.getOne(new LambdaQueryWrapper<CustEntity>().eq(CustEntity::getCustId, event.getData().getCustId()).last("for update"));
//        return change(cust, event);
//    }
//
//    @Transactional
//    @Override
//    public CustEntity rebate(Event<RebateData> event) {
//        log.debug(JSONUtil.toJsonStr(event));
//        // 三级分销计算上级返佣
//        CustEntity cust = custService.getOne(new LambdaQueryWrapper<CustEntity>().eq(CustEntity::getCustId, event.getData().getCustId()).last("for update"));
//        rebate(cust, event);
//        return cust;
//    }
//
//    @Transactional
//    @Override
//    public CustEntity rebate(CustEntity cust, Event<RebateData> event) {
//        RebateData data = event.getData();
//        if(StrUtil.isNotBlank(cust.getParentId()) && !"0".equals(cust.getParentId())) {
//            // 返佣比例
//            String configStr = sysConfigService.getValue(cust.getOrgId(), REBATE_CONFIG_KEY);
//            if (StrUtil.isNotBlank(configStr)) {
//                JSONObject config = JSONUtil.parseObj(configStr);
//                boolean open = config.getBool("open", false);
//                if (open) {
//                    BigDecimal first = config.getBigDecimal("first");
//                    BigDecimal second = config.getBigDecimal("second");
//                    BigDecimal third = config.getBigDecimal("third");
//                    // 一级返佣
//                    CustEntity fistCust = custService.getById(cust.getParentId());
//                    rebate(fistCust, first, data.getMoney(), EventConstant.BalanceChangeType.FIRST_REEBATE,data.getOrderId());
//                    if(StrUtil.isNotBlank(fistCust.getParentId()) && !"0".equals(fistCust.getParentId())){
//                        // 二级返佣
//                        CustEntity secondCust = custService.getById(fistCust.getParentId());
//                        rebate(secondCust, second, data.getMoney(), EventConstant.BalanceChangeType.SECOND_REEBATE,data.getOrderId());
//                        if(StrUtil.isNotBlank(secondCust.getParentId()) && !"0".equals(secondCust.getParentId())){
//                            // 三级级返佣
//                            CustEntity thirdCust = custService.getById(secondCust.getParentId());
//                            rebate(thirdCust, third, data.getMoney(), EventConstant.BalanceChangeType.THIRD_REEBATE,data.getOrderId());
//                        }
//                    }
//                }
//            }
//        }
//        return cust;
//    }
//
//    private void rebate(CustEntity cust, BigDecimal rate, BigDecimal money, EventConstant.BalanceChangeType balanceChangeType,Long orderId){
//        BigDecimal profit = money.multiply(rate).divide(new BigDecimal(100));
//        if(profit.compareTo(new BigDecimal(0)) > 0){
//            // 用固话账户余额变动
//            BalanceChangeData balanceChangeData = new BalanceChangeData();
//            balanceChangeData.setCustId(cust.getCustId());
//            balanceChangeData.setType(balanceChangeType);
//            balanceChangeData.setDirect(EventConstant.Direct.ADD);
//            balanceChangeData.setChangeMoney(profit);
//            balanceChangeData.setFrozenMoney(new BigDecimal(0));
//            balanceChangeData.setSourceId(orderId);
////            eventPublisher.publish(new Event("rebate", EventType.balanceChange, balanceChangeData));
//            this.change(cust, new Event("rebate", EventType.balanceChange, balanceChangeData));
//        }
//    }

}