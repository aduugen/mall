package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OmsAfterSaleStatistic {

    @ApiModelProperty("待处理数量")
    private Long pendingCount = 0L; // 状态 0

    @ApiModelProperty("处理中数量")
    private Long processingCount = 0L; // 状态 1

    @ApiModelProperty("已完成数量")
    private Long finishedCount = 0L; // 状态 2

    @ApiModelProperty("已拒绝数量")
    private Long rejectedCount = 0L; // 状态 3

    @ApiModelProperty("总计数量")
    private Long totalCount = 0L;

    // 可以根据前端图表需要添加其他统计维度
}