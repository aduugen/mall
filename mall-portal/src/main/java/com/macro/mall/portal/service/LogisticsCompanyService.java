package com.macro.mall.portal.service;

import com.macro.mall.model.PtnLogisticsCompany;
import java.util.List;
import java.util.Map;

/**
 * 物流公司服务
 */
public interface LogisticsCompanyService {
    /**
     * 获取所有物流公司
     */
    List<PtnLogisticsCompany> listAll();

    /**
     * 获取物流公司列表（用于前端显示）
     */
    List<Map<String, Object>> getLogisticsCompaniesForDisplay();

    /**
     * 根据ID获取物流公司
     */
    PtnLogisticsCompany getById(Long id);

    /**
     * 根据名称搜索物流公司
     */
    List<PtnLogisticsCompany> searchByName(String keyword);
}