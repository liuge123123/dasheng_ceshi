package com.tcc.common.utils;

/**
 * 系统参数相关Key
 *
 */
public class ConfigConstant {
    /**
     * 云存储配置KEY
     */
    public final static String CLOUD_STORAGE_CONFIG_KEY = "CLOUD_STORAGE_CONFIG_KEY";
    /**
     * 三级分销，返佣比例配置key
     */
    public final static String REBATE_CONFIG_KEY = "rebate.config";
    /**
     * 注册奖励
     */
    public final static String REGISTER_REWOARD_CONFIG_KEY = "register.reward.config";
    /**
     * 注册IP数量限制
     */
    public final static String REGISTER_IPLIMIT_CONFIG_KEY = "register.iplimit.config";
    /**
     * 注册用户初始等级
     */
    public final static String REGISTER_LEVEL_CONFIG_KEY = "register.level.config";
    /**
     * 首充奖励
     */
    public final static String FIRST_RECHARGE_REWARD_CONFIG_KEY = "first.recharge.reward.config";
    /**
     * 平台客服设置
     */
    public final static String KEFU_SETTING_CONFIG_KEY = "KEFU_SETTING";

    /**
     * 首页视频设置
     */
    public final static String VIDEO_SETTING_CONFIG_KEY = "VEDIO_SETTING";
    /**
     * 提现申请设置
     */
    public final static String CASH_SETTING_CONFIG_KEY = "CASH_SETTING";
    /**
     * 升级所需同等级VIP数量配置
     */
    public final static String UPGRADE_VIP_CONFIG_KEY = "upgrade.vip.config";

    public static final String UPGRADE_VIP_LEVEL_KEY = "upgrade.vip.level";
    /**
     * 登录验证码缓存key
     */
    public final static String VALIDATE_CODE_LOGIN = "VALIDATE_CODE:LOGIN:{}";
    public final static String REIGSTER_SMS_CODE = "REIGSTER_SMS_CODE"; //注册验证码
    public final static String FORGET_SMS_CODE = "FORGET_SMS_CODE"; //忘记密码验证码
    public final static String BANK_SMS_CODE = "BANK_SMS_CODE"; //绑卡验证码
    public static final String RECHARGE_REWARD_CONFIG_KEY = "recharge.reward.config";
    public static final String RECHARGE_COMMISSION_CONFIG_KEY = "recharge.commission.config";


    /**
     * redis key
     */
    public enum REDISKEY{
        TOKEN("user:token"),
        REFRESH_TOKEN("user:refresh_token"),
        H5_TOKEN("user:h5_token"),
        ;

        private String key;

        REDISKEY(String key){
            this.key = key;
        }

        public String getVal(String... args){
            StringBuffer buffer = new StringBuffer("crm:");
            buffer.append(key);
            if(args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    buffer.append(":").append(args[i]);
                }
            }
            return buffer.toString();
        }
    }
}
