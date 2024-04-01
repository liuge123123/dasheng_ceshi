package com.tcc.modules.exercise.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcc.common.exception.WTDPException;
import com.tcc.common.utils.DateUtils;
import com.tcc.common.utils.PageUtils;
import com.tcc.common.utils.Query;
import com.tcc.common.utils.ResultCode;
import com.tcc.common.utils.core.text.Convert;
import com.tcc.modules.cust.entity.CustEntity;
import com.tcc.modules.cust.service.CustService;
import com.tcc.modules.exercise.dao.SignProgressDao;
import com.tcc.modules.exercise.entity.SignProductEntity;
import com.tcc.modules.exercise.entity.SignProgressEntity;
import com.tcc.modules.exercise.service.SignProductService;
import com.tcc.modules.exercise.service.SignProgressService;
import com.tcc.modules.l.entity.LOrderEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("signProgressService")
public class SignProgressServiceImpl extends ServiceImpl<SignProgressDao, SignProgressEntity> implements SignProgressService {

    @Autowired
    private SignProductService signProductService;

    @Autowired
    private CustService custService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String key = Convert.toStr(params.get("key"),"");
        Long custId =Convert.toLong(params.get("id"),0L);
        List<Long> team = cn.hutool.core.convert.Convert.toList(Long.class, params.get("team"));
        QueryWrapper<SignProgressEntity> queryWrapper =new QueryWrapper<SignProgressEntity>();
        queryWrapper.eq(custId>0,"cust_id",custId);
        queryWrapper.and(StrUtil.isNotEmpty(key),a->a.like("cust_name",key).or().like("product_name",key));
        queryWrapper.in("salesman_id", team);

        IPage<SignProgressEntity> page = this.page(
                new Query<SignProgressEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<JSONObject> getProductProgress(Long custId) {
        List<SignProductEntity> productList = signProductService.list();
        List<SignProgressEntity> progressList = this.list(new LambdaQueryWrapper<SignProgressEntity>()
                .eq(SignProgressEntity::getCustId, custId));
        Map<Long, SignProgressEntity> progressMap = new HashMap<>();
        progressList.forEach(item -> {
            progressMap.put(item.getProductId(), item);
        });
        List<JSONObject> result = productList.stream().map(product -> {
            JSONObject obj = new JSONObject();
            if(progressMap.containsKey(product.getId())) {
                SignProgressEntity progress = progressMap.get(product.getId());
                obj.putOpt("productId", progress.getProductId());
                obj.putOpt("productName", progress.getProductName());
                obj.putOpt("productImg", progress.getProductImg());
                obj.putOpt("productPrice", progress.getProductPrice());
                obj.putOpt("progress", progress.getSignProgress() * 100 / 1000000);
            }else{
                obj.putOpt("productId", product.getId());
                obj.putOpt("productName", product.getName());
                obj.putOpt("productImg", product.getImg());
                obj.putOpt("productPrice", product.getPrice());
                obj.putOpt("progress", 0);
            }
            return obj;
        }).collect(Collectors.toList());
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void pointExchange(Long custId, Long productId) {
        CustEntity cust = custService.getById(custId);
        SignProductEntity product = signProductService.getById(productId);
        // 1. 扣减用户积分
        if(cust.getPointLeft() < product.getPrice()){
            throw new WTDPException(ResultCode.RESULT_100033);
        }
        Map<String, Object> params = new HashMap<>();
        params.put("custId", custId);
        params.put("pointLeft", product.getPrice() * -1);
        custService.updateMoney(params);

        // 2. 添加兑换进度
        SignProgressEntity progress = this.getOne(new LambdaQueryWrapper<SignProgressEntity>()
                .eq(SignProgressEntity::getCustId, custId)
                .eq(SignProgressEntity::getProductId, productId)
        );
        if(progress == null){
            progress = new SignProgressEntity();
            progress.setProductId(product.getId());
            progress.setProductName(product.getName());
            progress.setProductImg(product.getImg());
            progress.setProductPrice(product.getPrice());
            progress.setCustId(custId);
            progress.setCustName(cust.getCustName());
            progress.setSignNum(1);
            progress.setSignProgress(100000);
            progress.setLastTime(DateUtils.getCurrentTime());
            progress.setSalesmanId(cust.getSalesmanId());
            progress.setIsNb(cust.getIsNb());
            progress.setNextComplete(0);
        }else{
            progress.setSignNum(progress.getSignNum() + 1);
            progress.setLastTime(DateUtils.getCurrentTime());
            if(progress.getSignProgress() < 9900000){
                progress.setSignProgress(progress.getSignProgress() + 100000);
            }else if(progress.getSignProgress() < 9990000){
                progress.setSignProgress(progress.getSignProgress() + 10000);
            }else if(progress.getSignProgress() < 9999000){
                progress.setSignProgress(progress.getSignProgress() + 1000);
            }else {
                progress.setSignProgress(progress.getSignProgress() + 1);
            }
        }
        if(progress.getSignProgress() >= 10000000){
            progress.setSignProgress(9999999);
        }
        if(progress.getNextComplete() == 1){
            progress.setSignProgress(10000000);
        }
        this.saveOrUpdate(progress);
    }

}