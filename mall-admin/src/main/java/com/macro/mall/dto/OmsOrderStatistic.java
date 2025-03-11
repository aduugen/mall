package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OmsOrderStatistic {
    @ApiModelProperty("待付款订单数量")
    private Integer wait_pay_count;
    @ApiModelProperty("待发货订单数量")
    private Integer wait_ship_count;
    @ApiModelProperty("已发货订单数量")
    private Integer shipped_count;
    @ApiModelProperty("已完成订单数量")
    private Integer completed_count;
    @ApiModelProperty("已关闭订单数量")
    private Integer closed_count;
    @ApiModelProperty("无效订单数量")
    private Integer invalid_count;
}
