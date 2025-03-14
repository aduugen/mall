package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OmsReturnApplyStatistic {
    @ApiModelProperty("待处理退货申请数量")
    private Integer wait_handle_count;
    @ApiModelProperty("退货处理中订单数量")
    private Integer return_processing_count;
    @ApiModelProperty("退货已接收数量")
    private Integer return_received_count;
    @ApiModelProperty("已拒绝退货数量")
    private Integer rejected_count;
}
