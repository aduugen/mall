package com.macro.mall.portal.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 批量商品评价参数
 */
@Getter
@Setter
public class PmsBatchCommentParam {
    @ApiModelProperty("订单ID")
    private Long orderId;

    @ApiModelProperty("评价项列表")
    private List<CommentItem> commentItems;

    @Getter
    @Setter
    public static class CommentItem {
        @ApiModelProperty("商品ID")
        private Long productId;

        @ApiModelProperty("评分(1-5)")
        private Integer rating;

        @ApiModelProperty("评价内容")
        private String comment;

        @ApiModelProperty("评价图片URL列表")
        private List<String> pics;
    }
}