package com.macro.mall.dto;

import com.macro.mall.model.OmsAfterSale;
import com.macro.mall.model.OmsAfterSaleItem;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.UmsMember;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 售后详情信息 DTO
 * 继承 OmsAfterSale 以包含其所有基础字段
 */
@Getter
@Setter
public class OmsAfterSaleDetail extends OmsAfterSale {

    // OmsAfterSale 中的字段已通过继承获得，包括 id, orderId, orderSn, memberId, memberUsername 等
    // 这里只定义 OmsAfterSale 中没有的额外字段

    @ApiModelProperty("售后包含的商品列表")
    private List<OmsAfterSaleItem> afterSaleItemList;

    @ApiModelProperty("关联的用户信息完整对象(可选，用于展示更多信息)")
    private UmsMember member;

    @ApiModelProperty("关联的订单信息完整对象(可选)")
    private OmsOrder order;

    // --- 为了方便前端直接使用而冗余的字段 ---
    // 这些字段的值通常在 Service 层从关联对象(member, order)中获取并设置

    @ApiModelProperty("用户昵称")
    private String memberNickname;

    @ApiModelProperty("用户注册手机号")
    private String memberPhone;

    @ApiModelProperty("订单总金额")
    private BigDecimal orderTotalAmount;
}