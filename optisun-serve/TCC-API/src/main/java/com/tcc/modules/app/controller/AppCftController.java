package com.tcc.modules.app.controller;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcc.common.annotation.AccessLimit;
import com.tcc.common.annotation.SysLog;
import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.R;
import com.tcc.modules.app.annotation.Login;
import com.tcc.modules.base.AbstractAppController;
import com.tcc.modules.cust.service.CustScoreLogService;
import com.tcc.modules.l.entity.LGoodsEntity;
import com.tcc.modules.l.entity.LOrderEntity;
import com.tcc.modules.l.entity.LRoomEntity;
import com.tcc.modules.l.service.LGoodsService;
import com.tcc.modules.l.service.LOrderService;
import com.tcc.modules.l.service.LRoomService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * APP订单相关接口
 */
@Slf4j
@RestController
@RequestMapping("/app/cft")
@Api("APP订单相关接口")
public class AppCftController extends AbstractAppController {

    @Autowired
    private LOrderService lOrderService;

    @Autowired
    private LGoodsService lGoodsService;

    @Autowired
    private LRoomService lRoomService;

    @Autowired
    private CustScoreLogService custScoreLogService;

    @Login
    @GetMapping("/total")
    public R total(){
        BigDecimal tr = new BigDecimal(0);
        BigDecimal sy = new BigDecimal(0);
        BigDecimal jr = new BigDecimal(0);
        List<LOrderEntity> list = lOrderService.list(new LambdaQueryWrapper<LOrderEntity>()
                .eq(LOrderEntity::getCustId, getUserId())
                .orderByDesc(LOrderEntity::getCreateTime)
        );
        Date now = new Date();
        for (int i = 0; i < list.size(); i++) {
            LOrderEntity order = list.get(i);
//            sy = sy.add(lOrderService.getTotalProfit(now, order));
            sy = sy.add(order.getReceiveProfit());
            tr = tr.add(order.getGoodsPrice());
//            jr = jr.add(lOrderService.getTodayProfit(now, order));
        }
        jr = jr.add(custScoreLogService.custRewardToday(getUserId(),12));
        Map<String, Object> result = new HashMap<>();
        result.put("tr", tr);
        result.put("sy", sy);
        result.put("jr", jr);
        return R.ok().putData(result);
    }

    @Login
    @GetMapping("/room/list")
    public R roomList(){
        List<LRoomEntity> list = lRoomService.list(new LambdaQueryWrapper<LRoomEntity>()
                .orderByAsc(LRoomEntity::getSort)
        );
        return R.ok().putData(list);
    }

    @Login
    @GetMapping("/goods/list")
    public R goodsList(Long roomId){
        List<LGoodsEntity> list = lGoodsService.list(new LambdaQueryWrapper<LGoodsEntity>()
                .eq(LGoodsEntity::getRoomId, roomId)
                .eq(LGoodsEntity::getStatus,1)
                .orderByDesc(LGoodsEntity::getSort)
        );
        List<LOrderEntity> listOrder = lOrderService.list(new LambdaQueryWrapper<LOrderEntity>()
                        .eq(LOrderEntity::getCustId, getUserId())
                        .eq(LOrderEntity::getOrderStatus, 1)
                        .orderByDesc(LOrderEntity::getCreateTime)
        );
        Date now = new Date();
        Integer receiveNum = new Integer(0);

        for (int i = 0; i < listOrder.size(); i++) {
            LOrderEntity order = listOrder.get(i);
            if(order.getGoodsIsDay().equals(1)){         //产品订单是否日反
                //当前日期大于购买日期 且 当前日期大于上次领取日期
                if(DateUtil.formatDate(new Date()).compareTo(DateUtils.timestampToString1(order.getCreateTime())) > 0 &&
                        DateUtil.formatDate(new Date()).compareTo(DateUtils.timestampToString1(order.getReceiveTime())) > 0)
                {
                    receiveNum++;
                }
            }else{
                //非日反订单 计算累计收益 过0点即算一天
                int usdDay = ((DateUtils.getCurrentTime()-order.getCreateTime())>0?(DateUtils.getCurrentTime()-DateUtils.StringToTimestamp(DateUtils.timestampToString1(order.getCreateTime())+" 00:00:00")):0) / 3600/24;
                //logger.info("---------------->"+usdDay+"-------------->"+DateUtils.StringToTimestamp(DateUtils.timestampToString1(item.getCreateTime())+" 00:00:00"));
                if(order.getExpireTime() < now.getTime() / 1000){//非日反订单 到期全部返还
                    receiveNum++;
                }
            }
        }

        return R.ok().putData(list,receiveNum);
    }


    @AccessLimit
    @SysLog("购买理财产品")
    @Login
    @PostMapping("/goods/buy")
    public R goodsList(@RequestBody Map<String, Object> params){
        Long goodsId = Convert.toLong(params.get("goodsId"));
        lOrderService.buy(getUserId(), goodsId);
        return R.ok();
    }

