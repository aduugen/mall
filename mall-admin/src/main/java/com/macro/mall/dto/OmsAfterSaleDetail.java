package com.macro.mall.dto;

import com.macro.mall.model.OmsAfterSale;
import com.macro.mall.model.OmsAfterSaleItem;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OmsAfterSaleDetail extends OmsAfterSale { // 继承 OmsAfterSale 以包含其所有字段

    @ApiModelProperty("售后包含的商品列表")
    private List<OmsAfterSaleItem> afterSaleItemList;

    // 可以根据需要添加其他关联信息，比如订单信息、处理历史等
    // private OmsOrder orderInfo;
    // private List<OmsAfterSaleHistory> historyList;
}