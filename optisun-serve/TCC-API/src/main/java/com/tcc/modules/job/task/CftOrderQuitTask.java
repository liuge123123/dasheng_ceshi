package com.tcc.modules.job.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcc.common.utils.DateUtils;
import com.tcc.modules.l.entity.LOrderEntity;
import com.tcc.modules.l.service.LOrderService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 到期的退单的订单，退还本金
 */
@Log4j2
@Component("cftOrderQuitTask")
public class CftOrderQuitTask implements ITask {

    @Autowired
    private LOrderService lOrderService;

    @Override
    public void run(String params) throws Exception {
//        int currentTime = DateUtils.getCurrentTime();
//        List<LOrderEntity> list = lOrderService.list(new LambdaQueryWrapper<LOrderEntity>()
//                .select(LOrderEntity::getId)
//                .eq(LOrderEntity::getOrderStatus, 4)
//                .le(LOrderEntity::getLockTime, currentTime)
//        );
//        list.forEach(item -> {
//            try {
//                lOrderService.quitDone(item.getId());
//            } catch (Exception e) {
//                e.printStackTrace();
//                log.error(e.getMessage(), e);
//            }
//        });

    }

}
