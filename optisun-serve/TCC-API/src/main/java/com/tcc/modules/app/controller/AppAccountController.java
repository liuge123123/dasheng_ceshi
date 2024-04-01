package com.tcc.modules.app.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.tcc.common.annotation.AccessLimit;
import com.tcc.common.annotation.SysLog;
import com.tcc.common.utils.*;
import com.tcc.modules.app.annotation.Login;
import com.tcc.modules.app.annotation.LoginUser;
import com.tcc.modules.app.entity.UserEntity;
import com.tcc.modules.app.service.AppAccountService;
import com.tcc.modules.base.AbstractAppController;
import com.tcc.modules.cust.entity.BankEntity;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.entity.RechargeEntity;
import com.tcc.modules.cust.service.*;
import com.tcc.modules.exercise.entity.SignProgressEntity;
import com.tcc.modules.exercise.entity.TreasureEntity;
import com.tcc.modules.exercise.entity.TreasureOrderEntity;
import com.tcc.modules.exercise.service.TreasureOrderService;
import com.tcc.modules.exercise.service.TreasureService;
import com.tcc.modules.g.entity.GOrderEntity;
import com.tcc.modules.g.service.GOrderService;
import com.tcc.modules.pay.IPaymentService;
import com.tcc.modules.pay.PayResult;
import com.tcc.modules.s.entity.SBankCateEntity;
import com.tcc.modules.s.entity.SPlatformBankEntity;
import com.tcc.modules.s.service.SBankCateService;
import com.tcc.modules.s.service.SPlatformBankService;
import com.tcc.modules.sys.entity.SysTicketNoEntity;
import com.tcc.modules.sys.entity.SysUserEntity;
import com.tcc.modules.sys.service.SysConfigService;
import com.tcc.modules.sys.service.SysUserService;
import com.tcc.modules.t.entity.TGoodsGradeEntity;
import com.tcc.modules.t.entity.TOrderEntity;
import com.tcc.modules.t.service.TGoodsGradeService;
import com.tcc.modules.t.service.TOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * APP用户账户相关接口
 */
@Slf4j
@RestController
@RequestMapping("/app/account")
@Api("APP用户账户相关接口")
public class AppAccountController extends AbstractAppController {

    @Autowired
    private SPlatformBankService platformBankService;

    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private BankService bankService;

    @Autowired
    private SBankCateService sBankCateService;

    @Autowired
    private CustScoreLogService custScoreLogService;
    @Autowired
    private AppAccountService appAccountService;

    @Autowired
    private TGoodsGradeService tGoodsGradeService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private CustService custService;

    @Autowired
    private GOrderService gOrderService;

    @Autowired
    private TOrderService tOrderService;


    @Autowired
    private StringRedisTemplate redisTemplate;
    @Resource(name = "vipCommmissionService")
    private VipCommmissionService vipCommmissionService;

    @Autowired
    private TreasureService treasureService;

    @SysLog("领取宝箱")
    @AccessLimit
    @Login
    @ApiOperation("领取宝箱")
    @PostMapping("/treasure/receive")
    @Transactional
    public R treasureReceive(@LoginUser UserEntity user, @RequestBody JSONObject params) {
        String code = params.getStr("code");
        BigDecimal amount = treasureService.recive(user.getCustId(),code);
        return R.ok().putData(amount);
    }

