package com.macro.mall.service.impl;

import com.macro.mall.dto.HomeStatisticsDataDTO;
import com.macro.mall.mapper.HomeStatisticsDailyMapper;
import com.macro.mall.model.HomeStatisticsDaily;
import com.macro.mall.model.HomeStatisticsDailyExample;
import com.macro.mall.service.HomeStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 首页运营统计数据Service实现类
 */
@Service
public class HomeStatisticsServiceImpl implements HomeStatisticsService {
    @Autowired
    private HomeStatisticsDailyMapper homeStatisticsDailyMapper;

    @Override
    public List<HomeStatisticsDataDTO> getChartData(Date startDate, Date endDate) {
        // 查询指定日期范围内的统计数据
        HomeStatisticsDailyExample example = new HomeStatisticsDailyExample();
        example.createCriteria().andDateBetween(startDate, endDate);
        example.setOrderByClause("date asc"); // 按日期升序排序
        List<HomeStatisticsDaily> statisticsList = homeStatisticsDailyMapper.selectByExample(example);

        // 转换为DTO对象
        List<HomeStatisticsDataDTO> result = new ArrayList<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        for (HomeStatisticsDaily statistics : statisticsList) {
            HomeStatisticsDataDTO dto = new HomeStatisticsDataDTO();
            dto.setDate(dateFormat.format(statistics.getDate()));
            dto.setOrderCount(statistics.getOrderCount());
            dto.setOrderAmount(statistics.getOrderAmount().doubleValue());
            dto.setMemberCount(statistics.getMemberCount());
            dto.setActiveMemberCount(statistics.getActiveMemberCount());
            dto.setVisitorCount(statistics.getVisitorCount());
            result.add(dto);
        }

        return result;
    }
}