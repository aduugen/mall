package com.macro.mall.portal.controller;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.api.CommonResult;
import com.macro.mall.model.*;
import com.macro.mall.portal.domain.AfterSaleParam;
import com.macro.mall.portal.domain.PortalOmsAfterSaleDetail;
import com.macro.mall.portal.domain.PortalOmsAfterSaleDTO;
import com.macro.mall.portal.service.MemberAfterSaleService;
import com.macro.mall.portal.service.UmsMemberService;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.mapper.OmsAfterSaleMapper;
import com.macro.mall.mapper.OmsAfterSaleItemMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * 会员售后管理Controller
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
    @Autowired
    private OmsAfterSaleMapper afterSaleMapper;
    @Autowired
    private OmsAfterSaleItemMapper afterSaleItemMapper;

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
    public CommonResult<PortalOmsAfterSaleDetail> getItem(@RequestParam Long id) {
        UmsMember currentMember = memberService.getCurrentMember();
        PortalOmsAfterSaleDetail afterSaleDetail = afterSaleService.getDetail(id, currentMember.getId());
        return CommonResult.success(afterSaleDetail);
    }

    @ApiOperation("查询售后申请列表")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<CommonPage<PortalOmsAfterSaleDTO>> list(
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer pageSize,
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "orderId", required = false) Long orderId) {

        UmsMember currentMember = memberService.getCurrentMember();

        // 如果status为-1，表示查询全部状态，设置为null传递给Service层
        if (status != null && status == -1) {
            status = null;
        }

        System.out.println("查询售后列表: memberId=" + currentMember.getId() +
                ", status=" + status + ", orderId=" + orderId);

        List<PortalOmsAfterSaleDetail> afterSaleDetailList;
        List<PortalOmsAfterSaleDTO> resultList;

        if (orderId != null) {
            // 如果指定了订单ID，通过订单ID查询售后记录
            afterSaleDetailList = afterSaleService.listByOrderId(orderId, currentMember.getId());
            resultList = new ArrayList<>();

            // 转换PortalOmsAfterSaleDetail为PortalOmsAfterSaleDTO
            if (afterSaleDetailList != null) {
                for (PortalOmsAfterSaleDetail detail : afterSaleDetailList) {
                    PortalOmsAfterSaleDTO dto = new PortalOmsAfterSaleDTO();
                    BeanUtils.copyProperties(detail, dto);
                    dto.setAfterSaleItemList(detail.getAfterSaleItemList());
                    dto.setProofList(detail.getProofList());

                    // 初始化计算字段
                    dto.initializeCalculatedFields();

                    resultList.add(dto);
                }
            }

            // 如果status参数存在且大于等于0，进行状态过滤
            if (status != null && status >= 0) {
                // 在内存中根据状态过滤结果
                List<PortalOmsAfterSaleDTO> filteredList = new ArrayList<>();
                for (PortalOmsAfterSaleDTO afterSale : resultList) {
                    if (afterSale.getStatus() != null && afterSale.getStatus().equals(status)) {
                        filteredList.add(afterSale);
                    }
                }
                resultList = filteredList;
            }
        } else {
            // 没有指定订单ID，按状态查询所有售后记录
            resultList = afterSaleService.list(currentMember.getId(), status, pageSize, pageNum);
        }

        System.out.println("查询结果数量: " + (resultList != null ? resultList.size() : 0));

        return CommonResult.success(CommonPage.restPage(resultList));
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

        // 查询该订单的售后申请记录 - 使用Service方法
        List<PortalOmsAfterSaleDetail> afterSaleDetailList = afterSaleService.listByOrderId(orderId,
                currentMember.getId());

        // 重置每个订单项的已申请数量
        Map<Long, Integer> appliedQuantityMap = new HashMap<>();
        for (PortalOmsAfterSaleDetail afterSale : afterSaleDetailList) {
            // 如果售后申请已被取消或拒绝，不计入已申请数量
            if (afterSale.getStatus() == 3) {
                continue;
            }

            // 查询该售后申请包含的商品项 - 直接从 DTO 获取，不再调用 Service
            List<OmsAfterSaleItem> afterSaleItems = afterSale.getAfterSaleItemList();

            // 确保 afterSaleItems 不为 null
            if (afterSaleItems == null) {
                afterSaleItems = new ArrayList<>();
            }

            for (OmsAfterSaleItem item : afterSaleItems) {
                Long orderItemId = item.getOrderItemId();
                Integer returnQuantity = item.getReturnQuantity();

                // 累加已申请数量
                if (orderItemId != null && returnQuantity != null) {
                    appliedQuantityMap.put(orderItemId,
                            appliedQuantityMap.getOrDefault(orderItemId, 0) + returnQuantity);
                }
            }
        }

        // 遍历订单项，计算已申请数量和可申请数量
        for (OmsOrderItem item : orderItems) {
            // 从计算的映射中获取已申请数量，而不是使用数据库中存储的值
            Integer productQuantity = item.getProductQuantity() == null ? 0 : item.getProductQuantity();
            Integer appliedQuantity = appliedQuantityMap.getOrDefault(item.getId(), 0);

            // 确保已申请数量不超过总数量
            appliedQuantity = Math.min(appliedQuantity, productQuantity);

            // 更新订单项的已申请数量（如果不一致）
            if (!appliedQuantity.equals(item.getAppliedQuantity())) {
                OmsOrderItem updateItem = new OmsOrderItem();
                updateItem.setId(item.getId());
                updateItem.setAppliedQuantity(appliedQuantity);
                orderItemMapper.updateByPrimaryKeySelective(updateItem);
            }

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
        result.put("isApplied", afterSaleStatus > 0); // 是否已申请（部分或全部）
        result.put("items", itemStatusList);

        return CommonResult.success(result);
    }

    /**
     * 公共API，用于前端检查订单售后状态
     * 与会员API相同，但放在公共路径下，方便前端集成
     */
    @ApiOperation("公共API-检查订单售后状态")
    @RequestMapping(value = "/afterSale/checkOrderAfterSaleStatus", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> checkOrderAfterSaleStatusPublic(@RequestParam Long orderId) {
        try {
            UmsMember currentMember = memberService.getCurrentMember();
            if (currentMember == null) {
                Map<String, Object> result = new HashMap<>();
                result.put("message", "请先登录");
                return CommonResult.unauthorized(result);
            }

            // 复用会员API的逻辑
            return checkOrderAfterSaleStatus(orderId);
        } catch (Exception e) {
            System.out.println("检查订单售后状态异常: " + e.getMessage());
            e.printStackTrace();
            return CommonResult.failed("检查订单售后状态失败: " + e.getMessage());
        }
    }

    /**
     * 检查售后单是否可以寄回商品
     */
    @RequestMapping(value = "/checkReturnShipStatus", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> checkReturnShipStatus(@RequestParam Long afterSaleId) {
        UmsMember currentMember = memberService.getCurrentMember();
        Map<String, Object> result = afterSaleService.checkReturnShipStatus(afterSaleId, currentMember.getId());
        return CommonResult.success(result);
    }

    /**
     * 提交寄回物流信息
     */
    @RequestMapping(value = "/submitReturnShipping", method = RequestMethod.POST)
    @ResponseBody
    public CommonResult submitReturnShipping(@RequestBody OmsAfterSaleLogistics logistics) {
        UmsMember currentMember = memberService.getCurrentMember();
        int count = afterSaleService.submitReturnShipping(logistics, currentMember.getId());
        if (count > 0) {
            return CommonResult.success(count);
        }
        return CommonResult.failed("提交失败");
    }

    /**
     * 获取寄回商品所需的信息，包含收件人信息和寄件人信息
     */
    @ApiOperation("获取寄回商品信息")
    @RequestMapping(value = "/getReturnShippingInfo", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<Map<String, Object>> getReturnShippingInfo(@RequestParam Long afterSaleId) {
        UmsMember currentMember = memberService.getCurrentMember();
        Map<String, Object> result = afterSaleService.getReturnShippingInfo(afterSaleId, currentMember.getId());
        return CommonResult.success(result);
    }

    /**
     * 获取常用物流公司列表
     */
    @ApiOperation("获取物流公司列表")
    @RequestMapping(value = "/logisticsCompanies", method = RequestMethod.GET)
    @ResponseBody
    public CommonResult<List<Map<String, Object>>> getLogisticsCompanies() {
        List<Map<String, Object>> companies = afterSaleService.getLogisticsCompanies();
        return CommonResult.success(companies);
    }
}