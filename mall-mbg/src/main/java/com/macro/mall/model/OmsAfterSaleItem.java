package com.macro.mall.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class OmsAfterSaleItem implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale_item.id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private Long id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale_item.after_sale_id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private Long afterSaleId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale_item.order_item_id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private Long orderItemId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale_item.product_id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private Long productId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale_item.product_name
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private String productName;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale_item.product_sku_id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private Long productSkuId;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale_item.product_sku_code
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private String productSkuCode;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale_item.product_attr
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private String productAttr;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale_item.product_pic
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private String productPic;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale_item.return_quantity
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private Integer returnQuantity;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale_item.return_reason
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private String returnReason;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale_item.proof_pics
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private String proofPics;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale_item.product_price
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private BigDecimal productPrice;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale_item.product_real_price
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private BigDecimal productRealPrice;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale_item.product_quantity
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private Integer productQuantity;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column oms_after_sale_item.create_time
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table oms_after_sale_item
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oms_after_sale_item.id
     *
     * @return the value of oms_after_sale_item.id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public Long getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale_item.id
     *
     * @param id the value for oms_after_sale_item.id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oms_after_sale_item.after_sale_id
     *
     * @return the value of oms_after_sale_item.after_sale_id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public Long getAfterSaleId() {
        return afterSaleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale_item.after_sale_id
     *
     * @param afterSaleId the value for oms_after_sale_item.after_sale_id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setAfterSaleId(Long afterSaleId) {
        this.afterSaleId = afterSaleId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oms_after_sale_item.order_item_id
     *
     * @return the value of oms_after_sale_item.order_item_id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public Long getOrderItemId() {
        return orderItemId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale_item.order_item_id
     *
     * @param orderItemId the value for oms_after_sale_item.order_item_id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oms_after_sale_item.product_id
     *
     * @return the value of oms_after_sale_item.product_id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public Long getProductId() {
        return productId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale_item.product_id
     *
     * @param productId the value for oms_after_sale_item.product_id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setProductId(Long productId) {
        this.productId = productId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oms_after_sale_item.product_name
     *
     * @return the value of oms_after_sale_item.product_name
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public String getProductName() {
        return productName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale_item.product_name
     *
     * @param productName the value for oms_after_sale_item.product_name
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oms_after_sale_item.product_sku_id
     *
     * @return the value of oms_after_sale_item.product_sku_id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public Long getProductSkuId() {
        return productSkuId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale_item.product_sku_id
     *
     * @param productSkuId the value for oms_after_sale_item.product_sku_id
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setProductSkuId(Long productSkuId) {
        this.productSkuId = productSkuId;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oms_after_sale_item.product_sku_code
     *
     * @return the value of oms_after_sale_item.product_sku_code
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public String getProductSkuCode() {
        return productSkuCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale_item.product_sku_code
     *
     * @param productSkuCode the value for oms_after_sale_item.product_sku_code
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setProductSkuCode(String productSkuCode) {
        this.productSkuCode = productSkuCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oms_after_sale_item.product_attr
     *
     * @return the value of oms_after_sale_item.product_attr
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public String getProductAttr() {
        return productAttr;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale_item.product_attr
     *
     * @param productAttr the value for oms_after_sale_item.product_attr
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setProductAttr(String productAttr) {
        this.productAttr = productAttr;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oms_after_sale_item.product_pic
     *
     * @return the value of oms_after_sale_item.product_pic
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public String getProductPic() {
        return productPic;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale_item.product_pic
     *
     * @param productPic the value for oms_after_sale_item.product_pic
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setProductPic(String productPic) {
        this.productPic = productPic;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oms_after_sale_item.return_quantity
     *
     * @return the value of oms_after_sale_item.return_quantity
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public Integer getReturnQuantity() {
        return returnQuantity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale_item.return_quantity
     *
     * @param returnQuantity the value for oms_after_sale_item.return_quantity
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setReturnQuantity(Integer returnQuantity) {
        this.returnQuantity = returnQuantity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oms_after_sale_item.return_reason
     *
     * @return the value of oms_after_sale_item.return_reason
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public String getReturnReason() {
        return returnReason;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale_item.return_reason
     *
     * @param returnReason the value for oms_after_sale_item.return_reason
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setReturnReason(String returnReason) {
        this.returnReason = returnReason;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oms_after_sale_item.proof_pics
     *
     * @return the value of oms_after_sale_item.proof_pics
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public String getProofPics() {
        return proofPics;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale_item.proof_pics
     *
     * @param proofPics the value for oms_after_sale_item.proof_pics
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setProofPics(String proofPics) {
        this.proofPics = proofPics;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oms_after_sale_item.product_price
     *
     * @return the value of oms_after_sale_item.product_price
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public BigDecimal getProductPrice() {
        return productPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale_item.product_price
     *
     * @param productPrice the value for oms_after_sale_item.product_price
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setProductPrice(BigDecimal productPrice) {
        this.productPrice = productPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oms_after_sale_item.product_real_price
     *
     * @return the value of oms_after_sale_item.product_real_price
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public BigDecimal getProductRealPrice() {
        return productRealPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale_item.product_real_price
     *
     * @param productRealPrice the value for oms_after_sale_item.product_real_price
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setProductRealPrice(BigDecimal productRealPrice) {
        this.productRealPrice = productRealPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oms_after_sale_item.product_quantity
     *
     * @return the value of oms_after_sale_item.product_quantity
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public Integer getProductQuantity() {
        return productQuantity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale_item.product_quantity
     *
     * @param productQuantity the value for oms_after_sale_item.product_quantity
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column oms_after_sale_item.create_time
     *
     * @return the value of oms_after_sale_item.create_time
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column oms_after_sale_item.create_time
     *
     * @param createTime the value for oms_after_sale_item.create_time
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale_item
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", afterSaleId=").append(afterSaleId);
        sb.append(", orderItemId=").append(orderItemId);
        sb.append(", productId=").append(productId);
        sb.append(", productName=").append(productName);
        sb.append(", productSkuId=").append(productSkuId);
        sb.append(", productSkuCode=").append(productSkuCode);
        sb.append(", productAttr=").append(productAttr);
        sb.append(", productPic=").append(productPic);
        sb.append(", returnQuantity=").append(returnQuantity);
        sb.append(", returnReason=").append(returnReason);
        sb.append(", proofPics=").append(proofPics);
        sb.append(", productPrice=").append(productPrice);
        sb.append(", productRealPrice=").append(productRealPrice);
        sb.append(", productQuantity=").append(productQuantity);
        sb.append(", createTime=").append(createTime);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}