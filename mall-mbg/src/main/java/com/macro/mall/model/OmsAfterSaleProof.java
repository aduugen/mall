package com.macro.mall.model;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Date;

/**
 * 售后凭证图片表
 */
public class OmsAfterSaleProof implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    private Long id;

    @ApiModelProperty(value = "售后单ID")
    private Long afterSaleId;

    @ApiModelProperty(value = "售后单商品ID")
    private Long itemId;

    @ApiModelProperty(value = "图片URL")
    private String picUrl;

    @ApiModelProperty(value = "图片类型：0->商品图片；1->凭证图片")
    private Integer picType;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;

    // 图片类型常量
    public static final int PIC_TYPE_PRODUCT = 0; // 商品图片
    public static final int PIC_TYPE_PROOF = 1; // 凭证图片

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

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getPicUrl() {
        return picUrl;
    }

    public void setPicUrl(String picUrl) {
        this.picUrl = picUrl;
    }

    public Integer getPicType() {
        return picType;
    }

    public void setPicType(Integer picType) {
        this.picType = picType;
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

    @Override
    public String toString() {
        return "OmsAfterSaleProof{" +
                "id=" + id +
                ", afterSaleId=" + afterSaleId +
                ", itemId=" + itemId +
                ", picUrl='" + picUrl + '\'' +
                ", picType=" + picType +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}