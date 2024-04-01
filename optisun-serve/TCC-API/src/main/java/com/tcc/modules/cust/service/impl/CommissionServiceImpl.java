package com.tcc.modules.cust.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.*;
import com.tcc.common.utils.core.text.Convert;
import com.tcc.modules.cust.dao.CommissionDao;
import com.tcc.modules.cust.entity.BankEntity;
import com.tcc.modules.cust.entity.CommissionEntity;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.form.CommissionCheckForm;
import com.tcc.modules.cust.service.*;
import com.tcc.modules.event.EventPublisher;
import com.tcc.modules.exercise.entity.TreasureOrderEntity;
import com.tcc.modules.l.entity.LOrderEntity;
import com.tcc.modules.l.service.LOrderService;
import com.tcc.modules.sys.service.SysConfigService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service("commissionService")
public class CommissionServiceImpl extends ServiceImpl<CommissionDao, CommissionEntity> implements CommissionService {
    @Autowired
    private RechargeService rechargeService;
    @Autowired
    private CustService custService;
    @Autowired
    private CustScoreLogService custScoreLogService;

    @Autowired
    private BankService bankService;

    @Autowired
    private EventPublisher eventPublisher;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private LOrderService lOrderService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        Long custId = cn.hutool.core.convert.Convert.toLong(params.get("custId"));
        IPage<CommissionEntity> page = this.page(
                new Query<CommissionEntity>().getPage(params),
                new LambdaQueryWrapper<CommissionEntity>()
                        .eq(custId != null, CommissionEntity::getCustId, custId)
                        .orderByDesc(CommissionEntity::getId)
        );
        return new PageUtils(page);
    }

    /***
     *  获取提现申请列表
     * @param params
     * @return
     */
    public PageUtils getCommissionList(@Param("condition") Map<String, Object> params) {
        IPage page = new Query<>().getPage(params);
        List<Map<String, Object>> custList = this.baseMapper.getCommissionList(page, params);
        custList.stream().map(item -> {
            //会员余额
            Long custId = (Long) item.get("custId");
            CustEntity cust = custService.getById(custId);
            item.put("leftMoney",cust.getLeftCommissionMoney());
            item.put("haveWithdraw",this.getSumBySuccess(custId));
            item.put("haveRecharge",rechargeService.getSumBySuccess(custId));
            BigDecimal iban = (MathUtils.getBigDecimal(item.get("iban"))).setScale(0,BigDecimal.ROUND_HALF_UP);
            BigDecimal dzMoney = (MathUtils.getBigDecimal(item.get("dzMoney"))).setScale(0,BigDecimal.ROUND_HALF_UP);
            item.put("iban",iban);
            item.put("dzMoney",dzMoney);
            return item;
        }).collect(Collectors.toList());

        page.setRecords(custList);
        return new PageUtils(page);
    }

    /***
     *  获取提现申请列表
     * @return
     */
    public Map getCount(@Param("condition") Map<String, Object> params) {
        Map result = new HashMap();
        BigDecimal successCount = this.baseMapper.getSuccessCount(params);
        result.put("successCount", successCount);
        BigDecimal auditCount = this.baseMapper.getAuditCount(params);
        result.put("auditCount", auditCount);
        return result;
    }

    /***
     * 审核提现
     * @param form
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void check(CommissionCheckForm form, Long checkUserId) {
        CommissionEntity model = this.getById(form.getId());
        if (model == null) {
            throw new WTDPException("该提现记录不存在");
        }
        if (model.getStatus() == 2) {
            throw new WTDPException("驳回的订单无法再次审核");
        }
        model.setStatus(form.getStatus());
//        model.setRemark(form.getStatus() == 1 ? "打款成功" : form.getRemark());
        model.setRemark(form.getRemark());
        model.setCheckTime(DateUtils.getCurrentTime());
        model.setCheckBy(checkUserId);
        this.updateById(model);
        //获取客户之前积分
        CustEntity custEntity = custService.getById(model.getCustId());
        if (custEntity == null) {
            throw new WTDPException("该客户不存在");
        }
        //已支付
        if (form.getStatus() == 1) {

        } else if (form.getStatus() == 2) { //驳回
            //将提现金额退回到佣金余额
//            Map<String, Object> params = new HashMap<>();
//            params.put("custId", model.getCustId());
//            params.put("money", model.getMoney());
//            this.baseMapper.backLeftCommissionUpdateCust(params);
            custScoreLogService.scoreChange(model.getCustId(),1,model.getMoney(),11,"提现驳回，金额为"+model.getMoney());
        }
    }

    /**
     * 提现申请
     *
     * @param custId
     * @param cardId
     * @param money
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void apply(Long custId, Long cardId, BigDecimal money) {
        //新加判断 理财订单大于1次才可以体现

        int orderCount = lOrderService.count(new LambdaQueryWrapper<LOrderEntity>()
                .eq(LOrderEntity::getCustId, custId)
                .eq(LOrderEntity::getGoodsRoom, 2));
        if (orderCount <=0) {
            //throw new WTDPException(ResultCode.RESULT_100035);
        }
        //TODO 规则
        /**
         * 1.必须充值成为VIP1才能提款
         * 2.最低提款2000提款必须是2000的2倍，例如200，4000，6000
         * 3.提款时间48小时内,周六周日不提款
         * 4.提款后台审核，或提供自动出金的脚本（需提供AIS自动出金的脚本）
         * 5.提现扣除5%手续费
         */
        CustEntity cust = custService.getById(custId);
        String configStr = sysConfigService.getValue(cust.getOrgId(), ConfigConstant.CASH_SETTING_CONFIG_KEY);
        JSONObject config = JSONUtil.parseObj(configStr);
        BigDecimal minVal = config.getBigDecimal("minCash");
        BigDecimal rate = config.getBigDecimal("rate"); //优先使用个人设置

        BigDecimal v0MinCash = config.getBigDecimal("v0MinCash", BigDecimal.ZERO);
        int v0Count = config.getInt("v0Count", 0);
        int cashCount = config.getInt("cashCount", 0);
        Boolean ifCash = config.getBool("ifCash");
        Boolean ifRecharge = config.getBool("ifRecharge");
        JSONArray timeRang =  config.getJSONArray("timeRang");
        Long level = cust.getLevel();
