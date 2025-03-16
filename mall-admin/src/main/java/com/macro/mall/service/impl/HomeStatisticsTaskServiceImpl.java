package com.macro.mall.service.impl;

import com.macro.mall.mapper.HomeStatisticsDailyMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.HomeStatisticsDaily;
import com.macro.mall.model.HomeStatisticsDailyExample;
import com.macro.mall.service.HomeStatisticsTaskService;
import com.macro.mall.service.UmsVisitorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 首页统计数据定时任务Service实现类
 */
@Service
public class HomeStatisticsTaskServiceImpl implements HomeStatisticsTaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(HomeStatisticsTaskServiceImpl.class);

    @Autowired
    private HomeStatisticsDailyMapper homeStatisticsDailyMapper;

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private UmsMemberMapper memberMapper;

    @Autowired
    private UmsVisitorService visitorService;

    @Override
    public void generateYesterdayStatistics() {
        // 获取昨天的日期
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        Date yesterday = calendar.getTime();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String yesterdayStr = dateFormat.format(yesterday);

        // 生成昨天的统计数据
        generateStatistics(yesterdayStr);
    }

    @Override
    public void generateStatistics(String dateStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = dateFormat.parse(dateStr);

            // 检查该日期的统计数据是否已存在
            HomeStatisticsDailyExample example = new HomeStatisticsDailyExample();
            example.createCriteria().andDateEqualTo(date);
            List<HomeStatisticsDaily> existingStats = homeStatisticsDailyMapper.selectByExample(example);

            HomeStatisticsDaily statisticsDaily;
            if (existingStats != null && !existingStats.isEmpty()) {
                // 如果已存在，则更新
                statisticsDaily = existingStats.get(0);
            } else {
                // 如果不存在，则创建新记录
                statisticsDaily = new HomeStatisticsDaily();
                statisticsDaily.setDate(date);
            }

            // 获取订单统计数据
            Map<String, Object> orderStats = getOrderStatistics(date);
            statisticsDaily.setOrderCount(((Number) orderStats.get("count")).intValue());
            statisticsDaily.setOrderAmount((BigDecimal) orderStats.get("amount"));

            // 获取会员统计数据
            Map<String, Object> memberStats = getMemberStatistics(date);
            statisticsDaily.setMemberCount(((Number) memberStats.get("count")).intValue());
            statisticsDaily.setActiveMemberCount(((Number) memberStats.get("activeCount")).intValue());

            // 获取访客统计数据
            statisticsDaily.setVisitorCount(getVisitorCount(date));

            // 设置创建时间
            statisticsDaily.setCreateTime(new Date());

            // 保存或更新统计数据
            if (statisticsDaily.getId() != null) {
                homeStatisticsDailyMapper.updateByPrimaryKey(statisticsDaily);
                LOGGER.info("更新{}的统计数据成功", dateStr);
            } else {
                homeStatisticsDailyMapper.insert(statisticsDaily);
                LOGGER.info("生成{}的统计数据成功", dateStr);
            }
        } catch (ParseException e) {
            LOGGER.error("日期格式错误: {}", dateStr, e);
        } catch (Exception e) {
            LOGGER.error("生成统计数据失败: {}", dateStr, e);
        }
    }

    /**
     * 获取指定日期的订单统计数据
     */
    private Map<String, Object> getOrderStatistics(Date date) {
        // 这里应该调用orderMapper查询数据库获取真实数据
        // 例如：查询指定日期的订单数量和订单金额
        return orderMapper.getOrderStatistics(date);
    }

    /**
     * 获取指定日期的会员统计数据
     */
    private Map<String, Object> getMemberStatistics(Date date) {
        // 这里应该调用memberMapper查询数据库获取真实数据
        // 例如：查询截至指定日期的会员总数和当天活跃会员数
        return memberMapper.getMemberStatistics(date);
    }

    /**
     * 获取指定日期的访客数量
     */
    private Integer getVisitorCount(Date date) {
        // 这里应该调用visitorService查询数据库获取真实数据
        // 例如：查询指定日期的访客数量
        return visitorService.getVisitorCountByDate(date);
    }
}