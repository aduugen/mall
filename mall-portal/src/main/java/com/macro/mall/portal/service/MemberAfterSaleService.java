package com.macro.mall.portal.service;

import com.macro.mall.model.OmsAfterSale;
import com.macro.mall.model.OmsAfterSaleItem;
import com.macro.mall.model.OmsAfterSaleLogistics;
import com.macro.mall.portal.domain.AfterSaleParam;
import com.macro.mall.portal.domain.PortalOmsAfterSaleDetail;
import com.macro.mall.portal.domain.PortalOmsAfterSaleDTO;
import java.util.List;
import java.util.Map;

/**
 * 会员售后Service
 */
public interface MemberAfterSaleService {
    /**
     * 创建售后申请
     */
    int create(AfterSaleParam afterSaleParam);

    /**
     * 获取售后申请详情
     */
    PortalOmsAfterSaleDetail getDetail(Long id, Long memberId);

    /**
     * 查询会员的售后申请列表
     */
    List<PortalOmsAfterSaleDTO> list(Long memberId, Integer status, Integer pageSize, Integer pageNum);

    /**
     * 取消售后申请
     */
    int cancel(Long id, Long memberId);

    /**
     * 根据订单ID查询售后申请列表
     */
    List<PortalOmsAfterSaleDetail> listByOrderId(Long orderId, Long memberId);

    /**
     * 获取售后申请的商品项列表
     */
    List<OmsAfterSaleItem> getAfterSaleItems(Long afterSaleId);

    /**
     * 检查售后单是否可以寄回商品
     */
    Map<String, Object> checkReturnShipStatus(Long afterSaleId, Long memberId);

    /**
     * 提交寄回物流信息
     */
    int submitReturnShipping(OmsAfterSaleLogistics logistics, Long memberId);

    /**
     * 获取寄回商品所需的信息，包含收件人信息和寄件人信息
     */
    Map<String, Object> getReturnShippingInfo(Long afterSaleId, Long memberId);

    /**
     * 获取常用物流公司列表
     */
    List<Map<String, Object>> getLogisticsCompanies();
}