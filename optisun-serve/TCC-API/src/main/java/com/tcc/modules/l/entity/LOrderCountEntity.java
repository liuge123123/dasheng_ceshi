package com.tcc.modules.l.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Data;

/**
 *
 *
 * @author
 * @email
 * @date 2022-08-30 13:52:53
 */
@Data
@TableName("l_order_count")
public class LOrderCountEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 会员ID
     */
    private Long custId;


    /**
     * 下单时间
     */
    private Integer createTime;
    /**
     * 更新时间
     */
    private Integer updateTime;


    /**
     * 产品购买次数
     */
    private Integer goodsOrderCount;

    /**
     * 产品排序
     */
    private Integer goodsLevel;


}
