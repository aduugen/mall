package com.macro.mall.service.impl;

import com.macro.mall.mapper.OmsAfterSaleMapper;
import com.macro.mall.model.OmsAfterSale;
import com.macro.mall.service.OmsAfterSaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class OmsAfterSaleServiceImpl implements OmsAfterSaleService {
    @Autowired
    private OmsAfterSaleMapper afterSaleMapper;

    @Override
    public int create(OmsAfterSale afterSale) {
        afterSale.setStatus(0);
        afterSale.setCreateTime(new Date());
        afterSale.setUpdateTime(new Date());
        return afterSaleMapper.insert(afterSale);
    }

    @Override
    public OmsAfterSale getItem(Long id) {
        return afterSaleMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<OmsAfterSale> list(Integer status, Integer pageSize, Integer pageNum) {
        // 暂时只支持按状态查询,不支持分页
        return afterSaleMapper.selectByStatus(status);
    }

    @Override
    public int updateStatus(Long id, Integer status, String handleNote) {
        OmsAfterSale afterSale = new OmsAfterSale();
        afterSale.setId(id);
        afterSale.setStatus(status);
        afterSale.setHandleNote(handleNote);
        afterSale.setHandleTime(new Date());
        afterSale.setUpdateTime(new Date());
        return afterSaleMapper.updateByPrimaryKeySelective(afterSale);
    }

    @Override
    public int cancel(Long id) {
        OmsAfterSale afterSale = new OmsAfterSale();
        afterSale.setId(id);
        afterSale.setStatus(3);
        afterSale.setUpdateTime(new Date());
        return afterSaleMapper.updateByPrimaryKeySelective(afterSale);
    }
}