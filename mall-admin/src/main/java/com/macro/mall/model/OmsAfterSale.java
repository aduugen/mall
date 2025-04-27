package com.macro.mall.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OmsAfterSale implements Serializable {
    private static final long serialVersionUID = 1L;

    // 状态常量定义
    public static final int STATUS_PENDING = 0; // 待处理
    public static final int STATUS_APPROVED = 1; // 已批准
    public static final int STATUS_REJECTED = 2; // 已拒绝
    public static final int STATUS_SHIPPED = 3; // 处理中
    public static final int STATUS_RECEIVED = 4; // 已取消
    public static final int STATUS_CHECKING = 5; // 已完成
    public static final int STATUS_CHECK_PASS = 6; // 已退款
    public static final int STATUS_CHECK_FAIL = 7; // 退款失败
    public static final int STATUS_REFUNDING = 8; // 退款处理中
    public static final int STATUS_COMPLETED = 9; // 已完成

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @ApiModelProperty(value = "会员ID")
    private Long memberId;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal returnAmount;

    @ApiModelProperty(value = "申请状态：0->待处理；1->处理中；2->已完成等（可根据实际业务定义）")
    private Integer status;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "删除标记：0->未删除；1->已删除")
    private Integer delFlag;

    @ApiModelProperty(value = "版本号")
    private Integer version;
}