package com.tcc.modules.cust.dao;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.tcc.modules.cust.entity.BankEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 客户银行卡
 * 
 * @author 
 * @email 
 * @date 2022-07-19 13:53:03
 */
@Mapper
public interface BankDao extends BaseMapper<BankEntity> {

    /***
     * 获取银行列表
     * @param page
     * @param params
     * @return
     */
    List getBankList(IPage<Object> page, @Param("condition") Map<String, Object> params);

}
