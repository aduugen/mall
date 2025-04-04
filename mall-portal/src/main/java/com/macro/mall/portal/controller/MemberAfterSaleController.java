package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsAfterSale;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.domain.AfterSaleParam;
import com.macro.mall.portal.service.MemberAfterSaleService;
import com.macro.mall.portal.service.UmsMemberService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 会员售后Controller
 */
@Controller
@Api(tags = "MemberAfterSaleController", description = "会员售后管理")
@RequestMapping("/member/afterSale")
public class MemberAfterSaleController {
    @Autowired
    private MemberAfterSaleService afterSaleService;
    @Autowired
    private UmsMemberService memberService;

    @ApiOperation("创建售后申请")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody AfterSaleParam afterSaleParam) {
        UmsMember currentMember = memberService.getCurrentMember();
        // 设置会员ID和订单创建时间
        afterSaleParam.setMemberId(currentMember.getId());
        afterSaleParam.setCreateTime(new Date());
        afterSaleParam.setUpdateTime(new Date());

        int count = afterSaleService.create(afterSaleParam);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("获取售后申请详情")
    @RequestMapping(value = "/detail", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<OmsAfterSale> getItem(@RequestParam Long id) {
        UmsMember currentMember = memberService.getCurrentMember();
        OmsAfterSale afterSale = afterSaleService.getDetail(id, currentMember.getId());
        return CommonResult.success(afterSale);
    }

    @ApiOperation("查询售后申请列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<OmsAfterSale>> list(@RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        UmsMember currentMember = memberService.getCurrentMember();
        List<OmsAfterSale> afterSaleList = afterSaleService.list(currentMember.getId(), status, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(afterSaleList));
    }

    @ApiOperation("取消售后申请")
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult cancel(@RequestParam Long id) {
        UmsMember currentMember = memberService.getCurrentMember();
        int count = afterSaleService.cancel(id, currentMember.getId());
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }
}