//        if(level == 1L){
//            // 如果客户等级为VIP0，则费率固定为10%
//            rate = new BigDecimal(10);
//        }
        //TODO 最低提现金额
        if (minVal.compareTo(money) > 0) {
            throw new WTDPException(ResultCode.RESULT_100008);
        }
        //TODO 小于最低提现金额
        if (money.compareTo(cust.getLeftCommissionMoney()) > 0) {
            throw new WTDPException(ResultCode.RESULT_100007);
        }
        //TODO 判断是否是2000的2倍，VIP0无需判断
//        Integer num = money.intValue() % 2000;
//        if (level > 1 && num > 0) {
//            throw new WTDPException(ResultCode.RESULT_100028);
//        }
        //TODO 星期六星期天不提现
        if (!ifCash) {
            Integer week = DateUtil.dayOfWeek(new Date());
            if (week == 1 || week == 7) {
                throw new WTDPException(ResultCode.RESULT_100029);
            }
        }
        //判断非充值用户不能提款
        if (ifRecharge) {
            int recharge = rechargeService.getCountBySuccess(custId);
            if(recharge <= 0){
                throw new WTDPException(ResultCode.RESULT_100058);
            }
        }
        //判断是否是在 早上九点-下午6点
        if(timeRang!=null&&timeRang.size()==2){
            Boolean isIn =   DateUtil.isIn(DateUtil.date(),timeRang.getDate(0),timeRang.getDate(1));
            if(!isIn){
                throw new WTDPException(ResultCode.RESULT_100041);
            }
        }
        // TODO 任务完成后才可以提款
        String redisValue = redisTemplate.opsForValue().get("task:"+custId);
        if(StrUtil.isNotBlank(redisValue)){
            JSONObject obj = JSONUtil.parseObj(redisValue);
            int cacheNum = obj.getInt("num", 0);
            int cacleTotal = obj.getInt("total", 0);
            if(cacheNum < cacleTotal){
                throw new WTDPException(ResultCode.RESULT_100035);
            }
        }
        //TODO 判断客户等级是否是VIP0，VIP0只能无门槛提现一次
