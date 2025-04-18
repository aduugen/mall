package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsInvoice;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.portal.domain.OmsInvoiceParam;
import com.macro.mall.portal.service.OmsInvoiceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 发票管理Controller
 */
@Controller
@Api(tags = "OmsInvoiceController", description = "发票管理")
@RequestMapping("/order/invoice")
public class OmsInvoiceController {
    @Autowired
    private OmsInvoiceService invoiceService;

    @ApiOperation("申请发票")
    @RequestMapping(value = "/apply", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult apply(@RequestBody OmsInvoiceParam invoiceParam) {
        try {
            System.out.println("收到发票申请请求: " + invoiceParam.getOrderId() + ", 发票类型: " + invoiceParam.getInvoiceType());

            if (invoiceParam.getOrderId() == null) {
                return CommonResult.failed("订单ID不能为空");
            }

            int count = invoiceService.apply(invoiceParam);
            if (count > 0) {
                System.out.println("发票申请成功: orderId=" + invoiceParam.getOrderId());
                return CommonResult.success(null);
            }
            System.out.println("发票申请失败: orderId=" + invoiceParam.getOrderId());
            return CommonResult.failed("申请发票失败");
        } catch (IllegalStateException e) {
            // 用户未登录或认证失败
            System.err.println("发票申请失败 - 用户认证问题: " + e.getMessage());
            return CommonResult.failed("登录状态异常，请重新登录后再试");
        } catch (Exception e) {
            System.err.println("发票申请异常: " + e.getMessage());
            e.printStackTrace();
            return CommonResult.failed("申请发票时发生错误: " + e.getMessage());
        }
    }

    @ApiOperation("获取发票列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<OmsInvoice>> list(@RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize) {
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

    @ApiOperation("通过订单ID获取发票")
    @RequestMapping(value = "/getByOrderId/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsInvoice> getByOrderId(@PathVariable Long orderId) {
        OmsInvoice invoice = invoiceService.getByOrderId(orderId);
        return CommonResult.success(invoice);
    }

    @ApiOperation("检查订单发票状态")
    @RequestMapping(value = "/check/{orderId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> checkOrderInvoice(@PathVariable Long orderId) {
        OmsInvoice invoice = invoiceService.getByOrderId(orderId);
        Map<String, Object> result = new HashMap<>();
        if (invoice != null) {
            // 已有发票记录
            result.put("isApplied", true);
            result.put("invoiceId", invoice.getId());
            result.put("status", invoice.getStatus());
        } else {
            // 无发票记录
            result.put("isApplied", false);
        }
        return CommonResult.success(result);
    }
}