package com.macro.mall.portal.domain;

import com.macro.mall.model.OmsAfterSale;
import com.macro.mall.model.OmsAfterSaleItem;
import com.macro.mall.model.OmsAfterSaleProof;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 门户端售后DTO，用于返回给前端的数据
 */
@Getter
@Setter
public class PortalOmsAfterSaleDTO extends OmsAfterSale {

    @ApiModelProperty("售后包含的商品列表")
    private List<OmsAfterSaleItem> afterSaleItemList;

    @ApiModelProperty("售后凭证图片列表")
    private List<OmsAfterSaleProof> proofList;

    @ApiModelProperty("总退款金额")
    private BigDecimal totalRefundAmount;

    @ApiModelProperty("订单编号")
    private String orderSn;

    @ApiModelProperty("主要退货原因(所有商品的第一个)")
    private String mainReturnReason;

    /**
     * 计算总退款金额
     */
    public BigDecimal calculateTotalRefund() {
        if (afterSaleItemList == null || afterSaleItemList.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (OmsAfterSaleItem item : afterSaleItemList) {
            // 使用实际价格(如有)或原价计算
            BigDecimal price = item.getProductRealPrice() != null ? item.getProductRealPrice() : item.getProductPrice();

            if (price != null && item.getReturnQuantity() != null) {
                BigDecimal itemTotal = price.multiply(new BigDecimal(item.getReturnQuantity()));
                total = total.add(itemTotal);
            }
        }

        return total;
    }

    /**
     * 获取所有商品项的退货原因
     */
    public List<String> getAllReturnReasons() {
        List<String> reasons = new ArrayList<>();

        if (afterSaleItemList != null) {
            for (OmsAfterSaleItem item : afterSaleItemList) {
                if (item.getReturnReason() != null && !item.getReturnReason().trim().isEmpty()) {
                    reasons.add(item.getReturnReason());
                }
            }
        }

        return reasons;
    }

    /**
     * 获取主要退货原因(所有商品项中的第一个有效原因)
     */
    public String getMainReturnReason() {
        if (mainReturnReason != null) {
            return mainReturnReason;
        }

        List<String> reasons = getAllReturnReasons();
        return reasons.isEmpty() ? "未提供原因" : reasons.get(0);
    }

    /**
     * 是否有凭证图片
     */
    public boolean hasProofPics() {
        return proofList != null && !proofList.isEmpty();
    }

    /**
     * 初始化计算属性
     */
    public void initializeCalculatedFields() {
        // 计算总退款金额
        this.totalRefundAmount = calculateTotalRefund();

        // 提取主要退货原因
        List<String> reasons = getAllReturnReasons();
        this.mainReturnReason = reasons.isEmpty() ? "未提供原因" : reasons.get(0);
    }
}