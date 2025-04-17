package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * 会员售后申请参数
 */
@Getter
@Setter
@ToString
public class AfterSaleParam {
    @ApiModelProperty("订单ID")
    private Long orderId;

    @ApiModelProperty("会员ID")
    private Long memberId;

    @ApiModelProperty("售后商品项列表")
    private List<AfterSaleItemParam> items;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "售后状态:0->待处理;1->处理中;2->已完成;3->已拒绝")
    private Integer status = 0;
}