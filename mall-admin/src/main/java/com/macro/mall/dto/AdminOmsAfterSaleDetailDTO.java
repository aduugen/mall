package com.macro.mall.dto;

import com.macro.mall.model.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 售后单详情DTO（包含相关信息）
 */
@Getter
@Setter
public class AdminOmsAfterSaleDetailDTO {
    @ApiModelProperty("售后单ID")
    private Long id;
    @ApiModelProperty("订单ID")
    private Long orderId;
    @ApiModelProperty("会员ID")
    private Long memberId;
    @ApiModelProperty("申请状态")
    private Integer status;
    @ApiModelProperty("描述")
    private String description;
    @ApiModelProperty("创建时间")
    private Date createTime;
    @ApiModelProperty("更新时间")
    private Date updateTime;
    @ApiModelProperty("删除标记")
    private Boolean delFlag;
    @ApiModelProperty("版本号")
    private Integer version;
    @ApiModelProperty("会员昵称")
    private String memberNickname;
    @ApiModelProperty("会员手机号")
    private String memberPhone;
    @ApiModelProperty("退款金额")
    private BigDecimal returnAmount;
    @ApiModelProperty("订单总金额")
    private BigDecimal orderTotalAmount;

    // 服务点相关字段
    @ApiModelProperty("服务点ID")
    private Long servicePointId;
    @ApiModelProperty("服务点名称")
    private String servicePointName;
    @ApiModelProperty("服务点详情")
    private PtnServicePointDTO servicePointDetail;

    @ApiModelProperty("售后商品列表")
    private List<OmsAfterSaleItem> itemList;
    @ApiModelProperty("售后处理记录列表")
    private List<OmsAfterSaleProcess> processList;
    @ApiModelProperty("物流信息")
    private OmsAfterSaleLogistics logistics;
    @ApiModelProperty("质检信息")
    private OmsAfterSaleCheck check;
    @ApiModelProperty("退款信息")
    private OmsAfterSaleRefund refund;
    @ApiModelProperty("售后凭证列表")
    private List<OmsAfterSaleProof> proofList;
    @ApiModelProperty("操作日志列表")
    private List<OmsAfterSaleLog> logList;
    @ApiModelProperty("可用操作类型列表")
    private List<String> allowableOperations;
}