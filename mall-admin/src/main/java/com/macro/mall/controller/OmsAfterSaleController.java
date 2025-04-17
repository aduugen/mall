package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
// 引入新的 DTOs
import com.macro.mall.dto.OmsAfterSaleDetail;
import com.macro.mall.dto.OmsAfterSaleQueryParam;
import com.macro.mall.dto.OmsUpdateStatusParam;
import com.macro.mall.dto.OmsAfterSaleStatistic;
import com.macro.mall.model.OmsAfterSale; // 引入新的 Model 或列表 DTO
import com.macro.mall.model.OmsAfterSaleLog;
import com.macro.mall.service.OmsAfterSaleService; // 引入新的 Service 接口
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import com.github.pagehelper.PageHelper;
import com.macro.mall.aspect.AuditLogAspect.AuditLog;
import com.macro.mall.aspect.IdempotentAspect.Idempotent;

import java.util.List;
import java.util.Date;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;

/**
 * 售后服务管理Controller
 */
@Controller
@Api(tags = "OmsAfterSaleController", description = "售后服务管理")
@RequestMapping("/afterSale")
@Slf4j
public class OmsAfterSaleController {

    @Autowired
    private OmsAfterSaleService afterSaleService; // 注入新的 Service

    @PreAuthorize("hasRole('ADMIN') or @permissionService.hasPermission('afterSale:list')")
    @ApiOperation("分页查询售后申请")
    @GetMapping("/list")
    public CommonResult<CommonPage<OmsAfterSaleDetail>> list(OmsAfterSaleQueryParam queryParam,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        try {
            List<OmsAfterSaleDetail> afterSaleList = afterSaleService.list(queryParam, pageSize, pageNum); // 接收
            // OmsAfterSaleDetail
            // 列表
            return CommonResult.success(CommonPage.restPage(afterSaleList)); // 传入 OmsAfterSaleDetail 列表
        } catch (Exception e) {
            log.error("查询售后列表失败", e);
            return CommonResult.failed("查询售后列表失败: " + e.getMessage());
        }
    }

