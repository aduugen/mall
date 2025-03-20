package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsAfterSale;
import com.macro.mall.service.OmsAfterSaleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Api(tags = "OmsAfterSaleController", description = "售后管理")
@RequestMapping("/afterSale")
public class OmsAfterSaleController {
    @Autowired
    private OmsAfterSaleService afterSaleService;

    @ApiOperation("创建售后申请")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody OmsAfterSale afterSale) {
        int count = afterSaleService.create(afterSale);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取售后申请详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsAfterSale> getItem(@PathVariable Long id) {
        OmsAfterSale afterSale = afterSaleService.getItem(id);
        return CommonResult.success(afterSale);
    }

    @ApiOperation("查询售后申请列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<OmsAfterSale>> list(@RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<OmsAfterSale> afterSaleList = afterSaleService.list(status, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(afterSaleList));
    }

    @ApiOperation("更新售后申请状态")
    @RequestMapping(value = "/update/status", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult updateStatus(@RequestParam Long id,
            @RequestParam Integer status,
            @RequestParam(required = false) String handleNote) {
        int count = afterSaleService.updateStatus(id, status, handleNote);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("取消售后申请")
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult cancel(@RequestParam Long id) {
        int count = afterSaleService.cancel(id);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}