package com.tcc.modules.app.service.impl;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.*;
import com.tcc.modules.app.dao.AppAccountDao;
import com.tcc.modules.app.service.AppAccountService;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.entity.LevelLogEntity;
import com.tcc.modules.cust.entity.TaskCommissionEntity;
import com.tcc.modules.cust.entity.VipCommmissionEntity;
import com.tcc.modules.cust.service.*;
import com.tcc.modules.g.entity.GGoodsEntity;
import com.tcc.modules.g.entity.GOrderEntity;
import com.tcc.modules.g.service.GGoodsService;
import com.tcc.modules.g.service.GOrderService;
import com.tcc.modules.sys.service.SysConfigService;
import com.tcc.modules.t.entity.TGoodsGradeEntity;
import com.tcc.modules.t.service.TGoodsGradeService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service("appAccountService")
public class AppAccountServiceImpl implements AppAccountService {

    @Autowired
    private AppAccountDao appAccountDao;
    @Autowired
    private CustService custService;
    @Autowired
    private GOrderService gOrderService;
    @Autowired
    private TaskCommissionService taskCommissionService;
    @Autowired
    private TGoodsGradeService gradeService;
    @Autowired
    private GGoodsService gGoodsService;
    @Autowired
    private SysConfigService configService;
    @Autowired
    private LevelLogService levelLogService;
    @Autowired
    private VipCommmissionService vipCommmissionService;
    @Autowired
    private CustScoreLogService custScoreLogService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 获取团队成员列表
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils getAppCustList(@Param("condition") Map<String, Object> params) {
        IPage page = new Query<>().getPage(params);
        List<Map<String, Object>> custList = this.appAccountDao.getAppCustList(page, params);
        page.setRecords(custList);
        return new PageUtils(page);
    }
    /**
     * 获取充值团队成员列表
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils getAppCustListRecharge(@Param("condition") Map<String, Object> params) {
        IPage page = new Query<>().getPage(params);
        List<Map<String, Object>> custList = this.appAccountDao.getAppCustListRecharge(page, params);
        page.setRecords(custList);
        return new PageUtils(page);
    }

    /**
     * 获取团队汇总数据
     *
     * @param custId
     * @param orgId
     * @return
     */
    public JSONObject getTeamTotal(Long custId, Long orgId) {
        JSONObject result = new JSONObject();
        //TODO 获取团队总人数
        Map<String, Object> params = new HashMap<>();
        params.put("custId", custId);
        params.put("orgId", orgId);
//        Integer totalNum = this.appAccountDao.getTotalPerson(params);
        //TODO 获取团队人数及佣金
        //获取一级用户
        params.put("vip", "vip0-vip3");
        params.put("position", 1);
        Integer oneNum = this.appAccountDao.getAppCustCount(params);
        Integer oneNumUser = this.appAccountDao.getAppCustCountUser(params);
        Integer oneNumRecharge = this.appAccountDao.getAppCustCountRecharge(params);
        params.put("columMoney", "fisrt_money");
        params.put("columCust", "first_cust_id");
        BigDecimal oneMoney = appAccountDao.getAppCommission(params); //taskCommissionService.list(new QueryWrapper<TaskCommissionEntity>().eq("first_cust_id", custId)).stream().map(a -> a.getFisrtMoney()).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal oneTodayMoney = appAccountDao.getTodayAppCommission(params);
        //获取二级用户
        params.put("position", 2);
        Integer twoNum = this.appAccountDao.getAppCustCount(params);
        Integer twoNumUser = this.appAccountDao.getAppCustCountUser(params);
        Integer twoNumRecharge = this.appAccountDao.getAppCustCountRecharge(params);
        params.put("columMoney", "second_money");
        params.put("columCust", "second_cust_id");
        BigDecimal twoMoney = appAccountDao.getAppCommission(params); //taskCommissionService.list(new QueryWrapper<TaskCommissionEntity>().eq("second_cust_id", custId)).stream().map(a -> a.getSecondMoney()).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal twoTodayMoney = appAccountDao.getTodayAppCommission(params);
        //获取三级用户 //三级隐藏 20240227
        /*params.put("position", 3);
        Integer threeNum = this.appAccountDao.getAppCustCount(params);
        Integer threeNumUser = this.appAccountDao.getAppCustCountUser(params);
        Integer threeNumRecharge = this.appAccountDao.getAppCustCountRecharge(params);
        params.put("columMoney", "third_money");
        params.put("columCust", "third_cust_id");
        BigDecimal threeMoney = appAccountDao.getAppCommission(params); //taskCommissionService.list(new QueryWrapper<TaskCommissionEntity>().eq("third_cust_id", custId)).stream().map(a -> a.getThirdMoney()).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal threeTodayMoney = appAccountDao.getTodayAppCommission(params);*/
        //获取四级用户
//        params.put("position", 4);
//        Integer fourNum = this.appAccountDao.getAppCustCount(params);
//        Integer fourNumUser = this.appAccountDao.getAppCustCountUser(params);
//        params.put("columMoney", "four_money");
//        params.put("columCust", "four_cust_id");
//        BigDecimal fourMoney = appAccountDao.getAppCommission(params); //taskCommissionService.list(new QueryWrapper<TaskCommissionEntity>().eq("four_cust_id", custId)).stream().map(a -> a.getFourMoney()).reduce(BigDecimal.ZERO, BigDecimal::add);
//        //获取五级用户
//        params.put("position", 5);
//        Integer fiveNum = this.appAccountDao.getAppCustCount(params);
//        Integer fiveNumUser = this.appAccountDao.getAppCustCountUser(params);
//        params.put("columMoney", "five_money");
//        params.put("columCust", "five_cust_id");
//        BigDecimal fiveMoney = appAccountDao.getAppCommission(params); //taskCommissionService.list(new QueryWrapper<TaskCommissionEntity>().eq("five_cust_id", custId)).stream().map(a -> a.getFiveMoney()).reduce(BigDecimal.ZERO, BigDecimal::add);
//        //获取六级用户
//        params.put("position", 6);
//        Integer sixNum = this.appAccountDao.getAppCustCount(params);
//        params.put("columMoney", "six_money");
//        params.put("columCust", "six_cust_id");
//        BigDecimal sixMoney = appAccountDao.getAppCommission(params); //taskCommissionService.list(new QueryWrapper<TaskCommissionEntity>().eq("six_cust_id", custId)).stream().map(a -> a.getSixMoney()).reduce(BigDecimal.ZERO, BigDecimal::add);
//        //获取7级用户
//        params.put("position", 7);
//        Integer sevenNum = this.appAccountDao.getAppCustCount(params);
//        params.put("columMoney", "seven_money");
//        params.put("columCust", "seven_cust_id");
//        BigDecimal sevenMoney = appAccountDao.getAppCommission(params); //taskCommissionService.list(new QueryWrapper<TaskCommissionEntity>().eq("seven_cust_id", custId)).stream().map(a -> a.getSevenMoney()).reduce(BigDecimal.ZERO, BigDecimal::add);

        List list = new ArrayList();
        JSONObject oneJ = new JSONObject();
        oneJ.putOpt("vip", "vip0-vip3");
        oneJ.putOpt("level", "1");
        oneJ.putOpt("rate", "12%");
        oneJ.putOpt("num", oneNum);
        oneJ.putOpt("numUser", oneNumUser);
        oneJ.putOpt("numRecharge", oneNumRecharge);
        oneJ.putOpt("money", oneMoney);
        oneJ.putOpt("todaymoney", oneTodayMoney);

        JSONObject twoJ = new JSONObject();
        twoJ.putOpt("vip", "vip0-vip3");
        twoJ.putOpt("level", "2");
        twoJ.putOpt("rate", "6%");
        twoJ.putOpt("num", twoNum);
        twoJ.putOpt("numUser", twoNumUser);
        twoJ.putOpt("numRecharge", twoNumRecharge);
        twoJ.putOpt("money", twoMoney);
        twoJ.putOpt("todaymoney", twoTodayMoney);

        JSONObject threeJ = new JSONObject();
        threeJ.putOpt("vip", "vip0-vip3");
        threeJ.putOpt("level", "3");
        threeJ.putOpt("rate", "3%");
        /*threeJ.putOpt("num", threeNum);
        threeJ.putOpt("numUser", threeNumUser);
        threeJ.putOpt("numRecharge", threeNumRecharge);
        threeJ.putOpt("money", threeMoney);
        threeJ.putOpt("todaymoney", threeTodayMoney);*/
//
//        JSONObject fourJ = new JSONObject();
//        fourJ.putOpt("vip", "vip4");
//        fourJ.putOpt("level", "4");
//        fourJ.putOpt("rate", "2%");
//        fourJ.putOpt("num", fourNum);
//        fourJ.putOpt("money", fourMoney);
//
//        JSONObject fiveJ = new JSONObject();
//        fiveJ.putOpt("vip", "vip5");
//        fiveJ.putOpt("level", "5");
//        fiveJ.putOpt("rate", "1%");
//        fiveJ.putOpt("num", fiveNum);
//        fiveJ.putOpt("money", fiveMoney);
//
//        JSONObject sixJ = new JSONObject();
//        sixJ.putOpt("vip", "vip6");
//        sixJ.putOpt("level", "6");
//        sixJ.putOpt("rate", "0.5%");
//        sixJ.putOpt("num", sixNum);
//        sixJ.putOpt("money", sixMoney);
//
//        JSONObject sevenJ = new JSONObject();
//        sevenJ.putOpt("vip", "vip7");
//        sevenJ.putOpt("level", "7");
//        sevenJ.putOpt("rate", "0.5%");
//        sevenJ.putOpt("num", sevenNum);
//        sevenJ.putOpt("money", sevenMoney);

//        CustEntity cust = custService.getById(custId);
        list.add(oneJ);
        list.add(twoJ);
//        list.add(threeJ);
//        list.add(fourJ);
//        list.add(fiveJ);
//        list.add(sixJ);
//        list.add(sevenJ);
        result.putOpt("list", list);

//        result.putOpt("totalNum", oneNum + twoNum + threeNum + fourNum + fiveNum + sixNum + sevenNum);
//        result.putOpt("totalMoney", oneMoney.add(twoMoney).add(threeMoney).add(fourMoney).add(fiveMoney).add(sixMoney).add(sevenMoney));

//        result.putOpt("totalNum", oneNum + twoNum + threeNum + fourNum + fiveNum + sixNum + sevenNum);
        result.putOpt("totalNum", oneNum + twoNum);
        result.putOpt("memberNum", oneNumRecharge + twoNumRecharge);
//        result.putOpt("totalMoney", oneMoney.add(twoMoney).add(threeMoney).add(fourMoney).add(fiveMoney).add(sixMoney).add(sevenMoney));
        result.putOpt("totalMoney", oneMoney.add(twoMoney));
        result.putOpt("todayMoney", oneTodayMoney.add(twoTodayMoney));

//        result.putOpt("totalMoney", cust.getTeamCommissionMoney());
        return result;
    }

