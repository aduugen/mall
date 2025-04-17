package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 会员售后申请商品项参数
 */
@Getter
@Setter
public class AfterSaleItemParam {
    @ApiModelProperty("订单商品ID")
    private Long orderItemId;

    @ApiModelProperty("商品ID")
    private Long productId;

    @ApiModelProperty("商品数量")
    private Integer quantity;

    @ApiModelProperty(value = "退货数量")
    private Integer returnQuantity;

    @ApiModelProperty(value = "商品sku编号")
    private Long productSkuId;

    @ApiModelProperty(value = "商品名称")
    private String productName;

    @ApiModelProperty(value = "商品图片")
    private String productPic;

    @ApiModelProperty(value = "商品属性")
    private String productAttr;

    @ApiModelProperty(value = "商品单价")
    private BigDecimal productPrice;

    @ApiModelProperty(value = "商品sku条码")
    private String productSkuCode;

    @ApiModelProperty(value = "退货原因")
    private String returnReason;

    @ApiModelProperty(value = "退货凭证图片,多个用逗号分隔")
    private String proofPics;
}