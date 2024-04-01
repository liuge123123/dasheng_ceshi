package com.tcc.modules.t.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.*;
import com.tcc.common.utils.core.text.Convert;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.entity.LevelLogEntity;
import com.tcc.modules.cust.service.CustService;
import com.tcc.modules.cust.service.LevelLogService;
import com.tcc.modules.g.entity.GOrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.tcc.modules.t.dao.TGoodsGradeDao;
import com.tcc.modules.t.entity.TGoodsGradeEntity;
import com.tcc.modules.t.service.TGoodsGradeService;


@Service("tGoodsGradeService")
public class TGoodsGradeServiceImpl extends ServiceImpl<TGoodsGradeDao, TGoodsGradeEntity> implements TGoodsGradeService {
    @Autowired
    private CustService custService;
    @Autowired
    private LevelLogService levelLogService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key= Convert.toStr(params.get("key"),"");
        String id= Convert.toStr(params.get("id"),"");
        IPage<TGoodsGradeEntity> page = this.page(
                new Query<TGoodsGradeEntity>().getPage(params),
                new QueryWrapper<TGoodsGradeEntity>().eq("del_flag","0").eq("org_id",params.get("orgId"))
                        .and(StringUtils.isNotBlank(key),wrapper -> wrapper.like("grade_name", key)).eq(StringUtils.isNotBlank(id),"id",id)
        );
        return new PageUtils(page);
    }

    /**
     *
     * @param cust 用户ID
     * @param totalMoney 总金额
     */
    @Override
    public void updateLevel(CustEntity cust, BigDecimal totalMoney) {
        TGoodsGradeEntity goodsGrade = this.getOne(new LambdaQueryWrapper<TGoodsGradeEntity>()
                .le(TGoodsGradeEntity::getMoney, totalMoney)
                .ge(TGoodsGradeEntity::getParameter1, totalMoney)
                .last("limit 0, 1"));
        if(goodsGrade!=null){
            //设置等级
            if(!cust.getLevel().equals(goodsGrade.getId())){
                CustEntity custEntity = new CustEntity();
                custEntity.setLevel(goodsGrade.getId());
                custEntity.setLastLevel(cust.getLevel());
                custService.update(custEntity,
                        new QueryWrapper<CustEntity>().eq("cust_id", cust.getCustId()));
                //用户升级明细
                LevelLogEntity levelLog = new LevelLogEntity();
                levelLog.setCustId(cust.getCustId());
                levelLog.setBeforLevel(cust.getLevel());
                levelLog.setAfterLevel(goodsGrade.getId());
                levelLog.setCreateTime(DateUtils.getCurrentTime());
                levelLog.setIsNb(cust.getIsNb());
                levelLog.setSalesmanId(cust.getSalesmanId());
                levelLog.setUseMoney(totalMoney);
                levelLogService.save(levelLog);
            }
        }
    }

}
