package com.macro.mall.portal.service.impl;

import com.macro.mall.mapper.PtnLogisticsCompanyMapper;
import com.macro.mall.model.PtnLogisticsCompany;
import com.macro.mall.portal.service.LogisticsCompanyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 物流公司服务实现类
 */
@Service
public class LogisticsCompanyServiceImpl implements LogisticsCompanyService {
    @Autowired
    private PtnLogisticsCompanyMapper logisticsCompanyMapper;

    @Override
    public List<PtnLogisticsCompany> listAll() {
        return logisticsCompanyMapper.selectAll();
    }

    @Override
    public List<Map<String, Object>> getLogisticsCompaniesForDisplay() {
        List<Map<String, Object>> resultList = new ArrayList<>();
        List<PtnLogisticsCompany> companyList = logisticsCompanyMapper.selectAll();

        // 如果数据库中没有物流公司数据，提供默认物流公司列表
        if (companyList == null || companyList.isEmpty()) {
            // 添加常用物流公司
            resultList.add(createCompany("1", "顺丰速运", "SF"));
            resultList.add(createCompany("2", "圆通速递", "YTO"));
            resultList.add(createCompany("3", "中通快递", "ZTO"));
            resultList.add(createCompany("4", "申通快递", "STO"));
            resultList.add(createCompany("5", "韵达速递", "YD"));
            resultList.add(createCompany("6", "邮政快递包裹", "YZPY"));
            resultList.add(createCompany("7", "京东物流", "JD"));
            resultList.add(createCompany("8", "德邦快递", "DBL"));
            resultList.add(createCompany("9", "百世快递", "HTKY"));
        } else {
            // 使用数据库中的物流公司数据
            for (PtnLogisticsCompany company : companyList) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", company.getId().toString());
                map.put("name", company.getName());
                map.put("code", company.getCode());
                resultList.add(map);
            }
        }

        return resultList;
    }

    @Override
    public PtnLogisticsCompany getById(Long id) {
        return logisticsCompanyMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<PtnLogisticsCompany> searchByName(String keyword) {
        return logisticsCompanyMapper.selectByName(keyword);
    }

    /**
     * 创建物流公司信息
     */
    private Map<String, Object> createCompany(String id, String name, String code) {
        Map<String, Object> company = new HashMap<>();
        company.put("id", id);
        company.put("name", name);
        company.put("code", code);
        return company;
    }
}