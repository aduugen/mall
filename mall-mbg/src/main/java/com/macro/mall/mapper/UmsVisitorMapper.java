package com.macro.mall.mapper;

import org.apache.ibatis.annotations.Param;
import com.macro.mall.model.UmsVisitorLog;
import java.util.Date;

public interface UmsVisitorMapper {
    // 保存访客日志
    int insert(UmsVisitorLog visitorLog);

    // 根据session_id统计访客总数
    Integer getTotalVisitorCount();

    // 计算当天的访客统计总数
    Integer getTodayVisitorCount();

    // 计算当前30分钟访客统计总数
    Integer getCurrentOnlineCount();

    // 获取指定日期的访客数量
    Integer getVisitorCountByDate(Date date);
}
