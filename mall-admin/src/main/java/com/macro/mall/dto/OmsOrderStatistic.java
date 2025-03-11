package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OmsOrderStatistic {
    @ApiModelProperty("待付款订单数量")
    private Integer waitPayCount;
    @ApiModelProperty("待发货订单数量")
    private Integer waitShipCount;
    @ApiModelProperty("已发货订单数量")
    private Integer shippedCount;
    @ApiModelProperty("已完成订单数量")
    private Integer completedCount;
    @ApiModelProperty("已关闭订单数量")
    private Integer closedCount;
    @ApiModelProperty("无效订单数量")
    private Integer invalidCount;
}
