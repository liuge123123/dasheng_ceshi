package com.tcc.modules.job.task;

import com.tcc.common.utils.DateUtils;
import com.tcc.modules.cust.service.CustService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * vip0有效期14天，vip0到期到期后赠金没有了 赚取的佣金有，点击进去就提示需要升级到vip1才能继续完成任务
 */
@Log4j2
@Component("reduceVipTask")
public class ReduceVipTask  implements ITask{
    @Autowired
    private CustService custService;
    @Override
    public void run(String params) throws Exception {
//        custService.vip0Expire();
//        custService.reduceVip0();
    }
}
