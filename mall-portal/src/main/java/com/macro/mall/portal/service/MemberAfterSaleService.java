package com.macro.mall.portal.service;

import com.macro.mall.model.OmsAfterSale;
import com.macro.mall.portal.domain.AfterSaleParam;
import java.util.List;

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
    OmsAfterSale getDetail(Long id, Long memberId);

    /**
     * 查询会员的售后申请列表
     */
    List<OmsAfterSale> list(Long memberId, Integer status, Integer pageSize, Integer pageNum);

    /**
     * 取消售后申请
     */
    int cancel(Long id, Long memberId);
}