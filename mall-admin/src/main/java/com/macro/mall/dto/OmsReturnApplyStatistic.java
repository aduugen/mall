package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OmsReturnApplyStatistic {
    @ApiModelProperty("待处理退货申请数量")
    private Integer waitHandleCount;
    @ApiModelProperty("退货中数量")
    private Integer returnProcessingCount;
    @ApiModelProperty("退货已接收数量")
    private Integer returnReceivedCount;
    @ApiModelProperty("已拒绝退货数量")
    private Integer rejectedCount;
}
