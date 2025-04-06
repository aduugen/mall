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
     * 
     * @return 返回操作结果，大于0表示成功
     */
    int apply(OmsInvoiceParam invoiceParam);

    /**
     * 获取订单发票申请记录
     */
    OmsInvoice getByOrderId(Long orderId);

    /**
     * 获取发票列表
     */
    List<OmsInvoice> list(Integer status);

    /**
     * 获取发票分页列表
     */
    CommonPage<OmsInvoice> list(Integer status, Integer pageNum, Integer pageSize);

    /**
     * 查看发票详情
     */
    OmsInvoice detail(Long id);

    /**
     * 根据ID获取发票
     */
    OmsInvoice getItem(Long id);

    /**
     * 更新发票状态
     * 
     * @param id         发票ID
     * @param status     状态：0->待处理；1->已开票；2->已拒绝
     * @param handleNote 处理备注
     * @return 操作结果
     */
    int updateStatus(Long id, Integer status, String handleNote);
}