package com.tcc.modules.cust.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tcc.common.annotation.SysLog;
import com.tcc.common.utils.*;
import com.tcc.modules.base.AbstractBackController;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.service.CustService;
import com.tcc.modules.l.entity.LOrderEntity;
import com.tcc.modules.sys.entity.SysUserEntity;
import com.tcc.modules.sys.service.SysUserService;
import com.tcc.modules.t.entity.TGoodsGradeEntity;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * 会员表
 *
 * @author 
 * @email 
 * @date 2022-07-15 17:39:38
 */
@RestController
@RequestMapping("cust/cust")
public class CustController extends AbstractBackController {
    @Autowired
    private CustService custService;
    @Autowired private SysUserService sysUserService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 获取客户登录code
     * @param custId
     * @return
     */
    @GetMapping("/getAuthCode")
    public R getAuthCode(String custId){
        String code = MD5.create().digestHex(custId);
        redisTemplate.opsForValue().set("custauthcode:" + code, custId, 60, TimeUnit.SECONDS);
        return R.ok().put("authCode", code);
    }


    /**
     * 系统增加扣减体验金
     */
    @SysLog("系统增加扣减体验金")
    @PostMapping("/registerMoney")
    public R registerMoney(@RequestBody JSONObject params){
        Long custId = params.getLong("custId");
        BigDecimal money = params.getBigDecimal("money");
        Integer type = params.getInt("type");
        if (type == 2) {
            money = money.negate();
        }
        custService.update(new LambdaUpdateWrapper<CustEntity>()
                .eq(CustEntity::getCustId, custId)
                .setSql("register_money = register_money+"+money));
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    @RequiresPermissions("cust:cust:list")
    public R list(@RequestParam Map<String, Object> params){
        params.put("orgId",getOrgId());
        String teamList = Convert.toStr(params.get("team"));
        if(StrUtil.isNotBlank(teamList)){
            String[] checkList =  teamList.split(",");
            params.put("team",checkList);
        }
        else{
            return R.error("请选择人员");
        }
        params.put("key",StrUtil.cleanBlank(Convert.toStr(params.get("key"))));
        String sortField = MapUtil.getStr(params, "sortField");
        if(StrUtil.isBlank(sortField)){
            sortField = "joinTime";
        }
        sortField = StrUtil.toUnderlineCase(sortField);
        params.put("sortField", sortField);
        PageUtils page = custService.queryPage(params);

        return R.ok().put("page", page);
    }
    /**
     * 列表
     */
    @RequestMapping("/childList")
    public R childList(@RequestParam Map<String, Object> params){
        params.put("orgId",getOrgId());

        String sortField = MapUtil.getStr(params, "sortField");
        if(StrUtil.isBlank(sortField)){
            sortField = "joinTime";
        }
        sortField = StrUtil.toUnderlineCase(sortField);
        params.put("sortField", sortField);
        PageUtils page = custService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/parentList/{custId}")
    public R parentList(@PathVariable("custId") Long custId){
        CustEntity cust = custService.getById(custId);
        String parentId = cust.getParentIdList();
        List<Long> parentIdList = Convert.toList(Long.class, parentId);
        List<CustEntity> list = custService.list(new LambdaQueryWrapper<CustEntity>()
                .in(CustEntity::getCustId,parentIdList)
        );
        return R.ok().put("list",list);
    }

    /**
     * 信息
     */
    @RequestMapping("/info/{custId}")
    @RequiresPermissions("cust:cust:info")
    public R info(@PathVariable("custId") Long custId){
		CustEntity cust = custService.getById(custId);

        return R.ok().put("cust", cust);
    }

    /**
     * 直属下级个数
     */
    @RequestMapping("/count/{custId}")
    public R count(@PathVariable("custId") Long custId){
        Integer number = custService.getChildCount(custId);
        return R.ok().put("child", number);
    }

    /**
     * 保存
     */
    @SysLog("客户新增")
    @RequestMapping("/save")
    @RequiresPermissions("cust:cust:save")
    public R save(@RequestBody CustEntity cust){
       //是否关联内部员工  账号类型(1内部0外部)
       if(cust.getIsNb()==1){
           //判断该业务员id是否存在
           SysUserEntity userEntity= sysUserService.getById(cust.getSalesmanId());
           if(userEntity==null){
               return R.error("不存在该业务员");
           }
       }
       long expireTime = DateUtil.offsetDay(new Date(), 120).getTime();
        cust.setExpireTime(Convert.toInt(expireTime/1000));
        cust.setCreateTime(DateUtils.getCurrentTime());
        cust.setCreateBy(getUserId());
        cust.setDelFlag("0");
        cust.setOrgId(getOrgId());
        //密码
        String salt = RandomUtil.randomString(6);
        String password = MD5Util.md5(cust.getPwd());
        cust.setPwd(password);
        cust.setSalt(salt);
        cust.setTotalRechargeMoney(cust.getLeftRechargeMoney());
        cust.setParentId(0L);
        cust.setParentIdList("");
        cust.setJoinTime(DateUtils.getCurrentTime());
		custService.save(cust);
        return R.ok();
    }

    /**
     * 修改
     */
    @SysLog("客户修改")
    @RequestMapping("/update")
    @RequiresPermissions("cust:cust:update")
    public R update(@RequestBody CustEntity cust){
        //判断该业务员id是否存在
        SysUserEntity userEntity= sysUserService.getById(cust.getSalesmanId());
        if(userEntity==null){
            return R.error("不存在该业务员");
        }
        cust.setUpdateTime(DateUtils.getCurrentTime());
        cust.setUpdateBy(getUserId());
        if(StringUtils.isBlank(cust.getPwd())){
            cust.setPwd(null);
        }else{
            String salt = RandomUtil.randomString(6);
            String password = MD5Util.md5(cust.getPwd());
            cust.setPwd(password);
            cust.setSalt(salt);
        }
		custService.updateById(cust);

        return R.ok();
    }

    /**
     * 删除
     */
    @SysLog("客户删除")
    @RequestMapping("/delete")
    @RequiresPermissions("cust:cust:delete")
    public R delete(@RequestBody Long[] custIds){
        for (Long custId:custIds) {
            CustEntity custEntity=new CustEntity();
            custEntity.setCustId(custId);
            custEntity.setDelFlag("1");
            custService.updateById(custEntity);
        }
        return R.ok();
    }

    /**
     * 重置登陆密码
     */
    @SysLog("客户重置密码")
    @PostMapping("/resetPassword")
    public R resetPassword(@RequestBody Map<String, Object> params){
        //sha256加密
        Integer custId = Convert.toInt(params.get("custId"));
        CustEntity custEntity=	 custService.getById(custId);
        String newPassword = MD5Util.md5("123456");

       // String newPassword = new Sha256Hash("123456",custEntity.getSalt()).toHex();
        //更新密码
        boolean flag = custService.resetPassword(custId, newPassword);
        if(!flag){
            return R.error("初始化密码错误");
        }
        return R.ok();
    }
    /**
     * 重置提现密码
     */
    @SysLog("客户重置提现密码")
    @PostMapping("/resetMoneyPassword")
    public R resetMoneyPassword(@RequestBody Map<String, Object> params){
        //sha256加密
        Integer custId = Convert.toInt(params.get("custId"));
        CustEntity custEntity=	 custService.getById(custId);
        if(StrUtil.isBlank(custEntity.getSalt1())){
            String salt = RandomUtil.randomString(6);
            custEntity.setSalt1(salt);
        }
        String newPassword = MD5Util.md5("123456");
        //更新密码
        boolean flag = custService.resetMoneyPassword(custId, newPassword,custEntity.getSalt1());
        if(!flag){
            return R.error("初始化密码错误");
        }
        return R.ok();
    }

    /**
     * 状态变化
     * @param params
     * @return
     */
    @SysLog("客户状态修改")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/statusChage")
    public R statusChage(@RequestBody Map<String, Object> params){
        Long custId = Convert.toLong(params.get("custId"));
        Integer status =  Convert.toInt(params.get("status"));
        CustEntity custEntity = new CustEntity();
        custEntity.setCustId(custId);
        custEntity.setStatus(status);
        custService.update(new LambdaUpdateWrapper<CustEntity>()
                .eq(CustEntity::getCustId, custId)
                .set(CustEntity::getStatus, status));
        int type = Convert.toInt(params.get("type"), 1);
        if(type == 2) {
            String sql = StrUtil.format("where FIND_IN_SET({}, parent_id_list) != 0", custId);
            custService.update(custEntity,
                    new LambdaUpdateWrapper<CustEntity>()
                            .set(CustEntity::getStatus, status)
                            .last(sql));
        }
        return R.ok();
    }

    @SysLog("客户提现状态修改")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/limitStatusChage")
    public R limitStatusChage(@RequestBody Map<String, Object> params){
        Long custId = Convert.toLong(params.get("custId"));
        Integer status =  Convert.toInt(params.get("status"));
        custService.update(new LambdaUpdateWrapper<CustEntity>()
                .eq(CustEntity::getCustId, custId)
                .set(CustEntity::getWithdrawLimit, status));
        return R.ok();
    }

    @SysLog("客户特殊账号状态修改")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/custPrivilegeChage")
    public R custPrivilegeChage(@RequestBody Map<String, Object> params){
        Long custId = Convert.toLong(params.get("custId"));
        Integer isPrivilege =  Convert.toInt(params.get("isPrivilege"));
        custService.update(new LambdaUpdateWrapper<CustEntity>()
                .eq(CustEntity::getCustId, custId)
                .set(CustEntity::getIsPrivilege, isPrivilege));
        return R.ok();
    }

    @SysLog("客户理财状态修改")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/custTaskLimit")
    public R custTaskLimit(@RequestBody Map<String, Object> params){
        Long custId = Convert.toLong(params.get("custId"));
        Integer taskLimit =  Convert.toInt(params.get("taskLimit"));
        custService.update(new LambdaUpdateWrapper<CustEntity>()
                .eq(CustEntity::getCustId, custId)
                .set(CustEntity::getTaskLimit, taskLimit));
        return R.ok();
    }

    /**
     * 调整上级
     * @param params
     * @return
     */
    @SysLog("客户调整上级")
    @PostMapping("/changeUp")
    public R changeUp(@RequestBody JSONObject params){
        Long sid = params.getLong("sid");
        Long did = params.getLong("did");
        custService.changeUp(sid, did);
        return R.ok();
    }

    /**
     * 调整上级
     * @param params
     * @return
     */
    @SysLog("整线客户变更员工")
    @PostMapping("/changeUpGroup")
    public R changeUpGroup(@RequestBody JSONObject params){
        String sid = params.getStr("susername");
        String did = params.getStr("dusername");
        custService.changeUpGroup(sid, did);
        return R.ok();
    }

    /**
     * 员工合并
     * @param params
     * @return
     */
    @SysLog("员工合并")
    @PostMapping("/userMerge")
    public R userMerge(@RequestBody JSONObject params){
        String sid = params.getStr("susername");
        String did = params.getStr("dusername");
        custService.mergeUser(sid, did);
        return R.ok();
    }

    /**
     * 员工合并
     * @param params
     * @return
     */
    @SysLog("跨组员工合并")
    @PostMapping("/userMergeGroup")
    public R userMergeGroup(@RequestBody JSONObject params){
        String sid = params.getStr("susername");
        String did = params.getStr("dusername");
        custService.userMergeGroup(sid, did);
        return R.ok();
    }

    @SysLog("修改手机号")
    @PostMapping("/changeMobile")
    public R changeMobile(@RequestBody JSONObject params){
        Long custId = params.getLong("custId");
        String mobile = params.getStr("mobile");
        custService.update(new LambdaUpdateWrapper<CustEntity>()
                .eq(CustEntity::getCustId, custId)
                .set(CustEntity::getMobile, mobile));
        return R.ok();
    }

    @SysLog("修改备注")
    @PostMapping("/changeRemark")
    public R changeRemark(@RequestBody JSONObject params){
        Long custId = params.getLong("custId");
        String remark = params.getStr("remark");
        custService.update(new LambdaUpdateWrapper<CustEntity>()
                .eq(CustEntity::getCustId, custId)
                .set(CustEntity::getRemark, remark));
        return R.ok();
    }

    @SysLog("修改会员等级")
    @PostMapping("/changeVip")
    public R changeVip(@RequestBody JSONObject params){
        Long custId = params.getLong("custId");
        String level = params.getStr("level");
        custService.update(new LambdaUpdateWrapper<CustEntity>()
                .eq(CustEntity::getCustId, custId)
                .set(CustEntity::getLevel, level));
        return R.ok();
    }

    @SysLog("赠送抽奖次数")
    @PostMapping("/sendLuckNum")
    public R sendLuckNum(@RequestBody JSONObject params){
        Long custId = params.getLong("custId");
        int type = params.getInt("type", 1);
        int num = params.getInt("num", 1);
        CustEntity cust = custService.getById(custId);
        if(type == -1 && cust.getLuckLeftNum() < num){
            return R.error("扣减的抽奖次数不能大于客户的剩余的抽奖次数");
        }
        num = num * type;
        Map<String, Object> m = new HashMap<>();
        m.put("custId", custId);
        m.put("luckLeftNum", num);
        m.put("luckTotalNum", num);
        custService.updateMoney(m);
        return R.ok();
    }



}
