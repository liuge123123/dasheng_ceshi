package com.tcc.modules.app.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tcc.common.annotation.AccessLimit;
import com.tcc.common.annotation.SysLog;
import com.tcc.common.utils.*;
import com.tcc.common.validator.ValidatorUtils;
import com.tcc.modules.app.annotation.Login;
import com.tcc.modules.app.annotation.LoginUser;
import com.tcc.modules.app.entity.UserEntity;
import com.tcc.modules.app.form.LoginForm;
import com.tcc.modules.app.form.RegisterForm;
import com.tcc.modules.app.utils.LuckUtils;
import com.tcc.modules.app.utils.TokenUtils;
import com.tcc.modules.base.AbstractAppController;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.entity.CustScoreLogEntity;
import com.tcc.modules.cust.service.CustScoreLogService;
import com.tcc.modules.cust.service.CustService;
import com.tcc.modules.event.EventPublisher;
import com.tcc.modules.l.entity.LGoodsEntity;
import com.tcc.modules.l.entity.LOrderEntity;
import com.tcc.modules.l.service.LGoodsService;
import com.tcc.modules.l.service.LOrderService;
import com.tcc.modules.l.service.LRoomService;
import com.tcc.modules.sys.service.SysConfigService;
import com.tcc.modules.t.entity.TGoodsGradeEntity;
import com.tcc.modules.t.service.TGoodsGradeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static com.tcc.common.utils.ConfigConstant.*;

/**
 * APP登录/注册相关接口
 */
@Slf4j
@RestController
@RequestMapping("/app")
@Api("APP登录/注册相关接口")
public class AppLoginController extends AbstractAppController {

    @Autowired
    private CustService custService;

    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private TGoodsGradeService goodsGradeService;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private TGoodsGradeService tGoodsGradeService;

    @Autowired
    private CustScoreLogService custScoreLogService;

    @Autowired
    private LuckUtils luckUtils;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private LGoodsService lGoodsService;

    @Autowired
    private LOrderService lOrderService;
    @ApiOperation("登录")
    @PostMapping("/login")
    @SysLog("客户登录")
    public R login(HttpServletRequest request, @RequestBody LoginForm form) {
        log.debug("用户登录：" + JSONUtil.toJsonStr(form));
        ValidatorUtils.validateEntity(form);
        CustEntity cust = custService.getOne(new LambdaQueryWrapper<CustEntity>()
                .eq(CustEntity::getCustName, form.getUsername())
                .or()
                .eq(CustEntity::getMobile, form.getUsername())
                .last("limit 0, 1"));
        //账号不存在、密码错误
        if (cust == null || !cust.getPwd().equals(MD5Util.md5(form.getPassword()))) {
            return R.error(ResultCode.RESULT_100001);
        }
        if (cust.getLevel() != null && cust.getLevel() != 0) {
            cust.setGrade(goodsGradeService.getById(cust.getLevel()));
        }
        if (cust.getStatus() != 1) {
            return R.error(ResultCode.RESULT_100010);
        }
        cust.setLoginTime(DateUtils.getCurrentTime());
        cust.setLoginIp(IPUtils.getIpAddr(request));
        custService.update(new LambdaUpdateWrapper<CustEntity>()
                .eq(CustEntity::getCustId, cust.getCustId())
                .set(CustEntity::getLoginIp, IPUtils.getIpAddr(request))
                .set(CustEntity::getLoginTime, DateUtils.getCurrentTime())
        );
        JSONObject token = tokenUtils.genToken(Convert.toStr(cust.getCustId()));
        JSONObject result = new JSONObject();
        result.putOpt("token", token);
        result.putOpt("user", cust);
        return R.ok().putData(result);
    }

    @ApiOperation("一键登录")
    @PostMapping("/authCodeLogin")
    @SysLog("一键登录")
    public R login(HttpServletRequest request, @RequestBody Map<String, Object> form) {
        String code = MapUtil.getStr(form, "code");
        if(StrUtil.isBlank(code)){
            return R.error(ResultCode.RESULT_401);
        }
        String custId = redisTemplate.opsForValue().get("custauthcode:" + code);
        if(StrUtil.isBlank(code)){
            return R.error(ResultCode.RESULT_401);
        }

        CustEntity cust = custService.getById(custId);
        if (cust.getLevel() != null && cust.getLevel() != 0) {
            cust.setGrade(goodsGradeService.getById(cust.getLevel()));
        }
        if (cust.getStatus() != 1) {
            return R.error(ResultCode.RESULT_100010);
        }
        cust.setLoginTime(DateUtils.getCurrentTime());
        cust.setLoginIp(IPUtils.getIpAddr(request));
        custService.update(new LambdaUpdateWrapper<CustEntity>()
                .eq(CustEntity::getCustId, cust.getCustId())
                .set(CustEntity::getLoginIp, IPUtils.getIpAddr(request))
                .set(CustEntity::getLoginTime, DateUtils.getCurrentTime())
        );
        JSONObject token = tokenUtils.genToken(Convert.toStr(cust.getCustId()));
        JSONObject result = new JSONObject();
        result.putOpt("token", token);
        result.putOpt("user", cust);
        return R.ok().putData(result);
    }

