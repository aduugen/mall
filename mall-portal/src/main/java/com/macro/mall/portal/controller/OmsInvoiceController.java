package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsInvoice;
import com.macro.mall.portal.domain.OmsInvoiceParam;
import com.macro.mall.portal.service.OmsInvoiceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 发票管理Controller
 */
@Controller
@Api(tags = "OmsInvoiceController")
@Tag(name = "OmsInvoiceController", description = "发票管理")
@RequestMapping("/order/invoice")
public class OmsInvoiceController {
    @Autowired
    private OmsInvoiceService invoiceService;

    @ApiOperation("申请发票")
    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult<OmsInvoice> apply(@Validated @RequestBody OmsInvoiceParam invoiceParam) {
        OmsInvoice invoice = invoiceService.apply(invoiceParam);
        return CommonResult.success(invoice);
    }

    @ApiOperation("获取订单发票申请记录")
    @RequestMapping(value = "/order/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsInvoice> getByOrderId(@PathVariable Long orderId) {
        OmsInvoice invoice = invoiceService.getByOrderId(orderId);
        return CommonResult.success(invoice);
    }

    @ApiOperation("获取发票列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<OmsInvoice>> list(
            @RequestParam(value = "status", required = false, defaultValue = "-1") Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        CommonPage<OmsInvoice> invoicePage = invoiceService.list(status, pageNum, pageSize);
        return CommonResult.success(invoicePage);
    }

    @ApiOperation("获取发票详情")
    @RequestMapping(value = "/detail/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsInvoice> detail(@PathVariable Long id) {
        OmsInvoice invoice = invoiceService.detail(id);
        return CommonResult.success(invoice);
    }
}