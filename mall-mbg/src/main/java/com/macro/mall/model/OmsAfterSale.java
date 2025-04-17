package com.macro.mall.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OmsAfterSale implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale.id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private Long id;

    @ApiModelProperty(value = "订单ID")
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale.order_id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private Long orderId;

    @ApiModelProperty(value = "订单编号")
    private String orderSn;

    @ApiModelProperty(value = "会员ID")
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale.member_id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private Long memberId;

    @ApiModelProperty(value = "会员用户名")
    private String memberUsername;

    @ApiModelProperty(value = "订单总金额")
    private BigDecimal orderTotalAmount;

    @ApiModelProperty(value = "退款金额")
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale.return_amount
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private BigDecimal returnAmount;

    @ApiModelProperty(value = "申请状态：0->待处理；1->处理中；2->已完成等")
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale.status
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private Integer status;

    @ApiModelProperty(value = "描述")
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale.description
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private String description;

    @ApiModelProperty(value = "创建时间")
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale.create_time
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale.update_time
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private Date updateTime;

    @ApiModelProperty(value = "删除标记：0->未删除；1->已删除")
    private Integer delFlag;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    // 状态常量定义
    public static final int STATUS_PENDING = 0; // 待处理
    public static final int STATUS_PROCESSING = 1; // 处理中
    public static final int STATUS_COMPLETED = 2; // 已完成

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table oms_after_sale
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oms_after_sale.id
     *
     * @return the value of oms_after_sale.id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale.id
     *
     * @param id the value for oms_after_sale.id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oms_after_sale.order_id
     *
     * @return the value of oms_after_sale.order_id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public Long getOrderId() {
        return orderId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale.order_id
     *
     * @param orderId the value for oms_after_sale.order_id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    /**
     * 获取订单编号
     */
    public String getOrderSn() {
        return orderSn;
    }

    /**
     * 设置订单编号
     */
    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oms_after_sale.member_id
     *
     * @return the value of oms_after_sale.member_id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public Long getMemberId() {
        return memberId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale.member_id
     *
     * @param memberId the value for oms_after_sale.member_id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    /**
     * 获取会员用户名
     */
    public String getMemberUsername() {
        return memberUsername;
    }

    /**
     * 设置会员用户名
     */
    public void setMemberUsername(String memberUsername) {
        this.memberUsername = memberUsername;
    }

    /**
     * 获取订单总金额
     */
    public BigDecimal getOrderTotalAmount() {
        return orderTotalAmount;
    }

    /**
     * 设置订单总金额
     */
    public void setOrderTotalAmount(BigDecimal orderTotalAmount) {
        this.orderTotalAmount = orderTotalAmount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column
     * oms_after_sale.return_amount
     *
     * @return the value of oms_after_sale.return_amount
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public BigDecimal getReturnAmount() {
        return returnAmount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column
     * oms_after_sale.return_amount
     *
     * @param returnAmount the value for oms_after_sale.return_amount
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setReturnAmount(BigDecimal returnAmount) {
        this.returnAmount = returnAmount;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oms_after_sale.status
     *
     * @return the value of oms_after_sale.status
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale.status
     *
     * @param status the value for oms_after_sale.status
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column
     * oms_after_sale.description
     *
     * @return the value of oms_after_sale.description
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public String getDescription() {
        return description;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale.description
     *
     * @param description the value for oms_after_sale.description
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column
     * oms_after_sale.create_time
     *
     * @return the value of oms_after_sale.create_time
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale.create_time
     *
     * @param createTime the value for oms_after_sale.create_time
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column
     * oms_after_sale.update_time
     *
     * @return the value of oms_after_sale.update_time
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale.update_time
     *
     * @param updateTime the value for oms_after_sale.update_time
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column
     * oms_after_sale.del_flag
     *
     * @return the value of oms_after_sale.del_flag
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public Integer getDelFlag() {
        return delFlag;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale.del_flag
     *
     * @param delFlag the value for oms_after_sale.del_flag
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setDelFlag(Integer delFlag) {
        this.delFlag = delFlag;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column
     * oms_after_sale.version
     *
     * @return the value of oms_after_sale.version
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public Integer getVersion() {
        return version;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale.version
     *
     * @param version the value for oms_after_sale.version
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    @Override
    public String toString() {
        return "OmsAfterSale{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", memberId=" + memberId +
                ", returnAmount=" + returnAmount +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", delFlag=" + delFlag +
                ", version=" + version +
                '}';
    }
}