    @ApiOperation("获取在线客服")
    @GetMapping("/online/service")
    public R onlineService(Long custId) {
        JSONObject result = new JSONObject();
        if (custId != null && custId != 0L) {
            CustEntity cust = custService.getById(custId);
            SysUserEntity sysUser = sysUserService.getById(cust.getSalesmanId());
            result.putOpt("avatar", sysUser.getAvatar());
            result.putOpt("name", sysUser.getName());
            String urlStr = sysUser.getCustomerUrl();
            JSONObject url = new JSONObject();
            if(StrUtil.isNotBlank(urlStr)){
                url = JSONUtil.parseObj(urlStr);
            }
            if(StrUtil.isNotBlank(url.getStr("ws"))
                    || StrUtil.isNotBlank(url.getStr("wsGroup"))
                    || StrUtil.isNotBlank(url.getStr("tgGroup"))){
                result.putAll(url);
            }else {
                String kfConfigStr = sysConfigService.getValue(getOrgId(), ConfigConstant.KEFU_SETTING_CONFIG_KEY);
                if (StringUtils.isNotBlank(kfConfigStr)) {
                    JSONObject kfConfig = JSONUtil.parseObj(kfConfigStr);
                    result.putOpt("ws", kfConfig.get("ws"));
                    result.putOpt("wsGroup", kfConfig.get("wsGroup"));
                    result.putOpt("tgGroup", kfConfig.get("tgGroup"));
                }
            }

        } else {
            String kfConfigStr = sysConfigService.getValue(getOrgId(), ConfigConstant.KEFU_SETTING_CONFIG_KEY);
            if (StringUtils.isNotBlank(kfConfigStr)) {
                JSONObject kfConfig = JSONUtil.parseObj(kfConfigStr);
                result.putOpt("avatar", kfConfig.get("avater"));
                result.putOpt("name", kfConfig.get("name"));
                result.putOpt("ws", kfConfig.get("ws"));
                result.putOpt("wsGroup", kfConfig.get("wsGroup"));
                result.putOpt("tgGroup", kfConfig.get("tgGroup"));
            }
        }
        return R.ok().putData(result);
    }

    @ApiOperation("会员等级")
    @GetMapping("/level/list")
    public R levelList() {
        List<TGoodsGradeEntity> list = tGoodsGradeService.list(new LambdaQueryWrapper<TGoodsGradeEntity>()
                .eq(TGoodsGradeEntity::getOrgId, getOrgId())
                .eq(TGoodsGradeEntity::getDelFlag, "0"));
        return R.ok().putData(list);
    }


    @ApiOperation("获取银行卡列表")
    @GetMapping("/bank/list")
    public R bankList() {
        List<SPlatformBankEntity> list = platformBankService.list(new LambdaQueryWrapper<SPlatformBankEntity>().eq(SPlatformBankEntity::getDelFlag, "0"));
        return R.ok().putData(list);
    }

    @SysLog("客户充值")
    @AccessLimit
    @Login
    @ApiOperation("充值")
    @PostMapping("/recharge")
    public R recharge(@LoginUser UserEntity user, @RequestBody JSONObject params) {
        Long bankId = params.getLong("bankId");
        BigDecimal money = params.getBigDecimal("money");
        String transCode = params.getStr("transCode");
        String transDate = params.getStr("transDate");

        SPlatformBankEntity bank = platformBankService.getById(bankId);
        if(!"Zamani".equals(bank.getName())){
            //判断交易ID不能重复
            RechargeEntity rechargeEntity = rechargeService.getOne(new LambdaQueryWrapper<RechargeEntity>()
                    .eq(RechargeEntity::getTransid, transCode)
                    //.eq(RechargeEntity::getTransactionNo, transDate)
                    //.eq(RechargeEntity::getMoneyFront, money)
                    .last("limit 0, 1"));
            if(rechargeEntity!=null){
                return R.error(ResultCode.RESULT_601);
            }
        }

        // 计算充值后金额
        BigDecimal fee = Convert.toBigDecimal(bank.getFee(), new BigDecimal(1));
        BigDecimal amount = money.multiply(fee);
        //验证是否满足最低充值金额
        BigDecimal minMoney = Convert.toBigDecimal(sysConfigService.getValue(user.getOrgId(), "RECHARGE_SETTING"));
        if (minMoney.compareTo(amount) > 0) {
            return R.error(ResultCode.RESULT_100036);
        }
        RechargeEntity recharge = new RechargeEntity();
        recharge.setOrderCode(IdUtil.getSnowflake(1, 1).nextIdStr());
        recharge.setCustId(getUserId());
        recharge.setType("1");
        recharge.setPlatformBankId(bankId);
        recharge.setTransid(transCode);
        recharge.setTransactionNo(transDate);
        recharge.setMoneyFront(money);
        recharge.setAmount(amount);
        recharge.setFee(bank.getFee());
        recharge.setUnit(bank.getLetter());
        recharge.setMoneytypename(bank.getName());
        recharge.setIsFirst(0);
        recharge.setCreateBy(0L);
        recharge.setCreateTime(DateUtils.getCurrentTime());
        recharge.setUpdateTime(DateUtils.getCurrentTime());
        recharge.setOrgId(getOrgId());
        recharge.setSalesmanId(user.getSalesmanId());
        recharge.setIsNb(user.getIsNb());
        rechargeService.save(recharge);
        return R.ok();
    }

