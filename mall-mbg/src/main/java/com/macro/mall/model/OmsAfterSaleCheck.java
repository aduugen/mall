package com.macro.mall.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

public class OmsAfterSaleCheck implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "售后单ID")
    private Long afterSaleId;

    @ApiModelProperty(value = "质检人员ID")
    private Long checkManId;

    @ApiModelProperty(value = "质检时间")
    private Date checkTime;

    @ApiModelProperty(value = "质检结果：0->不通过；1->通过")
    private Integer checkResult;

    @ApiModelProperty(value = "质检备注")
    private String checkNote;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    // 质检结果常量
    public static final int CHECK_RESULT_FAIL = 0; // 不通过
    public static final int CHECK_RESULT_PASS = 1; // 通过

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

    public Long getCheckManId() {
        return checkManId;
    }

    public void setCheckManId(Long checkManId) {
        this.checkManId = checkManId;
    }

    public Date getCheckTime() {
        return checkTime;
    }

    public void setCheckTime(Date checkTime) {
        this.checkTime = checkTime;
    }

    public Integer getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(Integer checkResult) {
        this.checkResult = checkResult;
    }

    public String getCheckNote() {
        return checkNote;
    }

    public void setCheckNote(String checkNote) {
        this.checkNote = checkNote;
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
        return "OmsAfterSaleCheck{" +
                "id=" + id +
                ", afterSaleId=" + afterSaleId +
                ", checkManId=" + checkManId +
                ", checkTime=" + checkTime +
                ", checkResult=" + checkResult +
                ", checkNote='" + checkNote + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", version=" + version +
                '}';
    }
}