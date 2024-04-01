package com.tcc.modules.job.task;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.core.text.Convert;
import com.tcc.modules.s.entity.SSmsEntity;
import com.tcc.modules.s.service.SSmsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 清理长时间不处理的短信
 */
@Log4j2
@Component("smsSet3Task")
public class SmsSet3Task implements ITask {

    @Autowired
    private SSmsService mSmsService;

    @Override
    public void run(String params) throws Exception {
        int day = Convert.toInt(params, 3);
        long time = DateUtil.beginOfDay(new Date()).getTime() - (day * 24 * 3600 * 1000);
        mSmsService.update(new LambdaUpdateWrapper<SSmsEntity>()
                .eq(SSmsEntity::getIsDone, 0)
                .le(SSmsEntity::getReceiveTime, time)
                .set(SSmsEntity::getIsDone, 3)
                .set(SSmsEntity::getRemark, "处理" + DateUtil.formatDateTime(new Date(time)) + "之前的状态设置为3")
        );
    }

}
