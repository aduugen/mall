package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OmsAfterSaleQueryParam {
    @ApiModelProperty("服务单号")
    private Long id;

    @ApiModelProperty("订单编号")
    private String orderSn;

    @ApiModelProperty(value = "申请状态：0->待处理；1->处理中；2->已完成；3->已拒绝")
    private Integer status;

    @ApiModelProperty(value = "申请时间") // 格式如 yyyy-MM-dd
    private String createTime;

    @ApiModelProperty(value = "处理人员")
    private String handleMan;

    @ApiModelProperty(value = "会员用户名或ID")
    private String memberKeyword;

    // 可以根据需要添加其他查询字段，如退货原因类型等
}