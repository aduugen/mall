package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.portal.service.LogisticsCompanyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

/**
 * 会员通用功能Controller
 */
@Controller
@Api(tags = "MemberCommonController", description = "会员通用功能接口")
@RequestMapping("/member/common")
public class MemberCommonController {
    @Autowired
    private LogisticsCompanyService logisticsCompanyService;

    /**
     * 获取物流公司列表
     */
    @ApiOperation("获取物流公司列表")
    @RequestMapping(value = "/logisticsCompanies", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<Map<String, Object>>> getLogisticsCompanies() {
        List<Map<String, Object>> companies = logisticsCompanyService.getLogisticsCompaniesForDisplay();
        return CommonResult.success(companies);
    }
}