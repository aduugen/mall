package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@Api(tags = "UmsMemberController")
@Tag(name = "UmsMemberController", description = "会员管理")
@RequestMapping("/admin")
public class UmsMemberController {
    @Autowired
    private UmsMemberService memberService;

    @ApiOperation("获取注册用户总数")
    @RequestMapping(value = "/totalmembercount", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult geTotalMemberCount() {
        long totalMemberCount = memberService.geTotalMemberCount();
        return CommonResult.success(totalMemberCount);
    }

    @ApiOperation("获取今日新增注册用户数")
    @RequestMapping(value = "/todaynewmembercount", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult getTodayNewMemberCount() {
        int todayNewMemberCount = memberService.getTodayNewMemberCount();
        return CommonResult.success(todayNewMemberCount);
    }
}
