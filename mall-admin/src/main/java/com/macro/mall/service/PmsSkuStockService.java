package com.macro.mall.service;

import com.macro.mall.dto.PmsSkuStockWithProductNameDTO;
import com.macro.mall.model.PmsSkuStock;

import java.util.List;

/**
 * 商品SKU库存管理Service
 * Created by macro on 2018/4/27.
 */
public interface PmsSkuStockService {
    /**
     * 根据商品id和skuCode关键字模糊搜索
     */
    List<PmsSkuStock> getList(Long pid, String keyword);

    /**
     * 批量更新商品库存信息
     */
    int update(Long pid, List<PmsSkuStock> skuStockList);

    /**
     * 获取库存告警商品列表
     */
    List<PmsSkuStockWithProductNameDTO> getStockAlarmList();

    /**
     * 分页获取库存告警商品列表
     */
    List<PmsSkuStockWithProductNameDTO> getStockAlarmList(Integer pageSize, Integer pageNum);

    /**
     * 获取库存告警商品总数
     */
    Long getStockAlarmCount();
}
