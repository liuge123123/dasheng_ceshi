package com.tcc.modules.s.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HtmlUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.ResultHelper;
import com.tcc.modules.cust.entity.RechargeEntity;
import com.tcc.modules.s.entity.SSmsEntity;
import com.tcc.modules.s.service.SSmsService;
import lombok.extern.log4j.Log4j2;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;

import com.tcc.modules.s.dao.SSmsOriginalDao;
import com.tcc.modules.s.entity.SSmsOriginalEntity;
import com.tcc.modules.s.service.SSmsOriginalService;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

@Log4j2
@Service("sSmsOriginalService")
public class SSmsOriginalServiceImpl extends ServiceImpl<SSmsOriginalDao, SSmsOriginalEntity> implements SSmsOriginalService {

    @Autowired
    private SSmsService mSmsService;

    @Override
    public void downExcel(HttpServletResponse response, Map<String, Object> params) throws IOException {
        List<Map<String,Object>> list=this.baseMapper.getList(params);
        String sheetName = "短信记录列表";
        String fileName = ResultHelper.newId()+"_sms.xls";
        // 通过工具类创建writer，默认创建xls格式
        ExcelWriter writer = ExcelUtil.getWriter();
        writer.addHeaderAlias("comeTo", "来源");
        writer.addHeaderAlias("allContent", "短信内容");
        writer.addHeaderAlias("receiveTime", "接收时间");
        writer.addHeaderAlias("parseStatus", "解析状态");
        writer.addHeaderAlias("transId", "交易ID");
        writer.addHeaderAlias("money", "交易金额");
        writer.addHeaderAlias("isDone", "上分状态");
        writer.addHeaderAlias("doneTime", "上分时间");
        writer.addHeaderAlias("remark", "备注");
        for (Map<String,Object> item:list) {
            item.remove("id");
            item.remove("mid");
            if(item.get("parseStatus")!=null){
                if(Convert.toInt(item.get("parseStatus"))==0){
                    item.put("parseStatus","未解析");
                }else if(Convert.toInt(item.get("parseStatus"))==1){
                    item.put("parseStatus","解析失败");
                }else{
                    item.put("parseStatus","解析成功");
                }
            }
            if(item.get("receiveTime")!=null){
                Long receiveTime = Convert.toLong(item.get("receiveTime"),0L)/1000;
                item.put("receiveTime", DateUtils.timestampToString(receiveTime.intValue()));
            }
            if(item.get("isDone")!=null){
                if(Convert.toInt(item.get("isDone"))==0){
                    item.put("isDone","未上分");
                }else if(Convert.toInt(item.get("isDone"))==1){
                    item.put("isDone","已上分");
                }else if(Convert.toInt(item.get("isDone"))==2){
                    item.put("isDone","重复信息");
                }else if(Convert.toInt(item.get("isDone"))==3){
                    item.put("isDone","已过期");
                }
            }
            if(item.get("doneTime")!=null){
                Long doneTime = Convert.toLong(item.get("doneTime"),0L)/1000;
                item.put("doneTime", DateUtils.timestampToString(doneTime.intValue()));
            }


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
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<Map<String, Object>> page = new Query<Map<String, Object>>().getPage(params);
        List<Map<String, Object>> list = baseMapper.getList(page, params);
        page.setRecords(list);
        return new PageUtils(page);
    }

    @Override
    public Map getCount(@Param("condition") Map<String, Object> params) {
        Map result = new HashMap();
        BigDecimal successCount = this.baseMapper.getSuccessCount(params);
        result.put("successCount", successCount);
        BigDecimal auditCount = this.baseMapper.getAuditCount(params);
        result.put("auditCount", auditCount);
        return result;
    }

    @Override
    public void parseSms(List<SSmsOriginalEntity> list) {
        for (int i = 0; i < list.size(); i++) {
            SSmsOriginalEntity sSmsOriginalEntity = list.get(i);
            sSmsOriginalEntity.setId(IdUtil.getSnowflake(1, 1).nextId());
            Map map = parseSms(sSmsOriginalEntity.getAllContent());
            log.debug("tcc-sms:" + sSmsOriginalEntity);
            //根据接收短信内容 短信时间和短信ID 判断是否重复数据
            int count = this.count(new LambdaQueryWrapper<SSmsOriginalEntity>()
                    .eq(SSmsOriginalEntity::getAllContent, sSmsOriginalEntity.getAllContent())
                    .eq(SSmsOriginalEntity::getReceiveTime, sSmsOriginalEntity.getReceiveTime()));
//                    .eq(SSmsOriginalEntity::getOriginalId, sSmsOriginalEntity.getOriginalId()));
            if (count != 0) {
                log.debug("tcc-sms:重复短信数据" + sSmsOriginalEntity);
                continue;
            }
            if (map == null) {
                sSmsOriginalEntity.setParseStatus(1);
            } else {
                try {
                    sSmsOriginalEntity.setParseStatus(2);
                    String transId = (String) map.get("transId");
                    String money = (String) map.get("money");
                    String transDate = (String) map.get("transDate");
                    String wallet = (String) map.get("wallet");
                    SSmsEntity sms = new SSmsEntity(transId,transDate, new BigDecimal(money), sSmsOriginalEntity.getComeTo(), sSmsOriginalEntity.getAllContent(), 0, sSmsOriginalEntity.getReceiveTime(), sSmsOriginalEntity.getId(),wallet);
                    mSmsService.save(sms);
                }catch (Exception e){
                    // 解析失败
                    log.error(e.getMessage(), e);
                    sSmsOriginalEntity.setParseStatus(1);
                }
            }
            this.save(sSmsOriginalEntity);
        }
    }
    //[-EVCPlus-] $2 Received from 0770970026 at 23/12/23 13:28:55, Your balance is $22.5
    private String prefix1 = "[-EVCPlus-]";
    // [SAHAL] Tix: 4186982966, Waxaad $50 ka heshay  CAYDARUUS MAXAMUUD MAXAMED MAAMUUD (907696169) Tar: 03/01/24 11:15:13, Haraagaagu waa $50.055
    private String prefix2 = "[SAHAL]";
    //Vous avez recu 1000FCFA venant de 76468606, votre solde actuel est de 49750 FCFA. Numero de transaction PP240302.2231.C02470
    private String prefix3 = "Vous avez recu";

    //You received 100.00 FCFA from 80747229.
    private String prefix4 = "You received";

    private Map parseSms(String sms) {
        sms = sms.replaceAll("\n\r", " ")
                .replaceAll("\n", " ")
                .replaceAll("  ", " ");
        if (sms.startsWith(prefix1)) {
            return getParams(sms.split(" "), 1);
        }else if (sms.startsWith(prefix2)) {
            return getParams(sms.split(" "), 2);
        }else if (sms.startsWith(prefix3)) {
            return getParams(sms.split(" "), 3);
        }else if (sms.startsWith(prefix4)) {
            return getParams(sms.split(" "), 4);
        }
        return null;
    }

    private Map<String, String> getParams(String[] strArr, int i) {
        HashMap<String, String> hsshMap = new HashMap<>();
        switch (i) {
            case 1:
                hsshMap.put("wallet", "EVCPlus");
                hsshMap.put("money", strArr[1].substring(1));
                hsshMap.put("transId", strArr[4]);
                hsshMap.put("transDate", strArr[6]+' '+strArr[7].substring(0,strArr[7].length()-1));
                break;
            case 2:
                hsshMap.put("wallet", "golis");
                hsshMap.put("money", strArr[4].substring(1));
                hsshMap.put("transId", strArr[2].substring(0,strArr[2].length()-1));
                hsshMap.put("transDate", "");
                break;
            case 3:
                hsshMap.put("wallet", "Airtel Money");
                hsshMap.put("money", strArr[3].substring(0,strArr[3].length()-4));
                hsshMap.put("transId", strArr[17]);
                hsshMap.put("transDate", "");
                break;
            case 4:
                hsshMap.put("wallet", "Zamani");
                hsshMap.put("money", strArr[2]);
                hsshMap.put("transId", strArr[5].substring(0,strArr[5].length()-1));
                hsshMap.put("transDate", "");
                break;
        }
        return hsshMap;
    }

}