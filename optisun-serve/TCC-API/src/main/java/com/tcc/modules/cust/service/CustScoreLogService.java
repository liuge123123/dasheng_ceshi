package com.tcc.modules.cust.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.entity.CustScoreLogEntity;
import com.tcc.modules.event.Event;
import com.tcc.modules.event.data.BalanceChangeData;
import com.tcc.modules.event.data.RebateData;
import com.tcc.modules.t.entity.TGoodsGradeEntity;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 客户积分变动表
 *
 * @author
 * @email
 * @date 2022-07-19 15:57:58
 */
public interface CustScoreLogService extends IService<CustScoreLogEntity> {

    PageUtils queryPage(Map<String, Object> params);


    PageUtils queryPageTeam(Map<String, Object> params);

    PageUtils queryPageByBack(Map<String, Object> params);

//    void  scorceChageAdd(Long custId, Integer direct, BigDecimal money);
    void scoreChange(Long custId, Integer direct, BigDecimal money,Integer type,String remark,Long sourceId);
    /**
     * 客户佣金余额变动
     * @param custId
     * @param direct 方向，1：增加，2：扣减
     * @param money
     * @param type 类型：
     *   1:系统赠送；
     *   2系统扣减;3签到赠送；4首冲赠送 5vip0 过期扣减注册金 6个人任务返佣7团队佣金8基金佣金9抽奖奖金10充值 11提现12:'基金分红',
     *   13:'基金本金',
     *   14:'商城购物',
     *   15:'充值奖励',
     *   16:'注册赠送体验金',
     *   17:'体验金购买基金',
     *   18:'宝箱领取'
     */
    void scoreChange(Long custId, Integer direct, BigDecimal money,Integer type,String remark);

//    CustEntity change(CustEntity cust, Event<BalanceChangeData> event);
//
//    CustEntity change(Event<BalanceChangeData> event);
//
//    CustEntity rebate(Event<RebateData> event);
//    CustEntity rebate(CustEntity cust, Event<RebateData> event);

    TGoodsGradeEntity setCustLevel(CustEntity cust);

    BigDecimal custRewardTotal(Long custId);

    BigDecimal custRewardToday(Long custId,Integer type);
}