//        if(cust.getLevel()-1<1){
//            throw new WTDPException(ResultCode.RESULT_100027);
//        }
//        if (level == 1) {
//            int count = this.count(new LambdaQueryWrapper<CommissionEntity>()
//                    .eq(CommissionEntity::getCustId, custId)
//                    .ne(CommissionEntity::getStatus, 2)
//            );
//            if (count > v0Count) {
//                throw new WTDPException(ResultCode.RESULT_100034);
//            }
//            if(money.compareTo(v0MinCash) < 0){
//                throw new WTDPException(ResultCode.RESULT_100008);
//            }
//        }

        Date now = new Date();
        log.debug(custId + "提交提现申请时间：" + DateUtil.formatDateTime(now) + "，beginOfDay:" + DateUtil.formatDateTime(DateUtil.beginOfDay(now)) + "，endOfDay:" + DateUtil.formatDateTime(DateUtil.endOfDay(now)));
        long begin = DateUtil.beginOfDay(now).getTime() / 1000;
        long end = DateUtil.endOfDay(now).getTime() / 1000;
        log.debug(custId + "提交提现申请时间：" + now.getTime() / 1000 + "，beginOfDay:" + begin + "，endOfDay:" + end);
        int count = this.count(new LambdaQueryWrapper<CommissionEntity>()
                .eq(CommissionEntity::getCustId, custId)
                .eq(CommissionEntity::getDelFlag, "0")
                .lt(CommissionEntity::getStatus, "2")
                .ge(CommissionEntity::getCreateTime, begin)
                .le(CommissionEntity::getCreateTime, end));
        if (count >= cashCount) {
            throw new WTDPException(ResultCode.RESULT_100017);
        }

        String orderId = IdUtil.getSnowflake(1, 1).nextIdStr();
        BankEntity bank = bankService.getById(cardId);
        CommissionEntity commission = new CommissionEntity();
        commission.setOrderId(orderId);
        commission.setCustId(custId);
        commission.setBankname(bank.getCateName());
        commission.setCode(bank.getCode());
        commission.setName(bank.getName());
        commission.setAccount(bank.getAccount());
        commission.setMoney(money); //提现金额
        commission.setIban(Convert.toStr(money.multiply(rate).divide(new BigDecimal(100)))); //手续费
        commission.setDzMoney(money.multiply(BigDecimal.ONE.subtract(rate.divide(new BigDecimal(100))))); //到账金额
        commission.setStatus(0);
        commission.setCreateTime(DateUtils.getCurrentTime());
        commission.setOrgId(cust.getOrgId());
        commission.setDelFlag("0");
        commission.setIsNb(cust.getIsNb());
        commission.setSalesmanId(cust.getSalesmanId());
        this.save(commission);

        // 扣减佣金余额
