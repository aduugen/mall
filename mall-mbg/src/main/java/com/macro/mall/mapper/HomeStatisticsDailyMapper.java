package com.macro.mall.mapper;

import com.macro.mall.model.HomeStatisticsDaily;
import com.macro.mall.model.HomeStatisticsDailyExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface HomeStatisticsDailyMapper {
    long countByExample(HomeStatisticsDailyExample example);

    int deleteByExample(HomeStatisticsDailyExample example);

    int deleteByPrimaryKey(Long id);

    int insert(HomeStatisticsDaily record);

    int insertSelective(HomeStatisticsDaily record);

    List<HomeStatisticsDaily> selectByExample(HomeStatisticsDailyExample example);

    HomeStatisticsDaily selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") HomeStatisticsDaily record,
            @Param("example") HomeStatisticsDailyExample example);

    int updateByExample(@Param("record") HomeStatisticsDaily record,
            @Param("example") HomeStatisticsDailyExample example);

    int updateByPrimaryKeySelective(HomeStatisticsDaily record);

    int updateByPrimaryKey(HomeStatisticsDaily record);

    /**
     * 使用REPLACE INTO自动处理唯一键冲突
     */
    int replaceInto(HomeStatisticsDaily record);
}