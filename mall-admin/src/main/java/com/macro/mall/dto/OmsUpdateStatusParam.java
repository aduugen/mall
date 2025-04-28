package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * 售后状态更新参数
 */
@Getter
@Setter
public class OmsUpdateStatusParam {
    @ApiModelProperty(value = "售后单ID", required = true)
    @NotNull(message = "售后单ID不能为空")
    private Long id;

    @ApiModelProperty(value = "售后单状态", required = true)
    @NotNull(message = "状态不能为空")
    @Range(min = 0, max = 9, message = "状态值无效")
    private Integer status;

    @ApiModelProperty(value = "处理人员", required = true)
    @NotBlank(message = "处理人员不能为空")
    private String handleMan;

    @ApiModelProperty(value = "处理备注")
    @Size(max = 200, message = "处理备注不能超过200个字符")
    private String handleNote;

    @ApiModelProperty(value = "物流公司")
    @Size(max = 50, message = "物流公司名称不能超过50个字符")
    private String logisticsCompany;

    @ApiModelProperty(value = "物流单号")
    @Size(max = 50, message = "物流单号不能超过50个字符")
    private String logisticsNumber;

    @ApiModelProperty(value = "收货备注")
    @Size(max = 200, message = "收货备注不能超过200个字符")
    private String receiveNote;

    @ApiModelProperty(value = "质检结果：0->不通过；1->通过")
    @Range(min = 0, max = 1, message = "质检结果无效")
    private Integer checkResult;

    @ApiModelProperty(value = "质检备注")
    @Size(max = 200, message = "质检备注不能超过200个字符")
    private String checkNote;

    @ApiModelProperty(value = "服务点ID")
    private Long servicePointId;

    @ApiModelProperty(value = "服务点名称")
    private String servicePointName;

    @ApiModelProperty(value = "退款金额")
    @DecimalMin(value = "0.01", message = "退款金额必须大于0")
    @DecimalMax(value = "9999999.99", message = "退款金额过大")
    private BigDecimal returnAmount;

    @ApiModelProperty(value = "退款方式：0->原路退回；1->支付宝；2->微信；3->银行转账")
    @Range(min = 0, max = 3, message = "退款方式无效")
    private Integer refundType;

    @ApiModelProperty(value = "退款账户")
    @Size(max = 100, message = "退款账户不能超过100个字符")
    private String refundAccount;

    @ApiModelProperty(value = "版本号")
    @NotNull(message = "版本号不能为空")
    private Integer version;
}
