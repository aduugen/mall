package com.macro.mall.service;

/**
 * 首页统计数据定时任务Service
 */
public interface HomeStatisticsTaskService {
    /**
     * 生成前一天的统计数据
     */
    void generateYesterdayStatistics();

    /**
     * 生成指定日期的统计数据
     */
    void generateStatistics(String date);
}