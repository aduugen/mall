package com.macro.mall.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OmsAfterSale implements Serializable {
    // 状态常量定义
    public static final int STATUS_PENDING = 0; // 待处理
    public static final int STATUS_APPROVED = 1; // 已同意
    public static final int STATUS_REJECTED = 2; // 已拒绝
    public static final int STATUS_SHIPPED = 3; // 已发货
    public static final int STATUS_RECEIVED = 4; // 已收货
    public static final int STATUS_CHECKING = 5; // 质检中
    public static final int STATUS_CHECK_PASS = 6; // 质检通过
    public static final int STATUS_CHECK_FAIL = 7; // 质检不通过
    public static final int STATUS_REFUNDING = 8; // 退款中
    public static final int STATUS_COMPLETED = 9; // 已完成

    @ApiModelProperty(value = "ID")
    private Long id;

    @ApiModelProperty(value = "订单ID")
    private Long orderId;

    @ApiModelProperty(value = "订单编号")
    private String orderSn;

    @ApiModelProperty(value = "会员ID")
    private Long memberId;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal returnAmount;

    @ApiModelProperty(value = "订单总金额")
    private BigDecimal orderTotalAmount;

    @ApiModelProperty(value = "公司地址ID")
    private Long companyAddressId;

    @ApiModelProperty(value = "发货时间")
    private Date shippingTime;

    @ApiModelProperty(value = "申请状态：0->待处理；1->已同意；2->已拒绝；3->已发货；4->已收货；5->质检中；6->质检通过；7->质检不通过；8->退款中；9->已完成")
    private Integer status;

    @ApiModelProperty(value = "处理时间")
    private Date handleTime;

    @ApiModelProperty(value = "处理人")
    private String handleMan;

    @ApiModelProperty(value = "处理备注")
    private String handleNote;

    @ApiModelProperty(value = "收货人")
    private String receiveMan;

    @ApiModelProperty(value = "收货时间")
    private Date receiveTime;

    @ApiModelProperty(value = "收货备注")
    private String receiveNote;

    @ApiModelProperty(value = "物流公司")
    private String logisticsCompany;

    @ApiModelProperty(value = "物流单号")
    private String logisticsNumber;

    @ApiModelProperty(value = "质检人员")
    private String checkMan;

    @ApiModelProperty(value = "质检时间")
    private Date checkTime;

    @ApiModelProperty(value = "质检备注")
    private String checkNote;

    @ApiModelProperty(value = "质检结果：0-不通过，1-通过")
    private Integer checkResult;

    @ApiModelProperty(value = "退款方式：1-原路退回，2-其他方式")
    private Integer refundType;

    @ApiModelProperty(value = "退款时间")
    private Date refundTime;

    @ApiModelProperty(value = "退款状态：0-处理中，1-成功，2-失败")
    private Integer refundStatus;

    @ApiModelProperty(value = "退款备注")
    private String refundNote;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "版本号，用于乐观锁控制")
    private Integer version;

    @ApiModelProperty(value = "售后单描述")
    private String description;
}