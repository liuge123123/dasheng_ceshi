package com.tcc.modules.job.task;

import com.tcc.modules.s.entity.SSmsEntity;
import com.tcc.modules.s.service.SSmsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自动审核
 */
@Log4j2
@Component("autoAuditTask")
public class AutoAduitTask implements ITask {

    @Autowired
    private SSmsService mSmsService;

    @Override
    public void run(String params) throws Exception {
        List<SSmsEntity> smsList = mSmsService.lambdaQuery().eq(SSmsEntity::getIsDone, 0).orderByAsc(SSmsEntity::getReceiveTime).list();
        for (SSmsEntity smsEntity : smsList) {
            try {
                mSmsService.dealAudit(smsEntity);
            }catch (Exception e){
                log.error(e.getMessage(), e);
            }
        }
    }

}
