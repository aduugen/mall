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

    @ApiModelProperty(value = "退款金额")
    @DecimalMin(value = "0.00", message = "退款金额不能小于0")
    @DecimalMax(value = "999999.99", message = "退款金额超过允许范围")
    private BigDecimal returnAmount;

    @ApiModelProperty(value = "退款方式：1->原路退回；2->其他方式")
    @Range(min = 1, max = 2, message = "退款方式无效")
    private Integer refundType;

    @ApiModelProperty(value = "退款备注")
    @Size(max = 200, message = "退款备注不能超过200个字符")
    private String refundNote;

    @ApiModelProperty(value = "版本号")
    @NotNull(message = "版本号不能为空")
    private Integer version;
}
