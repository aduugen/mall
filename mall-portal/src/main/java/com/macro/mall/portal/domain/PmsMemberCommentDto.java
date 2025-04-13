package com.macro.mall.portal.domain;

import com.macro.mall.model.PmsComment;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

/**
 * 用户评价列表 DTO
 * 包含商品图片
 */
@Getter
@Setter
public class PmsMemberCommentDto extends PmsComment {

    @ApiModelProperty("商品图片")
    private String productPic;

    @ApiModelProperty("购买时商品价格")
    private BigDecimal productPrice;

    @ApiModelProperty(value = "评价状态文本 (例如：审核中, 已显示)")
    private String statusText;

    // 保留原始的 showStatus 以便前端可能需要
    // private Integer showStatus; // 继承自 PmsComment, 无需重复定义
}