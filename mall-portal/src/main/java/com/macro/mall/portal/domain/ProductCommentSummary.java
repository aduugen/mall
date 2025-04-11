package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 商品评价统计信息DTO
 * Created by macro on 2024/x/x. // Replace with actual date
 */
@Getter
@Setter
public class ProductCommentSummary {
    @ApiModelProperty("评价总数")
    private long totalCount;

    @ApiModelProperty("好评率（例如：98.5 表示 98.5%）")
    private Double positiveRate;
}