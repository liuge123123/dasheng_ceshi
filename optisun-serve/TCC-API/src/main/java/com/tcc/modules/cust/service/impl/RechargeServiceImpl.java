package com.tcc.modules.cust.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.*;
import com.tcc.modules.app.dao.AppAccountDao;
import com.tcc.modules.app.utils.LuckUtils;
import com.tcc.modules.cust.dao.RechargeDao;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.entity.CustScoreLogEntity;
import com.tcc.modules.cust.entity.RechargeEntity;
import com.tcc.modules.cust.form.RechargeCheckForm;
import com.tcc.modules.cust.service.CustScoreLogService;
import com.tcc.modules.cust.service.CustService;
import com.tcc.modules.cust.service.RechargeService;
import com.tcc.modules.event.Event;
import com.tcc.modules.event.EventConstant;
import com.tcc.modules.event.EventPublisher;
import com.tcc.modules.event.EventType;
import com.tcc.modules.event.data.BalanceChangeData;
import com.tcc.modules.sys.service.SysConfigService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("rechargeService")
public class RechargeServiceImpl extends ServiceImpl<RechargeDao, RechargeEntity> implements RechargeService {

    @Autowired
    private EventPublisher eventPublisher;
    @Autowired
    private SysConfigService sysConfigService;
    @Autowired private CustService custService;
    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private CustScoreLogService custScoreLogService;

