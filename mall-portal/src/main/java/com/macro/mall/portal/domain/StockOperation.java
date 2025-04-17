package com.macro.mall.portal.domain;

import lombok.Data;

/**
 * 库存操作参数封装，用于批量处理库存
 */
@Data
public class StockOperation {
    /**
     * 商品skuId
     */
    private Long productSkuId;

    /**
     * 商品数量
     */
    private Integer quantity;

    /**
     * 商品名称（用于错误提示）
     */
    private String productName;

    /**
     * 操作是否成功
     */
    private Boolean success;

    /**
     * 错误信息
     */
    private String errorMessage;

    public StockOperation() {
        this.success = true;
    }

    public StockOperation(Long productSkuId, Integer quantity) {
        this.productSkuId = productSkuId;
        this.quantity = quantity;
        this.success = true;
    }

    public StockOperation(Long productSkuId, Integer quantity, String productName) {
        this.productSkuId = productSkuId;
        this.quantity = quantity;
        this.productName = productName;
        this.success = true;
    }
}