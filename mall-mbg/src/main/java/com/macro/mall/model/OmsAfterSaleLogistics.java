package com.macro.mall.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

public class OmsAfterSaleLogistics implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "售后单ID")
    private Long afterSaleId;

    @ApiModelProperty(value = "收货网点ID")
    private Long servicePointId;

    @ApiModelProperty(value = "物流公司ID")
    private Long logisticsCompanyId;

    @ApiModelProperty(value = "物流单号")
    private String logisticsNumber;

    @ApiModelProperty(value = "发货时间")
    private Date shippingTime;

    @ApiModelProperty(value = "收货时间")
    private Date receiveTime;

    @ApiModelProperty(value = "收货备注")
    private String receiveNote;

    @ApiModelProperty(value = "退货人姓名")
    private String returnName;

    @ApiModelProperty(value = "退货人电话")
    private String returnPhone;

    @ApiModelProperty(value = "物流状态：0->待发货；1->已发货；2->运输中；3->已签收")
    private Integer logisticsStatus;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    // 物流状态常量
    public static final int LOGISTICS_STATUS_PENDING = 0; // 待发货
    public static final int LOGISTICS_STATUS_SHIPPED = 1; // 已发货
    public static final int LOGISTICS_STATUS_TRANSIT = 2; // 运输中
    public static final int LOGISTICS_STATUS_RECEIVED = 3; // 已签收

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

    public Long getServicePointId() {
        return servicePointId;
    }

    public void setServicePointId(Long servicePointId) {
        this.servicePointId = servicePointId;
    }

    public Long getLogisticsCompanyId() {
        return logisticsCompanyId;
    }

    public void setLogisticsCompanyId(Long logisticsCompanyId) {
        this.logisticsCompanyId = logisticsCompanyId;
    }

    public String getLogisticsNumber() {
        return logisticsNumber;
    }

    public void setLogisticsNumber(String logisticsNumber) {
        this.logisticsNumber = logisticsNumber;
    }

    public Date getShippingTime() {
        return shippingTime;
    }

    public void setShippingTime(Date shippingTime) {
        this.shippingTime = shippingTime;
    }

    public Date getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(Date receiveTime) {
        this.receiveTime = receiveTime;
    }

    public String getReceiveNote() {
        return receiveNote;
    }

    public void setReceiveNote(String receiveNote) {
        this.receiveNote = receiveNote;
    }

    public String getReturnName() {
        return returnName;
    }

    public void setReturnName(String returnName) {
        this.returnName = returnName;
    }

    public String getReturnPhone() {
        return returnPhone;
    }

    public void setReturnPhone(String returnPhone) {
        this.returnPhone = returnPhone;
    }

    public Integer getLogisticsStatus() {
        return logisticsStatus;
    }

    public void setLogisticsStatus(Integer logisticsStatus) {
        this.logisticsStatus = logisticsStatus;
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
        return "OmsAfterSaleLogistics{" +
                "id=" + id +
                ", afterSaleId=" + afterSaleId +
                ", servicePointId=" + servicePointId +
                ", logisticsCompanyId=" + logisticsCompanyId +
                ", logisticsNumber='" + logisticsNumber + '\'' +
                ", shippingTime=" + shippingTime +
                ", receiveTime=" + receiveTime +
                ", receiveNote='" + receiveNote + '\'' +
                ", returnName='" + returnName + '\'' +
                ", returnPhone='" + returnPhone + '\'' +
                ", logisticsStatus=" + logisticsStatus +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", version=" + version +
                '}';
    }
}