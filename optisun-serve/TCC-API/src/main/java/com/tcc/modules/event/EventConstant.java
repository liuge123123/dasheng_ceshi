package com.tcc.modules.event;

public class EventConstant {

    /**
     * 余额变动方向
     */
    public enum Direct{
        ADD,
        SUB
    }

    /**
     * 余额变动类型
     */
    public enum BalanceChangeType{
        COMMON, // 共同
        RECHARGE, //充值
        WITHDRAW, // 提现
        WITHDRAW_FAIL, // 提现失败回退
        SIGN, // 签到
        FIRST_RECHAGE, // 首充赠送
        REGISTER, // 注册赠送
        FIRST_REEBATE, // 一级分销返佣
        SECOND_REEBATE, // 二级分销返佣
        THIRD_REEBATE, // 三级分销返佣
        REFUND_PROFIT, // 个人退税佣金
        GRABBING_PROFIT, // 个人抢单佣金
        REFUND_BUY, // 购买退税产品消耗
        BACK_RECHAGE, //后台充值赠送
        BACK_REDUCE, //后台扣减
        LUCKY_DRAW, // 抽奖
        CFT_SUB, // 理财扣减
        CFT_ADD // 理财结算
    }


}