    @Override
    public String getTaskRedis(Long custId) {
        String redisValue = redisTemplate.opsForValue().get("task:" + custId);
        return redisValue;
    }

    /**
     * 做任务
     *
     * @param custId
     * @param orgId
     * @param level
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public GOrderEntity taskDeal(Long custId, Long orgId, Long level, Long productId) {
        //TODO 获取用户等级
        /**
         * 不同的客户等级所做任务单不一样，如vip1：每天固定2单，每单佣金125，门票10000 其他参考会员等级表
         * 上级返佣金 第一位置的佣金：vip等级必须大于等于1；第二位置的佣金：vip等级必须大于等于2 依次类推；
         */
        CustEntity custEntity = custService.getById(custId);
        if (custEntity.getLevel() < level) {
            throw new WTDPException(ResultCode.RESULT_100024);
        }
        // 任务等级
        TGoodsGradeEntity taskLevel = gradeService.getById(level);
        //TODO 获取基准等级佣金
        //本金 = 每天第一单的 该用户的佣金余额/会员等级可做单数
        //佣金 = 本金*会员等级的佣金比例
        BigDecimal commission = new BigDecimal(0); //佣金
        BigDecimal baseMoney = new BigDecimal(0);//本金
        int num = 0;
        String redisValue = redisTemplate.opsForValue().get("task:" + custId);
        if (StringUtils.isNotBlank(redisValue)) { //redis 存在
            JSONObject jsValue = JSONUtil.parseObj(redisValue);
            baseMoney = jsValue.getBigDecimal("baseMoney", new BigDecimal(0));
            num = jsValue.getInt("num", 0);
        } else {
            //redis不存在任务做单信息
            baseMoney = custEntity.getLeftCommissionMoney().add(custEntity.getRegisterMoney()).divide(new BigDecimal(taskLevel.getGmNums()));
        }
        commission = baseMoney.multiply(taskLevel.getRate()).multiply(new BigDecimal(0.01));

