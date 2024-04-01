package com.tcc.modules.cust.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.Constant;
import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;
import com.tcc.modules.cust.dao.CustDao;
import com.tcc.modules.cust.entity.CommissionEntity;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.entity.CustScoreLogEntity;
import com.tcc.modules.cust.entity.RechargeEntity;
import com.tcc.modules.cust.service.CommissionService;
import com.tcc.modules.cust.service.CustScoreLogService;
import com.tcc.modules.cust.service.CustService;
import com.tcc.modules.cust.service.RechargeService;
import com.tcc.modules.exercise.entity.SignProgressEntity;
import com.tcc.modules.exercise.service.SignProgressService;
import com.tcc.modules.g.entity.GOrderEntity;
import com.tcc.modules.g.service.GOrderService;
import com.tcc.modules.l.entity.LOrderCommmissionEntity;
import com.tcc.modules.l.entity.LOrderEntity;
import com.tcc.modules.l.service.LOrderCommmissionService;
import com.tcc.modules.l.service.LOrderService;
import com.tcc.modules.sys.entity.SysUserEntity;
import com.tcc.modules.sys.service.SysConfigService;
import com.tcc.modules.sys.service.SysUserService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tcc.common.utils.ConfigConstant.REGISTER_REWOARD_CONFIG_KEY;


@Service("custService")
public class CustServiceImpl extends ServiceImpl<CustDao, CustEntity> implements CustService {

    @Autowired
    private CustScoreLogService custScoreLogService;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private RechargeService rechargeService;

    @Autowired
    private CommissionService commissionService;

    @Autowired
    private GOrderService gOrderService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SignProgressService signProgressService;

    @Autowired
    private LOrderService lOrderService;

    @Autowired
    private LOrderCommmissionService lOrderCommmissionService;

    @Override
    public PageUtils queryPage(@Param("condition") Map<String, Object> params) {
        IPage page = new Query<>().getPage(params);
        List<Map<String, Object>> custList = this.baseMapper.getCustList(page, params);
        page.setRecords(custList);
        return new PageUtils(page);
    }

    @Override
    public CustEntity getByUsername(String username) {
        return this.getOne(new LambdaQueryWrapper<CustEntity>()
                .eq(CustEntity::getCustName, username)
                .or()
                .eq(CustEntity::getMobile, username)
                .last("limit 0, 1"));
    }

    @Override
    public Integer getChildCount(Long custId) {
        return this.count(new LambdaQueryWrapper<CustEntity>().eq(CustEntity::getParentId, custId));
    }

    @Override
    public boolean resetPassword(Integer custId, String newPassword) {
        CustEntity custEntity = new CustEntity();
        custEntity.setPwd(newPassword);
        return this.update(custEntity,
                new QueryWrapper<CustEntity>().eq("cust_id", custId));
    }

    @Override
    public boolean resetMoneyPassword(Integer custId, String newPassword, String salt) {
        CustEntity custEntity = new CustEntity();
        custEntity.setPwd1(newPassword);
        custEntity.setSalt1(salt);
        return this.update(custEntity,
                new QueryWrapper<CustEntity>().eq("cust_id", custId));
    }

    @Override
    public void updateMoney(Map<String, Object> params) {
        this.baseMapper.updateMoney(params);
    }

    /***
     * 调整上级
     * @param custId
     * @param parentId
     */
    public void changeUp(long custId, long parentId) {
        //当前客户信息
        CustEntity custEntity = this.getById(custId);
        //
    }