//        Map<String, Object> params = new HashMap<>();
//        params.put("custId", custId);
//        params.put("money", money);
//        this.baseMapper.reduceLeftCommissionUpdateCust(params);
          custScoreLogService.scoreChange(custId,2,money,11,"提现扣减余额为"+money);

    }

    /***
     * 导出
     * @return
     */
    @Override
    public void downExcel(HttpServletResponse response, Map<String, Object> params) throws IOException {
        List<Map<String, Object>> list = this.baseMapper.getCommissionList(params);
        String exportType = Convert.toStr(params.get("exportType"));
        for (Map<String, Object> item : list) {
            item.remove("id");
            int status = cn.hutool.core.convert.Convert.toInt(item.get("status"));
            if (status == 0) {
                item.put("status", "待审核");
            } else if (status == 1) {
                item.put("status", "已打款");
            } else if (status == 2) {
                item.put("status", "已驳回");
            }
            BigDecimal iban = (MathUtils.getBigDecimal(item.get("iban"))).setScale(0,BigDecimal.ROUND_HALF_UP);
            BigDecimal dzMoney = (MathUtils.getBigDecimal(item.get("dzMoney"))).setScale(0,BigDecimal.ROUND_HALF_UP);
            item.put("iban",iban);
            item.put("dzMoney",dzMoney);
            item.put("createTime", DateUtils.timestampToString(cn.hutool.core.convert.Convert.toInt(item.get("createTime"), 0)));
            item.put("checkTime", DateUtils.timestampToString(cn.hutool.core.convert.Convert.toInt(item.get("checkTime"), 0)));
        }
        if ("excel".equals(exportType)) {
            exportExcel(response, list);
        } else if ("txt".equals(exportType)) {
            exportTxt(response, list);
        }
    }

    private void exportTxt(HttpServletResponse response, List<Map<String, Object>> list) throws IOException {
        List<String> dataList = list.stream().sorted(((o1, o2) -> {
            String createTime1 = MapUtil.getStr(o1, "createTime");
            String createTime2 = MapUtil.getStr(o2, "createTime");
            return createTime1.compareTo(createTime2);
        })).map(item -> {
            StringBuilder builder = new StringBuilder();
            String account = Convert.toStr(item.get("account"));
            Integer money = Convert.toInt(item.get("dzMoney"), 0);
            return builder.append(account).append("----").append(money).toString();
        }).collect(Collectors.toList());
        String fileName = ResultHelper.newId() + "_commission.txt";
        File file = FileUtil.newFile(fileName);
        FileUtil.writeUtf8Lines(dataList, file);
        //response为HttpServletResponse对象
        response.setContentType("application/txt;charset=utf-8");
        //test.xls是弹出下载对话框的文件名，不能为中文，中文请自行编码
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        ServletOutputStream out = response.getOutputStream();
        FileUtil.writeToStream(file, out);
        IoUtil.close(out);
        file.deleteOnExit();
    }

    private void exportExcel(HttpServletResponse response, List<Map<String, Object>> list) throws IOException {
        String fileName = ResultHelper.newId() + "_commission.xls";
        String sheetName = "提现列表";
        // 通过工具类创建writer，默认创建xls格式
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.addHeaderAlias("custId", "用户编号");
        writer.addHeaderAlias("custName", "用户名称");
        writer.addHeaderAlias("name", "持卡人");
        writer.addHeaderAlias("account", "账号");
        writer.addHeaderAlias("money", "提现金额");
        writer.addHeaderAlias("iban", "手续费");
        writer.addHeaderAlias("dzMoney", "到账金额");
        writer.addHeaderAlias("status", "状态");
        writer.addHeaderAlias("remark", "备注");
        writer.addHeaderAlias("bankname", "银行名称");
        writer.addHeaderAlias("code", "银行代码");
        writer.addHeaderAlias("salesmanId", "业务员id");
        writer.addHeaderAlias("createTime", "创建时间");
        writer.addHeaderAlias("checkTime", "更新时间");

        writer.setColumnWidth(0, 20);
        writer.setColumnWidth(1, 20);
        writer.setColumnWidth(2, 20);
        writer.setColumnWidth(3, 20);
        writer.setColumnWidth(4, 20);
        writer.setColumnWidth(5, 20);
        writer.setColumnWidth(6, 15);
        writer.setColumnWidth(7, 20);
        writer.setColumnWidth(8, 20);
        writer.setColumnWidth(9, 20);
        writer.setColumnWidth(10, 20);
        writer.setColumnWidth(11, 20);
        writer.setColumnWidth(12, 20);
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
    public List<Map<String, Object>> getCustListByAccount(Map<String, Object> params) {
        return baseMapper.getCustListByAccount(params);
    }

    @Override
    public BigDecimal getSum(Long custId) {
        return baseMapper.getSum(custId);
    }
    @Override
    public BigDecimal getSumBySuccess(Long custId) {
        return baseMapper.getSumBySuccess(custId);
    }
}