    @AccessLimit
    @SysLog("理财领取收益")
    @Login
    @PostMapping("/order/receive")
    public R orderReceive(@RequestBody Map<String, Object> params){
        Long orderId = Convert.toLong(params.get("orderId"));
        lOrderService.orderReceive(getUserId(), orderId);
        return R.ok();
    }

    @Login
    @GetMapping("/order/list")
    public R orderList(Integer status){
//        List<Integer> statusList = Convert.toList(Integer.class, "1,6");
        List<LOrderEntity> list = lOrderService.list(new LambdaQueryWrapper<LOrderEntity>()
                .eq(LOrderEntity::getCustId, getUserId())
//                .in(LOrderEntity::getOrderStatus,statusList)
                .eq(status != null, LOrderEntity::getOrderStatus, status)
                .orderByDesc(LOrderEntity::getCreateTime)
        );
        Date now = new Date();
        List<Map<String, Object>> resultList = list.stream().map(item -> {
            JSONObject json = JSONUtil.parseObj(item);
//            json.putOpt("createTime", DateUtil.date(item.getCreateTime() * 1000L).toString());
//            json.putOpt("expireTime", DateUtil.date(item.getExpireTime() * 1000L).toString());
//            if(item.getLockTime() != null) {
//                json.putOpt("lockTime", DateUtil.date(item.getLockTime() * 1000L).toString());
//            }
            //计算还剩余多少天
            json.putOpt("lastDays", ((item.getExpireTime()-DateUtils.getCurrentTime())>0?(item.getExpireTime()-DateUtils.getCurrentTime())+3600*24:1) / 3600/24);//20240318修改剩余时间
            //json.putOpt("lastDays", ((item.getExpireTime()-DateUtils.getCurrentTime())>0?(item.getExpireTime()-DateUtils.getCurrentTime()):0) / 3600/24);
            //计算已购买多少个小时
            json.putOpt("hours", (DateUtils.getCurrentTime() - item.getCreateTime()) / 3600);
            json.putOpt("totalProfit", lOrderService.getTotalProfit(now, item));
            if(item.getOrderStatus() == 1 && item.getExpireTime() < now.getTime() / 1000){
                json.putOpt("orderStatus", 3);
            }
            LRoomEntity lRoom = lRoomService.getById(item.getGoodsRoom());
//            if (((DateUtils.getCurrentTime()-item.getReceiveTime())>lRoom.getReceiveCircle()*3600)&&item.getOrderStatus()==1){
//                json.putOpt("iscanReceive", true);
//            }
            if(item.getOrderStatus()==1){                   //进行中的订单
                if(item.getGoodsIsDay().equals(1)){         //产品订单是否日反
                    //当前日期大于购买日期 且 当前日期大于上次领取日期 20240306搁置
                    if(DateUtil.formatDate(new Date()).compareTo(DateUtils.timestampToString1(item.getCreateTime())) > 0 &&
                            DateUtil.formatDate(new Date()).compareTo(DateUtils.timestampToString1(item.getReceiveTime())) > 0)
                    {
                        json.putOpt("iscanReceive", true);
                    }

                    /*if(Objects.equals(item.getCreateTime(), item.getReceiveTime()) && DateUtil.formatDate(new Date()).compareTo(DateUtils.timestampToString1(item.getCreateTime())) == 0){
                        //创建时间等于领取时间 当前日期等于创建日期 当天购买当天领取
                        json.putOpt("iscanReceive", true);

                    }else if(DateUtil.formatDate(new Date()).compareTo(DateUtils.timestampToString1(item.getCreateTime())) > 0 &&
                            DateUtil.formatDate(new Date()).compareTo(DateUtils.timestampToString1(item.getReceiveTime())) > 0){
                        //当前日期大于购买日期 且 当前日期大于上次领取日期 正常隔日领取
                        json.putOpt("iscanReceive", true);
                    }*/

                }else{
                    //非日反订单 计算累计收益 过0点即算一天
                    int usdDay = ((DateUtils.getCurrentTime()-item.getCreateTime())>0?(DateUtils.getCurrentTime()-DateUtils.StringToTimestamp(DateUtils.timestampToString1(item.getCreateTime())+" 00:00:00")):0) / 3600/24;
                    //logger.info("---------------->"+usdDay+"-------------->"+DateUtils.StringToTimestamp(DateUtils.timestampToString1(item.getCreateTime())+" 00:00:00"));
                    json.putOpt("receiveProfit",usdDay * item.getGoodsIncomeDay().intValue());

                    if(item.getExpireTime() < now.getTime() / 1000){//非日反订单 到期全部返还
                        json.putOpt("iscanReceive", true);
                    }
                }
            }

            return json;
        }).collect(Collectors.toList());
        return R.ok().putData(resultList);
    }
}