    @Autowired
    private LuckUtils luckUtils;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Long custId = Convert.toLong(params.get("custId"));
        IPage<RechargeEntity> page = this.page(
                new Query<RechargeEntity>().getPage(params),
                new LambdaQueryWrapper<RechargeEntity>()
                        .eq(custId != null, RechargeEntity::getCustId, custId)
                        .orderByDesc(RechargeEntity::getRechargeId)
        );
        return new PageUtils(page);
    }

    /***
     * 获取充值记录
     * @param params
     * @return
     */
    @Override
    public PageUtils getRechargelist(@Param("condition") Map<String, Object> params){
        IPage page = new Query<>().getPage(params);
        List<Map<String,Object>> custList=this.baseMapper.getRechargelist(page, params);
        page.setRecords(custList);
        return new PageUtils(page);
    }
    /***
     *  获取充值汇总
     * @return
     */
    public Map getCount(@Param("condition") Map<String, Object> params) {
        Map result = new HashMap();
        BigDecimal successCount = this.baseMapper.getSuccessCount(params);
        result.put("successCount", successCount);
        return result;
    }
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void check(RechargeCheckForm form){
        RechargeEntity model = this.getById(form.getRechargeId());
         if(model==null){
             throw new WTDPException("该充值记录不存在");
         }
         if(model.getStatus()!=0){
             throw new WTDPException("已审核无需重复审核");
         }

        int count = rechargeService.count(new LambdaQueryWrapper<RechargeEntity>()
                .eq(RechargeEntity::getCustId, model.getCustId())
                .eq(RechargeEntity::getStatus, 1));
        model.setStatus(form.getStatus());
        model.setCheckRemark(form.getRemark());
        model.setCheckTime(DateUtils.getCurrentTime());
        model.setUpdateTime(DateUtils.getCurrentTime());
        this.updateById(model);
        CustEntity cust = custService.getById(model.getCustId());
        //已支付
        if(form.getStatus()==1){
            //TODO 更新客户充值余额与总金额
            Map<String,Object> reParams = new HashMap<>();
            reParams.put("custId",model.getCustId());
            reParams.put("totalRechargeMoney",model.getAmount());
            //reParams.put("LeftRechargeMoney",model.getAmount());
            if(luckUtils.checkAble(LuckUtils.RECHARGEABLE, cust.getLevel(), model.getAmount())){
                // 符合条件的充值赠送抽奖次数
                reParams.put("luckLeftNum", 1);
                reParams.put("luckTotalNum", 1);
            }
            custService.updateMoney(reParams);
            //TODO 佣金余额变化记录 更新佣金余额
            custScoreLogService.scoreChange(model.getCustId(),1,model.getAmount(),10,"用户充值，充值金额为："+model.getAmount(),model.getRechargeId());
            // 充值奖励
            rechargeReward(model);
            if(count == 0){
                // 首充处理
                rechargeFirst(cust, model);
            }
            // 上级返佣
            rechargeCommission(cust, model);
            // 解锁用户任务状态
//            int taskLimitConfig = Convert.toInt(sysConfigService.getValue(Constant.SUPER_ORG, "task.limit.config"), 0);
//            if(taskLimitConfig == 1
//                    && cust.getTaskLimit() == 1
//                    && model.getAmount().compareTo(new BigDecimal(1000)) >= 0){
//                custService.update(new LambdaUpdateWrapper<CustEntity>()
//                        .eq(CustEntity::getCustId, model.getCustId())
//                        .set(CustEntity::getTaskLimit, 0)
//                );
//            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void reject(RechargeCheckForm form) {
        RechargeEntity model = this.getById(form.getRechargeId());
        if(model==null){
            throw new WTDPException("该充值记录不存在");
        }
        if(model.getStatus() != 1){
            throw new WTDPException("已审核无需重复审核");
        }
        // 撤销首充金额
        BigDecimal money = model.getAmount();
        if(model.getIsFirst() == 1){
            CustScoreLogEntity log = custScoreLogService.getOne(new LambdaQueryWrapper<CustScoreLogEntity>()
                    .eq(CustScoreLogEntity::getCustId, model.getCustId())
                    .eq(CustScoreLogEntity::getType, 4)
                    .last("limit 0, 1")
            );
            if(log != null){
                money = money.add(log.getScore());
            }
        }
        CustEntity cust = custService.getById(model.getCustId());
        if(cust.getLeftCommissionMoney().compareTo(money) < 0){
            throw  new WTDPException("用户账户余额不足，无法驳回");
        }
        // 修改审核状态
        model.setStatus(2);
        model.setCheckRemark(form.getRemark());
        model.setUpdateTime(DateUtils.getCurrentTime());
        this.updateById(model);
        //TODO 更新客户充值余额与总金额
        Map<String,Object> reParams = new HashMap<>();
        reParams.put("custId",model.getCustId());
        reParams.put("money",money.multiply(new BigDecimal(-1)));
        this.baseMapper.rechargeUpdateCust(reParams);
        //TODO  回退账户 余额减少
        custScoreLogService.scoreChange(model.getCustId(),2,money,10,"充值驳回扣减金额："+money);
    }

    /***
     * 导出
     * @return
     */
    @Override
    public void   downExcel(HttpServletResponse response, Map<String,Object> params) throws IOException {
        List<Map<String,Object>> list=this.baseMapper.getRechargelist(params);
        String sheetName = "充值记录列表";
        String fileName = ResultHelper.newId()+"_record.xls";
        // 通过工具类创建writer，默认创建xls格式
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.addHeaderAlias("orderCode", "订单号");
        writer.addHeaderAlias("custId", "用户编号");
        writer.addHeaderAlias("custName", "用户名称");
        writer.addHeaderAlias("isFirst", "是否首充");
        writer.addHeaderAlias("moneyFront", "换算前充值金额");
        writer.addHeaderAlias("amount", "充值金额");
        writer.addHeaderAlias("type", "充值类型");
        writer.addHeaderAlias("moneytypename", "充值名称");
        writer.addHeaderAlias("transid", "交易id");
        writer.addHeaderAlias("salesmanId", "业务员id");
        writer.addHeaderAlias("fee", "对美元汇率");
        writer.addHeaderAlias("status", "状态");
        writer.addHeaderAlias("createTime", "创建时间");
        writer.addHeaderAlias("updateTime", "修改时间");
        writer.addHeaderAlias("platformBankName", "钱包类型");
        writer.addHeaderAlias("checkRemark", "备注");
        for (Map<String,Object> item:list) {
            item.remove("name");
            item.remove("rechargeId");
            if(Convert.toInt(item.get("isFirst"))==1){
                item.put("isFirst","是");
            }
            else{
                item.put("isFirst","否");
            }
            int type = Convert.toInt(item.get("type"));
            if(type==1){
                item.put("type","充值-web端");
            }
            else if(type==2){
                item.put("type","赠送");
            }
            else if(type==3){
                item.put("type","签到");
            }
            else if(type==4){
                item.put("type","充值-后台");
            }
            int status =  Convert.toInt(item.get("status"));
            if(status==0){
                item.put("status","待审核");
            }
            else  if(status==1){
                item.put("status","已付款");
            }
            else  if(status==2){
                item.put("status","已驳回");
            }
            item.put("createTime",DateUtils.timestampToString(Convert.toInt(item.get("createTime"),0)));
            item.put("updateTime",DateUtils.timestampToString(Convert.toInt(item.get("updateTime"),0)));
        }
        writer.setColumnWidth(0,20);
        writer.setColumnWidth(1,20);
        writer.setColumnWidth(2,20);
        writer.setColumnWidth(3,20);
        writer.setColumnWidth(4,20);
        writer.setColumnWidth(5,20);
        writer.setColumnWidth(6,15);
        writer.setColumnWidth(7,20);
        writer.setColumnWidth(8,40);
        writer.setColumnWidth(9,20);
        writer.setColumnWidth(10,20);
        writer.setColumnWidth(11,20);
        writer.setColumnWidth(12,20);
        writer.setColumnWidth(13,20);
        writer.setColumnWidth(14,20);
        writer.setColumnWidth(15,50);
        // 一次性写出内容，使用默认样式，强制输出标题
        writer.write(list, true);
        //out为OutputStream，需要写出到的目标流
        // 默认的，未添加alias的属性也会写出，如果想只写出加了别名的字段，可以调用此方法排除之
        writer.setOnlyAlias(true);
        //response为HttpServletResponse对象
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        ServletOutputStream out = response.getOutputStream();
        writer.flush(out, true);
        // 关闭writer，释放内存
        writer.close();
        //此处记得关闭输出Servlet流
        IoUtil.close(out);
    }

    @Override
    public BigDecimal getSumBySuccess(Long custId) {
        return baseMapper.getSumBySuccess(custId);
    }

    @Override
    public Integer getCountBySuccess(Long custId) {
        return baseMapper.getCountBySuccess(custId);
    }

    /**
     * 充值奖励
     * @param model
     */
    private void rechargeReward(RechargeEntity model){
        // 充值奖励
        String rewardConfig = sysConfigService.getValue(Constant.SUPER_ORG, ConfigConstant.RECHARGE_REWARD_CONFIG_KEY);
        if(StrUtil.isNotBlank(rewardConfig)&&JSONUtil.isJsonArray(rewardConfig)){
            JSONArray rewardList = JSONUtil.parseArray(rewardConfig);
            if(rewardList != null && rewardList.size() > 0){
                BigDecimal amount = model.getAmount();
                for (int i = 0; i < rewardList.size(); i++) {
                    JSONObject reward = rewardList.getJSONObject(i);
                    BigDecimal start = reward.getBigDecimal("start", new BigDecimal(0));
                    BigDecimal end = reward.getBigDecimal("end", new BigDecimal(0));
                    if(amount.compareTo(start) >= 0 && amount.compareTo(end) <= 0){
                        // 充值金额在区间范围内
                        BigDecimal value = reward.getBigDecimal("value", new BigDecimal(0));
                        if(value.compareTo(new BigDecimal(0)) > 0){
                            CustEntity cust = custService.getById(model.getCustId());

                            BigDecimal rewardMoney = amount.multiply(value).divide(new BigDecimal(100));
                            rewardMoney.setScale(4);
                            Map<String, Object> rewardMap = new HashMap<>();
                            rewardMap.put("custId",model.getCustId());
                            rewardMap.put("totalRechargeMoney", rewardMoney);
                            rewardMap.put("LeftRechargeMoney", rewardMoney);
                            rewardMap.put("otherCommissionMoney", rewardMoney);
                            //rewardMap.put("totalCommissionMoney", rewardMoney);
                            //rewardMap.put("leftCommissionMoney", rewardMoney);
                            custService.updateMoney(rewardMap);
                            CustScoreLogEntity scoreLogEntity = new CustScoreLogEntity();
                            scoreLogEntity.setCustId(model.getCustId());
                            scoreLogEntity.setDirect(1);
                            scoreLogEntity.setType(15);
                            scoreLogEntity.setScore(rewardMoney);
                            //====lxy
                            scoreLogEntity.setBeforeScore(cust.getLeftRechargeMoney());
                            scoreLogEntity.setAfterScore(cust.getLeftRechargeMoney().add(rewardMoney));
                            //====lxy
                            scoreLogEntity.setRemark("充值奖励：" + rewardMoney);
                            scoreLogEntity.setCreateTime(DateUtils.getCurrentTime());
                            scoreLogEntity.setOrgId(model.getOrgId());
                            scoreLogEntity.setIsNb(model.getIsNb());
                            scoreLogEntity.setSalesmanId(model.getSalesmanId());
                            scoreLogEntity.setSourceId(model.getRechargeId());
                            custScoreLogService.save(scoreLogEntity);
                        }
                    }
                }
            }
        }
    }

    /**
     * 首充奖励
     * @param cust
     * @param model
     */
    private void rechargeFirst(CustEntity cust, RechargeEntity model){
        // 修改为首充
        model.setIsFirst(1);
        this.updateById(model);
        // 首充奖励
        String firstRewardConfig = sysConfigService.getValue(model.getOrgId(), ConfigConstant.FIRST_RECHARGE_REWARD_CONFIG_KEY);
        JSONObject config = JSONUtil.parseObj(firstRewardConfig);
        JSONArray firstList = config.getJSONArray("firstList");
        for (int i = 0; i < firstList.size(); i++) {
            JSONObject item = firstList.getJSONObject(i);
            BigDecimal start = item.getBigDecimal("start");
            BigDecimal end = item.getBigDecimal("end");
            if(end.compareTo(BigDecimal.ZERO) == 0){
                end = new BigDecimal(Integer.MAX_VALUE);
            }
            if(model.getAmount().compareTo(start) >= 0 && model.getAmount().compareTo(end) <= 0){
                BigDecimal value = item.getBigDecimal("value");
                //赠送明细
                custScoreLogService.scoreChange(model.getCustId(),1,value,4,"首充赠送金额为："+value);
                break;
            }
        }
        //首充奖励上级3%，奖励自己本人2%
        int upRate = config.getInt("upRate",0); //上级
        int rate  =  config.getInt("rate",0); //本人
        if(upRate>0){
            //奖励上级
            BigDecimal value = model.getAmount().multiply(new BigDecimal(upRate)).multiply(new BigDecimal(0.01));
            if(cust.getParentId()>0){
                custScoreLogService.scoreChange(cust.getParentId(),1,value,7,"下级首充赠送返佣金额为："+value);
            }
        }
        if(rate>0){
            //奖励本人
            BigDecimal value = model.getAmount().multiply(new BigDecimal(rate)).multiply(new BigDecimal(0.01));
            custScoreLogService.scoreChange(model.getCustId(),1,value,4,"首充赠送金额为："+value);
        }
        String signConfigStr = sysConfigService.getValue(Constant.SUPER_ORG, "SIGN_IN_SETTING");
        if(signConfigStr != null){
            // 首充送积分
            JSONObject signConfig = JSONUtil.parseObj(signConfigStr);
            int firstAward = signConfig.getInt("firstAward", 0);
            if(firstAward > 0){
                Map<String, Object> rewardParams = new HashMap<>();
                rewardParams.put("custId", cust.getCustId());
                rewardParams.put("pointTotal", firstAward);
                rewardParams.put("pointLeft", firstAward);
                custService.updateMoney(rewardParams);
            }
        }

        if(cust.getParentId() > 0){
            // 首充上级奖励抽奖次数
            CustEntity parent = custService.getById(cust.getParentId());
            if(parent != null && luckUtils.checkAble(LuckUtils.SHARERECHARGEFIRSTABLE, parent.getLevel(), null)){
                Map<String, Object> uParams = new HashMap<>();
                uParams.put("custId", parent.getCustId());
                uParams.put("luckTotalNum", 1);
                uParams.put("luckLeftNum", 1);
                custService.updateMoney(uParams);
            }
        }
    }

    /**
     * 充值上级返佣
     * @param cust
     * @param model
     */
    private void rechargeCommission(CustEntity cust, RechargeEntity model){
        if(cust != null && cust.getParentId() > 0) {
            CustEntity parent = custService.getById(cust.getParentId());
            if(parent != null){
                BigDecimal amount = model.getAmount();
                String configStr = sysConfigService.getValue(Constant.SUPER_ORG, ConfigConstant.RECHARGE_COMMISSION_CONFIG_KEY);
                BigDecimal rate = Convert.toBigDecimal(configStr, BigDecimal.ZERO);
                BigDecimal commission =  amount.multiply(rate).divide(new BigDecimal("100"));
                commission.setScale(4, BigDecimal.ROUND_HALF_DOWN);
                if(commission.compareTo(BigDecimal.ZERO) > 0){
                    Map<String,Object> params = new HashMap<>();
                    params.put("custId", parent.getCustId());
                    params.put("otherCommissionMoney", commission);
                    custService.updateMoney(params);
                    custScoreLogService.scoreChange(parent.getCustId(),1, commission,7,"下级充值返佣：" + commission);
                }
                // 充值上级送积分
                String signConfigStr = sysConfigService.getValue(Constant.SUPER_ORG, "SIGN_IN_SETTING");
                if(signConfigStr != null){
                    JSONObject signConfig = JSONUtil.parseObj(signConfigStr);
                    int parentAward = signConfig.getInt("parentAward", 0);
                    if(parentAward > 0){
                        Map<String, Object> rewardParams = new HashMap<>();
                        rewardParams.put("custId", parent.getCustId());
                        rewardParams.put("pointTotal", parentAward);
                        rewardParams.put("pointLeft", parentAward);
                        custService.updateMoney(rewardParams);
                    }
                }
            }
        }
    }
}