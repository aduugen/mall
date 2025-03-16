package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 首页运营统计曲线数据
 */
@Getter
@Setter
public class HomeStatisticsDataDTO {
    @ApiModelProperty("日期")
    private String date;

    @ApiModelProperty("订单数量")
    private Integer orderCount;

    @ApiModelProperty("订单金额")
    private Double orderAmount;

    @ApiModelProperty("会员数量")
    private Integer memberCount;

    @ApiModelProperty("活跃会员数量")
    private Integer activeMemberCount;

    @ApiModelProperty("访客数量")
    private Integer visitorCount;
}