        //获取客户等级信息
        //  TGoodsGradeEntity custLevel = gradeService.getById(custEntity.getLevel());
        //判断今日是否完成任务
        Integer count = gOrderService.count(new QueryWrapper<GOrderEntity>().eq("FROM_UNIXTIME(create_time, '%Y-%m-%d')", DateUtil.today()).eq("status", 2)
                .eq("org_id", orgId).eq("del_flag", 0).eq("cust_id", custId));
        if (count >= taskLevel.getGmNums()) {
            throw new WTDPException(ResultCode.RESULT_100026);
        }
        //获取单一产品信息
        GGoodsEntity goodModel = gGoodsService.getById(productId);
//        Map goodModel = this.getGoodModel(orgId, level);
        if (goodModel == null) {
            throw new WTDPException(ResultCode.RESULT_100030);
        }
        //TODO 保存客户订单
        GOrderEntity gOrderEntity = new GOrderEntity();
        gOrderEntity.setOrderNo(ResultHelper.orderNo());
        gOrderEntity.setOrderMoney(baseMoney);
        gOrderEntity.setCustId(custId);
        gOrderEntity.setGoodsId(goodModel.getId());
        gOrderEntity.setGoodsName(goodModel.getGoodsName());
        gOrderEntity.setGoodsImg(goodModel.getImage());
        gOrderEntity.setCommission(commission);
        gOrderEntity.setOrgId(orgId);
        gOrderEntity.setStatus(2);
        gOrderEntity.setCreateTime(DateUtils.getCurrentTime());
        gOrderEntity.setDelFlag("0");
        gOrderEntity.setIsNb(custEntity.getIsNb());
        gOrderEntity.setSalesmanId(custEntity.getSalesmanId());
        gOrderEntity.setCustName(custEntity.getCustName());
        gOrderEntity.setLevel(goodModel.getLevel());
        gOrderService.save(gOrderEntity);
        //上级客户id
        String[] upCustIdList = custEntity.getParentIdList().split(",");
        //分销比例
        String config = configService.getValue(orgId, ConfigConstant.REBATE_CONFIG_KEY);
        JSONObject configJ = JSONUtil.parseObj(config);
        if (configJ.getBool("open")) {
            //判断当前用户是否是vip0 团队佣金
            if ((custEntity.getLevel() == 1 && configJ.getBool("vip0IsReturn")) || custEntity.getLevel() > 1) {
                //TODO 保存任务佣金
                TaskCommissionEntity taskCommission = new TaskCommissionEntity();
                taskCommission.setOrderId(gOrderEntity.getId());
                taskCommission.setOrderTime(DateUtils.getCurrentTime());
                taskCommission.setOrderCust(custId);
                for (Integer i = 0; i < upCustIdList.length; i++) {
                    //upCustIdList 直属上级依次往下
                    String upCustId = upCustIdList[i];
                    CustEntity upCustModel = custService.getById(upCustId);
                    if (upCustModel == null) {
                        continue;
                    }
                    Long upLevel = upCustModel.getLevel() - 1 > 0 ? upCustModel.getLevel() - 1 : 0;
                    if (i == 0) {
                        if (upLevel >= 0) {
                            //获取1代佣金
                            taskCommission.setFirstCustId(Convert.toLong(upCustId));
                            taskCommission.setFirstSalesmanId(upCustModel.getSalesmanId());
                            taskCommission.setFirstIsNb(upCustModel.getIsNb());
                            //提成比例
                            BigDecimal first = configJ.getBigDecimal("first");
                            BigDecimal firstComminMoney = commission.multiply(first).divide(new BigDecimal(100));
                            taskCommission.setFisrtMoney(firstComminMoney);
                            //TODO 更新客户的团队佣金
                            Map<String, Object> teamParams = new HashMap<>();
                            teamParams.put("custId", upCustId);
                            teamParams.put("teamMoney", firstComminMoney);
                            appAccountDao.updateCustTeamMoney(teamParams);
                            custScoreLogService.scoreChange(Convert.toLong(upCustId), 1, firstComminMoney, 7, "第一代团队佣金金额为：" + firstComminMoney);
                        }
                    } else if (i == 1) {
                        if (upLevel >= 0) {
                            //获取2代佣金
                            taskCommission.setSecondCustId(Convert.toLong(upCustId));
                            taskCommission.setSecondSalesmanId(upCustModel.getSalesmanId());
                            taskCommission.setSecondIsNb(upCustModel.getIsNb());
                            //提成比例
                            BigDecimal second = configJ.getBigDecimal("second");
                            BigDecimal secondComminMoney = commission.multiply(second).divide(new BigDecimal(100));
                            taskCommission.setSecondMoney(secondComminMoney);
                            //TODO 更新客户的团队佣金
                            Map<String, Object> teamParams = new HashMap<>();
                            teamParams.put("custId", upCustId);
                            teamParams.put("teamMoney", secondComminMoney);
                            appAccountDao.updateCustTeamMoney(teamParams);
                            custScoreLogService.scoreChange(Convert.toLong(upCustId), 1, secondComminMoney, 7, "第二代团队佣金金额为：" + secondComminMoney);
                        }
                    } else if (i == 2) {
                        if (upLevel >= 0) {
                            //获取3代佣金
                            taskCommission.setThirdCustId(Convert.toLong(upCustId));
                            taskCommission.setThirdSalesmanId(upCustModel.getSalesmanId());
                            taskCommission.setThirdIsNb(upCustModel.getIsNb());
                            //提成比例
                            BigDecimal third = configJ.getBigDecimal("third");
                            BigDecimal thirdComminMoney = commission.multiply(third).divide(new BigDecimal(100));
                            taskCommission.setThirdMoney(thirdComminMoney);
                            //TODO 更新客户的团队佣金
                            Map<String, Object> teamParams = new HashMap<>();
                            teamParams.put("custId", upCustId);
                            teamParams.put("teamMoney", thirdComminMoney);
                            appAccountDao.updateCustTeamMoney(teamParams);
                            custScoreLogService.scoreChange(Convert.toLong(upCustId), 1, thirdComminMoney, 7, "第三代团队佣金金额为：" + thirdComminMoney);
                        }
                    } else if (i == 3) {
                        if (upLevel >= 4) {
                            //获取4代佣金
                            taskCommission.setFourCustId(Convert.toLong(upCustId));
                            taskCommission.setFourSalesmanId(upCustModel.getSalesmanId());
                            taskCommission.setFourIsNb(upCustModel.getIsNb());
                            //提成比例
                            BigDecimal four = configJ.getBigDecimal("four");
                            BigDecimal fourComminMoney = commission.multiply(four).divide(new BigDecimal(100));
                            taskCommission.setFourMoney(fourComminMoney);
                            //TODO 更新客户的团队佣金
                            Map<String, Object> teamParams = new HashMap<>();
                            teamParams.put("custId", upCustId);
                            teamParams.put("teamMoney", fourComminMoney);
                            appAccountDao.updateCustTeamMoney(teamParams);
                            custScoreLogService.scoreChange(Convert.toLong(upCustId), 1, fourComminMoney, 7, "第四代团队佣金金额为：" + fourComminMoney);
                        }
                    } else if (i == 4) {
                        if (upLevel >= 5) {
                            //获取5代佣金
                            taskCommission.setFiveCustId(Convert.toLong(upCustId));
                            taskCommission.setFiveSalesmanId(upCustModel.getSalesmanId());
                            taskCommission.setFiveIsNb(upCustModel.getIsNb());
                            //提成比例
                            BigDecimal five = configJ.getBigDecimal("five");
                            BigDecimal fiveComminMoney = commission.multiply(five).divide(new BigDecimal(100));
                            taskCommission.setFiveMoney(fiveComminMoney);
                            //TODO 更新客户的团队佣金
                            Map<String, Object> teamParams = new HashMap<>();
                            teamParams.put("custId", upCustId);
                            teamParams.put("teamMoney", fiveComminMoney);
                            appAccountDao.updateCustTeamMoney(teamParams);
                            custScoreLogService.scoreChange(Convert.toLong(upCustId), 1, fiveComminMoney, 7, "第五代团队佣金金额为：" + fiveComminMoney);
                        }
                    } else if (i == 5) {
                        if (upLevel >= 6) {
                            //获取6代佣金
                            taskCommission.setSixCustId(Convert.toLong(upCustId));
                            taskCommission.setSixSalesmanId(upCustModel.getSalesmanId());
                            taskCommission.setSixIsNb(upCustModel.getIsNb());
                            //提成比例
                            BigDecimal six = configJ.getBigDecimal("six");
                            BigDecimal sixComminMoney = commission.multiply(six).divide(new BigDecimal(100));
                            taskCommission.setSixMoney(sixComminMoney);
                            Map<String, Object> teamParams = new HashMap<>();
                            teamParams.put("custId", upCustId);
                            teamParams.put("teamMoney", sixComminMoney);
                            appAccountDao.updateCustTeamMoney(teamParams);
                            custScoreLogService.scoreChange(Convert.toLong(upCustId), 1, sixComminMoney, 7, "第六代团队佣金金额为：" + sixComminMoney);
                        }
                    } else if (i == 6) {
                        if (upLevel >= 7) {
                            //获取7代佣金
                            taskCommission.setSevenCustId(Convert.toLong(upCustId));
                            taskCommission.setSevenSalesmanId(upCustModel.getSalesmanId());
                            taskCommission.setSevenIsNb(upCustModel.getIsNb());
                            //提成比例
                            BigDecimal seven = configJ.getBigDecimal("seven");
                            BigDecimal sevenComminMoney = commission.multiply(seven).divide(new BigDecimal(100));
                            taskCommission.setSevenMoney(sevenComminMoney);
                            Map<String, Object> teamParams = new HashMap<>();
                            teamParams.put("custId", upCustId);
                            teamParams.put("teamMoney", sevenComminMoney);
                            appAccountDao.updateCustTeamMoney(teamParams);
                            custScoreLogService.scoreChange(Convert.toLong(upCustId), 1, sevenComminMoney, 7, "第七代团队佣金金额为：" + sevenComminMoney);
                        }
                    }
                }
                taskCommissionService.save(taskCommission);
            }
        }
        //TODO 更新客户的任务佣金
        Map<String, Object> taskParams = new HashMap<>();
        taskParams.put("custId", custId);
        taskParams.put("taskMoney", commission);
        appAccountDao.updateCustTaskMoney(taskParams);
        custScoreLogService.scoreChange(Convert.toLong(custId), 1, commission, 6, "个人任务佣金金额为：" + commission);
        //保存redis
        JSONObject redis = new JSONObject();
        redis.putOpt("baseMoney", baseMoney);
        redis.putOpt("level", level);
        redis.putOpt("num", num + 1);
        redis.putOpt("total", taskLevel.getGmNums());
        String dateStr1 = DateUtil.now();
        Date date1 = DateUtil.parse(dateStr1);
        String dateStr2 = DateUtil.today() + " 23:59:59";
        Date date2 = DateUtil.parse(dateStr2);
        long betweenSecond = DateUtil.between(date1, date2, DateUnit.SECOND);
        redisTemplate.opsForValue().set("task:" + custId, JSONUtil.toJsonStr(redis), betweenSecond, TimeUnit.SECONDS);
        return gOrderEntity;
    }

    /***
     * 随机抽取同一等级的产品
     * @param orgId
     * @param level 等级表id
     */
    private Map getGoodModel(Long orgId, Long level) {
        Double d = RandomUtil.randomDouble(0, 1);
        Map<String, Object> params = new HashMap();
        params.put("orgId", orgId);
        params.put("level", level);
        return this.appAccountDao.getOneGGoodsModel(params);
    }

    /***
     * 解锁vip
     * @param orgId
     * @param custId
     * @param level 解锁等级id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unlockVip(Long orgId, Long custId, Long level) {
        CustEntity custEntity = custService.getById(custId);
        //TODO 判断该客户是否有条件进行解锁
        if (level > 3) {
            /*vip2升v3开始才会有升级限制
             *VIP升级规则是您名下直接邀请的人的VIP等级必须满足5名和你同等级或高于你的VIP等级才能升级
             * 例如你是VIP2，如果你想升级，你必须满足直接邀请5名VIP2或4名VIP2,1名VIP3或3名VIP2,2名VIP3。以此类推）
             **/
            //TODO  获取直接名下的用户且大于等于我的等级
            Map<String, Object> params = new HashMap<>();
            params.put("custId", custId);
            params.put("orgId", orgId);
            params.put("level", level - 1);
            Integer actNum = this.appAccountDao.getAppUnlockCustCount(params);
            Integer needNum = Convert.toInt(configService.getValue(orgId, "upgrade.vip.config"));
            if (needNum > actNum) {
                //不满足升级条件
                throw new WTDPException(ResultCode.RESULT_100025);
            }
        }
        //TODO 用户升级明细（下订单）
        LevelLogEntity levelLog = new LevelLogEntity();
        levelLog.setCustId(custId);
        levelLog.setBeforLevel(custEntity.getLevel());
        levelLog.setAfterLevel(level);
        levelLog.setCreateTime(DateUtils.getCurrentTime());
        levelLog.setIsNb(custEntity.getIsNb());
        levelLog.setSalesmanId(custEntity.getSalesmanId());
        BigDecimal payMoney = new BigDecimal(0);
        //TODO 消耗金额 补差价
        /**
         * 比如我是vip1  升级到vip3
         * 支付金额为 vip3-vip1
         */
        //当前用户会员等级model
        TGoodsGradeEntity currentGradeModel = gradeService.getById(custEntity.getLevel());
        //解锁vip会员等级model
        TGoodsGradeEntity gradeEntity = gradeService.getById(level);
        payMoney = gradeEntity.getParameter1().subtract(currentGradeModel.getParameter1());
        levelLog.setUseMoney(payMoney);
        //TODO 判断充值余额是否够扣减
        BigDecimal leftMoney = custEntity.getLeftRechargeMoney().add(custEntity.getRegisterMoney());
        if (leftMoney.compareTo(payMoney) == -1) {
            throw new WTDPException(ResultCode.RESULT_100003);
        }
        BigDecimal reduceRegisterMoney = new BigDecimal(0); //扣减注册金
        BigDecimal reduceLeftRechareMoney = new BigDecimal(0); //扣减余额
        if (custEntity.getRegisterMoney().compareTo(new BigDecimal(0)) > 0) { //注册金额大于0
            if (payMoney.compareTo(custEntity.getRegisterMoney()) >= 0) {
                reduceRegisterMoney = custEntity.getRegisterMoney();
                reduceLeftRechareMoney = payMoney.subtract(custEntity.getRegisterMoney());
            } else {
                reduceRegisterMoney = payMoney; //注册金够扣
            }
        } else {
            reduceLeftRechareMoney = payMoney;
        }
        //第一次解锁返上级一积分
        int count = levelLogService.count(new LambdaQueryWrapper<LevelLogEntity>().eq(LevelLogEntity::getCustId, custId));

        levelLogService.save(levelLog);
        //TODO 扣减我的充值余额 增加我的vip时长
        Integer expireTime = 0;
        DateTime newDate = DateUtil.offsetDay(new Date(), 120);
        expireTime = DateUtils.StringToTimestamp(Convert.toStr(newDate));
        Map<String, Object> reParams = new HashMap<>();
        reParams.put("custId", custId);
        reParams.put("expireTime", expireTime);
        reParams.put("registerMoney", reduceRegisterMoney);
        reParams.put("leftRechargeMoney", reduceLeftRechareMoney);
        reParams.put("level", level);
        reParams.put("time", DateUtils.getCurrentTime());
        this.appAccountDao.updateCustLeftRechargeMoney(reParams);
        //TODO 奖励我的直接上级
        /**
         * 上级高于我的等级，返还解锁金额的8%
         * 上级等于或小于我的等级，返还解锁金额4%
         */
        String upIdList = custEntity.getParentIdList();
        String[] upIdArr = upIdList.split(",");
        BigDecimal rewardMoney = payMoney;
        if (upIdArr.length > 0) {
            String upCustId = upIdArr[0];
            CustEntity upCustModel = custService.getById(upCustId);
            if (upCustModel != null) {
                if (upCustModel.getLevel() <= custEntity.getLevel()) {
                    rewardMoney = rewardMoney.multiply(new BigDecimal(0.08));
                } else {
                    rewardMoney = rewardMoney.multiply(new BigDecimal(0.04));
                }
                VipCommmissionEntity vipCommmissionEntity = new VipCommmissionEntity();
                vipCommmissionEntity.setOrderId(levelLog.getId());
                vipCommmissionEntity.setOrderTime(DateUtils.getCurrentTime());
                vipCommmissionEntity.setOrderCust(custId);
                vipCommmissionEntity.setRewardCust(upCustModel.getCustId());
                vipCommmissionEntity.setRewardMoney(rewardMoney);
                vipCommmissionEntity.setIsNb(upCustModel.getIsNb());
                vipCommmissionEntity.setSalesmanId(upCustModel.getSalesmanId());
                vipCommmissionService.save(vipCommmissionEntity);
                //更新奖励
                Map<String, Object> rewardParams = new HashMap<>();
                rewardParams.put("custId", upCustModel.getCustId());
                rewardParams.put("money", rewardMoney);
                if (count == 0) {
                    rewardParams.put("pointTotal", 1);
                    rewardParams.put("pointLeft", 1);
                }
                this.appAccountDao.updateCustRewardMoney(rewardParams);
            }
        }
    }
}
