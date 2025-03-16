package com.macro.mall.service;

import com.macro.mall.model.UmsVisitorLog;
import com.macro.mall.mapper.UmsVisitorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Date;

/**
 * 访客统计Service
 */
public interface UmsVisitorService {
    /**
     * 获取今日访客数量
     */
    Integer getTodayVisitorCount();

    /**
     * 获取当前在线访客数量（30分钟内）
     */
    Integer getCurrentOnlineCount();

    /**
     * 获取总访客数量
     */
    Integer getTotalVisitorCount();

    /**
     * 获取指定日期的访客数量
     */
    Integer getVisitorCountByDate(Date date);
}
