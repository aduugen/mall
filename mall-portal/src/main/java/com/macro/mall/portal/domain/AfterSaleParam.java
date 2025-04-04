package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

/**
 * 申请售后参数
 */
@Getter
@Setter
public class AfterSaleParam {
    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @ApiModelProperty(value = "售后商品详情列表")
    private List<AfterSaleItemParam> items;

    @ApiModelProperty(value = "会员ID")
    private Long memberId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "售后状态:0->待处理;1->处理中;2->已完成;3->已拒绝")
    private Integer status = 0;
}