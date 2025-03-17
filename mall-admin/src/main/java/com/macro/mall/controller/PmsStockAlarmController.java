package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.PmsSkuStockWithProductNameDTO;
import com.macro.mall.service.PmsSkuStockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 商品库存告警管理Controller
 */
@Controller
@Api(tags = "PmsStockAlarmController")
@Tag(name = "PmsStockAlarmController", description = "商品库存告警管理")
@RequestMapping("/stockAlarm")
public class PmsStockAlarmController {
    @Autowired
    private PmsSkuStockService skuStockService;

    @ApiOperation("获取库存告警商品列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PmsSkuStockWithProductNameDTO>> getList() {
        List<PmsSkuStockWithProductNameDTO> stockAlarmList = skuStockService.getStockAlarmList();
        return CommonResult.success(stockAlarmList);
    }

    @ApiOperation("分页获取库存告警商品列表")
    @RequestMapping(value = "/list/page", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsSkuStockWithProductNameDTO>> getList(
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<PmsSkuStockWithProductNameDTO> stockAlarmList = skuStockService.getStockAlarmList(pageSize, pageNum);
        Long total = skuStockService.getStockAlarmCount();
        return CommonResult.success(CommonPage.restPage(stockAlarmList, total, pageNum, pageSize));
    }
}