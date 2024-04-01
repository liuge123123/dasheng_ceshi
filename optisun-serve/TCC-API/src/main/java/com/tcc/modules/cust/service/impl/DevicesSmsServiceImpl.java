package com.tcc.modules.cust.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tcc.common.utils.StringUtils;
import com.tcc.modules.cust.entity.CommissionEntity;
import com.tcc.modules.cust.entity.DevicesEntity;
import com.tcc.modules.cust.form.CommissionCheckForm;
import com.tcc.modules.cust.service.CommissionService;
import com.tcc.modules.s.entity.SSmsEntity;
import com.tcc.modules.s.entity.SSmsOriginalEntity;
import com.tcc.modules.s.service.SSmsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.cust.dao.DevicesSmsDao;
import com.tcc.modules.cust.entity.DevicesSmsEntity;
import com.tcc.modules.cust.service.DevicesSmsService;

import static com.tcc.common.utils.ShiroUtils.getUserId;

@Log4j2
@Service("devicesSmsService")
public class DevicesSmsServiceImpl extends ServiceImpl<DevicesSmsDao, DevicesSmsEntity> implements DevicesSmsService {
    @Autowired
    private CommissionService commissionService;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = Convert.toStr(params.get("key"));
        String transId = Convert.toStr(params.get("transId"));
        Integer parseStatus = Convert.toInt(params.get("parseStatus"));
        BigDecimal money = Convert.toBigDecimal(params.get("money"));
        Integer isDone = Convert.toInt(params.get("isDone"));
        IPage<DevicesSmsEntity> page = this.page(
                new Query<DevicesSmsEntity>().getPage(params),
                new LambdaQueryWrapper<DevicesSmsEntity>()
                        .eq(isDone!=null, DevicesSmsEntity::getIsDone, isDone)
                        .eq(money!=null, DevicesSmsEntity::getMoney, money)
                        .eq(parseStatus!=null, DevicesSmsEntity::getParseStatus, parseStatus)
                        .eq(StringUtils.isNotBlank(transId), DevicesSmsEntity::getTransId, transId)
                        .like(StringUtils.isNotBlank(key), DevicesSmsEntity::getAllContent, key)
                        .orderByDesc(DevicesSmsEntity::getId)
        );

        return new PageUtils(page);
    }

    @Override
    public void parseSms(List<DevicesSmsEntity> list) {
        for (int i = 0; i < list.size(); i++) {
            DevicesSmsEntity devicesSmsEntity = list.get(i);
            Map map = parseSms(devicesSmsEntity.getAllContent());
            if (map == null) {
                devicesSmsEntity.setParseStatus(1);
            } else {
                String transId = (String) map.get("transId");
                String money = (String) map.get("money");
                String wallet = (String) map.get("wallet");
                devicesSmsEntity.setParseStatus(2);
                devicesSmsEntity.setTransId(transId);
                devicesSmsEntity.setMoney(new BigDecimal(money));
                devicesSmsEntity.setWallet(wallet);
            }
            this.save(devicesSmsEntity);
            log.debug("tcc-devicessms:" + devicesSmsEntity);
            if(devicesSmsEntity.getParseStatus()==2){
                //查询提现订单 匹配短信
                CommissionEntity commission =commissionService.getOne(new LambdaQueryWrapper<CommissionEntity>()
                        .eq(CommissionEntity::getBankname, devicesSmsEntity.getWallet())
                        .eq(CommissionEntity::getStatus,4)
                        .eq(CommissionEntity::getDzMoney,devicesSmsEntity.getMoney())
                        .eq(CommissionEntity::getAccount,devicesSmsEntity.getTransId())
                        .last("limit 0, 1"));
                if(commission!=null){
                    //存在订单 进行下分操作
                    CommissionCheckForm form = new CommissionCheckForm();
                    form.setId(commission.getId());
                    form.setStatus(1);
                    form.setRemark("短信回调："+devicesSmsEntity.getId());
                    commissionService.check(form,1L);

                    this.update(new LambdaUpdateWrapper<DevicesSmsEntity>()
                            .eq(DevicesSmsEntity::getId, devicesSmsEntity.getId())
                            .set(DevicesSmsEntity::getIsDone, 1)
                            .set(DevicesSmsEntity::getDoneTime, System.currentTimeMillis()));
                }
            }
        }
    }
    //Vous avez envoye 1000 FCFA a 76492319 Cout 0FCFASolde 53119FCFA. Numero de transaction PP240317.1101.C04946
    private String prefix1 = "Vous avez envoye";// A钱包接受短信格式
    private Map parseSms(String sms) {
        sms = sms.replaceAll("\n\r", " ")
                .replaceAll("\n", " ")
                .replaceAll("  ", " ");
        if (sms.startsWith(prefix1)) {
            return getParams(sms.split(" "), 1);
        }
        return null;
    }

    private Map<String, String> getParams(String[] strArr, int i) {
        HashMap<String, String> hsshMap = new HashMap<>();
        switch (i) {
            case 1:
                hsshMap.put("wallet", "Airtel Money");
                hsshMap.put("money", strArr[3]);
                hsshMap.put("transId", strArr[6]);
                break;
        }
        return hsshMap;
    }

}