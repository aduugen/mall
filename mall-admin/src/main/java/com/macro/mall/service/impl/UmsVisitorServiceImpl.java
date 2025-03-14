package com.macro.mall.service.impl;

import com.macro.mall.service.UmsVisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.macro.mall.mapper.UmsVisitorMapper;

@Service
public class UmsVisitorServiceImpl implements UmsVisitorService {
    @Autowired
    private UmsVisitorMapper visitorLogMapper;

    @Override
    public Integer getTodayVisitorCount() {
        return visitorLogMapper.getTodayVisitorCount();
    }

    @Override
    public Integer getTotalVisitorCount() {
        return visitorLogMapper.getTotalVisitorCount();
    }

    @Override
    public Integer getCurrentOnlineCount() {
        return visitorLogMapper.getCurrentOnlineCount();
    }
}