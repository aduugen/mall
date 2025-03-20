package com.macro.mall.service;

import com.macro.mall.model.OmsAfterSale;
import java.util.List;

public interface OmsAfterSaleService {
    /**
     * 创建售后申请
     */
    int create(OmsAfterSale afterSale);

    /**
     * 获取售后申请详情
     */
    OmsAfterSale getItem(Long id);

    /**
     * 获取售后申请列表
     */
    List<OmsAfterSale> list(Integer status, Integer pageSize, Integer pageNum);

    /**
     * 修改售后申请状态
     */
    int updateStatus(Long id, Integer status, String handleNote);

    /**
     * 取消售后申请
     */
    int cancel(Long id);
}