    @SysLog("客户注册")
    @Transactional(rollbackFor = Exception.class)
    @ApiOperation("注册")
    @PostMapping("/register")
    public R register(HttpServletRequest request, @RequestBody RegisterForm form) {
        ValidatorUtils.validateEntity(form);

        //判断手机号不等于10位
        if(form.getMobile().length()!=8)  return R.error(ResultCode.RESULT_100051);
        String superSmsCode = sysConfigService.getValue(getOrgId(), "super_smscode");
        //判断手机验证码是否正确 暂时搁置
        /*String key = "REIGSTER_SMS_CODE:91" + form.getMobile();
        String redisValue = redisTemplate.opsForValue().get(key);
        if(!superSmsCode.equals(form.getOtp())){
            if(!form.getOtp().equals(redisValue)){
                return R.error(ResultCode.RESULT_100050);
            }
        }*/

        String ip = IPUtils.getIpAddr(request);
        int ipLimit = Convert.toInt(sysConfigService.getValue(getOrgId(), REGISTER_IPLIMIT_CONFIG_KEY), 0);
        if (ipLimit > 0) {
            int ipRegisterCount = custService.count(new LambdaQueryWrapper<CustEntity>()
                    .eq(CustEntity::getJoinIp, ip)
                    .eq(CustEntity::getDelFlag, "0")
                    .eq(CustEntity::getOrgId, getOrgId()));
            if (ipRegisterCount >= ipLimit) {
                return R.error(ResultCode.RESULT_100009);
            }
        }
        int count = custService.count(new LambdaQueryWrapper<CustEntity>().eq(CustEntity::getMobile, form.getMobile()));
        if (count > 0) {
            return R.error(ResultCode.RESULT_100013);
        }
        Long initLevel = Convert.toLong(sysConfigService.getValue(getOrgId(), REGISTER_LEVEL_CONFIG_KEY));
        String salt = RandomUtil.randomString(6);
        //平台平移数据 双md5修改成md5
        String password = MD5Util.md5(form.getPassword());
        CustEntity cust = new CustEntity();
        cust.setCustName(form.getMobile());
        cust.setMobileArea(form.getMobileArea());
        cust.setMobile(form.getMobile());
        cust.setPwd(password);
        cust.setSalt(salt);
        cust.setCreateTime(DateUtils.getCurrentTime());
        cust.setJoinTime(DateUtils.getCurrentTime());
        cust.setJoinIp(ip);
        cust.setOrgId(getOrgId());
        cust.setLevel(initLevel);
        cust.setLastLevel(initLevel);
        cust.setStatus(1);
        cust.setLevelUpTime(DateUtils.getCurrentTime());
        // 上级
        CustEntity parent = custService.getById(form.getInviteCode());
        if (parent != null) {
            cust.setSalesmanId(parent.getSalesmanId());
            String parentIds = parent.getParentIdList();
            if (StrUtil.isNotBlank(parentIds)) {
                parentIds = parent.getCustId() + "," + parentIds;
            } else {
                parentIds = parent.getCustId() + "";
            }
            cust.setParentId(parent.getCustId());
            cust.setParentIdList(parentIds);
            //符合条件的上级赠送一次抽奖
            if(luckUtils.checkAble(LuckUtils.SHAREABLE, parent.getLevel(), null)) {
                Map<String, Object> custUpdate = new HashMap<>();
                custUpdate.put("custId", parent.getCustId());
                custUpdate.put("luckLeftNum", 1);
                custUpdate.put("luckTotalNum", 1);
                custService.updateMoney(custUpdate);
            }
        } else {
            return R.error(ResultCode.RESULT_100016);
        }
        String configStr = sysConfigService.getValue(Convert.toInt(getOrgId()), REGISTER_REWOARD_CONFIG_KEY);
        if(StrUtil.isNotBlank(configStr)){
            JSONObject configObj = JSONUtil.parseObj(configStr);
            BigDecimal money = configObj.getBigDecimal("reward", new BigDecimal("0"));
            int expire = configObj.getInt("expire", 0);
            //设置过期时间
            cust.setExpireTime(DateUtils.getCurrentTime() + 3600 * 24 * expire);
            // 赠送注册金
            cust.setLeftCommissionMoney(money);
        }
        custService.save(cust);
        //注册赠送体验设备

        /*LGoodsEntity goods = lGoodsService.getById(78);
        LOrderEntity order = new LOrderEntity();
        order.setCustId(cust.getCustId());
        order.setCustName(form.getMobile());
        order.setSalesmanId(parent.getSalesmanId());
        order.setIsNb(0);
        order.setGoodsId(goods.getId());
        order.setGoodsRoom(goods.getRoomId());
        order.setGoodsName(goods.getName());
        order.setGoodsImg(goods.getImg());
        order.setGoodsPrice(goods.getPrice());
        order.setGoodsCycle(goods.getDays());
        order.setGoodsRate(goods.getRate());
        order.setGoodsSort(goods.getSort());
        order.setCreateTime(DateUtils.getCurrentTime());
        order.setReceiveTime(DateUtils.getCurrentTime());
        order.setOrderStatus(1);
        order.setExpireTime(DateUtils.getCurrentTime() + 3600 * 24 * goods.getDays());
        order.setGoodsIncomeDay(goods.getIncomeDay());
        order.setGoodsIsDay(goods.getIsDay());
        order.setGoodsLevel(goods.getGoodsLevel());
        order.setGoodsIsGive(goods.getIsGive());
        order.setGoodsIsCapitalReturn(goods.getIsCapitalReturn());
        lOrderService.save(order);*/


        // 写余额变化记录
        CustScoreLogEntity log = new CustScoreLogEntity();
        log.setCustId(cust.getCustId());
        log.setType(16);
        log.setCreateTime(DateUtils.getCurrentTime());
        log.setScore(cust.getLeftCommissionMoney());
        //====lxy
        log.setBeforeScore(new BigDecimal(0));
        log.setAfterScore(cust.getLeftCommissionMoney());
        //====lxy
        log.setDirect(1);
        log.setSalesmanId(cust.getSalesmanId());
        log.setIsNb(cust.getIsNb());
        log.setRemark("注册赠送余额：" + cust.getLeftCommissionMoney());
        log.setSourceId(0L);
        custScoreLogService.save(log);

        return R.ok();
    }

