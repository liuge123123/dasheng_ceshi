package com.tcc.modules.l.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.*;
import com.tcc.modules.app.dao.AppAccountDao;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.entity.CustScoreLogEntity;
import com.tcc.modules.cust.entity.TaskCommissionEntity;
import com.tcc.modules.cust.service.CustScoreLogService;
import com.tcc.modules.cust.service.CustService;
import com.tcc.modules.cust.service.TaskCommissionService;
import com.tcc.modules.exercise.entity.PrizeEntity;
import com.tcc.modules.exercise.entity.RecordEntity;
import com.tcc.modules.l.dao.LOrderDao;
import com.tcc.modules.l.entity.LGoodsEntity;
import com.tcc.modules.l.entity.LOrderCommmissionEntity;
import com.tcc.modules.l.entity.LOrderEntity;
import com.tcc.modules.l.entity.LOrderCountEntity;
import com.tcc.modules.l.entity.LRoomEntity;
import com.tcc.modules.l.service.LGoodsService;
import com.tcc.modules.l.service.LOrderCommmissionService;
import com.tcc.modules.l.service.LOrderService;
import com.tcc.modules.l.service.LOrderCountService;
import com.tcc.modules.l.service.LRoomService;
import com.tcc.modules.sys.service.SysConfigService;
import com.tcc.modules.t.entity.TGoodsGradeEntity;
import com.tcc.modules.t.service.TGoodsGradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("lOrderService")
public class LOrderServiceImpl extends ServiceImpl<LOrderDao, LOrderEntity> implements LOrderService {

    @Autowired
    private LGoodsService lGoodsService;

    @Autowired
    private CustService custService;

    @Autowired
    private CustScoreLogService custScoreLogService;

    @Autowired
    private LOrderCommmissionService orderCommmissionService;

    @Autowired
    private LOrderCountService lOrderCountService;

    @Autowired
    private LRoomService lRoomService;

    @Autowired
    private SysConfigService configService;

    @Autowired
    private AppAccountDao appAccountDao;

    @Autowired
    private TaskCommissionService taskCommissionService;

