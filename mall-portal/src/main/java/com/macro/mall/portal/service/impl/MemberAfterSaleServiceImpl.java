package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.OmsAfterSaleItemMapper;
import com.macro.mall.mapper.OmsAfterSaleMapper;
import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.model.*;
import com.macro.mall.portal.domain.AfterSaleItemParam;
import com.macro.mall.portal.domain.AfterSaleParam;
import com.macro.mall.portal.service.MemberAfterSaleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 会员售后Service实现类
 */
@Service
public class MemberAfterSaleServiceImpl implements MemberAfterSaleService {
    @Autowired
    private OmsAfterSaleMapper afterSaleMapper;

    @Autowired
    private OmsAfterSaleItemMapper afterSaleItemMapper;

    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private OmsOrderItemMapper orderItemMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int create(AfterSaleParam afterSaleParam) {
        // 增加日志记录
        System.out.println("===== 开始创建售后申请 =====");
        System.out.println("售后参数: " + afterSaleParam);

        // 验证订单是否存在
        OmsOrder order = orderMapper.selectByPrimaryKey(afterSaleParam.getOrderId());
        if (order == null) {
            System.out.println("订单不存在: orderId=" + afterSaleParam.getOrderId());
            Asserts.fail("订单不存在");
        }
        System.out.println("订单验证通过: orderId=" + order.getId());

        // 验证订单项
        List<AfterSaleItemParam> itemParams = afterSaleParam.getItems();
        if (CollectionUtils.isEmpty(itemParams)) {
            System.out.println("未选择退货商品");
            Asserts.fail("请选择退货商品");
        }
        System.out.println("售后商品数量: " + itemParams.size());

        // 保存售后主信息
        OmsAfterSale afterSale = new OmsAfterSale();
        afterSale.setOrderId(afterSaleParam.getOrderId());
        afterSale.setMemberId(afterSaleParam.getMemberId());

        // 设置初始状态为待处理
        afterSale.setStatus(0);
        afterSale.setCreateTime(new Date());
        afterSale.setUpdateTime(new Date());

        // 打印debug信息
        System.out.println("准备插入售后申请记录: " + afterSale);

        try {
            int count = afterSaleMapper.insert(afterSale);
            System.out.println("售后申请记录插入结果: " + count + ", 生成ID: " + afterSale.getId());

            // 保存售后商品项
            List<OmsAfterSaleItem> afterSaleItems = new ArrayList<>();
            for (AfterSaleItemParam itemParam : itemParams) {
                // 验证订单项数量
                OmsOrderItemExample example = new OmsOrderItemExample();
                example.createCriteria().andIdEqualTo(itemParam.getOrderItemId())
                        .andOrderIdEqualTo(afterSaleParam.getOrderId());
                List<OmsOrderItem> orderItems = orderItemMapper.selectByExample(example);

                if (CollectionUtils.isEmpty(orderItems)) {
                    System.out.println("订单项不存在: orderItemId=" + itemParam.getOrderItemId());
                    Asserts.fail("订单项不存在");
                }

                OmsOrderItem orderItem = orderItems.get(0);

                // 检查可申请数量 = 购买数量 - 已申请数量
                Integer appliedQuantity = orderItem.getAppliedQuantity() == null ? 0 : orderItem.getAppliedQuantity();
                Integer availableQuantity = orderItem.getProductQuantity() - appliedQuantity;

                if (itemParam.getReturnQuantity() > availableQuantity) {
                    System.out.println(
                            "退货数量错误: 退货=" + itemParam.getReturnQuantity() +
                                    ", 可申请=" + availableQuantity +
                                    ", 已申请=" + appliedQuantity +
                                    ", 购买=" + orderItem.getProductQuantity());
                    Asserts.fail("退货数量不能大于可申请数量: " + orderItem.getProductName());
                }

                // 创建售后商品项
                OmsAfterSaleItem afterSaleItem = new OmsAfterSaleItem();
                BeanUtils.copyProperties(itemParam, afterSaleItem);
                afterSaleItem.setAfterSaleId(afterSale.getId());
                afterSaleItem.setCreateTime(new Date());
                afterSaleItem.setProductQuantity(orderItem.getProductQuantity()); // 设置购买数量，便于后续处理

                // 确保每个商品项的退货原因不为空
                if (afterSaleItem.getReturnReason() == null || afterSaleItem.getReturnReason().trim().isEmpty()) {
                    afterSaleItem.setReturnReason("商品退货");
                }

                System.out.println("插入售后商品项: " + afterSaleItem);
                afterSaleItemMapper.insert(afterSaleItem);
                afterSaleItems.add(afterSaleItem);

                // 更新订单项的已申请数量
                appliedQuantity += itemParam.getReturnQuantity();
                orderItem.setAppliedQuantity(appliedQuantity);
                orderItemMapper.updateByPrimaryKeySelective(orderItem);
                System.out.println("更新订单项已申请数量: 订单项ID=" + orderItem.getId() +
                        ", 新已申请数量=" + appliedQuantity);
            }

            // 设置售后商品列表，便于返回给前端
            afterSale.setAfterSaleItemList(afterSaleItems);

            // 更新订单的售后状态
            updateOrderAfterSaleStatus(afterSaleParam.getOrderId());

            System.out.println("===== 售后申请创建成功 =====");

            return count;
        } catch (Exception e) {
            System.out.println("===== 售后申请创建异常 =====");
            System.out.println("异常类型: " + e.getClass().getName());
            System.out.println("异常信息: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 更新订单的售后状态
     * 
     * @param orderId 订单ID
     */
    private void updateOrderAfterSaleStatus(Long orderId) {
        // 获取订单所有订单项
        OmsOrderItemExample itemExample = new OmsOrderItemExample();
        itemExample.createCriteria().andOrderIdEqualTo(orderId);
        List<OmsOrderItem> orderItems = orderItemMapper.selectByExample(itemExample);

        boolean allApplied = true;
        boolean partialApplied = false;

        for (OmsOrderItem item : orderItems) {
            // 确保applied_quantity字段不为null
            Integer appliedQuantity = item.getAppliedQuantity() == null ? 0 : item.getAppliedQuantity();
            Integer productQuantity = item.getProductQuantity() == null ? 0 : item.getProductQuantity();

            // 判断是否全部申请
            if (appliedQuantity < productQuantity) {
                allApplied = false;
            }

            // 判断是否部分申请
            if (appliedQuantity > 0) {
                partialApplied = true;
            }
        }

        // 根据判断结果设置售后状态
        byte afterSaleStatus;
        if (allApplied) {
            afterSaleStatus = 2; // 全部申请
        } else if (partialApplied) {
            afterSaleStatus = 1; // 部分申请
        } else {
            afterSaleStatus = 0; // 未申请
        }

        // 更新订单售后状态
        OmsOrder order = new OmsOrder();
        order.setId(orderId);
        order.setAfterSaleStatus(afterSaleStatus);
        orderMapper.updateByPrimaryKeySelective(order);

        System.out.println("更新订单售后状态: 订单ID=" + orderId + ", 新售后状态=" + afterSaleStatus);
    }

    @Override
    public OmsAfterSale getDetail(Long id, Long memberId) {
        OmsAfterSale afterSale = afterSaleMapper.selectByPrimaryKey(id);
        if (afterSale == null) {
            Asserts.fail("售后申请不存在");
        }

        // 验证是否是当前会员的申请
        if (!afterSale.getMemberId().equals(memberId)) {
            Asserts.fail("无权查看该售后申请");
        }

        // 查询售后商品项
        OmsAfterSaleItemExample example = new OmsAfterSaleItemExample();
        example.createCriteria().andAfterSaleIdEqualTo(id);
        List<OmsAfterSaleItem> afterSaleItems = afterSaleItemMapper.selectByExample(example);
        afterSale.setAfterSaleItemList(afterSaleItems);

        return afterSale;
    }

    @Override
    public List<OmsAfterSale> list(Long memberId, Integer status, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        // 需要按会员ID查询，这里实现可能不完整
        // 这里应该从关联的订单中查询当前会员的售后申请
        if (status != null) {
            return afterSaleMapper.selectByStatus(status);
        } else {
            // 如果没有提供status，需要查询所有状态的申请
            // 由于我们当前简化版本的Mapper没有提供查询所有的方法，这里直接返回空列表
            return afterSaleMapper.selectByStatus(0); // 临时解决方案
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancel(Long id, Long memberId) {
        OmsAfterSale afterSale = afterSaleMapper.selectByPrimaryKey(id);
        if (afterSale == null) {
            Asserts.fail("售后申请不存在");
        }
        // 验证是否是当前会员的申请
        if (!afterSale.getMemberId().equals(memberId)) {
            Asserts.fail("无权操作该售后申请");
        }

        // 只有待处理状态的售后申请才能取消
        if (afterSale.getStatus() != 0) {
            Asserts.fail("当前售后申请状态不允许取消");
        }

        // 查询售后商品项
        OmsAfterSaleItemExample itemExample = new OmsAfterSaleItemExample();
        itemExample.createCriteria().andAfterSaleIdEqualTo(id);
        List<OmsAfterSaleItem> afterSaleItems = afterSaleItemMapper.selectByExample(itemExample);

        // 恢复订单项的已申请数量
        for (OmsAfterSaleItem afterSaleItem : afterSaleItems) {
            // 获取订单项
            OmsOrderItem orderItem = orderItemMapper.selectByPrimaryKey(afterSaleItem.getOrderItemId());
            if (orderItem != null) {
                // 减少已申请数量
                Integer appliedQuantity = orderItem.getAppliedQuantity();
                if (appliedQuantity != null && appliedQuantity > 0) {
                    appliedQuantity = Math.max(0, appliedQuantity - afterSaleItem.getReturnQuantity());
                    orderItem.setAppliedQuantity(appliedQuantity);
                    orderItemMapper.updateByPrimaryKeySelective(orderItem);
                    System.out.println("恢复订单项已申请数量: 订单项ID=" + orderItem.getId() +
                            ", 新已申请数量=" + appliedQuantity);
                }
            }
        }

        // 更新售后申请状态为已取消（或其他状态码）
        afterSale.setStatus(3); // 假设3表示已取消
        afterSale.setUpdateTime(new Date());

        // 更新订单的售后状态
        if (afterSale.getOrderId() != null) {
            updateOrderAfterSaleStatus(afterSale.getOrderId());
        }

        return afterSaleMapper.updateByPrimaryKeySelective(afterSale);
    }
}