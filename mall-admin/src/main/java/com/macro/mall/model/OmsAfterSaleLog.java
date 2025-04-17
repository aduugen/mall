package com.macro.mall.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 售后单操作日志
 */
@Data
public class OmsAfterSaleLog implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "售后单ID")
    private Long afterSaleId;

    @ApiModelProperty(value = "操作人ID")
    private Long operatorId;

    @ApiModelProperty(value = "操作人类型：0->用户；1->管理员")
    private Integer operatorType;

    @ApiModelProperty(value = "操作类型：1->提交申请；2->审核；3->发货等")
    private Integer operateType;

    @ApiModelProperty(value = "操作后售后单状态")
    private Integer status;

    @ApiModelProperty(value = "操作备注")
    private String note;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    // 操作类型常量
    public static final int OPERATE_TYPE_SUBMIT = 1; // 提交申请
    public static final int OPERATE_TYPE_AUDIT = 2; // 审核
    public static final int OPERATE_TYPE_SHIP = 3; // 发货
    public static final int OPERATE_TYPE_RECEIVE = 4; // 收货
    public static final int OPERATE_TYPE_CHECK = 5; // 质检
    public static final int OPERATE_TYPE_REFUND = 6; // 退款
    public static final int OPERATE_TYPE_COMPLETE = 7; // 完成

    // 操作人类型常量
    public static final int OPERATOR_TYPE_MEMBER = 0; // 会员
    public static final int OPERATOR_TYPE_ADMIN = 1; // 管理员
}