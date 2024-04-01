package com.tcc.modules.cust.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tcc.common.utils.PageUtils;
import com.tcc.modules.cust.entity.CustEntity;
import org.apache.ibatis.annotations.Param;

import java.util.Map;

/**
 * 会员表
 *
 * @author 
 * @email 
 * @date 2022-07-15 17:39:38
 */
public interface CustService extends IService<CustEntity> {

    PageUtils queryPage(Map<String, Object> params);

    CustEntity getByUsername(String username);

    Integer getChildCount(Long custId);
    /**
     * 重置登陆密码
     * @param custId
     * @param newPassword
     * @return
     */
     boolean resetPassword(Integer custId,String newPassword);

    /**
     * 重置提现密码
     * @param custId
     * @param newPassword
     * @return
     */
    boolean resetMoneyPassword(Integer custId,String newPassword,String salt);

    void updateMoney(Map<String, Object> params);

    /**
     * vop0到期
     */
    void vip0Expire();

    /***
     * vip1及vip1以上到期自动降为vip0
     */
    void reduceVip0();

    /**
     * 修改上级
     * @param sid
     * @param did
     */
    void changeUp(Long sid, Long did);

    /**
     * 合并员工数据
     * @param susername
     * @param dusername
     */
    void mergeUser(String susername, String dusername);

    /**
     * 跨组合并员工数据
     * @param susername
     * @param dusername
     */
    void userMergeGroup(String susername, String dusername);

    void changeUpGroup(String susername, String dusername);
}

