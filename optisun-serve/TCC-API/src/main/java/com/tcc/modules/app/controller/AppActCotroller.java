package com.tcc.modules.app.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.math.MathUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcc.common.annotation.AccessLimit;
import com.tcc.common.annotation.SysLog;
import com.tcc.common.utils.Constant;
import com.tcc.common.utils.R;
import com.tcc.common.utils.StringUtils;
import com.tcc.modules.app.annotation.Login;
import com.tcc.modules.app.annotation.LoginUser;
import com.tcc.modules.app.entity.UserEntity;
import com.tcc.modules.base.AbstractAppController;
import com.tcc.modules.exercise.dao.SignProductDao;
import com.tcc.modules.exercise.entity.*;
import com.tcc.modules.exercise.service.*;
import com.tcc.modules.sys.service.SysConfigService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/app/act")
@Api("APP活动接口")
public class AppActCotroller extends AbstractAppController {

    @Autowired
    private LuckyDrawService luckyDrawService;

    @Autowired
    private PrizeService prizeService;

    @Autowired
    private SignInService signInService;

    @Autowired
    private RecordService recordService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private SignProductService signProductService;

    @Autowired
    private SignProgressService signProgressService;


    @Login
    @GetMapping("/info")
    public R actInfo(Long actId) {
        LuckyDrawEntity lucky = luckyDrawService.getById(actId);
        if (lucky != null && "0".equals(lucky.getDelFlag())) {
            List<PrizeEntity> list = prizeService.list(new LambdaQueryWrapper<PrizeEntity>().eq(PrizeEntity::getDelFlag, "0"));
            lucky.setPrizeList(list);
            Date now = new Date();
            Date beginTime = DateUtil.beginOfDay(now);
            Date endTime = DateUtil.endOfDay(now);
            int count = recordService.count(new LambdaQueryWrapper<RecordEntity>()
                    .eq(RecordEntity::getUserId, getUserId())
                    .ge(RecordEntity::getDrawTime, beginTime)
                    .le(RecordEntity::getDrawTime, endTime)
            );
            lucky.setTodayNum(count);
            return R.ok().putData(lucky);
        }
        return R.error();
    }

    @AccessLimit
    @SysLog("参与抽奖")
    @Login
    @PostMapping("/start")
    public R start(@LoginUser UserEntity user, @RequestBody Map<String, Object> params) {
        Long actId = Convert.toLong(params.get("actId"));
        long custId = getUserId();
        PrizeEntity prize = luckyDrawService.excute(actId, custId, user.getCustName());
        return R.ok().putData(prize);
    }

    @AccessLimit
    @Login
    @GetMapping("/luckList")
    public R luckList(int pageSize, int pageNo) {
        long userId = getUserId();
        Map<String, Object> params = new HashMap<>();
        params.put(Constant.LIMIT, pageSize);
        params.put(Constant.PAGE, pageNo);
        params.put("userId", userId);
        return R.ok().putData(recordService.queryPage(params));
    }

    @AccessLimit
    @SysLog("参与签到")
    @Login
    @PostMapping("/sign")
    public R sign() {
        signInService.excute(getUserId());
        return R.ok();
    }

    @Login
    @GetMapping("/signList")
    public R signList() {
        List<SignInEntity> list = signInService.list(new LambdaQueryWrapper<SignInEntity>()
                .select(SignInEntity::getSignInTime, SignInEntity::getPointNum)
                .eq(SignInEntity::getCustId, getUserId())
                .orderByDesc(SignInEntity::getSignInTime)
                .last("limit 0, 5")
        );
        List<Map<String, Object>> result = list.stream().sorted(new Comparator<SignInEntity>() {
            @Override
            public int compare(SignInEntity o1, SignInEntity o2) {
                return o1.getSignInTime().compareTo(o2.getSignInTime());
            }
        }).map(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("time", DateUtil.format(item.getSignInTime(), "MM.dd"));
            map.put("pointNum", item.getPointNum());
            map.put("amount", item.getGiftAmount());
            return map;
        }).collect(Collectors.toList());
        return R.ok().putData(result);
    }

    @Login
    @GetMapping("/signReward")
    public R signReward(){
        String configStr = sysConfigService.getValue(Constant.SUPER_ORG, "SIGN_IN_SETTING");
        if (StringUtils.isNotBlank(configStr)) {
            JSONObject config = JSONUtil.parseObj(configStr);
            if (config.containsKey("domains")) {
                JSONArray domains = JSONUtil.parseArray(config.get("domains"));
                return R.ok().putData(domains);
            }
        }
        return R.error("no data");
    }

    @Login
    @GetMapping("/pointProduct")
    public R pointProduct(){
        List<SignProductEntity> productList = signProductService.list();
        List<SignProgressEntity> progressList = signProgressService.list(new LambdaQueryWrapper<SignProgressEntity>()
                .eq(SignProgressEntity::getCustId, getUserId()));
        Map<Long, SignProgressEntity> progressMap = new HashMap<>();
        progressList.forEach(item -> {
            progressMap.put(item.getProductId(), item);
        });
        List<JSONObject> result = productList.stream().map(product -> {
            JSONObject obj = new JSONObject();
            if(progressMap.containsKey(product.getId())) {
                SignProgressEntity progress = progressMap.get(product.getId());
                obj.putOpt("productId", progress.getProductId());
                obj.putOpt("productName", progress.getProductName());
                obj.putOpt("productImg", progress.getProductImg());
                obj.putOpt("productPrice", progress.getProductPrice());
                obj.putOpt("progress", NumberUtil.roundDown(Convert.toDouble(progress.getSignProgress() / 100000.00), 5));
            }else{
                obj.putOpt("productId", product.getId());
                obj.putOpt("productName", product.getName());
                obj.putOpt("productImg", product.getImg());
                obj.putOpt("productPrice", product.getPrice());
                obj.putOpt("progress", 0);
            }
            return obj;
        }).collect(Collectors.toList());
        return R.ok().putData(result);
    }

    @AccessLimit
    @SysLog("积分夺宝")
    @Login
    @PostMapping("/pointExchange")
    public R pointExchange(@RequestBody Map<String, Object> params) {
        Long productId = Convert.toLong(params.get("productId"));
        signProgressService.pointExchange(getUserId(), productId);
        return R.ok();
    }

}
