package com.tcc.modules.l.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author
 * @email
 * @date 2022-08-30 13:52:53
 */
@Data
@TableName("l_goods")
public class LGoodsEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId
    private Long id;
    /**
     * 房间ID
     */
    private Long roomId;
    /**
     * 名称
     */
    private String name;
    /**
     * 图片
     */
    private String img;
    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 创建时间
     */
    private Integer createTime;
    /**
     * 创建者
     */
    private Long createBy;
    /**
     * 排序
     */
    private Integer sort;

    /**
     * 所属房间
     */
    @TableField(exist = false)
    private LRoomEntity room;

    /**
     * 持有天数
     */
    private Integer days;

    /**
     * 收益率
     */
    private BigDecimal rate;

    /**
     * 可申购数量
     */
    private Integer maxNum;

    /**
     * 0 隐藏 1显示
     */
    private Integer status;

    /**
     * 背景颜色
     */
    private String backColor;

    /**
     * 日反收益
     */
    private BigDecimal incomeDay;

    /**
     * 是否日反 0 否 1是
     */
    private Integer isDay;
    /**
     * 是否赠送产品 0 否 1是
     */
    private Integer isGive;
    /**
     * 是否到期返本 0 否 1是
     */
    private Integer isCapitalReturn;

    /**
     * 产品级别A-E
     */
    private Integer goodsLevel;

}