    @ApiOperation("发送短信验证码")
    @PostMapping("/sendSms")
    @SysLog("发送短信验证码")
    public R sendSms(HttpServletRequest request, @RequestBody Map<String, Object> form) {
        String tel = MapUtil.getStr(form, "tel");
        String type = MapUtil.getStr(form, "type"); // REIGSTER_SMS_CODE 注册 FORGET_SMS_CODE 忘记密码 BANK_SMS_CODE绑定银行卡

        if(StrUtil.isBlank(tel)){
            return R.error(ResultCode.RESULT_401);
        }
        if(StrUtil.isBlank(type)){
            return R.error(ResultCode.RESULT_401);
        }
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for(int i=0;i<6;i++){
            sb.append(random.nextInt(10));
        }
        //发送短信验证码  todo
        String key = type+":91" + tel;
        String appId = "vCs0ScQj";
        String appKey = "Z4iorqvy";
        String appAccount = "7RRVNrvO";
        long datetime =  System.currentTimeMillis() / 1000;
        String sign =  MD5Util.md5(appAccount+appKey+datetime);
        String msg = sb + " is your register verification code.";
        String url = "https://api.itniotech.com/sms/sendSms?"+appId+"&91"+tel+"&"+ URLEncoder.encode(msg) +
                "&appId="+appId+"&numbers=91"+tel+"&content="+URLEncoder.encode(msg);

        if(sendSms(url,appAccount,sign,datetime)==false){ //todo
            return R.error(ResultCode.RESULT_100050);
        }
        String redisValue = redisTemplate.opsForValue().get(key);
        if(StrUtil.isNotBlank(redisValue)){
            return R.error(ResultCode.RESULT_100049);
        }
        redisTemplate.opsForValue().set(key, sb.toString(), 300, TimeUnit.SECONDS);
        return R.ok();
    }

