package com.macro.mall.controller;

import com.macro.mall.aspect.IdempotentAspect;
import com.macro.mall.common.api.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统Token控制器
 */
@RestController
@Api(tags = "TokenController", description = "系统Token管理")
@RequestMapping("/token")
public class TokenController {

    @Autowired
    private IdempotentAspect idempotentAspect;

    /**
     * 获取幂等性Token，用于防止表单重复提交
     * 
     * @return 包含token的结果
     */
    @ApiOperation("获取幂等性Token")
    @GetMapping("/idempotent")
    public CommonResult<Map<String, String>> getIdempotentToken() {
        String token = idempotentAspect.generateToken();
        Map<String, String> result = new HashMap<>();
        result.put("token", token);
        return CommonResult.success(result);
    }
}