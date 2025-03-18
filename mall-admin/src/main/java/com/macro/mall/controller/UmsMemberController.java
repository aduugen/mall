package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.dto.MemberConsumptionInfoDTO;
import com.macro.mall.dto.MemberInfoDTO;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Api(tags = "UmsMemberController")
@Tag(name = "UmsMemberController", description = "会员管理")
@RequestMapping("/member")
public class UmsMemberController {
    @Autowired
    private UmsMemberService memberService;

    @ApiOperation("获取注册用户总数")
    @RequestMapping(value = "/count", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Long> getTotalMemberCount() {
        long totalMemberCount = memberService.geTotalMemberCount();
        return CommonResult.success(totalMemberCount);
    }

    @ApiOperation("获取今日新增注册用户数")
    @RequestMapping(value = "/today", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Integer> getTodayNewMemberCount() {
        int todayNewMemberCount = memberService.getTodayNewMemberCount();
        return CommonResult.success(todayNewMemberCount);
    }

    @ApiOperation("分页获取会员列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<MemberInfoDTO>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<MemberInfoDTO> memberList = memberService.list(keyword, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(memberList));
    }

    @ApiOperation("获取会员详情")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<MemberInfoDTO> detail(@PathVariable Long id) {
        MemberInfoDTO member = memberService.getMemberInfo(id);
        return CommonResult.success(member);
    }

    @ApiOperation("分页获取会员消费信息列表")
    @RequestMapping(value = "/consumption/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<MemberConsumptionInfoDTO>> listConsumption(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<MemberConsumptionInfoDTO> consumptionList = memberService.listConsumptionInfo(keyword, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(consumptionList));
    }

    @ApiOperation("获取会员消费信息详情")
    @RequestMapping(value = "/consumption/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<MemberConsumptionInfoDTO> consumptionDetail(@PathVariable Long id) {
        MemberConsumptionInfoDTO consumptionInfo = memberService.getMemberConsumptionInfo(id);
        return CommonResult.success(consumptionInfo);
    }

    @ApiOperation("获取会员订单列表")
    @RequestMapping(value = "/{id}/orders", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<OmsOrder>> listOrders(
            @PathVariable Long id,
            @RequestParam(value = "pageSize", defaultValue = "15") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<OmsOrder> orderList = memberService.getMemberOrders(id, pageSize, pageNum);
        long count = memberService.getMemberOrderCount(id);
        return CommonResult.success(CommonPage.restPage(orderList, count, pageNum, pageSize));
    }
}
