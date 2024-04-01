package com.tcc.common.utils;

/**
 * 常量
 */
public class Constant {

    /**
     * 系统机构
     */
    public static final Long SUPER_ORG = 0L;

    /**
     * 缓存key
     */
	public static final String REDIS_CACHE_KEY = "tcc-redis-cache";

    /**
     * 当前页码
     */
    public static final String PAGE = "page";
    /**
     * 每页显示记录数
     */
    public static final String LIMIT = "limit";
    /**
     * 排序字段
     */
    public static final String ORDER_FIELD = "sidx";
    /**
     * 排序方式
     */
    public static final String ORDER = "order";
    /**
     *  升序
     */
    public static final String ASC = "asc";
    /**
     *  降序
     */
    public static final String DESC = "desc";

	/**
	 * 菜单类型
	 */
    public enum MenuType {
        /**
         * 目录
         */
    	CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private int value;

        MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }
    
    /**
     * 定时任务状态
     * 
     */
    public enum ScheduleStatus {
        /**
         * 正常
         */
    	NORMAL(0),
        /**
         * 暂停
         */
    	PAUSE(1);

        private int value;

        ScheduleStatus(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
    }

    /**
     * 云服务商
     */
    public enum CloudService {
        /**
         * 七牛云
         */
        QINIU(1),
        /**
         * 阿里云
         */
        ALIYUN(2),
        /**
         * 腾讯云
         */
        QCLOUD(3),
        /**
         * 本地
         */
        LOCAL(4);

        private int value;

        CloudService(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


    public interface MQ{

        String EXCAHNGE_DIRECT = "ai.direct";
        String EXCAHNGE_FANOUT = "ai.fanout";
        String EXCAHNGE_TOPIC = "ai.topic";

    }

    /**
     * 业务流水号类型
     */
    public interface TICKET_NO_TYPE{

    }

    /**
     * 业务参数配置key
     */
    public interface CONFIG_KEY{
        /**
         * 短信配置
         */
        String SMS_SETTING = "SMS_SETTING";

        /**
         * 邮件配置
         */
        String MAIL_SETTING = "MAIL_SETTING";
    }

}
