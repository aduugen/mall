package com.macro.mall.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

/**
 * 服务点详情DTO
 */
@Getter
@Setter
public class PtnServicePointDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "服务点ID")
    private Long id;

    @ApiModelProperty(value = "服务点名称")
    private String pointName;

    @ApiModelProperty(value = "服务点编码")
    private String pointCode;

    @ApiModelProperty(value = "详细地址")
    private String address;

    @ApiModelProperty(value = "省份")
    private String province;

    @ApiModelProperty(value = "城市")
    private String city;

    @ApiModelProperty(value = "区/县")
    private String district;

    @ApiModelProperty(value = "联系人")
    private String contact;

    @ApiModelProperty(value = "联系电话")
    private String phone;

    @ApiModelProperty(value = "服务点状态：0->正常；1->已关闭")
    private Integer servicePointStatus;

    @ApiModelProperty(value = "服务点类型：0->自提点; 1->收货点; 2->综合点")
    private Integer servicePointType;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}