package com.macro.mall.dto;

import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberLevel;
import com.macro.mall.model.UmsMemberReceiveAddress;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 会员信息DTO，包含会员基本信息和收货地址
 */
@Getter
@Setter
public class MemberInfoDTO extends UmsMember {
    @ApiModelProperty(value = "会员等级名称")
    private String levelName;

    @ApiModelProperty(value = "收货地址列表")
    private List<UmsMemberReceiveAddress> addressList;
}