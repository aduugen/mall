package com.macro.mall.portal.service;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.model.OmsInvoice;
import com.macro.mall.portal.domain.OmsInvoiceParam;

import java.util.List;

/**
 * 发票管理Service
 */
public interface OmsInvoiceService {
    /**
     * 申请发票
     */
    OmsInvoice apply(OmsInvoiceParam invoiceParam);

    /**
     * 获取订单发票申请记录
     */
    OmsInvoice getByOrderId(Long orderId);

    /**
     * 获取会员发票列表
     */
    CommonPage<OmsInvoice> list(Integer status, Integer pageNum, Integer pageSize);

    /**
     * 查看发票详情
     */
    OmsInvoice detail(Long id);
}