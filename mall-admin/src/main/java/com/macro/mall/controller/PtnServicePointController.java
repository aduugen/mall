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

    @ApiOperation("根据网点类型获取网点列表")
    @RequestMapping(value = "/type/{type}", method = RequestMethod.GET)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:servicePoint:read')")
    public CommonResult<List<PtnServicePoint>> getTypeList(@PathVariable Integer type) {
        List<PtnServicePoint> pointList = servicePointService.listByType(type);
        return CommonResult.success(pointList);
    }

    @ApiOperation("更新网点状态")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:servicePoint:update')")
    public CommonResult updateStatus(@RequestParam("id") Long id, @RequestParam("status") Integer status) {
        int count = servicePointService.updateStatus(id, status);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("更新网点业务数量")
    @RequestMapping(value = "/updateBillCount", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:servicePoint:update')")
    public CommonResult updateBillCount(
            @RequestParam("id") Long id,
            @RequestParam(value = "selfPickCount", required = false) Integer selfPickCount,
            @RequestParam(value = "receiveCount", required = false) Integer receiveCount) {
        int count = servicePointService.updateBillCount(id, selfPickCount, receiveCount);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("更新网点服务星级")
    @RequestMapping(value = "/updateServiceRating", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAuthority('pms:servicePoint:update')")
    public CommonResult updateServiceRating(
            @RequestParam("id") Long id,
            @RequestParam("rating") Integer rating) {
        int count = servicePointService.updateServiceRating(id, rating);
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed();
    }

    @ApiOperation("搜索收货服务网点")
    @RequestMapping(value = "/searchReceivePoints", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<PtnServicePoint>> searchReceivePoints(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", defaultValue = "0") Integer status) {
        // 查询类型为1(收货点)或2(综合点)的服务网点
        List<PtnServicePoint> pointList = servicePointService.searchReceivePoints(keyword, status);
        return CommonResult.success(pointList);
    }
}
