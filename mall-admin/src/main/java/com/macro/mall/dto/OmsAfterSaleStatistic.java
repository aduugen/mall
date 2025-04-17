package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 售后统计信息DTO
 */
@Getter
@Setter
public class OmsAfterSaleStatistic {
    @ApiModelProperty("售后单总数")
    private Long totalCount;

    @ApiModelProperty("待处理数量")
    private Long pendingCount;

    @ApiModelProperty("处理中数量")
    private Long processingCount;

    @ApiModelProperty("已完成数量")
    private Long completedCount;

    @ApiModelProperty("已拒绝数量")
    private Long rejectedCount;

    @ApiModelProperty("当日新增数量")
    private Long todayNewCount;

    @ApiModelProperty("当日完成数量")
    private Long todayCompletedCount;

    @ApiModelProperty("本周新增数量")
    private Long weekNewCount;

    @ApiModelProperty("本月新增数量")
    private Long monthNewCount;

    @ApiModelProperty("退款总金额")
    private BigDecimal totalRefundAmount;

    @ApiModelProperty("按状态分组统计")
    private List<Map<String, Object>> statusStatistic;

    @ApiModelProperty("按操作类型分组统计")
    private List<Map<String, Object>> operateTypeStatistic;

    @ApiModelProperty("按处理人分组统计")
    private List<Map<String, Object>> handleManStatistic;

    @ApiModelProperty("处理时效统计(平均处理时间，单位：小时)")
    private Map<String, Double> processingTimeStatistic;

    @ApiModelProperty("已处理率(%)")
    private Double handledRate;

    @ApiModelProperty("完成率(%)")
    private Double completedRate;
}