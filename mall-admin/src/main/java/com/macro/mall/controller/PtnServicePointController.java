package com.macro.mall.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.PtnServicePoint;
import com.macro.mall.service.PtnServicePointService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@Api(tags = "PtnServicePointController", description = "合作网点管理")
@RequestMapping("/servicePoint")
public class PtnServicePointController {
    @Autowired
    private PtnServicePointService servicePointService;

    @ApiOperation("获取所有合作网点")
    @RequestMapping(value = "/listAll", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:servicePoint:read')")
    public CommonResult<List<PtnServicePoint>> getList() {
        List<PtnServicePoint> pointList = servicePointService.listAll();
        return CommonResult.success(pointList);
    }

    @ApiOperation("添加合作网点")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:servicePoint:create')")
    public CommonResult create(@Validated @RequestBody PtnServicePoint point) {
        int count = servicePointService.create(point);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("更新合作网点")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:servicePoint:update')")
    public CommonResult update(@PathVariable Long id, @Validated @RequestBody PtnServicePoint point) {
        int count = servicePointService.update(id, point);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("删除合作网点")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:servicePoint:delete')")
    public CommonResult delete(@PathVariable Long id) {
        int count = servicePointService.delete(id);
        if (count > 0) {
            return CommonResult.success(count);
        } else {
            return CommonResult.failed();
        }
    }

    @ApiOperation("根据ID获取合作网点")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:servicePoint:read')")
    public CommonResult<PtnServicePoint> getItem(@PathVariable Long id) {
        PtnServicePoint point = servicePointService.getItem(id);
        return CommonResult.success(point);
    }

    @ApiOperation("分页查询合作网点")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:servicePoint:read')")
    public CommonResult<CommonPage<PtnServicePoint>> list(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum) {
        List<PtnServicePoint> pointList = servicePointService.list(keyword, pageSize, pageNum);
        return CommonResult.success(CommonPage.restPage(pointList));
    }
}