    private Boolean sendSms(String url,String appAccount,String sign,long datetime){
        HashMap<String, String> headers = new HashMap<>();//存放请求头，可以存放多个请求头
        headers.put("Content-Type", "application/json");
        headers.put("Sign", sign);
        headers.put("Api-Key", appAccount);
        headers.put("Timestamp", String.valueOf(datetime));
        log.info("短信验证码请求url:{}", url);
        log.info("短信验证码请求headers:{}", headers.toString());
        String body = HttpUtil.createGet(url).addHeaders(headers).execute().body();
        log.info("短信验证码返回:{}", body);
        JSONObject object = JSONUtil.parseObj(body);
        String status = (String) object.get("status");
        if(status.equals("0")){
            return true;
        }
        return false;
    }

    @Login
    @ApiOperation("退出")
    @PostMapping("/logout")
    public R logout() {
        tokenUtils.delToken(Convert.toStr(getUserId()));
        return R.ok();
    }

    @AccessLimit
    @ApiOperation("app下载地址")
    @GetMapping("/download")
    public R download() {
        String downloadUrl = sysConfigService.getValue(Convert.toInt(getOrgId()), "DOWNLOAD_URL");
        JSONObject dConfig = JSONUtil.parseObj(downloadUrl);
        return R.ok().putData(dConfig);
    }

    @SysLog("客户通过验证码修改登录密码")
    @AccessLimit
    @ApiOperation("短信验证码修改密码")
    @PostMapping("/changepwdbysms")
    public R changepwdbysms( @RequestBody JSONObject params) {
        String mobile = params.getStr("mobile");
        String password = params.getStr("password");
        String otp = params.getStr("otp");
        //判断手机号不小于10位
        if(mobile.length()!=8)  return R.error(ResultCode.RESULT_100051);
        String key = "FORGET_SMS_CODE:91" + mobile;
        String redisValue = redisTemplate.opsForValue().get(key);
        String superSmsCode = sysConfigService.getValue(getOrgId(), "super_smscode");
        if(!superSmsCode.equals(otp)){
            if(!otp.equals(redisValue)){
                return R.error(ResultCode.RESULT_100050);
            }
        }
        String newPwdSe = MD5Util.md5(password);
        custService.update(new LambdaUpdateWrapper<CustEntity>()
                .eq(CustEntity::getCustName, mobile)
                .set(CustEntity::getPwd, newPwdSe));
        return R.ok();
    }
    @SysLog("客户修改登录密码")
    @AccessLimit
    @Login
    @ApiOperation("修改密码")
    @PostMapping("/changepwd")
    public R changepwd(@LoginUser UserEntity user, @RequestBody JSONObject params) {
        String newPwd = params.getStr("newPwd");
        String oldPwd = params.getStr("oldPwd");
        String oldPwdSe = MD5Util.md5(oldPwd);
        if (!oldPwdSe.equals(user.getPwd())) {
            return R.error(ResultCode.RESULT_100006);
        }
        String newPwdSe = MD5Util.md5(newPwd);
        custService.update(new LambdaUpdateWrapper<CustEntity>()
                .eq(CustEntity::getCustId, user.getCustId())
                .set(CustEntity::getPwd, newPwdSe));
        return R.ok();
    }

    @SysLog("客户修改提现密码")
    @AccessLimit
    @Login
    @ApiOperation("修改提现密码")
    @PostMapping("/cashpwd")
    public R cashpwd(@LoginUser UserEntity user, @RequestBody JSONObject params) {
        String newPwd = params.getStr("newPwd");
        String oldPwd = params.getStr("oldPwd");
        int type = params.getInt("type", 1);
        if (type == 1) {
            // 设置新密码
            user.setSalt1(RandomUtil.randomString(6));
        } else if (type == 2) {
            // 修改原密码
            String oldPwdSe = MD5Util.md5(oldPwd);
            if (!oldPwdSe.equals(user.getPwd1())) {
                return R.error(ResultCode.RESULT_100006);
            }
        }
        String newPwdSe = MD5Util.md5(newPwd);
        custService.update(new LambdaUpdateWrapper<CustEntity>()
                .eq(CustEntity::getCustId, user.getCustId())
                .set(CustEntity::getPwd1, newPwdSe)
                .set(type == 1, CustEntity::getSalt1, user.getSalt1()));
        return R.ok();
    }

    @Login
    @ApiOperation("用户信息")
    @GetMapping("/user/info")
    public R userInfo(@LoginUser UserEntity user) {
        TGoodsGradeEntity grade = custScoreLogService.setCustLevel(user);
        user.setGrade(grade);
        user.setLevel(grade.getId());
        return R.ok().putData(user);
    }

}