    @SysLog("充值回调")
    @AccessLimit
    @ApiOperation("充值回调")
    @ResponseBody
    @RequestMapping(value= {"/notify/{ifCode}", "/notify/{ifCode}/{payOrderId}"})
    public String doNotify(HttpServletRequest request, @PathVariable("ifCode") String ifCode, @PathVariable(value = "payOrderId", required = false) String urlOrderId){

        String payOrderId = null;
        String logPrefix = "进入[" +ifCode+ "]支付回调：urlOrderId：["+ StringUtils.defaultIfEmpty(urlOrderId, "") + "] ";
        log.info("===== {} =====" , logPrefix);
        // 参数有误
        if(StringUtils.isEmpty(ifCode)){
            return "ifCode is empty";
        }
        IPaymentService paymentService = SpringContextUtils.getBean(ifCode.toLowerCase() + "PaymentService", IPaymentService.class);
        return paymentService.notify(request,urlOrderId);
    }

    @SysLog("客户提现")
    @AccessLimit
    @Login
    @ApiOperation("提现")
    @PostMapping("/withdraw")
    public R withdraw(@LoginUser UserEntity user, @RequestBody JSONObject params) {
        Long cardId = params.getLong("cardId");
        BigDecimal money = params.getBigDecimal("money");
        String pwd = params.getStr("pwd");
        String pwdSec = MD5Util.md5(pwd);
        if (!pwdSec.equals(user.getPwd1())) {
            return R.error(ResultCode.RESULT_100006);
        }
        int taskLimitConfig = Convert.toInt(sysConfigService.getValue(Constant.SUPER_ORG, "task.limit.config"), 0);
        if(taskLimitConfig == 1 && user.getTaskLimit() == 1){
            return R.error(ResultCode.RESULT_100042);
        }
        int withdrawLimit = Convert.toInt(sysConfigService.getValue(Constant.SUPER_ORG, "withdraw.enable.config"), 0);
        if(withdrawLimit == 1
                && Convert.toInt(user.getWithdrawLimit(), 0) == 1){
            return R.error(ResultCode.RESULT_100041);
        }
        commissionService.apply(getUserId(), cardId, money);
        return R.ok();
    }


    @ApiOperation("卡种类列表")
    @GetMapping("/bank/cate/list")
    public R bankCateList(SBankCateEntity entity) {
        List<SBankCateEntity> list = sBankCateService.list(new LambdaQueryWrapper<SBankCateEntity>()
                .eq(entity.getRechargeOpen() != null, SBankCateEntity::getRechargeOpen, entity.getRechargeOpen())
                .eq(entity.getCashoutOpen() != null, SBankCateEntity::getCashoutOpen, entity.getCashoutOpen())
                .eq(SBankCateEntity::getDelFlag, "0").orderByDesc(SBankCateEntity::getSort)
        );
        return R.ok().putData(list);
    }

    @Login
    @ApiOperation("查询用户银行卡列表")
    @GetMapping("/my/bank/list")
    public R mybankList() {
        List<BankEntity> list = bankService.list(new LambdaQueryWrapper<BankEntity>()
                .eq(BankEntity::getCustId, getUserId())
                .eq(BankEntity::getDelFlag, "0"));
        return R.ok().putData(list);
    }

    @SysLog("客户添加银行卡")
    @AccessLimit
    @Login
    @ApiOperation("添加银行卡")
    @PostMapping("/my/bank/save")
    public R myBankSave(@RequestBody JSONObject params) {
        String cardName = params.getStr("cardName");
        String cardNo = params.getStr("cardNo");
        Long cateId = params.getLong("cateId");

        int count = bankService.count(new LambdaQueryWrapper<BankEntity>()
                .eq(BankEntity::getCateId, cateId)
                .eq(BankEntity::getAccount, cardNo)
                .eq(BankEntity::getDelFlag, "0"));
        if (count > 0) {
            return R.error(ResultCode.RESULT_100011);
        }
        SBankCateEntity cate = sBankCateService.getById(cateId);
        BankEntity card = new BankEntity();
        card.setCustId(getUserId());
        card.setName(cardName);
        card.setAccount(cardNo);
        card.setCateId(cate.getId());
        card.setCateName(cate.getName());
        card.setImage(cate.getLogo());
        card.setType(1);
        card.setOrgId(getOrgId());
        card.setCreateTime(DateUtils.getCurrentTime());
        card.setUpdateTime(DateUtils.getCurrentTime());
        bankService.save(card);
        return R.ok();
    }

