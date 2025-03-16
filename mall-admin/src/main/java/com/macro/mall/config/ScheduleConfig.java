package com.macro.mall.config;

import com.macro.mall.service.HomeStatisticsTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * 定时任务配置
 */
@Configuration
@EnableScheduling
public class ScheduleConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduleConfig.class);

    @Autowired
    private HomeStatisticsTaskService homeStatisticsTaskService;

    /**
     * 每日11:30执行统计数据生成任务
     * cron表达式：秒 分 时 日 月 星期
     */
    @Scheduled(cron = "0 30 11 * * ?")
    public void generateDailyStatistics() {
        LOGGER.info("开始执行每日统计数据生成任务");
        try {
            homeStatisticsTaskService.generateYesterdayStatistics();
            LOGGER.info("每日统计数据生成任务执行完成");
        } catch (Exception e) {
            LOGGER.error("每日统计数据生成任务执行失败", e);
        }
    }
}