package com.macro.mall.service;

import com.macro.mall.dto.HomeStatisticsDataDTO;

import java.util.Date;
import java.util.List;

/**
 * 首页运营统计数据Service
 */
public interface HomeStatisticsService {
    /**
     * 获取运营统计曲线数据
     */
    List<HomeStatisticsDataDTO> getChartData(Date startDate, Date endDate);
}