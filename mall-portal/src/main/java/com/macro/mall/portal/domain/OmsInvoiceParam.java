package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 发票申请请求参数
 */
@Getter
@Setter
public class OmsInvoiceParam {
    @ApiModelProperty(value = "订单ID", required = true)
    @NotNull(message = "订单ID不能为空")
    private Long orderId;

    @ApiModelProperty(value = "订单编号", required = true)
    @NotEmpty(message = "订单编号不能为空")
    private String orderSn;

    @ApiModelProperty(value = "发票类型：1->电子发票；2->纸质发票", required = true)
    @NotNull(message = "发票类型不能为空")
    private Integer invoiceType;

    @ApiModelProperty(value = "抬头类型：1->个人；2->企业", required = true)
    @NotNull(message = "抬头类型不能为空")
    private Integer titleType;

    @ApiModelProperty(value = "发票抬头", required = true)
    @NotEmpty(message = "发票抬头不能为空")
    private String invoiceTitle;

    @ApiModelProperty(value = "纳税人识别号")
    private String taxNumber;

    @ApiModelProperty(value = "发票内容", required = true)
    @NotEmpty(message = "发票内容不能为空")
    private String invoiceContent;

    @ApiModelProperty(value = "发票金额")
    private BigDecimal invoiceAmount;

    @ApiModelProperty(value = "收票人邮箱(电子发票)")
    private String receiverEmail;

    @ApiModelProperty(value = "收件人姓名(纸质发票)")
    private String receiverName;

    @ApiModelProperty(value = "收件人电话(纸质发票)")
    private String receiverPhone;

    @ApiModelProperty(value = "收件地址(纸质发票)")
    private String receiverAddress;
}