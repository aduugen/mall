package com.macro.mall.controller;

import com.macro.mall.common.api.CommonResult;
import com.macro.mall.service.UmsVisitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@Controller
@Api(tags = "UmsVisitorController")
@Tag(name = "UmsVisitorController", description = "访客统计管理")
@RequestMapping("admin/visitor")
public class UmsVisitorController {
    @Autowired
    private UmsVisitorService visitorService;

    @ApiOperation("获取今日访客数和在线访客数")
    @RequestMapping(value = "/today", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Integer>> getTodayVisitorStats() {
        Map<String, Integer> data = new HashMap<>();
        data.put("totalCount", visitorService.getTodayVisitorCount());
        data.put("onlineCount", visitorService.getCurrentOnlineCount());
        return CommonResult.success(data);
    }

    @ApiOperation("获取访客数量")
    @RequestMapping(value = "/total", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Integer> getTotalVisitorCount() {
        return CommonResult.success(visitorService.getTotalVisitorCount());
    }
}
