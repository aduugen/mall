package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.OmsAfterSale;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderItem;
import com.macro.mall.model.OmsOrderItemExample;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.domain.AfterSaleParam;
import com.macro.mall.portal.service.MemberAfterSaleService;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.OmsOrderItemMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

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
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private OmsOrderItemMapper orderItemMapper;

    @ApiOperation("创建售后申请")
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult create(@RequestBody AfterSaleParam afterSaleParam) {
        try {
            System.out.println("===== 接收到售后申请创建请求 =====");

            // 记录请求信息
            System.out.println("售后参数: " + afterSaleParam);

            UmsMember currentMember = memberService.getCurrentMember();
            if (currentMember == null) {
                System.out.println("创建售后申请失败: 未获取到当前登录会员");
                return CommonResult.unauthorized(null);
            }
            System.out.println("当前会员: id=" + currentMember.getId() + ", 用户名=" + currentMember.getUsername());

            // 设置会员ID和订单创建时间
            afterSaleParam.setMemberId(currentMember.getId());
            afterSaleParam.setCreateTime(new Date());
            afterSaleParam.setUpdateTime(new Date());

            int count = afterSaleService.create(afterSaleParam);
            System.out.println("售后申请创建结果: " + count);

            if (count > 0) {
                System.out.println("===== 售后申请创建成功 =====");
                return CommonResult.success(count);
            }
            System.out.println("===== 售后申请创建失败: 返回了0 =====");
            return CommonResult.failed("创建售后申请失败");
        } catch (Exception e) {
            System.out.println("===== 售后申请创建异常 =====");
            System.out.println("异常类型: " + e.getClass().getName());
            System.out.println("异常信息: " + e.getMessage());
            e.printStackTrace();
            return CommonResult.failed(e.getMessage());
        }
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

    /**
     * 检查订单是否可以申请售后
     */
    @ApiOperation("检查订单是否可以申请售后")
    @RequestMapping(value = "/checkOrderAfterSaleStatus", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> checkOrderAfterSaleStatus(@RequestParam Long orderId) {
        UmsMember currentMember = memberService.getCurrentMember();

        // 查询订单
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            return CommonResult.failed("订单不存在");
        }

        // 验证是否是当前会员的订单
        if (!order.getMemberId().equals(currentMember.getId())) {
            return CommonResult.failed("无权查看该订单");
        }

        Map<String, Object> result = new HashMap<>();

        // 查询订单项的售后状态
        OmsOrderItemExample example = new OmsOrderItemExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        List<OmsOrderItem> orderItems = orderItemMapper.selectByExample(example);

        boolean allApplied = true;
        boolean partialApplied = false;
        List<Map<String, Object>> itemStatusList = new ArrayList<>();

        for (OmsOrderItem item : orderItems) {
            // 计算已申请数量和可申请数量
            Integer appliedQuantity = item.getAppliedQuantity() == null ? 0 : item.getAppliedQuantity();
            Integer productQuantity = item.getProductQuantity() == null ? 0 : item.getProductQuantity();
            Integer availableQuantity = productQuantity - appliedQuantity;

            // 检查是否全部申请
            if (appliedQuantity < productQuantity) {
                allApplied = false;
            }

            // 检查是否部分申请
            if (appliedQuantity > 0) {
                partialApplied = true;
            }

            // 添加每个订单项的状态信息
            Map<String, Object> itemStatus = new HashMap<>();
            itemStatus.put("orderItemId", item.getId());
            itemStatus.put("productName", item.getProductName());
            itemStatus.put("productQuantity", productQuantity);
            itemStatus.put("appliedQuantity", appliedQuantity);
            itemStatus.put("availableQuantity", availableQuantity);
            itemStatus.put("isFullyApplied", appliedQuantity >= productQuantity);
            itemStatusList.add(itemStatus);
        }

        // 确定订单整体售后状态
        byte afterSaleStatus;
        if (allApplied) {
            afterSaleStatus = 2; // 全部申请
        } else if (partialApplied) {
            afterSaleStatus = 1; // 部分申请
        } else {
            afterSaleStatus = 0; // 未申请
        }

        // 如果订单状态为null或与计算结果不一致，则更新
        if (order.getAfterSaleStatus() == null || order.getAfterSaleStatus() != afterSaleStatus) {
            OmsOrder updateOrder = new OmsOrder();
            updateOrder.setId(orderId);
            updateOrder.setAfterSaleStatus(afterSaleStatus);
            orderMapper.updateByPrimaryKeySelective(updateOrder);
        }

        // 设置结果
        result.put("orderId", orderId);
        result.put("afterSaleStatus", afterSaleStatus);
        result.put("canApplyAfterSale", afterSaleStatus != 2); // 全部申请时不能再申请
        result.put("items", itemStatusList);

        return CommonResult.success(result);
    }
}