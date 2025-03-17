package com.macro.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.macro.mall.dao.PmsSkuStockDao;
import com.macro.mall.dto.PmsSkuStockWithProductNameDTO;
import com.macro.mall.mapper.PmsSkuStockMapper;
import com.macro.mall.model.PmsSkuStock;
import com.macro.mall.model.PmsSkuStockExample;
import com.macro.mall.service.PmsSkuStockService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品SKU库存管理Service实现类
 * Created by macro on 2018/4/27.
 */
@Service
public class PmsSkuStockServiceImpl implements PmsSkuStockService {
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    @Autowired
    private PmsSkuStockDao skuStockDao;

    @Override
    public List<PmsSkuStock> getList(Long pid, String keyword) {
        PmsSkuStockExample example = new PmsSkuStockExample();
        PmsSkuStockExample.Criteria criteria = example.createCriteria().andProductIdEqualTo(pid);
        if (!StrUtil.isEmpty(keyword)) {
            criteria.andSkuCodeLike("%" + keyword + "%");
        }
        return skuStockMapper.selectByExample(example);
    }

    @Override
    public int update(Long pid, List<PmsSkuStock> skuStockList) {
        List<PmsSkuStock> filterSkuList = skuStockList.stream()
                .filter(item -> pid.equals(item.getProductId()))
                .collect(Collectors.toList());
        return skuStockDao.replaceList(filterSkuList);
    }

    @Override
    public List<PmsSkuStockWithProductNameDTO> getStockAlarmList() {
        List<PmsSkuStock> stockAlarmList = skuStockDao.getStockAlarmList();
        List<PmsSkuStockWithProductNameDTO> result = new ArrayList<>();
        for (PmsSkuStock skuStock : stockAlarmList) {
            PmsSkuStockWithProductNameDTO dto = new PmsSkuStockWithProductNameDTO();
            BeanUtils.copyProperties(skuStock, dto);
            result.add(dto);
        }
        return result;
    }

    @Override
    public List<PmsSkuStockWithProductNameDTO> getStockAlarmList(Integer pageSize, Integer pageNum) {
        // 计算起始索引
        Integer startIndex = (pageNum - 1) * pageSize;
        List<PmsSkuStock> stockAlarmList = skuStockDao.getStockAlarmListByPage(pageSize, startIndex);
        List<PmsSkuStockWithProductNameDTO> result = new ArrayList<>();
        for (PmsSkuStock skuStock : stockAlarmList) {
            PmsSkuStockWithProductNameDTO dto = new PmsSkuStockWithProductNameDTO();
            BeanUtils.copyProperties(skuStock, dto);
            result.add(dto);
        }
        return result;
    }

    @Override
    public Long getStockAlarmCount() {
        return skuStockDao.getStockAlarmCount();
    }
}
