package com.macro.mall.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

public class OmsAfterSaleProcess implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "售后单ID")
    private Long afterSaleId;

    @ApiModelProperty(value = "处理人员ID")
    private Long handleManId;

    @ApiModelProperty(value = "处理时间")
    private Date handleTime;

    @ApiModelProperty(value = "处理备注")
    private String handleNote;

    @ApiModelProperty(value = "处理类型：1->审核；2->发货；3->收货；4->质检；5->退款")
    private Integer processType;

    @ApiModelProperty(value = "处理结果：0->不通过；1->通过")
    private Integer processResult;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    // 处理类型常量
    public static final int PROCESS_TYPE_AUDIT = 1; // 审核
    public static final int PROCESS_TYPE_SHIP = 2; // 发货
    public static final int PROCESS_TYPE_RECEIVE = 3; // 收货
    public static final int PROCESS_TYPE_CHECK = 4; // 质检
    public static final int PROCESS_TYPE_REFUND = 5; // 退款

    // 处理结果常量
    public static final int PROCESS_RESULT_FAIL = 0; // 不通过
    public static final int PROCESS_RESULT_PASS = 1; // 通过

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

    public Long getHandleManId() {
        return handleManId;
    }

    public void setHandleManId(Long handleManId) {
        this.handleManId = handleManId;
    }

    public Date getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(Date handleTime) {
        this.handleTime = handleTime;
    }

    public String getHandleNote() {
        return handleNote;
    }

    public void setHandleNote(String handleNote) {
        this.handleNote = handleNote;
    }

    public Integer getProcessType() {
        return processType;
    }

    public void setProcessType(Integer processType) {
        this.processType = processType;
    }

    public Integer getProcessResult() {
        return processResult;
    }

    public void setProcessResult(Integer processResult) {
        this.processResult = processResult;
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
        return "OmsAfterSaleProcess{" +
                "id=" + id +
                ", afterSaleId=" + afterSaleId +
                ", handleManId=" + handleManId +
                ", handleTime=" + handleTime +
                ", handleNote='" + handleNote + '\'' +
                ", processType=" + processType +
                ", processResult=" + processResult +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", version=" + version +
                '}';
    }
}