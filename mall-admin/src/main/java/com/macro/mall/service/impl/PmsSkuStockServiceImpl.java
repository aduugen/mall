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
import java.util.Map;
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
        List<PmsSkuStockWithProductNameDTO> stockAlarmList = skuStockDao.getStockAlarmList();
        // 处理图片URL
        for (PmsSkuStockWithProductNameDTO item : stockAlarmList) {
            processImageUrl(item);
        }
        return stockAlarmList;
    }

    @Override
    public List<PmsSkuStockWithProductNameDTO> getStockAlarmList(Integer pageSize, Integer pageNum) {
        Integer startIndex = (pageNum - 1) * pageSize;
        List<PmsSkuStockWithProductNameDTO> stockAlarmList = skuStockDao.getStockAlarmListByPage(pageSize, startIndex);
        // 处理图片URL
        for (PmsSkuStockWithProductNameDTO item : stockAlarmList) {
            processImageUrl(item);
        }
        return stockAlarmList;
    }

    @Override
    public Long getStockAlarmCount() {
        return skuStockDao.getStockAlarmCount();
    }

    /**
     * 处理图片URL，优先使用SKU图片，如果为空则使用商品主图
     */
    private void processImageUrl(PmsSkuStockWithProductNameDTO item) {
        // 如果SKU图片为空，则使用商品主图
        if (item.getPic() == null || item.getPic().trim().isEmpty()) {
            if (item.getProductPic() != null && !item.getProductPic().trim().isEmpty()) {
                item.setPic(item.getProductPic());
            } else {
                // 如果商品主图也为空，设置一个默认图片
                item.setPic("/images/default-product.png");
            }
        }
    }
}
