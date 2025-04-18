package com.macro.mall.portal.domain;

import com.macro.mall.model.OmsAfterSale;
import com.macro.mall.model.OmsAfterSaleItem;
import com.macro.mall.model.OmsAfterSaleProof;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * 门户端售后详情封装
 */
public class PortalOmsAfterSaleDetail extends OmsAfterSale {

    @ApiModelProperty("售后包含的商品列表")
    private List<OmsAfterSaleItem> afterSaleItemList;

    @ApiModelProperty("售后凭证图片列表")
    private List<OmsAfterSaleProof> proofList;

    public List<OmsAfterSaleItem> getAfterSaleItemList() {
        return afterSaleItemList;
    }

    public void setAfterSaleItemList(List<OmsAfterSaleItem> afterSaleItemList) {
        this.afterSaleItemList = afterSaleItemList;
    }

    public List<OmsAfterSaleProof> getProofList() {
        return proofList;
    }

    public void setProofList(List<OmsAfterSaleProof> proofList) {
        this.proofList = proofList;
    }
}