    /***
     * vip0有效期4天，vip0到期到期后赠金没有了 赚取的佣金有，点击进去就提示需要升级到vip1才能继续完成任务
     * 赠金
     */
    public void vip0Expire() {
        String configStr = sysConfigService.getValue(Constant.SUPER_ORG, REGISTER_REWOARD_CONFIG_KEY);
        int expire = 0;
        if (StrUtil.isNotBlank(configStr)) {
            JSONObject configObj = JSONUtil.parseObj(configStr);
            expire = configObj.getInt("expire", 0);
        }
        if (expire == 0) {
            return;
        }
        Date now = new Date();
        int begin = Convert.toInt(DateUtil.beginOfDay(now).getTime() / 1000);
        int start = begin - (expire - 1) * 24 * 3600 - 1;
        List<CustEntity> list = this.list(new LambdaQueryWrapper<CustEntity>()
                .le(CustEntity::getJoinTime, start)
                .gt(CustEntity::getRegisterMoney, 0)
        );
        list.forEach(item -> {
            if (item.getRegisterMoney().compareTo(new BigDecimal(0)) > 0) {
                Map<String, Object> params = new HashMap<>();
                params.put("custId", item.getCustId());
                params.put("registerMoney", item.getRegisterMoney().multiply(new BigDecimal(-1)));
                this.baseMapper.updateMoney(params);
                //记录变动日志
                //TODO 客户充值余额变动记录
                CustScoreLogEntity scoreLogEntity = new CustScoreLogEntity();
                scoreLogEntity.setCustId(item.getCustId());
                scoreLogEntity.setDirect(2);
                scoreLogEntity.setType(5);
                scoreLogEntity.setScore(item.getRegisterMoney());
                //======lxy=====
                scoreLogEntity.setBeforeScore(item.getLeftRechargeMoney());
                scoreLogEntity.setAfterScore(item.getLeftRechargeMoney()); //清除体验金 余额不发生变化
                //======lxy=====
                scoreLogEntity.setRemark("vip0过期清除注册赠送金额：" + item.getRegisterMoney());
                scoreLogEntity.setCreateTime(DateUtils.getCurrentTime());
                scoreLogEntity.setOrgId(item.getOrgId());
                scoreLogEntity.setIsNb(item.getIsNb());
                scoreLogEntity.setSalesmanId(item.getSalesmanId());
                custScoreLogService.save(scoreLogEntity);
            }
        });
    }

    /***
     * vip1 20天到期自动降为vip0
     */
    public void reduceVip0() {
        int currentTime = DateUtils.getCurrentTime();
        //vip1及vip1以上
        List<CustEntity> list = this.list(new LambdaQueryWrapper<CustEntity>().ge(CustEntity::getLevel, 2).le(CustEntity::getExpireTime, currentTime));
        list.forEach(item -> {
            item.setLevel(1L);
            item.setExpireTime(DateUtils.getCurrentTime() + 3600 * 24 * 14);
            this.updateById(item);
        });
    }


    @Transactional
    @Override
    public void changeUp(Long sid, Long did) {
        CustEntity destCust = getById(did);
        CustEntity sourceCust = getById(sid);
        if (sourceCust == null) {
            throw new WTDPException("被转移的客户ID不存在");
        }
        if (destCust == null) {
            throw new WTDPException("合并到的客户ID不存在");
        }
        Long salesmanId = destCust.getSalesmanId();
        String pids = did + "," + destCust.getParentId();

        this.update(new LambdaUpdateWrapper<CustEntity>()
                .eq(CustEntity::getCustId, sid)
                .set(CustEntity::getParentId, did)
                .set(CustEntity::getParentIdList, pids)
                .set(CustEntity::getSalesmanId, salesmanId)
        );

        rechargeService.update(new LambdaUpdateWrapper<RechargeEntity>()
                .eq(RechargeEntity::getCustId, sid)
                .set(RechargeEntity::getSalesmanId, salesmanId)
        );

        commissionService.update(new LambdaUpdateWrapper<CommissionEntity>()
                .eq(CommissionEntity::getCustId, sid)
                .set(CommissionEntity::getSalesmanId, salesmanId)
        );

        gOrderService.update(new LambdaUpdateWrapper<GOrderEntity>()
                .eq(GOrderEntity::getCustId, sid)
                .set(GOrderEntity::getSalesmanId, salesmanId)
        );

        custScoreLogService.update(new LambdaUpdateWrapper<CustScoreLogEntity>()
                .eq(CustScoreLogEntity::getCustId, sid)
                .set(CustScoreLogEntity::getSalesmanId, salesmanId)
        );

        signProgressService.update(new LambdaUpdateWrapper<SignProgressEntity>()
                .eq(SignProgressEntity::getCustId, sid)
                .set(SignProgressEntity::getSalesmanId, salesmanId)
        );

        lOrderService.update(new LambdaUpdateWrapper<LOrderEntity>()
                .eq(LOrderEntity::getCustId, sid)
                .set(LOrderEntity::getSalesmanId, salesmanId)
        );

        lOrderCommmissionService.update(new LambdaUpdateWrapper<LOrderCommmissionEntity>()
                .eq(LOrderCommmissionEntity::getOrderCust, sid)
                .set(LOrderCommmissionEntity::getSalesmanId, salesmanId)
        );
    }

