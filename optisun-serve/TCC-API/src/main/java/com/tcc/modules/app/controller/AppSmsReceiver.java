package com.tcc.modules.app.controller;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tcc.common.annotation.AccessLimit;
import com.tcc.common.utils.R;
import com.tcc.modules.base.AbstractAppController;
import com.tcc.modules.cust.entity.CommissionEntity;
import com.tcc.modules.cust.entity.DevicesEntity;
import com.tcc.modules.cust.entity.DevicesSmsEntity;
import com.tcc.modules.cust.service.CommissionService;
import com.tcc.modules.cust.service.DevicesService;
import com.tcc.modules.cust.service.DevicesSmsService;
import com.tcc.modules.s.entity.SSmsOriginalEntity;
import com.tcc.modules.s.service.SSmsOriginalService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/app/sms")
@Api("APP短信接收接口")
public class AppSmsReceiver extends AbstractAppController {


    @Autowired
    private DevicesSmsService devicesSmsService;

    @Autowired
    private DevicesService devicesService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private SSmsOriginalService mSmsService;

//    @ApiOperation("短信接收")
//    @PostMapping("/transmitSms")
//    public R rechargeCheck(@RequestBody SSmsEntity entity) {
//        entity.setIsDone(0);
//        entity.setReceiveTime(System.currentTimeMillis());
//        mSmsService.save(entity);
//        return R.ok();
//    }

    @ApiOperation("下分设备登录")
    @PostMapping("/login")
    public R login(@RequestBody DevicesEntity entity) {
        return R.ok().putData(devicesService.getOne(new LambdaQueryWrapper<DevicesEntity>()
                .eq(DevicesEntity::getMobile, entity.getMobile())
                .last("limit 0, 1")
        ));
    }


    @ApiOperation("代付订单")
    @PostMapping("/auditorder")
    public R auditOrder(@RequestBody Map<String, Object> form) {
        String mobile = MapUtil.getStr(form, "mobile");
        CommissionEntity commission =commissionService.getOne(new LambdaQueryWrapper<CommissionEntity>()
                .eq(CommissionEntity::getChannelName, mobile)
                .eq(CommissionEntity::getStatus,3)
                .last("limit 0, 1"));
        if(commission!=null){
            //修改状态为4 已发起付款
            commissionService.update(new LambdaUpdateWrapper<CommissionEntity>()
                    .eq(CommissionEntity::getId, commission.getId())
                    .set(CommissionEntity::getStatus, 4));
        }
        return R.ok().putData(commission);
    }

    @Async
    @ApiOperation("短信接收")
    @PostMapping("/commissionsms")
    public R commissionsms(@RequestBody List<DevicesSmsEntity> list) {
        devicesSmsService.parseSms(list);
        return R.ok();
    }


    @Async
    @ApiOperation("短信接收")
    @PostMapping("/transmitSms")
    public R rechargeCheck(@RequestBody List<SSmsOriginalEntity> list) {
//        mSmsService.saveBatch(list);
        mSmsService.parseSms(list);

        return R.ok();
    }


}
