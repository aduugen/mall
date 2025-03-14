package com.macro.mall.service;

import com.macro.mall.model.UmsVisitorLog;
import com.macro.mall.mapper.UmsVisitorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public interface UmsVisitorService {

    public Integer getTodayVisitorCount();

    public Integer getTotalVisitorCount();

    public Integer getCurrentOnlineCount();
}
