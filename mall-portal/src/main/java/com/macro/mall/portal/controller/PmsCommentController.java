package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.PmsComment;
import com.macro.mall.portal.domain.ProductCommentSummary;
import com.macro.mall.portal.domain.PmsMemberCommentDto;
import com.macro.mall.portal.service.PmsCommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * 商品评价管理Controller
 * Created by macro on 2024/x/x. // Replace with actual date
 */
@Controller
@Api(tags = "PmsCommentController")
@Tag(name = "PmsCommentController", description = "商品评价管理")
@RequestMapping("/comment")
public class PmsCommentController {

    @Autowired
    private PmsCommentService commentService;

    @ApiOperation("获取指定商品的评价分页列表")
    @RequestMapping(value = "/list/{productId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsComment>> list(@PathVariable Long productId,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        CommonPage<PmsComment> commentPage = commentService.list(productId, pageNum, pageSize);
        return CommonResult.success(commentPage);
    }

    @ApiOperation("获取指定商品的评价概要信息")
    @RequestMapping(value = "/summary/{productId}", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<ProductCommentSummary> getSummary(@PathVariable Long productId) {
        ProductCommentSummary summary = commentService.getSummary(productId);
        return CommonResult.success(summary);
    }

    @ApiOperation("分页获取当前用户的评价列表")
    @RequestMapping(value = "/myList", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PmsMemberCommentDto>> getMyList(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        CommonPage<PmsMemberCommentDto> commentPage = commentService.getMyList(pageNum, pageSize);
        return CommonResult.success(commentPage);
    }
}