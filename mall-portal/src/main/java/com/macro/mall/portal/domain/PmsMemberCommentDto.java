package com.macro.mall.portal.domain;

import com.macro.mall.model.PmsComment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户评价列表 DTO
 * 包含商品图片
 */
@Getter
@Setter
public class PmsMemberCommentDto extends PmsComment {

    @ApiModelProperty("商品图片")
    private String productPic;

}