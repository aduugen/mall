package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.HomeStatisticsDataDTO;
import com.macro.mall.service.HomeStatisticsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 首页运营统计数据Controller
 */
@Controller
@Api(tags = "HomeStatisticsController")
@Tag(name = "HomeStatisticsController", description = "首页运营统计数据")
@RequestMapping("/home/statistics")
public class HomeStatisticsController {
    @Autowired
    private HomeStatisticsService homeStatisticsService;

    @ApiOperation("获取运营统计曲线数据")
    @RequestMapping(value = "/chart", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<HomeStatisticsDataDTO>> getChartData(
            @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        List<HomeStatisticsDataDTO> chartData = homeStatisticsService.getChartData(startDate, endDate);
        return CommonResult.success(chartData);
    }
}