    @ApiOperation("批量删除售后申请")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@RequestParam("ids") List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return CommonResult.failed("请选择要删除的记录");
        }

        try {
            int count = afterSaleService.delete(ids); // 调用新的 Service 方法
            if (count > 0) {
                return CommonResult.success(count);
            } else {
                return CommonResult.failed("没有可删除的记录");
            }
        } catch (Exception e) {
            log.error("删除售后申请失败", e);
            return CommonResult.failed("删除售后申请失败: " + e.getMessage());
        }
    }

    @ApiOperation("获取售后申请详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsAfterSaleDetail> getItem(@PathVariable Long id) { // 返回新的 Detail DTO
        if (id == null || id <= 0) {
            return CommonResult.failed("售后单ID无效");
        }

        try {
            OmsAfterSaleDetail result = afterSaleService.getDetailDTO(id);
            if (result != null) {
                return CommonResult.success(result);
            } else {
                return CommonResult.failed("售后申请不存在");
            }
        } catch (Exception e) {
            log.error("获取售后详情失败", e);
            return CommonResult.failed("获取售后详情失败: " + e.getMessage());
        }
    }

    @PreAuthorize("hasRole('ADMIN') or @permissionService.hasPermission('afterSale:update')")
    @ApiOperation("修改售后申请状态")
    @PostMapping("/update/status/{id}")
    @AuditLog(value = "修改售后申请状态", businessType = "售后管理")
    @Idempotent(timeout = 60, message = "请勿重复提交售后状态更新请求")
    public CommonResult updateStatus(@PathVariable Long id, @Validated @RequestBody OmsUpdateStatusParam statusParam) { // statusParam
        // DTO
        // 保持不变，但
        // Service
        // 层处理逻辑已变
        if (id == null || id <= 0) {
            return CommonResult.failed("售后单ID无效");
        }

        if (statusParam == null || statusParam.getStatus() == null) {
            return CommonResult.failed("请提供有效的状态参数");
        }

        try {
            // 获取当前登录用户
            String currentUser = SecurityContextHolder.getContext().getAuthentication().getName();
            statusParam.setHandleMan(currentUser);

            // 检查操作权限
            OmsAfterSale afterSale = afterSaleService.getDetail(id);
            if (afterSale == null) {
                return CommonResult.failed("售后单不存在");
            }

            // 金额验证逻辑已移至Service层，这里不再重复验证
            // 删除了对不存在的getOrderTotalAmount()方法的调用

            boolean success = afterSaleService.updateStatus(id, statusParam);
            if (success) {
                return CommonResult.success("状态更新成功");
            } else {
                return CommonResult.failed("状态更新失败");
            }
        } catch (Exception e) {
            log.error("更新售后状态失败", e);
            return CommonResult.failed("更新售后状态失败: " + e.getMessage());
        }
    }

    @ApiOperation("获取售后申请状态统计")
    @RequestMapping(value = "/statusStatistic", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsAfterSaleStatistic> getStatusStatistic() { // 返回新的 Statistic DTO
        try {
            OmsAfterSaleStatistic afterSaleStatistic = afterSaleService.getAfterSaleStatistic(); // 调用新的 Service 方法
            return CommonResult.success(afterSaleStatistic);
        } catch (Exception e) {
            log.error("获取售后统计失败", e);
            return CommonResult.failed("获取售后统计失败: " + e.getMessage());
        }
    }

    @ApiOperation("检查异常售后单")
    @RequestMapping(value = "/checkAbnormal", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<OmsAfterSale>> checkAbnormalAfterSales(
            @RequestParam(value = "days", defaultValue = "7") Integer days) {
        if (days == null || days <= 0) {
            return CommonResult.failed("天数参数无效，请提供大于0的值");
        }

        try {
            List<OmsAfterSale> abnormalList = afterSaleService.checkAbnormalAfterSales(days);
            return CommonResult.success(abnormalList);
        } catch (Exception e) {
            log.error("检查异常售后单失败", e);
            return CommonResult.failed("检查异常售后单失败: " + e.getMessage());
        }
    }

    @ApiOperation("获取售后操作日志列表")
    @PreAuthorize("hasAuthority('oms:afterSale:read')")
    @RequestMapping(value = "/log/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<OmsAfterSaleLog>> getOperationLogs(
            @RequestParam(value = "afterSaleId", required = false) Long afterSaleId,
            @RequestParam(value = "operateMan", required = false) String operateMan,
            @RequestParam(value = "operateType", required = false) Integer operateType,
            @RequestParam(value = "afterSaleStatus", required = false) Integer afterSaleStatus,
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        try {
            PageHelper.startPage(pageNum, pageSize);
            List<OmsAfterSaleLog> logs = afterSaleService.getOperationLogs(
                    afterSaleId, operateMan, operateType, afterSaleStatus, startTime, endTime);
            return CommonResult.success(logs);
        } catch (Exception e) {
            return CommonResult.failed("获取售后操作日志失败: " + e.getMessage());
        }
    }

    @ApiOperation("统计操作类型数量")
    @PreAuthorize("hasAuthority('oms:afterSale:read')")
    @RequestMapping(value = "/log/statistic/type", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<Map<String, Object>>> countOperationsByType(
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime,
            @RequestParam(value = "operateMan", required = false) String operateMan) {
        try {
            List<Map<String, Object>> result = afterSaleService.countOperationsByType(startTime, endTime, operateMan);
            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.failed("统计操作类型数量失败: " + e.getMessage());
        }
    }

    @ApiOperation("统计状态转换耗时")
    @PreAuthorize("hasAuthority('oms:afterSale:read')")
    @RequestMapping(value = "/log/statistic/transition", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<Map<String, Object>>> getStatusTransitionTime(
            @RequestParam(value = "startTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(value = "endTime", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime) {
        try {
            List<Map<String, Object>> result = afterSaleService.getStatusTransitionTime(startTime, endTime);
            return CommonResult.success(result);
        } catch (Exception e) {
            return CommonResult.failed("统计状态转换耗时失败: " + e.getMessage());
        }
    }

    @ApiOperation("获取售后统计信息")
    @PreAuthorize("hasAuthority('oms:afterSale:read')")
    @RequestMapping(value = "/statistic", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsAfterSaleStatistic> getStatistic() {
        try {
            OmsAfterSaleStatistic statistic = afterSaleService.getStatistic();
            return CommonResult.success(statistic);
        } catch (Exception e) {
            return CommonResult.failed("获取售后统计信息失败: " + e.getMessage());
        }
    }
}