    @Login
    @ApiOperation("账户明细表")
    @GetMapping("/log/list")
    public R logList(int pageNo, int pageSize, Integer type) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constant.PAGE, pageNo);
        params.put(Constant.LIMIT, pageSize);
        params.put("custId", getUserId());
        PageUtils page = null;
        if (type == 0) {
            page = rechargeService.queryPage(params);
        } else if (type == 1) {
            page = commissionService.queryPage(params);
        }
        return R.ok().putData(page);
    }

    @Login
    @ApiOperation("任务完成状态")
    @GetMapping("/task/status")
    public R taskStatus(Long custId) {
        Date now = new Date();
        int startTime = Convert.toInt(DateUtil.beginOfDay(now).getTime() / 1000);
        int endTime = Convert.toInt(DateUtil.endOfDay(now).getTime() / 1000);
        int gcount = gOrderService.count(new LambdaQueryWrapper<GOrderEntity>()
                .eq(GOrderEntity::getCustId, custId)
                .ge(GOrderEntity::getCreateTime, startTime)
                .le(GOrderEntity::getCreateTime, endTime));
        int tcount = tOrderService.count(new LambdaQueryWrapper<TOrderEntity>()
                .eq(TOrderEntity::getCustId, custId)
                .ge(TOrderEntity::getCreateTime, startTime)
                .le(TOrderEntity::getCreateTime, endTime));
        if (gcount > 0 && tcount > 0) {
            return R.ok().putData(1);
        }
        return R.ok().putData(0);
    }

    @Login
    @ApiOperation("今日任务完成情况")
    @GetMapping("/task/count")
    public R taskCount(@LoginUser UserEntity user) {
        Long custId = user.getCustId();
        int gcount = 0; //已完成量
        int total = 0; //任务总量
        long level = user.getLevel();
        String redisResult = appAccountService.getTaskRedis(custId);
        if (StringUtils.isNotBlank(redisResult)) {
            JSONObject jsonObject = JSONUtil.parseObj(redisResult);
            gcount = jsonObject.getInt("num", 0);
            total = jsonObject.getInt("total", 0);
            level = jsonObject.getLong("level", level);
        } else {
            TGoodsGradeEntity tGoodsGradeEntity = tGoodsGradeService.getById(user.getLevel());
            total = tGoodsGradeEntity.getGmNums();

            Date now = new Date();
            Long beginTime = DateUtil.beginOfDay(now).getTime() / 1000;
            Long endTime = DateUtil.endOfDay(now).getTime() / 1000;

            gcount = gOrderService.count(new LambdaQueryWrapper<GOrderEntity>()
                    .eq(GOrderEntity::getCustId, custId)
                    .ge(GOrderEntity::getCreateTime, beginTime)
                    .le(GOrderEntity::getCreateTime, endTime)
            );

        }
        Map<String, Object> result = new HashMap<>();
        result.put("completeCount", gcount);
        result.put("totalCount", total);
        result.put("complete", gcount >= total);
        result.put("level", level);
        return R.ok().putData(result);
    }


    @Login
    @ApiOperation("获取团队成员汇总信息")
    @GetMapping("/team/getTeamTotal")
    public R getTeamTotal() {
        return R.ok().putData(appAccountService.getTeamTotal(getUserId(), getOrgId()));
    }

    @Login
    @ApiOperation("获取团队成员列表")
    @GetMapping("/team/getTeamPersonList")
    public R getPersonList(int pageNo, int pageSize, Integer position) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constant.PAGE, pageNo);
        params.put(Constant.LIMIT, pageSize);
        params.put("custId", getUserId());
        params.put("position", position);
        params.put("orgId", getOrgId());
        PageUtils page = appAccountService.getAppCustList(params);
        return R.ok().putData(page);
    }
    @Login
    @ApiOperation("获取团队已充值成员列表")
    @GetMapping("/team/getTeamPersonListRecharge")
    public R getPersonListRecharge(int pageNo, int pageSize, Integer position) {
        Map<String, Object> params = new HashMap<>();
        params.put(Constant.PAGE, pageNo);
        params.put(Constant.LIMIT, pageSize);
        params.put("custId", getUserId());
        params.put("position", position);
        params.put("orgId", getOrgId());
        PageUtils page = appAccountService.getAppCustListRecharge(params);
        return R.ok().putData(page);
    }

    @AccessLimit
    @Login
    @ApiOperation("做任务")
    @PostMapping("/taskDeal")
    @SysLog("做任务")
    public R taskDeal(@LoginUser UserEntity user, @RequestBody JSONObject params) {
        Integer taskLimitConfig = Convert.toInt(sysConfigService.getValue(Constant.SUPER_ORG, "task.limit.config"), 0);
        if(taskLimitConfig == 1 && user.getTaskLimit() == 1){
            return R.error(ResultCode.RESULT_100040); //任务限制
        }
        GOrderEntity gOrderEntity = appAccountService.taskDeal(user.getCustId(), user.getOrgId(), params.getLong("level"), params.getLong("goodsId"));
        return R.ok().put("data", gOrderEntity);
    }

    @AccessLimit
    @Login
    @ApiOperation("解锁vip")
    @PostMapping("/unlockVip")
    @SysLog("解锁vip")
    public R unlockVip(@LoginUser UserEntity user, @RequestBody JSONObject params) {
        appAccountService.unlockVip(user.getOrgId(), user.getCustId(), params.getLong("level"));
        return R.ok();
    }


    @ApiOperation("根据输入金额获取一张银行卡")
    @GetMapping("/bank/listByMoney")
    public R bankListByMoney(String bankCode, String money) {
        BigDecimal durmoney = Convert.toBigDecimal(money, new BigDecimal(0));
        SPlatformBankEntity sPlatformBankEntity = platformBankService.getOne(new LambdaQueryWrapper<SPlatformBankEntity>()
                .eq(SPlatformBankEntity::getName, bankCode)
                .le(SPlatformBankEntity::getMinMoney, durmoney)
                .ge(SPlatformBankEntity::getMaxMoney, durmoney)
                .eq(SPlatformBankEntity::getDelFlag, 0)
                .last("limit 0,1"));
        if (sPlatformBankEntity == null) {
            SPlatformBankEntity newModel = platformBankService.getOne(new LambdaQueryWrapper<SPlatformBankEntity>()
                    .eq(SPlatformBankEntity::getName, bankCode)
                    .eq(SPlatformBankEntity::getDelFlag, 0)
                    .last("limit 0,1"));
            return R.ok().putData(newModel);
        } else {
            return R.ok().putData(sPlatformBankEntity);
        }
    }

    @Login
    @GetMapping("/vipCommmission")
    public R vipCommmission(int pageSize, int pageNo) {
        long userId = getUserId();
        Map<String, Object> params = new HashMap<>();
        params.put(Constant.LIMIT, pageSize);
        params.put(Constant.PAGE, pageNo);
        params.put("custId", userId);
        params.put("type", 7);
        params.put("remark", "下级充值返佣");
        return R.ok().putData(custScoreLogService.queryPage(params));
    }

    @Login
    @GetMapping("/detail")
    public R detail(int pageSize, int pageNo) {
        long userId = getUserId();
        Map<String, Object> params = new HashMap<>();
        params.put(Constant.LIMIT, pageSize);
        params.put(Constant.PAGE, pageNo);
        params.put("custId", userId);
        return R.ok().putData(custScoreLogService.queryPage(params));
    }

    @Login
    @GetMapping("/commmissionMoney")
    public R commmissionMoney() {
        return R.ok().putData(commissionService.getSum(getUserId()));
    }

    @Login
    @GetMapping("/rewardMoney")
    public R rewardMoney() {
        BigDecimal money = custScoreLogService.custRewardTotal(getUserId());
        money.setScale(0, RoundingMode.FLOOR);
        return R.ok().putData(money);
    }
}
