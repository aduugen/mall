package com.macro.mall.mapper;

import org.apache.ibatis.annotations.Param;
import com.macro.mall.model.UmsVisitorLog;

public interface UmsVisitorMapper {
    // 保存访客日志
    int insert(UmsVisitorLog visitorLog);

    // 根据session_id统计访客总数
    Integer getTotalVisitorCount();

    // 计算当天的访客统计总数
    Integer getTodayVisitorCount();
    
    // 计算当前30分钟访客统计总数
    Integer getCurrentOnlineCount();
}
