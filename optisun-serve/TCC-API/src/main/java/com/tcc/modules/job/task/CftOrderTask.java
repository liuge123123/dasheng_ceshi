package com.tcc.modules.job.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcc.common.utils.DateUtils;
import com.tcc.modules.l.entity.LOrderEntity;
import com.tcc.modules.l.service.LOrderService;
import com.tcc.modules.s.service.SSmsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 到期的理财订单 给用户自动结算
 */
@Log4j2
@Component("cftOrderTask")
public class CftOrderTask implements ITask {

    @Autowired
    private LOrderService lOrderService;

    @Override
    public void run(String params) throws Exception {
        int currentTime = DateUtils.getCurrentTime();
        List<LOrderEntity> list = lOrderService.list(new LambdaQueryWrapper<LOrderEntity>()
                .select(LOrderEntity::getId)
                .eq(LOrderEntity::getOrderStatus, 1)
                .le(LOrderEntity::getExpireTime, currentTime)
        );
        list.forEach(item -> {
            try {
                lOrderService.sale(item.getId());
            }catch (Exception e){
                e.printStackTrace();
                log.error(e.getMessage(), e);
            }
        });

    }

}
