package com.macro.mall.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 售后退款记录表
 */
public class OmsAfterSaleRefund implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "售后单ID")
    private Long afterSaleId;

    @ApiModelProperty(value = "退款单号")
    private String refundNo;

    @ApiModelProperty(value = "退款金额")
    private BigDecimal refundAmount;

    @ApiModelProperty(value = "退款方式：1->原路退回；2->其他方式")
    private Integer refundType;

    @ApiModelProperty(value = "退款状态：0->处理中；1->成功；2->失败")
    private Integer refundStatus;

    @ApiModelProperty(value = "退款时间")
    private Date refundTime;

    @ApiModelProperty(value = "退款备注")
    private String refundNote;

    @ApiModelProperty(value = "支付账户信息")
    private String paymentAccount;

    @ApiModelProperty(value = "退款操作人ID")
    private Long operatorId;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    // 退款方式常量
    public static final int REFUND_TYPE_ORIGINAL = 1; // 原路退回
    public static final int REFUND_TYPE_OTHER = 2; // 其他方式

    // 退款状态常量
    public static final int REFUND_STATUS_PROCESSING = 0; // 处理中
    public static final int REFUND_STATUS_SUCCESS = 1; // 成功
    public static final int REFUND_STATUS_FAIL = 2; // 失败

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAfterSaleId() {
        return afterSaleId;
    }

    public void setAfterSaleId(Long afterSaleId) {
        this.afterSaleId = afterSaleId;
    }

    public String getRefundNo() {
        return refundNo;
    }

    public void setRefundNo(String refundNo) {
        this.refundNo = refundNo;
    }

    public BigDecimal getRefundAmount() {
        return refundAmount;
    }

    public void setRefundAmount(BigDecimal refundAmount) {
        this.refundAmount = refundAmount;
    }

    public Integer getRefundType() {
        return refundType;
    }

    public void setRefundType(Integer refundType) {
        this.refundType = refundType;
    }

    public Integer getRefundStatus() {
        return refundStatus;
    }

    public void setRefundStatus(Integer refundStatus) {
        this.refundStatus = refundStatus;
    }

    public Date getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(Date refundTime) {
        this.refundTime = refundTime;
    }

    public String getRefundNote() {
        return refundNote;
    }

    public void setRefundNote(String refundNote) {
        this.refundNote = refundNote;
    }

    public String getPaymentAccount() {
        return paymentAccount;
    }

    public void setPaymentAccount(String paymentAccount) {
        this.paymentAccount = paymentAccount;
    }

    public Long getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Long operatorId) {
        this.operatorId = operatorId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "OmsAfterSaleRefund{" +
                "id=" + id +
                ", afterSaleId=" + afterSaleId +
                ", refundNo='" + refundNo + '\'' +
                ", refundAmount=" + refundAmount +
                ", refundType=" + refundType +
                ", refundStatus=" + refundStatus +
                ", refundTime=" + refundTime +
                ", refundNote='" + refundNote + '\'' +
                ", paymentAccount='" + paymentAccount + '\'' +
                ", operatorId=" + operatorId +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", version=" + version +
                '}';
    }
}