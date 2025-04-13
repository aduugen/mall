package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
// 引入新的 DTOs
import com.macro.mall.dto.OmsAfterSaleDetail;
import com.macro.mall.dto.OmsAfterSaleQueryParam;
import com.macro.mall.dto.OmsUpdateStatusParam;
import com.macro.mall.dto.OmsAfterSaleStatistic;
import com.macro.mall.model.OmsAfterSale; // 引入新的 Model 或列表 DTO
import com.macro.mall.service.OmsAfterSaleService; // 引入新的 Service 接口
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 售后服务管理Controller
 */
@Controller
@Api(tags = "OmsAfterSaleController") // 更新 Api 注解
@Tag(name = "OmsAfterSaleController", description = "售后服务管理") // 更新 Tag 注解
@RequestMapping("/afterSale") // 更新 RequestMapping 路径
public class OmsAfterSaleController { // 类名建议修改

    @Autowired
    private OmsAfterSaleService afterSaleService; // 注入新的 Service

    @ApiOperation("分页查询售后申请") // 更新描述
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<OmsAfterSale>> list(OmsAfterSaleQueryParam queryParam, // 使用新的 QueryParam DTO
                                                       @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
                                                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<OmsAfterSale> afterSaleList = afterSaleService.list(queryParam, pageSize, pageNum); // 调用新的 Service 方法
        return CommonResult.success(CommonPage.restPage(afterSaleList));
    }

    @ApiOperation("批量删除售后申请") // 更新描述
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult delete(@RequestParam("ids") List<Long> ids) {
        int count = afterSaleService.delete(ids); // 调用新的 Service 方法
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取售后申请详情") // 更新描述
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsAfterSaleDetail> getItem(@PathVariable Long id) { // 返回新的 Detail DTO
        OmsAfterSaleDetail result = afterSaleService.getDetail(id); // 调用新的 Service 方法
        return CommonResult.success(result);
    }

    @ApiOperation("修改售后申请状态") // 更新描述
    @RequestMapping(value = "/update/status/{id}", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateStatus(@PathVariable Long id, @RequestBody OmsUpdateStatusParam statusParam) { // statusParam DTO 保持不变，但 Service 层处理逻辑已变
        int count = afterSaleService.updateStatus(id, statusParam); // 调用新的 Service 方法
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取售后申请状态统计") // 更新描述
    @RequestMapping(value = "/statusStatistic", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsAfterSaleStatistic> getStatusStatistic() { // 返回新的 Statistic DTO
        OmsAfterSaleStatistic afterSaleStatistic = afterSaleService.getAfterSaleStatistic(); // 调用新的 Service 方法
        return CommonResult.success(afterSaleStatistic);
    }
}