    /**
     * 合并业务员数据
     *
     * @param susername
     * @param dusername
     */
    @Transactional
    @Override
    public void mergeUser(String susername, String dusername) {
        SysUserEntity sourceUser = sysUserService.getOne(new LambdaQueryWrapper<SysUserEntity>().eq(SysUserEntity::getUsername, susername));
        SysUserEntity destUser = sysUserService.getOne(new LambdaQueryWrapper<SysUserEntity>().eq(SysUserEntity::getUsername, dusername));
        if (sourceUser == null) {
            throw new WTDPException("被转移的员工不存在");
        }
        if (destUser == null) {
            throw new WTDPException("合并到的员工不存在");
        }
        Long sourceUserId = sourceUser.getUserId();
        Long destUserId = destUser.getUserId();

        this.update(new LambdaUpdateWrapper<CustEntity>().eq(CustEntity::getSalesmanId, sourceUserId)
                .set(CustEntity::getSalesmanId, destUserId));
        rechargeService.update(new LambdaUpdateWrapper<RechargeEntity>().eq(RechargeEntity::getSalesmanId, sourceUserId)
                .set(RechargeEntity::getSalesmanId, destUserId));
        commissionService.update(new LambdaUpdateWrapper<CommissionEntity>().eq(CommissionEntity::getSalesmanId, sourceUserId)
                .set(CommissionEntity::getSalesmanId, destUserId));
        custScoreLogService.update(new LambdaUpdateWrapper<CustScoreLogEntity>().eq(CustScoreLogEntity::getSalesmanId, sourceUserId)
                .set(CustScoreLogEntity::getSalesmanId, destUserId));
        gOrderService.update(new LambdaUpdateWrapper<GOrderEntity>().eq(GOrderEntity::getSalesmanId, sourceUserId)
                .set(GOrderEntity::getSalesmanId, destUserId));
        gOrderService.update(new LambdaUpdateWrapper<GOrderEntity>().eq(GOrderEntity::getSalesmanId, sourceUserId)
                .set(GOrderEntity::getSalesmanId, destUserId));
        signProgressService.update(new LambdaUpdateWrapper<SignProgressEntity>().eq(SignProgressEntity::getSalesmanId, sourceUserId)
                .set(SignProgressEntity::getSalesmanId, destUserId));
        lOrderService.update(new LambdaUpdateWrapper<LOrderEntity>().eq(LOrderEntity::getSalesmanId, sourceUserId)
                .set(LOrderEntity::getSalesmanId, destUserId));
        lOrderCommmissionService.update(new LambdaUpdateWrapper<LOrderCommmissionEntity>().eq(LOrderCommmissionEntity::getSalesmanId, sourceUserId)
                .set(LOrderCommmissionEntity::getSalesmanId, destUserId));

    }


    /**
     * 合并业务员数据
     *
     * @param susername
     * @param dusername
     */
    @Transactional
    @Override
    public void userMergeGroup(String susername, String dusername) {
        SysUserEntity sourceUser = sysUserService.getOne(new LambdaQueryWrapper<SysUserEntity>().eq(SysUserEntity::getUsername, susername));
        SysUserEntity destUser = sysUserService.getOne(new LambdaQueryWrapper<SysUserEntity>().eq(SysUserEntity::getUsername, dusername));
        if (sourceUser == null) {
            throw new WTDPException("被转移的员工不存在");
        }
        if (destUser == null) {
            throw new WTDPException("合并到的员工不存在");
        }
        Long sourceUserId = sourceUser.getUserId();
        Long destUserId = destUser.getUserId();
        //支持跨组变线 修改parentIds
        this.update(new LambdaUpdateWrapper<CustEntity>().eq(CustEntity::getSalesmanId, sourceUserId)
                .set(CustEntity::getSalesmanId, destUserId)
                .setSql("parent_id = REPLACE(parent_id,"+sourceUserId+","+destUserId+")")
                .setSql("parent_id_list = REPLACE(parent_id_list,"+sourceUserId+","+destUserId+") ")
        );
        rechargeService.update(new LambdaUpdateWrapper<RechargeEntity>().eq(RechargeEntity::getSalesmanId, sourceUserId)
                .set(RechargeEntity::getSalesmanId, destUserId));
        commissionService.update(new LambdaUpdateWrapper<CommissionEntity>().eq(CommissionEntity::getSalesmanId, sourceUserId)
                .set(CommissionEntity::getSalesmanId, destUserId));
        custScoreLogService.update(new LambdaUpdateWrapper<CustScoreLogEntity>().eq(CustScoreLogEntity::getSalesmanId, sourceUserId)
                .set(CustScoreLogEntity::getSalesmanId, destUserId));
        gOrderService.update(new LambdaUpdateWrapper<GOrderEntity>().eq(GOrderEntity::getSalesmanId, sourceUserId)
                .set(GOrderEntity::getSalesmanId, destUserId));
        gOrderService.update(new LambdaUpdateWrapper<GOrderEntity>().eq(GOrderEntity::getSalesmanId, sourceUserId)
                .set(GOrderEntity::getSalesmanId, destUserId));
        signProgressService.update(new LambdaUpdateWrapper<SignProgressEntity>().eq(SignProgressEntity::getSalesmanId, sourceUserId)
                .set(SignProgressEntity::getSalesmanId, destUserId));
        lOrderService.update(new LambdaUpdateWrapper<LOrderEntity>().eq(LOrderEntity::getSalesmanId, sourceUserId)
                .set(LOrderEntity::getSalesmanId, destUserId));
        lOrderCommmissionService.update(new LambdaUpdateWrapper<LOrderCommmissionEntity>().eq(LOrderCommmissionEntity::getSalesmanId, sourceUserId)
                .set(LOrderCommmissionEntity::getSalesmanId, destUserId));

    }

