package com.tcc.modules.cust.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.entity.RechargeEntity;
import com.tcc.modules.cust.form.RechargeCheckForm;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

/**
 * 充值记录表
 *
 * @author 
 * @email 
 * @date 2022-07-19 10:14:26
 */
public interface RechargeService extends IService<RechargeEntity> {

    PageUtils queryPage(Map<String, Object> params);


    /***
     * 获取充值记录
     * @param params
     * @return
     */
    PageUtils getRechargelist(@Param("condition") Map<String, Object> params);
    /***
     *  获取充值统计
     * @return
     */
    Map getCount(@Param("condition") Map<String, Object> params);


    /***
     * 审核
     * @param form
     */
    void check(@RequestBody RechargeCheckForm form);

    /**
     * 反审驳回
     * @param form
     */
    void reject(@RequestBody RechargeCheckForm form);

    void downExcel(HttpServletResponse response, Map<String,Object> params) throws IOException;

    BigDecimal getSumBySuccess(Long custId);

    Integer  getCountBySuccess(Long custId);
}

