package com.macro.mall.service.impl;

import com.macro.mall.mapper.OmsAfterSaleMapper;
import com.macro.mall.mapper.OmsAfterSaleItemMapper;
import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.model.*;
import com.macro.mall.service.OmsAfterSaleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;

@Service
public class OmsAfterSaleServiceImpl implements OmsAfterSaleService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OmsAfterSaleServiceImpl.class);

    @Autowired
    private OmsAfterSaleMapper afterSaleMapper;

    @Autowired
    private OmsAfterSaleItemMapper afterSaleItemMapper;

    @Autowired
    private OmsOrderItemMapper orderItemMapper;

    @Autowired
    private OmsOrderMapper orderMapper;

    @Override
    public int create(OmsAfterSale afterSale) {
        afterSale.setStatus(0);
        afterSale.setCreateTime(new Date());
        afterSale.setUpdateTime(new Date());
        return afterSaleMapper.insert(afterSale);
    }

    @Override
    public OmsAfterSale getItem(Long id) {
        return afterSaleMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<OmsAfterSale> list(Integer status, Integer pageSize, Integer pageNum) {
        // 暂时只支持按状态查询,不支持分页
        return afterSaleMapper.selectByStatus(status);
    }

    @Override
    public int updateStatus(Long id, Integer status, String handleNote) {
        OmsAfterSale afterSale = new OmsAfterSale();
        afterSale.setId(id);
        afterSale.setStatus(status);
        afterSale.setHandleNote(handleNote);
        afterSale.setHandleTime(new Date());
        afterSale.setUpdateTime(new Date());
        return afterSaleMapper.updateByPrimaryKeySelective(afterSale);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int cancel(Long id) {
        // 查询售后申请以获取相关信息
        OmsAfterSale afterSale = afterSaleMapper.selectByPrimaryKey(id);
        if (afterSale == null) {
            LOGGER.warn("取消售后申请失败：售后申请不存在, id={}", id);
            return 0;
        }

        // 状态验证：仅允许取消待处理(0)状态的售后申请
        if (afterSale.getStatus() != 0) {
            LOGGER.warn("取消售后申请失败：当前状态不允许取消, id={}, status={}", id, afterSale.getStatus());
            return 0;
        }

        // 记录日志
        LOGGER.info("管理员取消售后申请: afterSaleId={}, orderSn={}, orderId={}",
                id, afterSale.getOrderSn(), afterSale.getOrderId());

        try {
            // 查询售后申请项
            OmsAfterSaleItemExample itemExample = new OmsAfterSaleItemExample();
            itemExample.createCriteria().andAfterSaleIdEqualTo(id);
            List<OmsAfterSaleItem> afterSaleItems = afterSaleItemMapper.selectByExample(itemExample);

            // 恢复订单项的已申请数量
            if (!CollectionUtils.isEmpty(afterSaleItems)) {
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
                            LOGGER.info("恢复订单项已申请数量: 订单项ID={}, 新已申请数量={}",
                                    orderItem.getId(), appliedQuantity);
                        }
                    }
                }
            }

            // 更新订单的售后状态
            if (afterSale.getOrderId() != null) {
                updateOrderAfterSaleStatus(afterSale.getOrderId());
            }

            // 删除售后申请项
            int itemCount = afterSaleItemMapper.deleteByExample(itemExample);
            LOGGER.info("已删除售后申请项: {}条", itemCount);

            // 删除售后申请
            int result = afterSaleMapper.deleteByPrimaryKey(id);
            LOGGER.info("已删除售后申请: afterSaleId={}, 结果={}", id, result);

            return result;
        } catch (Exception e) {
            LOGGER.error("取消售后申请过程中发生异常: afterSaleId=" + id, e);
            throw e; // 抛出异常以触发事务回滚
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

        LOGGER.info("更新订单售后状态: 订单ID={}, 新售后状态={}", orderId, afterSaleStatus);
    }
}