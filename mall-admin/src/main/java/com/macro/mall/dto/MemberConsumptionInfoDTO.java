package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 会员消费信息统计DTO
 */
@Getter
@Setter
public class MemberConsumptionInfoDTO {
    @ApiModelProperty(value = "会员ID")
    private Long id;

    @ApiModelProperty(value = "会员昵称")
    private String nickname;

    @ApiModelProperty(value = "手机号码")
    private String phone;

    @ApiModelProperty(value = "总消费金额")
    private BigDecimal totalAmount;

    @ApiModelProperty(value = "最后购买时间")
    private Date lastOrderTime;

    @ApiModelProperty(value = "完成订单数")
    private Integer completedOrderCount;

    @ApiModelProperty(value = "退货订单数")
    private Integer returnOrderCount;

    @ApiModelProperty(value = "总积分")
    private Integer integration;
}