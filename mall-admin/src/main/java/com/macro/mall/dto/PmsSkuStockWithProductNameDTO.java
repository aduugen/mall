package com.macro.mall.dto;

import com.macro.mall.model.PmsSkuStock;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * 包含商品名称的SKU库存信息
 */
@Getter
@Setter
public class PmsSkuStockWithProductNameDTO extends PmsSkuStock {
    @ApiModelProperty(value = "商品名称")
    private String productName;
}