    @Autowired
    private TGoodsGradeService tGoodsGradeService;
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String custId = Convert.toStr(params.get("custId"));
        String goodsName = Convert.toStr(params.get("goodsName"));
        String goodsCycle = Convert.toStr(params.get("goodsCycle"));
        String orderStatus = Convert.toStr(params.get("orderStatus"));
        String createTime = Convert.toStr(params.get("createTime"));
        String expireTime = Convert.toStr(params.get("expireTime"));
        List<Long> team = Convert.toList(Long.class, params.get("team"));
        LambdaQueryWrapper<LOrderEntity> ew = new LambdaQueryWrapper<LOrderEntity>()
                .eq(StrUtil.isNotBlank(custId), LOrderEntity::getCustId, custId)
                .eq(StrUtil.isNotBlank(goodsName), LOrderEntity::getGoodsName, goodsName)
                .eq(StrUtil.isNotBlank(goodsCycle), LOrderEntity::getGoodsCycle, goodsCycle)
                .eq(StrUtil.isNotBlank(orderStatus), LOrderEntity::getOrderStatus, orderStatus)
                .in(LOrderEntity::getSalesmanId, team)
                .orderByDesc(LOrderEntity::getCreateTime);
        if (StrUtil.isNotBlank(createTime)) {
            ew.ge(LOrderEntity::getCreateTime, DateUtil.beginOfDay(DateUtil.parseDate(createTime)).getTime() / 1000)
                    .le(LOrderEntity::getCreateTime, DateUtil.endOfDay(DateUtil.parseDate(createTime)).getTime() / 1000);
        }
        if (StrUtil.isNotBlank(expireTime)) {
            ew.ge(StrUtil.isNotBlank(expireTime), LOrderEntity::getExpireTime, DateUtil.beginOfDay(DateUtil.parseDate(expireTime)).getTime() / 1000)
                    .le(StrUtil.isNotBlank(expireTime), LOrderEntity::getExpireTime, DateUtil.endOfDay(DateUtil.parseDate(expireTime)).getTime() / 1000);
        }
        IPage<LOrderEntity> page = this.page(new Query<LOrderEntity>().getPage(params), ew);
        Date now = new Date();
        List<LOrderEntity> list = page.getRecords();
        List<Map<String, Object>> resultList = list.stream().map(item -> {
            JSONObject json = JSONUtil.parseObj(item);
            json.putOpt("totalProfit", getTotalProfit(now, item));
            if (item.getOrderStatus() == 1 && item.getExpireTime() < now.getTime() / 1000) {
                json.putOpt("orderStatus", 3);
            }
            return json;
        }).collect(Collectors.toList());
        PageUtils pageUtils = new PageUtils(page);
        pageUtils.setList(resultList);
        return pageUtils;
    }

    @Transactional
    @Override
    public void buy(Long custId, Long goodsId) {
        CustEntity cust = custService.getOne(new LambdaQueryWrapper<CustEntity>()
                .eq(CustEntity::getCustId, custId));

        //判断是否允许做单
        if(!cust.getTaskLimit().equals(1)){
            throw new WTDPException("暂时没有匹配到租用设备", ResultCode.RESULT_100046.getCode());
        }

        LGoodsEntity goods = lGoodsService.getById(goodsId);
        if(!goods.getStatus().equals(1)){
            throw new WTDPException("该产品已下架", ResultCode.RESULT_100047.getCode());
        }
        LRoomEntity room = lRoomService.getById(goods.getRoomId());
        Integer isFund = room.getIsFund();//0 普通房间(常规区) 1体验金房间(福利区) 2保健区


        if (goods.getMaxNum() == -1) { //判断是否售罄
            //判断是否特殊账号
            if(cust.getIsPrivilege() ==0){
                throw new WTDPException(ResultCode.RESULT_100048);
            }
        }else if (goods.getMaxNum() > 0) {// 产品购买次数
            int count = this.count(new LambdaQueryWrapper<LOrderEntity>()
                    .eq(LOrderEntity::getGoodsId, goodsId)
                    .eq(LOrderEntity::getCustId, custId)
            );
            if (count >= goods.getMaxNum()) {
                throw new WTDPException(ResultCode.RESULT_100023);
            }
        }
        /*if(isFund.equals(1)){//福利区 购买限制 须常规区(稳健区)购买对应产品 搁置 重新定义该逻辑方法
            int countDaily = this.count(new LambdaQueryWrapper<LOrderEntity>()
                    //.eq(LOrderEntity::getGoodsName, goods.getName())
                    .eq(LOrderEntity::getCustId, custId)
                    .ge(LOrderEntity::getGoodsPrice, 0)
                    .ge(LOrderEntity::getGoodsLevel, goods.getGoodsLevel())        //常规区 高于当前级别的产品 对应级别
                    .ne(LOrderEntity::getGoodsRoom, 1)
                    .ne(LOrderEntity::getOrderStatus, 5)                  //非取消状态
            );
            if (countDaily <= 0) {
                throw new WTDPException(ResultCode.RESULT_100059);
            }
        }*/
        //20240223 级别限制取消
        /*if(isFund.equals(1)){//福利区 购买限制 须常规区(稳健区)购买对应大于等于该产品级别
            int countLevel = lOrderCountService.getCountByLevel(custId, Long.valueOf(goods.getGoodsLevel()));
            logger.info("countLevel______________________>"+countLevel);
            if(countLevel <= 0){
                throw new WTDPException(ResultCode.RESULT_100059);
            }
        }*/

        //根据房间判断是否是体验房间 体验房间扣除体验金
//        if(isFund.equals(1)){
//            if (goods.getPrice().compareTo(cust.getRegisterMoney()) > 0) {
//                // 账户余额不足
//                throw new WTDPException("客户体验金不足，体验金为" + cust.getRegisterMoney(), ResultCode.RESULT_100043.getCode());
//            }
//        }else{
//            if(cust.getPersonCftMoney().compareTo(room.getBuyLimit()) < 0){//小于
//                // 账户余额不足
//                throw new WTDPException("余额限制：" + room.getBuyLimit(), ResultCode.RESULT_100052.getCode());
//            }
//            if (goods.getPrice().compareTo(cust.getLeftCommissionMoney()) > 0) {
//                // 账户余额不足
//                throw new WTDPException("客户账户余额不足，余额为" + cust.getLeftCommissionMoney(), ResultCode.RESULT_100003.getCode());
//            }
//        }

        if(cust.getPersonCftMoney().compareTo(room.getBuyLimit()) < 0){//小于 (buyLimit 房间金额限制)
            // 账户余额不足
            throw new WTDPException("余额限制：" + room.getBuyLimit(), ResultCode.RESULT_100052.getCode());
        }
        if (goods.getPrice().compareTo(cust.getLeftCommissionMoney()) > 0) {//getLeftCommissionMoney
            // 账户余额不足
            throw new WTDPException("客户账户余额不足，余额为" + cust.getLeftCommissionMoney(), ResultCode.RESULT_100003.getCode());
        }
        // 扣减佣金账户余额
        Map<String, Object> custUpdate = new HashMap<>();
        custUpdate.put("custId", custId);
//        if(isFund.equals(1)){//体验金
//            custUpdate.put("registerMoney", goods.getPrice().multiply(new BigDecimal(-1)));
//        }else{
//            custUpdate.put("leftCommissionMoney", goods.getPrice().multiply(new BigDecimal(-1)));
//            custUpdate.put("personCftMoney",goods.getPrice());
//        }
        custUpdate.put("leftCommissionMoney", goods.getPrice().multiply(new BigDecimal(-1)));//可提现佣金(余额)
        //custUpdate.put("LeftRechargeMoney", goods.getPrice().multiply(new BigDecimal(-1)));//减扣充值余额 20240226搁置 改成一个钱包余额
        custUpdate.put("personCftMoney",goods.getPrice());                                       //理财认购总金额
        custUpdate.put("joinCftNum", 1);                                                         //参与理财的次数

        //根据产品更新用户等级
        Long levelOld = cust.getLevel();
        long levelNew = goods.getGoodsLevel();
        if(levelNew > levelOld){//当前产品等级大于用户原等级 则更新等级
            custUpdate.put("level" , levelNew);
            custUpdate.put("lastLevel" ,levelOld);
        }
        custService.updateMoney(custUpdate);


        // 生成订单
        LOrderEntity order = new LOrderEntity();
        order.setCustId(cust.getCustId());
        order.setCustName(cust.getCustName());
        order.setSalesmanId(cust.getSalesmanId());
        order.setIsNb(cust.getIsNb());
        order.setGoodsId(goods.getId());
        order.setGoodsRoom(goods.getRoomId());
        order.setGoodsName(goods.getName());
        order.setGoodsImg(goods.getImg());
        order.setGoodsPrice(goods.getPrice());
        order.setGoodsCycle(goods.getDays());
        order.setGoodsRate(goods.getRate());
        order.setGoodsSort(goods.getSort());
        order.setCreateTime(DateUtils.getCurrentTime());
        order.setReceiveTime(order.getCreateTime());
        order.setOrderStatus(1);
        order.setExpireTime(order.getCreateTime() + 3600 * 24 * goods.getDays());
        order.setGoodsIncomeDay(goods.getIncomeDay());
        order.setGoodsIsDay(goods.getIsDay());
        order.setGoodsLevel(goods.getGoodsLevel());
        order.setGoodsIsGive(goods.getIsGive());
        order.setGoodsIsCapitalReturn(goods.getIsCapitalReturn());
        this.save(order);

        // 生成订单次数记录
        Integer isGive = goods.getIsGive();

        if(isFund.equals(1) || isGive.equals(1)) { //福利区或者赠送产品 不计算次数

        }else{
            int orderCountOld = lOrderCountService.count(new LambdaUpdateWrapper<LOrderCountEntity>()
                            .eq(LOrderCountEntity::getCustId, cust.getCustId())
                            .eq(LOrderCountEntity::getGoodsLevel, goods.getGoodsLevel()));

            if(orderCountOld == 0){//不存在 即新增记录
                LOrderCountEntity orderCount = new LOrderCountEntity();
                orderCount.setCustId(cust.getCustId());
                orderCount.setCreateTime(DateUtils.getCurrentTime());
                orderCount.setUpdateTime(DateUtils.getCurrentTime());
                orderCount.setGoodsOrderCount(1);
                orderCount.setGoodsLevel(goods.getGoodsLevel());
                lOrderCountService.save(orderCount);
            }else{//存在 更新当前级别数量GoodsOrderCount+1
                LOrderCountEntity goodsOrderCount = lOrderCountService.getOne(new LambdaUpdateWrapper<LOrderCountEntity>()
                        .eq(LOrderCountEntity::getCustId, cust.getCustId())
                        .eq(LOrderCountEntity::getGoodsLevel, goods.getGoodsLevel()));
                lOrderCountService.update(new LambdaUpdateWrapper<LOrderCountEntity>()
                                .set(LOrderCountEntity::getGoodsOrderCount,goodsOrderCount.getGoodsOrderCount()+1)
                                .set(LOrderCountEntity::getUpdateTime,DateUtils.getCurrentTime())
                                .eq(LOrderCountEntity::getCustId, cust.getCustId())
                                .eq(LOrderCountEntity::getGoodsLevel, goods.getGoodsLevel()));
            }

        }
        if(isFund.equals(1)){//福利区购买 对应减少订单次数

            for(int i=goods.getGoodsLevel();i<=5;i++){
                int orderCount = lOrderCountService.count(new LambdaUpdateWrapper<LOrderCountEntity>()
                        .eq(LOrderCountEntity::getCustId, cust.getCustId())
                        .gt(LOrderCountEntity::getGoodsOrderCount, 0)
                        .eq(LOrderCountEntity::getGoodsLevel, i));
                if(orderCount >= 1){
                    LOrderCountEntity goodsOrderCount = lOrderCountService.getOne(new LambdaUpdateWrapper<LOrderCountEntity>()
                            .eq(LOrderCountEntity::getCustId, cust.getCustId())
                            .eq(LOrderCountEntity::getGoodsLevel, i));
                    lOrderCountService.update(new LambdaUpdateWrapper<LOrderCountEntity>()
                            .set(LOrderCountEntity::getGoodsOrderCount,goodsOrderCount.getGoodsOrderCount()-1)
                            .set(LOrderCountEntity::getUpdateTime,DateUtils.getCurrentTime())
                            .eq(LOrderCountEntity::getCustId, cust.getCustId())
                            .eq(LOrderCountEntity::getGoodsLevel, i));
                    break;
                }
            }
        }
        // 写扣除本金明细
        CustScoreLogEntity scoreLogEntity = new CustScoreLogEntity();
        scoreLogEntity.setCustId(custId);
        scoreLogEntity.setDirect(2);
        scoreLogEntity.setSourceId(order.getId());
        scoreLogEntity.setScore(goods.getPrice().multiply(new BigDecimal(-1)));
//        if(isFund.equals(1)){ //体验金
//            scoreLogEntity.setType(17);
//            scoreLogEntity.setRemark("基金扣除体验金：" + scoreLogEntity.getScore());
//            //====lxy
//            scoreLogEntity.setBeforeScore(cust.getRegisterMoney());
//            scoreLogEntity.setAfterScore(cust.getRegisterMoney().add(goods.getPrice().multiply(new BigDecimal(-1))));
//            //====lxy
//        }else{

            scoreLogEntity.setType(13);
            scoreLogEntity.setRemark("基金扣除本金：" + scoreLogEntity.getScore()+","+goods.getName());
            //====lxy
            //12.18改 扣除充值余额 20240226 修改钱包余额
            scoreLogEntity.setBeforeScore(cust.getLeftCommissionMoney());
            scoreLogEntity.setAfterScore(cust.getLeftCommissionMoney().add(goods.getPrice().multiply(new BigDecimal(-1))));
            //====lxy
        //}

        scoreLogEntity.setCreateTime(DateUtils.getCurrentTime());
        scoreLogEntity.setOrgId(cust.getOrgId());
        scoreLogEntity.setIsNb(cust.getIsNb());
        scoreLogEntity.setSalesmanId(cust.getSalesmanId());
        custScoreLogService.save(scoreLogEntity);
        //if(isFund.equals(0)){
            //更新等级 12.11 搁置
            //tGoodsGradeService.updateLevel(cust,cust.getPersonCftMoney().add(goods.getPrice()));
        //}
    }
    @Transactional
    @Override
    public void buyGive(Long custId, Long goodsId) {
        CustEntity cust = custService.getOne(new LambdaQueryWrapper<CustEntity>()
                .eq(CustEntity::getCustId, custId));

        //判断是否允许做单
        if(!cust.getTaskLimit().equals(1)){
            throw new WTDPException("暂时没有匹配到租用设备", ResultCode.RESULT_100046.getCode());
        }

        LGoodsEntity goods = lGoodsService.getById(goodsId);
        if(!goods.getStatus().equals(1)){
            throw new WTDPException("该产品已下架", ResultCode.RESULT_100047.getCode());
        }
        LRoomEntity room = lRoomService.getById(goods.getRoomId());
        Integer isFund = room.getIsFund();//0 普通房间(常规区) 1体验金房间(福利区) 2保健区


        if (goods.getMaxNum() == -1) { //判断是否售罄
            //判断是否特殊账号
            if(cust.getIsPrivilege() ==0){
                throw new WTDPException(ResultCode.RESULT_100048);
            }
        }else if (goods.getMaxNum() > 0) {// 产品购买次数
            int count = this.count(new LambdaQueryWrapper<LOrderEntity>()
                    .eq(LOrderEntity::getGoodsId, goodsId)
                    .eq(LOrderEntity::getCustId, custId)
            );
            if (count >= goods.getMaxNum()) {
                throw new WTDPException(ResultCode.RESULT_100023);
            }
        }
        Integer isGive = goods.getIsGive();
        if(isGive.equals(0)) {//非赠送产品
            if (cust.getPersonCftMoney().compareTo(room.getBuyLimit()) < 0) {//小于 (buyLimit 房间理财金额收益限制)
                // 账户余额不足
                throw new WTDPException("余额限制：" + room.getBuyLimit(), ResultCode.RESULT_100052.getCode());
            }
            if (goods.getPrice().compareTo(cust.getLeftCommissionMoney()) > 0) {//getLeftCommissionMoney  getLeftRechargeMoney 20240226钱包余额修改
                // 账户余额不足
                throw new WTDPException("客户账户余额不足，余额为" + cust.getLeftCommissionMoney(), ResultCode.RESULT_100003.getCode());
            }
            // 扣减佣金账户余额
            Map<String, Object> custUpdate = new HashMap<>();
            custUpdate.put("custId", custId);
            custUpdate.put("leftCommissionMoney", goods.getPrice().multiply(new BigDecimal(-1)));//可提现佣金(余额)
            //custUpdate.put("LeftRechargeMoney", goods.getPrice().multiply(new BigDecimal(-1)));//减扣充值余额
            custUpdate.put("personCftMoney",goods.getPrice());                                       //理财认购总金额
            custUpdate.put("joinCftNum", 1);

            //根据产品更新用户等级
            Long levelOld = cust.getLevel();
            long levelNew = goods.getGoodsLevel();
            if(levelNew > levelOld){//当前产品等级大于用户原等级 则更新等级
                custUpdate.put("level" , levelNew);
                custUpdate.put("lastLevel" ,levelOld);
            }
            custService.updateMoney(custUpdate);

            // 生成订单
            LOrderEntity order = new LOrderEntity();
            order.setCustId(cust.getCustId());
            order.setCustName(cust.getCustName());
            order.setSalesmanId(cust.getSalesmanId());
            order.setIsNb(cust.getIsNb());
            order.setGoodsId(goods.getId());
            order.setGoodsRoom(goods.getRoomId());
            order.setGoodsName(goods.getName());
            order.setGoodsImg(goods.getImg());
            order.setGoodsPrice(goods.getPrice());
            order.setGoodsCycle(goods.getDays());
            order.setGoodsRate(goods.getRate());
            order.setGoodsSort(goods.getSort());
            order.setCreateTime(DateUtils.getCurrentTime());
            order.setReceiveTime(order.getCreateTime());
            order.setOrderStatus(1);
            order.setExpireTime(order.getCreateTime() + 3600 * 24 * goods.getDays());
            order.setGoodsIncomeDay(goods.getIncomeDay());
            order.setGoodsIsDay(goods.getIsDay());
            order.setGoodsLevel(goods.getGoodsLevel());
            order.setGoodsIsGive(goods.getIsGive());
            order.setGoodsIsCapitalReturn(goods.getIsCapitalReturn());
            this.save(order);

            // 写扣除本金明细
            CustScoreLogEntity scoreLogEntity = new CustScoreLogEntity();
            scoreLogEntity.setCustId(custId);
            scoreLogEntity.setDirect(2);
            scoreLogEntity.setSourceId(order.getId());
            scoreLogEntity.setScore(goods.getPrice().multiply(new BigDecimal(-1)));

            scoreLogEntity.setType(13);
            scoreLogEntity.setRemark("基金扣除本金：" + scoreLogEntity.getScore()+","+goods.getName());
            //====lxy
            //12.18改 扣除充值余额
            scoreLogEntity.setBeforeScore(cust.getLeftCommissionMoney());
            scoreLogEntity.setAfterScore(cust.getLeftCommissionMoney().add(goods.getPrice().multiply(new BigDecimal(-1))));
            //====lxy
            //}

            scoreLogEntity.setCreateTime(DateUtils.getCurrentTime());
            scoreLogEntity.setOrgId(cust.getOrgId());
            scoreLogEntity.setIsNb(cust.getIsNb());
            scoreLogEntity.setSalesmanId(cust.getSalesmanId());
            custScoreLogService.save(scoreLogEntity);

        }else{//赠送产品 直接生成订单即可
            // 生成订单
            LOrderEntity order = new LOrderEntity();
            order.setCustId(cust.getCustId());
            order.setCustName(cust.getCustName());
            order.setSalesmanId(cust.getSalesmanId());
            order.setIsNb(cust.getIsNb());
            order.setGoodsId(goods.getId());
            order.setGoodsRoom(goods.getRoomId());
            order.setGoodsName(goods.getName());
            order.setGoodsImg(goods.getImg());
            order.setGoodsPrice(goods.getPrice());
            order.setGoodsCycle(goods.getDays());
            order.setGoodsRate(goods.getRate());
            order.setGoodsSort(goods.getSort());
            order.setCreateTime(DateUtils.getCurrentTime());
            order.setReceiveTime(order.getCreateTime());
            order.setOrderStatus(1);
            order.setExpireTime(order.getCreateTime() + 3600 * 24 * goods.getDays());
            order.setGoodsIncomeDay(goods.getIncomeDay());
            order.setGoodsIsDay(goods.getIsDay());
            order.setGoodsLevel(goods.getGoodsLevel());
            order.setGoodsIsGive(goods.getIsGive());
            order.setGoodsIsCapitalReturn(goods.getIsCapitalReturn());
            this.save(order);

        }

    }
    @Transactional
    @Override
    public void sale(Long orderId) {
        LOrderEntity order = this.getOne(new LambdaQueryWrapper<LOrderEntity>()
                .eq(LOrderEntity::getId, orderId)
        );
        BigDecimal profit = null;
        int currentTime = DateUtils.getCurrentTime();
        if (order.getOrderStatus() == 1) {

            if(order.getGoodsIsDay() == 1){                 //是否日反 日反直接反当天 否则直接反全部
                profit = order.getGoodsIncomeDay();
            }else{

                if(currentTime >= order.getExpireTime()){   //到期结算

                    profit = order.getGoodsIncomeDay().multiply(new BigDecimal(order.getGoodsCycle()));

                }
            }

            if(currentTime >= order.getExpireTime()){   //到期结算
                if(order.getGoodsIsCapitalReturn() == 1){//到期反本金
                    BigDecimal benjin = order.getGoodsPrice();
                    custScoreLogService.scoreChange(order.getCustId(), 1, benjin, 13, "基金到期退还本金：" + benjin+","+order.getGoodsName());
                }
            }
            boolean flag = this.update(new LambdaUpdateWrapper<LOrderEntity>()
                    .eq(LOrderEntity::getId, orderId)
                    .eq(LOrderEntity::getOrderStatus, 1)
                    .set(LOrderEntity::getOrderStatus, 2)
                    .set(LOrderEntity::getReceiveProfit,profit.add(order.getReceiveProfit()))
                    .set(LOrderEntity::getReceiveTime,DateUtils.getCurrentTime())
                    .set(LOrderEntity::getDoneTime, DateUtils.getCurrentTime())
            );
            if (!flag) {
                throw new WTDPException(ResultCode.RESULT_100022);
            }
            LRoomEntity lRoom = lRoomService.getById(order.getGoodsRoom());
            //Integer isFund = lRoom.getIsFund();//0 普通 1体验金(福利区) 2稳健区
            Integer isGive = order.getGoodsIsGive();
            Long custId = order.getCustId();
            //返还到客户 全部收益和本金一次性到账
            CustEntity cust = custService.getById(custId);
            if(cust==null){
                return;
            }
//            BigDecimal profit = order.getGoodsPrice()
//                    .multiply(order.getGoodsRate())
//                    .multiply(new BigDecimal(order.getGoodsCycle()))
//                    .divide(new BigDecimal(100)).subtract(order.getReceiveProfit());

//            BigDecimal benjin = order.getGoodsPrice();
            Map<String, Object> custUpdate = new HashMap<>();
            custUpdate.put("custId", order.getCustId());
            // 本人理财收益
            custUpdate.put("fundCommissionMoney", profit);

            custUpdate.put("leftCommissionMoney", profit);
            // 累计收益只需要加收益
            custUpdate.put("totalCommissionMoney", profit);
            custService.updateMoney(custUpdate);
            // 返还本金明细 体验金模式不返还
//            if(isFund.equals(0)){
//                CustScoreLogEntity benjinLog = new CustScoreLogEntity();
//                benjinLog.setCustId(custId);
//                benjinLog.setDirect(1);
//                benjinLog.setType(13);
//                benjinLog.setScore(benjin);
//                //====lxy
//                benjinLog.setBeforeScore(cust.getLeftCommissionMoney());
//                benjinLog.setAfterScore(cust.getLeftCommissionMoney().add(benjin));
//                //====lxy
//                benjinLog.setRemark("基金返还本金：" + benjin);
//                benjinLog.setCreateTime(DateUtils.getCurrentTime());
//                benjinLog.setOrgId(cust.getOrgId());
//                benjinLog.setIsNb(cust.getIsNb());
//                benjinLog.setSalesmanId(cust.getSalesmanId());
//                benjinLog.setSourceId(orderId);
//                custScoreLogService.save(benjinLog);
//            }
            // 收益明细
            CustScoreLogEntity profitLog = new CustScoreLogEntity();
            profitLog.setCustId(custId);
            profitLog.setDirect(1);
            profitLog.setType(12);
            profitLog.setScore(profit);
            //====lxy

            profitLog.setBeforeScore(cust.getLeftCommissionMoney());
            profitLog.setAfterScore(cust.getLeftCommissionMoney().add(profit));
            //====lxy
            profitLog.setRemark("基金收益金额：" + profit);
            profitLog.setCreateTime(DateUtils.getCurrentTime());
            profitLog.setOrgId(cust.getOrgId());
            profitLog.setIsNb(cust.getIsNb());
            profitLog.setSalesmanId(cust.getSalesmanId());
            profitLog.setSourceId(orderId);
            custScoreLogService.save(profitLog);
            if(isGive.equals(1)) { //福利区或者赠送产品 不算返佣
                return;
            }else{
                //级别返佣比例 暂时搁置
//                TGoodsGradeEntity goodsGrade = tGoodsGradeService.getById(cust.getLevel());
//                BigDecimal levelProfit = profit.multiply(goodsGrade.getRate()).divide(new BigDecimal(100));
//                if(levelProfit.compareTo(BigDecimal.ZERO)==1){
//                    custScoreLogService.scoreChange(custId, 1, levelProfit, 12, "基金领取级别额外收益：" + levelProfit,orderId);
//                }

                // 上级返佣
                String[] upCustIdList = cust.getParentIdList().split(",");
                //分销比例
                String config = configService.getValue(0, ConfigConstant.REBATE_CONFIG_KEY);
                JSONObject configJ = JSONUtil.parseObj(config);
                if (configJ.getBool("open")) {
                    //判断当前用户是否是vip0 团队佣金
                    if ((cust.getLevel() == 1 && configJ.getBool("vip0IsReturn")) || cust.getLevel() > 1) {
                        //TODO 保存任务佣金
                        TaskCommissionEntity task = taskCommissionService.getById(order.getId());

                        TaskCommissionEntity taskCommission = new TaskCommissionEntity();
                        taskCommission.setOrderId(order.getId());
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
                                    if(first.compareTo(BigDecimal.ZERO)<=0){
                                        continue;
                                    }
                                    BigDecimal firstComminMoney = profit.multiply(first).divide(new BigDecimal(100));
                                    taskCommission.setFisrtMoney(task==null?firstComminMoney:task.getFisrtMoney().add(firstComminMoney));
                                    //TODO 更新客户的团队佣金
                                    Map<String, Object> teamParams = new HashMap<>();
                                    teamParams.put("custId", upCustId);
                                    teamParams.put("teamMoney", firstComminMoney);
                                    appAccountDao.updateCustTeamMoney(teamParams);
                                    custScoreLogService.scoreChange(Convert.toLong(upCustId), 1, firstComminMoney, 7, "第一代团队佣金金额为：" + firstComminMoney,order.getId());
                                }
                            } else if (i == 1) {
                                if (upLevel >= 0) {
                                    //获取2代佣金
                                    taskCommission.setSecondCustId(Convert.toLong(upCustId));
                                    taskCommission.setSecondSalesmanId(upCustModel.getSalesmanId());
                                    taskCommission.setSecondIsNb(upCustModel.getIsNb());
                                    //提成比例
                                    BigDecimal second = configJ.getBigDecimal("second");
                                    if(second.compareTo(BigDecimal.ZERO)<=0){continue;}
                                    BigDecimal secondComminMoney = profit.multiply(second).divide(new BigDecimal(100));
                                    taskCommission.setSecondMoney(task==null?secondComminMoney:task.getSecondMoney().add(secondComminMoney));
                                    //TODO 更新客户的团队佣金
                                    Map<String, Object> teamParams = new HashMap<>();
                                    teamParams.put("custId", upCustId);
                                    teamParams.put("teamMoney", secondComminMoney);
                                    appAccountDao.updateCustTeamMoney(teamParams);
                                    custScoreLogService.scoreChange(Convert.toLong(upCustId), 1, secondComminMoney, 7, "第二代团队佣金金额为：" + secondComminMoney,order.getId());
                                }
                            } else if (i == 2) {
                                if (upLevel >= 0) {
                                    //获取3代佣金
                                    taskCommission.setThirdCustId(Convert.toLong(upCustId));
                                    taskCommission.setThirdSalesmanId(upCustModel.getSalesmanId());
                                    taskCommission.setThirdIsNb(upCustModel.getIsNb());
                                    //提成比例
                                    BigDecimal third = configJ.getBigDecimal("third");
                                    if(third.compareTo(BigDecimal.ZERO)<=0){continue;}
                                    BigDecimal thirdComminMoney = profit.multiply(third).divide(new BigDecimal(100));
                                    taskCommission.setThirdMoney(task==null?thirdComminMoney:task.getThirdMoney().add(thirdComminMoney));
                                    //TODO 更新客户的团队佣金
                                    Map<String, Object> teamParams = new HashMap<>();
                                    teamParams.put("custId", upCustId);
                                    teamParams.put("teamMoney", thirdComminMoney);
                                    appAccountDao.updateCustTeamMoney(teamParams);
                                    custScoreLogService.scoreChange(Convert.toLong(upCustId), 1, thirdComminMoney, 7, "第三代团队佣金金额为：" + thirdComminMoney,order.getId());
                                }
                            } else if (i == 3) {
                                if (upLevel >= 4) {
                                    //获取4代佣金
                                    taskCommission.setFourCustId(Convert.toLong(upCustId));
                                    taskCommission.setFourSalesmanId(upCustModel.getSalesmanId());
                                    taskCommission.setFourIsNb(upCustModel.getIsNb());
                                    //提成比例
                                    BigDecimal four = configJ.getBigDecimal("four");
                                    if(four.compareTo(BigDecimal.ZERO)<=0){continue;}
                                    BigDecimal fourComminMoney = profit.multiply(four).divide(new BigDecimal(100));
                                    taskCommission.setFourMoney(task==null?fourComminMoney:task.getFourMoney().add(fourComminMoney));
                                    //TODO 更新客户的团队佣金
                                    Map<String, Object> teamParams = new HashMap<>();
                                    teamParams.put("custId", upCustId);
                                    teamParams.put("teamMoney", fourComminMoney);
                                    appAccountDao.updateCustTeamMoney(teamParams);
                                    custScoreLogService.scoreChange(Convert.toLong(upCustId), 1, fourComminMoney, 7, "第四代团队佣金金额为：" + fourComminMoney,order.getId());
                                }
                            } else if (i == 4) {
                                if (upLevel >= 5) {
                                    //获取5代佣金
                                    taskCommission.setFiveCustId(Convert.toLong(upCustId));
                                    taskCommission.setFiveSalesmanId(upCustModel.getSalesmanId());
                                    taskCommission.setFiveIsNb(upCustModel.getIsNb());
                                    //提成比例
                                    BigDecimal five = configJ.getBigDecimal("five");
                                    if(five.compareTo(BigDecimal.ZERO)<=0){continue;}
                                    BigDecimal fiveComminMoney = profit.multiply(five).divide(new BigDecimal(100));
                                    taskCommission.setFiveMoney(task==null?fiveComminMoney:task.getFiveMoney().add(fiveComminMoney));
                                    //TODO 更新客户的团队佣金
                                    Map<String, Object> teamParams = new HashMap<>();
                                    teamParams.put("custId", upCustId);
                                    teamParams.put("teamMoney", fiveComminMoney);
                                    appAccountDao.updateCustTeamMoney(teamParams);
                                    custScoreLogService.scoreChange(Convert.toLong(upCustId), 1, fiveComminMoney, 7, "第五代团队佣金金额为：" + fiveComminMoney,order.getId());
                                }
                            } else if (i == 5) {
                                if (upLevel >= 6) {
                                    //获取6代佣金
                                    taskCommission.setSixCustId(Convert.toLong(upCustId));
                                    taskCommission.setSixSalesmanId(upCustModel.getSalesmanId());
                                    taskCommission.setSixIsNb(upCustModel.getIsNb());
                                    //提成比例
                                    BigDecimal six = configJ.getBigDecimal("six");
                                    if(six.compareTo(BigDecimal.ZERO)<=0){continue;}
                                    BigDecimal sixComminMoney = profit.multiply(six).divide(new BigDecimal(100));
                                    taskCommission.setSixMoney(task==null?sixComminMoney:task.getSixMoney().add(sixComminMoney));
                                    Map<String, Object> teamParams = new HashMap<>();
                                    teamParams.put("custId", upCustId);
                                    teamParams.put("teamMoney", sixComminMoney);
                                    appAccountDao.updateCustTeamMoney(teamParams);
                                    custScoreLogService.scoreChange(Convert.toLong(upCustId), 1, sixComminMoney, 7, "第六代团队佣金金额为：" + sixComminMoney,order.getId());
                                }
                            } else if (i == 6) {
                                if (upLevel >= 7) {
                                    //获取7代佣金
                                    taskCommission.setSevenCustId(Convert.toLong(upCustId));
                                    taskCommission.setSevenSalesmanId(upCustModel.getSalesmanId());
                                    taskCommission.setSevenIsNb(upCustModel.getIsNb());
                                    //提成比例
                                    BigDecimal seven = configJ.getBigDecimal("seven");
                                    if(seven.compareTo(BigDecimal.ZERO)<=0){continue;}
                                    BigDecimal sevenComminMoney = profit.multiply(seven).divide(new BigDecimal(100));
                                    taskCommission.setSevenMoney(task==null?sevenComminMoney:task.getSevenMoney().add(sevenComminMoney));
                                    Map<String, Object> teamParams = new HashMap<>();
                                    teamParams.put("custId", upCustId);
                                    teamParams.put("teamMoney", sevenComminMoney);
                                    appAccountDao.updateCustTeamMoney(teamParams);
                                    custScoreLogService.scoreChange(Convert.toLong(upCustId), 1, sevenComminMoney, 7, "第七代团队佣金金额为：" + sevenComminMoney,order.getId());
                                }
                            }
                        }
                        taskCommissionService.saveOrUpdate(taskCommission);
                    }
                }
            }
        }
    }

    @Override
    public BigDecimal getTotalProfit(Date now, LOrderEntity item) {
        if(item.getOrderStatus() == 4 || item.getOrderStatus() == 5){
            return new BigDecimal(0);
        }else if (item.getExpireTime() > now.getTime() / 1000) {
            BigDecimal dayProfit = item.getGoodsPrice().multiply(item.getGoodsRate()).divide(new BigDecimal(100));
            BigDecimal minuteProfit = dayProfit.divide(new BigDecimal(24 * 60), BigDecimal.ROUND_HALF_DOWN);
            int minute = (DateUtils.getCurrentTime() - item.getCreateTime()) / 60;
            BigDecimal totalProfit = minuteProfit.multiply(new BigDecimal(minute));
            return totalProfit.setScale(4, BigDecimal.ROUND_HALF_DOWN);
        } else {
            return item.getGoodsPrice()
                    .multiply(item.getGoodsRate())
                    .multiply(new BigDecimal(item.getGoodsCycle()))
                    .divide(new BigDecimal(100));
        }
    }

    /**
     * 今日收益
     * @param now
     * @param item
     * @return
     */
    @Override
    public BigDecimal getTodayProfit(Date now, LOrderEntity item) {
        int todayBegin = Convert.toInt(DateUtil.beginOfDay(now).getTime() / 1000);
        int nowTime = Convert.toInt(now.getTime() / 1000);
        if (item.getOrderStatus() == 4 || item.getOrderStatus() == 5) {
            return new BigDecimal(0);
        } else {
            // 每天收益
            BigDecimal dayProfit = item.getGoodsPrice().multiply(item.getGoodsRate()).divide(new BigDecimal(100));
            // 每分钟收益
            BigDecimal minuteProfit = dayProfit.divide(new BigDecimal(24 * 60), BigDecimal.ROUND_HALF_DOWN);
            // 今日已收益分钟数
            int minute = 0;
            // 开始时间，如果今日购买的则从购买时间算起，否则从今日0时起算
            int beginTime = item.getCreateTime() > todayBegin ? item.getCreateTime() : todayBegin;
            if (item.getExpireTime() > nowTime) {
                // 当前时间还处于有效期内
                minute = (DateUtils.getCurrentTime() - beginTime) / 60;
            } else{
                // 当前时间已过期
                if (item.getExpireTime() > todayBegin){
                    // 当天过期的
                    minute = (item.getExpireTime() - beginTime) / 60;
                }else{
                    // 不是当天过期的，说明再前一天已经过期
                    minute = 0;
                }
            }
            BigDecimal todayProfit = minuteProfit.multiply(new BigDecimal(minute));
            return todayProfit.setScale(4, BigDecimal.ROUND_HALF_DOWN);
        }
    }

    @Override
    public void quit(Long orderId, String cancelRemark, int lockDay) {
        LOrderEntity order = getById(orderId);
        if (order.getOrderStatus() != 1) {
            throw new WTDPException("只有进行中的订单才可以退单");
        }
        int now = DateUtils.getCurrentTime();
        int lockTime = now + lockDay * 24 * 3600;
        this.update(new LambdaUpdateWrapper<LOrderEntity>()
                .eq(LOrderEntity::getId, orderId)
                .set(LOrderEntity::getOrderStatus, 4)
                .set(LOrderEntity::getCancelRemark, cancelRemark)
                .set(LOrderEntity::getCancelTime, now)
                .set(LOrderEntity::getLockTime, lockTime)
        );
    }

    @Override
    public void quitDone(Long orderId) {
        LOrderEntity order = this.getOne(new LambdaQueryWrapper<LOrderEntity>()
                .eq(LOrderEntity::getId, orderId)
        );
        if (order.getOrderStatus() == 4) {
            boolean flag = this.update(new LambdaUpdateWrapper<LOrderEntity>()
                    .eq(LOrderEntity::getId, orderId)
                    .eq(LOrderEntity::getOrderStatus, 4)
                    .set(LOrderEntity::getOrderStatus, 5)
                    .set(LOrderEntity::getDoneTime, DateUtils.getCurrentTime())
            );
            if (!flag) {
                throw new WTDPException(ResultCode.RESULT_100022);
            }
            // 退还本金 体验金不退还本金 写入记录
            BigDecimal benjin = order.getGoodsPrice();
            LRoomEntity lRoom = lRoomService.getById(order.getGoodsRoom());
            if(lRoom.getIsFund().equals(0)){
                custScoreLogService.scoreChange(order.getCustId(), 1, benjin, 13, "理财退单退还本金：" + benjin);
            }
            // 重算等级
            custScoreLogService.setCustLevel(custService.getById(order.getCustId()));
        }
    }

    @Transactional
    @Override
    public void quitnew(Long orderId, String cancelRemark, int quitType) {
        //0 退还本金 1 退还本金和领取收益
        LOrderEntity order = getById(orderId);
        if (order.getOrderStatus() != 1) {
            throw new WTDPException("只有进行中的订单才可以退单");
        }
        int now = DateUtils.getCurrentTime();
        boolean flag = this.update(new LambdaUpdateWrapper<LOrderEntity>()
                .eq(LOrderEntity::getId, orderId)
                .set(LOrderEntity::getOrderStatus, 5)
                .set(LOrderEntity::getCancelRemark, cancelRemark)
                .set(LOrderEntity::getCancelTime, now)
                .set(LOrderEntity::getLockTime, now)
        );
        if (!flag) {
            throw new WTDPException(ResultCode.RESULT_100022);
        }
        // 退还本金 体验金不退还本金 写入记录 0 现金 1体验金
        BigDecimal benjin = order.getGoodsPrice();
        BigDecimal shouyi = order.getReceiveProfit();
        LRoomEntity lRoom = lRoomService.getById(order.getGoodsRoom());
//        if(lRoom.getIsFund().equals(0)){//现金
//            custScoreLogService.scoreChange(order.getCustId(), 1, benjin, 13, "基金退单退还本金：" + benjin,orderId);
//            if(quitType ==1&&shouyi.compareTo(BigDecimal.ZERO)==1){
//                custScoreLogService.scoreChange(order.getCustId(), 2, shouyi, 12, "基金退单退还领取收益：" + shouyi,orderId);
//            }
//        }else{
//            //体验金
//            if(quitType ==1&&shouyi.compareTo(BigDecimal.ZERO)==1){
//                custScoreLogService.scoreChange(order.getCustId(), 2, shouyi, 12, "基金退单退还领取收益：" + shouyi,orderId);
//            }
//        }
        if(order.getGoodsIsGive().equals(1)){//赠送 只退收益
            if(quitType ==1&&shouyi.compareTo(BigDecimal.ZERO)==1){
                custScoreLogService.scoreChange(order.getCustId(), 2, shouyi, 12, "基金退单退还领取收益：" + shouyi,orderId);
            }
        }else{
            custScoreLogService.scoreChange(order.getCustId(), 1, benjin, 13, "基金退单退还本金：" + benjin,orderId);
            if(quitType ==1&&shouyi.compareTo(BigDecimal.ZERO)==1){
                custScoreLogService.scoreChange(order.getCustId(), 2, shouyi, 12, "基金退单退还领取收益：" + shouyi,orderId);
            }
        }
    }



    @Transactional
    @Override
    public void orderReceive(Long custId, Long orderId) {
        CustEntity cust = custService.getOne(new LambdaQueryWrapper<CustEntity>()
                .eq(CustEntity::getCustId, custId));
        LOrderEntity order = this.getOne(new LambdaQueryWrapper<LOrderEntity>()
                .eq(LOrderEntity::getId, orderId).eq(LOrderEntity::getOrderStatus,1));
        if (order == null){
            throw new WTDPException("订单不存在", ResultCode.RESULT_100040.getCode());
        }
        LRoomEntity room = lRoomService.getById(order.getGoodsRoom());
        Integer isFund = room.getIsFund();//0 普通 1体验金(福利区) 2稳健区
        Integer isGive = order.getGoodsIsGive();
        Integer isCapitalReturn = order.getGoodsIsCapitalReturn();
        int currentTime = DateUtils.getCurrentTime();
        //是否领取过
//        if(currentTime-order.getReceiveTime() < room.getReceiveCircle()*3600){
//            throw new WTDPException(room.getReceiveCircle()+"", ResultCode.RESULT_100044.getCode());
//        }
        //当前日期小于等于购买日期 或者 当前日期小于等于上次领取日期 不可领取 20240306
        if(DateUtil.formatDate(new Date()).compareTo(DateUtils.timestampToString1(order.getCreateTime())) <= 0 ||
                DateUtil.formatDate(new Date()).compareTo(DateUtils.timestampToString1(order.getReceiveTime())) <= 0)
        {
            throw new WTDPException(room.getReceiveCircle()+"", ResultCode.RESULT_100044.getCode());
        }

        //判断设备即将到期,到期就不能领取 等待系统发放本金和剩下的收益
//        if(currentTime+3600>=order.getExpireTime()){
//            throw new WTDPException("设备即将到期,等待发放奖励", ResultCode.RESULT_100045.getCode());
//        }

//        BigDecimal profit = BigDecimal.valueOf(Double.valueOf((currentTime-order.getReceiveTime()))/(24*3600))
//                .multiply(order.getGoodsPrice().multiply(order.getGoodsRate()).divide(new BigDecimal(100)));
//        BigDecimal maxProfit = order.getGoodsPrice()
//                .multiply(order.getGoodsRate())
//                .multiply(new BigDecimal(order.getGoodsCycle()))
//                .divide(new BigDecimal(100));
        BigDecimal profit = null;
        if(order.getGoodsIsDay() == 1){                 //是否日反 日反直接反当天 否则直接反全部
             profit = order.getGoodsIncomeDay();
        }else{
            if(currentTime >= order.getExpireTime()){   //到期结算
                profit = order.getGoodsIncomeDay().multiply(new BigDecimal(order.getGoodsCycle()));
            }

        }

        BigDecimal maxProfit = order.getGoodsIncomeDay().multiply(new BigDecimal(order.getGoodsCycle()));
        // 查看当前领取收益和总收益对比  大于等于总收益 结束
        Boolean isOver = false;
        if((profit.add(order.getReceiveProfit())).compareTo(maxProfit) > -1){ //大于等于
            //System.out.println("a大于等于b");
            profit = maxProfit.subtract(order.getReceiveProfit());
            isOver = true;
        }
        if(currentTime >= order.getExpireTime()){
            isOver = true;
        }
        // 停止领取收益，让订单不能领取   开启领取收益得时候 修改receiveTime

        //更新订单领取收益
        boolean flag = this.update(new LambdaUpdateWrapper<LOrderEntity>()
                .eq(LOrderEntity::getId, orderId)
                .eq(LOrderEntity::getOrderStatus, 1)
                .set(isOver, LOrderEntity::getOrderStatus, 2)
                .set(LOrderEntity::getReceiveTime, currentTime)
                .set(LOrderEntity::getReceiveProfit, profit.add(order.getReceiveProfit()))
        );
        if (!flag) {
            throw new WTDPException(ResultCode.RESULT_100022);
        }

        custScoreLogService.scoreChange(custId, 1, profit, 12, "基金领取收益：" + profit+","+order.getGoodsName(),orderId);
        BigDecimal benjin = order.getGoodsPrice();
        if(isCapitalReturn.equals(1) && isOver) { //到期是否退还本金
            custScoreLogService.scoreChange(custId, 1, benjin, 13, "基金到期退还本金：" + benjin+","+order.getGoodsName(), orderId);
        }
        if(isGive.equals(1)) { //福利区或者赠送产品 不算返佣
            return;
        }else{
            //级别返佣比例 新版本 搁置
//            TGoodsGradeEntity goodsGrade = tGoodsGradeService.getById(cust.getLevel());
//            BigDecimal levelProfit = profit.multiply(goodsGrade.getRate()).divide(new BigDecimal(100));
//            if(levelProfit.compareTo(BigDecimal.ZERO)==1){
//                custScoreLogService.scoreChange(custId, 1, levelProfit, 12, "基金领取级别额外收益：" + levelProfit,orderId);
//            }
            // 上级返佣
            //上级客户id
            String[] upCustIdList = cust.getParentIdList().split(",");
            //分销比例
            String config = configService.getValue(0, ConfigConstant.REBATE_CONFIG_KEY);
            JSONObject configJ = JSONUtil.parseObj(config);
            if (configJ.getBool("open")) {
                //判断当前用户是否是vip0 团队佣金
                if ((cust.getLevel() == 1 && configJ.getBool("vip0IsReturn")) || cust.getLevel() > 1) {
                    //TODO 保存任务佣金
                    TaskCommissionEntity task = taskCommissionService.getById(order.getId());

                    TaskCommissionEntity taskCommission = new TaskCommissionEntity();
                    taskCommission.setOrderId(order.getId());
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
                                if(first.compareTo(BigDecimal.ZERO)<=0){
                                    continue;
                                }
                                BigDecimal firstComminMoney = profit.multiply(first).divide(new BigDecimal(100));
                                taskCommission.setFisrtMoney(task==null?firstComminMoney:task.getFisrtMoney().add(firstComminMoney));
                                //TODO 更新客户的团队佣金
                                Map<String, Object> teamParams = new HashMap<>();
                                teamParams.put("custId", upCustId);
                                teamParams.put("teamMoney", firstComminMoney);
                                appAccountDao.updateCustTeamMoney(teamParams);
                                custScoreLogService.scoreChange(Convert.toLong(upCustId), 1, firstComminMoney, 7, "第一代团队佣金金额为：" + firstComminMoney,order.getId());
                            }
                        } else if (i == 1) {
                            if (upLevel >= 0) {
                                //获取2代佣金
                                taskCommission.setSecondCustId(Convert.toLong(upCustId));
                                taskCommission.setSecondSalesmanId(upCustModel.getSalesmanId());
                                taskCommission.setSecondIsNb(upCustModel.getIsNb());
                                //提成比例
                                BigDecimal second = configJ.getBigDecimal("second");
                                if(second.compareTo(BigDecimal.ZERO)<=0){continue;}
                                BigDecimal secondComminMoney = profit.multiply(second).divide(new BigDecimal(100));
                                taskCommission.setSecondMoney(task==null?secondComminMoney:task.getSecondMoney().add(secondComminMoney));
                                //TODO 更新客户的团队佣金
                                Map<String, Object> teamParams = new HashMap<>();
                                teamParams.put("custId", upCustId);
                                teamParams.put("teamMoney", secondComminMoney);
                                appAccountDao.updateCustTeamMoney(teamParams);
                                custScoreLogService.scoreChange(Convert.toLong(upCustId), 1, secondComminMoney, 7, "第二代团队佣金金额为：" + secondComminMoney,order.getId());
                            }
                        } else if (i == 2) {
                            if (upLevel >= 0) {
                                //获取3代佣金
                                taskCommission.setThirdCustId(Convert.toLong(upCustId));
                                taskCommission.setThirdSalesmanId(upCustModel.getSalesmanId());
                                taskCommission.setThirdIsNb(upCustModel.getIsNb());
                                //提成比例
                                BigDecimal third = configJ.getBigDecimal("third");
                                if(third.compareTo(BigDecimal.ZERO)<=0){continue;}
                                BigDecimal thirdComminMoney = profit.multiply(third).divide(new BigDecimal(100));
                                taskCommission.setThirdMoney(task==null?thirdComminMoney:task.getThirdMoney().add(thirdComminMoney));
                                //TODO 更新客户的团队佣金
                                Map<String, Object> teamParams = new HashMap<>();
                                teamParams.put("custId", upCustId);
                                teamParams.put("teamMoney", thirdComminMoney);
                                appAccountDao.updateCustTeamMoney(teamParams);
                                custScoreLogService.scoreChange(Convert.toLong(upCustId), 1, thirdComminMoney, 7, "第三代团队佣金金额为：" + thirdComminMoney,order.getId());
                            }
                        } else if (i == 3) {
                            if (upLevel >= 4) {
                                //获取4代佣金
                                taskCommission.setFourCustId(Convert.toLong(upCustId));
                                taskCommission.setFourSalesmanId(upCustModel.getSalesmanId());
                                taskCommission.setFourIsNb(upCustModel.getIsNb());
                                //提成比例
                                BigDecimal four = configJ.getBigDecimal("four");
                                if(four.compareTo(BigDecimal.ZERO)<=0){continue;}
                                BigDecimal fourComminMoney = profit.multiply(four).divide(new BigDecimal(100));
                                taskCommission.setFourMoney(task==null?fourComminMoney:task.getFourMoney().add(fourComminMoney));
                                //TODO 更新客户的团队佣金
                                Map<String, Object> teamParams = new HashMap<>();
                                teamParams.put("custId", upCustId);
                                teamParams.put("teamMoney", fourComminMoney);
                                appAccountDao.updateCustTeamMoney(teamParams);
                                custScoreLogService.scoreChange(Convert.toLong(upCustId), 1, fourComminMoney, 7, "第四代团队佣金金额为：" + fourComminMoney,order.getId());
                            }
                        } else if (i == 4) {
                            if (upLevel >= 5) {
                                //获取5代佣金
                                taskCommission.setFiveCustId(Convert.toLong(upCustId));
                                taskCommission.setFiveSalesmanId(upCustModel.getSalesmanId());
                                taskCommission.setFiveIsNb(upCustModel.getIsNb());
                                //提成比例
                                BigDecimal five = configJ.getBigDecimal("five");
                                if(five.compareTo(BigDecimal.ZERO)<=0){continue;}
                                BigDecimal fiveComminMoney = profit.multiply(five).divide(new BigDecimal(100));
                                taskCommission.setFiveMoney(task==null?fiveComminMoney:task.getFiveMoney().add(fiveComminMoney));
                                //TODO 更新客户的团队佣金
                                Map<String, Object> teamParams = new HashMap<>();
                                teamParams.put("custId", upCustId);
                                teamParams.put("teamMoney", fiveComminMoney);
                                appAccountDao.updateCustTeamMoney(teamParams);
                                custScoreLogService.scoreChange(Convert.toLong(upCustId), 1, fiveComminMoney, 7, "第五代团队佣金金额为：" + fiveComminMoney,order.getId());
                            }
                        } else if (i == 5) {
                            if (upLevel >= 6) {
                                //获取6代佣金
                                taskCommission.setSixCustId(Convert.toLong(upCustId));
                                taskCommission.setSixSalesmanId(upCustModel.getSalesmanId());
                                taskCommission.setSixIsNb(upCustModel.getIsNb());
                                //提成比例
                                BigDecimal six = configJ.getBigDecimal("six");
                                if(six.compareTo(BigDecimal.ZERO)<=0){continue;}
                                BigDecimal sixComminMoney = profit.multiply(six).divide(new BigDecimal(100));
                                taskCommission.setSixMoney(task==null?sixComminMoney:task.getSixMoney().add(sixComminMoney));
                                Map<String, Object> teamParams = new HashMap<>();
                                teamParams.put("custId", upCustId);
                                teamParams.put("teamMoney", sixComminMoney);
                                appAccountDao.updateCustTeamMoney(teamParams);
                                custScoreLogService.scoreChange(Convert.toLong(upCustId), 1, sixComminMoney, 7, "第六代团队佣金金额为：" + sixComminMoney,order.getId());
                            }
                        } else if (i == 6) {
                            if (upLevel >= 7) {
                                //获取7代佣金
                                taskCommission.setSevenCustId(Convert.toLong(upCustId));
                                taskCommission.setSevenSalesmanId(upCustModel.getSalesmanId());
                                taskCommission.setSevenIsNb(upCustModel.getIsNb());
                                //提成比例
                                BigDecimal seven = configJ.getBigDecimal("seven");
                                if(seven.compareTo(BigDecimal.ZERO)<=0){continue;}
                                BigDecimal sevenComminMoney = profit.multiply(seven).divide(new BigDecimal(100));
                                taskCommission.setSevenMoney(task==null?sevenComminMoney:task.getSevenMoney().add(sevenComminMoney));
                                Map<String, Object> teamParams = new HashMap<>();
                                teamParams.put("custId", upCustId);
                                teamParams.put("teamMoney", sevenComminMoney);
                                appAccountDao.updateCustTeamMoney(teamParams);
                                custScoreLogService.scoreChange(Convert.toLong(upCustId), 1, sevenComminMoney, 7, "第七代团队佣金金额为：" + sevenComminMoney,order.getId());
                            }
                        }
                    }
                    taskCommissionService.saveOrUpdate(taskCommission);
                }
            }
        }

    }

}