    /**
     * 整线客户变更员工
     *
     * @param susername
     * @param dusername
     */
    @Transactional
    @Override
    public void changeUpGroup(String susername, String dusername) {
        CustEntity sourceUser = this.getOne(new LambdaQueryWrapper<CustEntity>().eq(CustEntity::getCustName, susername));
        SysUserEntity destUser = sysUserService.getOne(new LambdaQueryWrapper<SysUserEntity>().eq(SysUserEntity::getUsername, dusername));
        if (sourceUser == null) {
            throw new WTDPException("被转移的客户不存在");
        }
        if (destUser == null) {
            throw new WTDPException("合并到的员工不存在");
        }
        CustEntity destCustUser = this.getOne(new LambdaQueryWrapper<CustEntity>().eq(CustEntity::getCustName, dusername));
        Long sourceUserId = sourceUser.getCustId();
        Long destUserId = destUser.getUserId();
        //支持跨组变线 修改parentIds
        this.update(new LambdaUpdateWrapper<CustEntity>().eq(CustEntity::getSalesmanId, sourceUser.getSalesmanId())
                .like(CustEntity::getParentIdList,sourceUserId)
                .set(CustEntity::getSalesmanId, destUserId)
                .setSql("parent_id = REPLACE(parent_id,"+sourceUser.getCustId()+","+destCustUser.getCustId()+")")
                .setSql("parent_id_list = REPLACE(parent_id_list,"+sourceUser.getCustId()+","+destCustUser.getCustId()+") ")
        );


        rechargeService.update(new LambdaUpdateWrapper<RechargeEntity>().eq(RechargeEntity::getSalesmanId, sourceUserId)
                .set(RechargeEntity::getSalesmanId, destUserId));
        commissionService.update(new LambdaUpdateWrapper<CommissionEntity>().eq(CommissionEntity::getSalesmanId, sourceUserId)
                .set(CommissionEntity::getSalesmanId, destUserId));
        custScoreLogService.update(new LambdaUpdateWrapper<CustScoreLogEntity>().eq(CustScoreLogEntity::getSalesmanId, sourceUserId)
                .set(CustScoreLogEntity::getSalesmanId, destUserId));
        gOrderService.update(new LambdaUpdateWrapper<GOrderEntity>().eq(GOrderEntity::getSalesmanId, sourceUserId)
                .set(GOrderEntity::getSalesmanId, destUserId));
        gOrderService.update(new LambdaUpdateWrapper<GOrderEntity>().eq(GOrderEntity::getSalesmanId, sourceUserId)
                .set(GOrderEntity::getSalesmanId, destUserId));
        signProgressService.update(new LambdaUpdateWrapper<SignProgressEntity>().eq(SignProgressEntity::getSalesmanId, sourceUserId)
                .set(SignProgressEntity::getSalesmanId, destUserId));
        lOrderService.update(new LambdaUpdateWrapper<LOrderEntity>().eq(LOrderEntity::getSalesmanId, sourceUserId)
                .set(LOrderEntity::getSalesmanId, destUserId));
        lOrderCommmissionService.update(new LambdaUpdateWrapper<LOrderCommmissionEntity>().eq(LOrderCommmissionEntity::getSalesmanId, sourceUserId)
                .set(LOrderCommmissionEntity::getSalesmanId, destUserId));

    }
}