package com.tcc.modules.cust.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.cust.entity.CommissionEntity;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.form.CommissionCheckForm;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 提现申请
 *
 * @author 
 * @email 
 * @date 2022-07-19 20:16:56
 */
public interface CommissionService extends IService<CommissionEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /***
     *  获取提现申请列表
     * @param params
     * @return
     */
    PageUtils getCommissionList(@Param("condition") Map<String, Object> params);
    /***
     *  获取提现统计
     * @return
     */
    Map getCount(@Param("condition") Map<String, Object> params);
    /**
     * 审核提现
     * @param form
     * @param checkUserId
     */
     void check(CommissionCheckForm form, Long checkUserId);

     void apply(Long custId, Long cardId, BigDecimal money);


     void downExcel(HttpServletResponse response, Map<String,Object> params) throws IOException;

    List<Map<String, Object>> getCustListByAccount(Map<String, Object> params);

    BigDecimal getSum(Long custId);

    BigDecimal getSumBySuccess(Long custId);
}

