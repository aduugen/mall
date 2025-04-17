package com.macro.mall.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsAfterSaleLog;
import com.macro.mall.service.OmsAfterSaleLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 售后操作日志管理Controller
 */
@Api(tags = "OmsAfterSaleLogController", description = "售后操作日志管理")
@RestController
@RequestMapping("/afterSale/log")
public class OmsAfterSaleLogController {

    @Autowired
    private OmsAfterSaleLogService afterSaleLogService;

    @ApiOperation("分页查询售后日志")
    @RequestMapping(value = "/query", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<OmsAfterSaleLog>> getList(
            @RequestParam(value = "afterSaleId", required = false) Long afterSaleId,
            @RequestParam(value = "operateMan", required = false) String operateMan,
            @RequestParam(value = "operateType", required = false) Integer operateType,
            @RequestParam(value = "afterSaleStatus", required = false) Integer afterSaleStatus,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        List<OmsAfterSaleLog> logList = afterSaleLogService.getLogList(
                afterSaleId, operateMan, operateType, afterSaleStatus, startTime, endTime);
        return CommonResult.success(CommonPage.restPage(logList));
    }

    @ApiOperation("获取售后操作类型统计数据")
    @PreAuthorize("hasAuthority('oms:afterSale:read')")
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<Map<String, Object>>> getOperationStatistics(
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime) {

        List<Map<String, Object>> statistics = afterSaleLogService.getOperationStatistics(startTime, endTime);
        return CommonResult.success(statistics);
    }

    @ApiOperation("获取售后单的最后一条操作日志")
    @PreAuthorize("hasAuthority('oms:afterSale:read')")
    @RequestMapping(value = "/last/{afterSaleId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsAfterSaleLog> getLastLog(
            @PathVariable @ApiParam("售后单ID") Long afterSaleId) {

        OmsAfterSaleLog log = afterSaleLogService.getLastLog(afterSaleId);
        if (log != null) {
            return CommonResult.success(log);
        }
        return CommonResult.failed("未找到相关操作日志");
    }

    @ApiOperation("添加操作日志")
    @PreAuthorize("hasAuthority('oms:afterSale:update')")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<OmsAfterSaleLog> create(@RequestBody OmsAfterSaleLog log) {
        int count = afterSaleLogService.saveLog(log);
        if (count > 0) {
            return CommonResult.success(log);
        }
        return CommonResult.failed("添加操作日志失败");
    }
}