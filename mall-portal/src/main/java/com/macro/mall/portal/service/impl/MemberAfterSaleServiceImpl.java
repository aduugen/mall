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

        // 设置订单编号
        if (order != null && order.getOrderSn() != null && !order.getOrderSn().isEmpty()) {
            afterSale.setOrderSn(order.getOrderSn());
            System.out.println("创建售后单时设置订单编号: " + order.getOrderSn());
        } else {
            afterSale.setOrderSn("订单" + afterSaleParam.getOrderId());
            System.out.println("创建售后单时设置默认订单编号: " + afterSale.getOrderSn());
        }

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
        // 启用分页
        PageHelper.startPage(pageNum, pageSize);

        // 由于OmsAfterSaleExample不存在，我们需要使用现有的方法
        // 获取所有当前状态的售后记录，然后在内存中过滤会员ID
        List<OmsAfterSale> allAfterSalesByStatus;
        if (status != null && status >= 0) {
            // 只查询指定状态的记录
            allAfterSalesByStatus = afterSaleMapper.selectByStatus(status);
            System.out.println("查询指定状态的售后申请: status=" + status + ", 结果数量=" +
                    (allAfterSalesByStatus != null ? allAfterSalesByStatus.size() : 0));
        } else {
            // 查询所有状态的记录
            List<OmsAfterSale> statusList = new ArrayList<>();
            // 依次查询各个状态的记录
            for (int i = 0; i <= 3; i++) {
                List<OmsAfterSale> statusRecords = afterSaleMapper.selectByStatus(i);
                if (statusRecords != null) {
                    statusList.addAll(statusRecords);
                    System.out.println("查询状态" + i + "的售后申请结果数量: " + statusRecords.size());
                }
            }
            allAfterSalesByStatus = statusList;
            System.out.println("查询所有状态的售后申请总数量: " + allAfterSalesByStatus.size());
        }

        // 在结果中过滤会员ID
        List<OmsAfterSale> afterSaleList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(allAfterSalesByStatus)) {
            for (OmsAfterSale afterSale : allAfterSalesByStatus) {
                if (afterSale.getMemberId() != null && afterSale.getMemberId().equals(memberId)) {
                    // 确保状态匹配（双重检查）
                    if (status == null || status < 0 || afterSale.getStatus().equals(status)) {
                        afterSaleList.add(afterSale);
                    }
                }
            }
        }

        // 为每个售后申请关联查询售后商品项
        if (!CollectionUtils.isEmpty(afterSaleList)) {
            for (OmsAfterSale afterSale : afterSaleList) {
                // 查询售后商品项
                OmsAfterSaleItemExample itemExample = new OmsAfterSaleItemExample();
                itemExample.createCriteria().andAfterSaleIdEqualTo(afterSale.getId());
                List<OmsAfterSaleItem> afterSaleItems = afterSaleItemMapper.selectByExample(itemExample);

                // 设置到售后申请对象中
                afterSale.setAfterSaleItemList(afterSaleItems);

                // 查询订单信息获取订单编号
                if (afterSale.getOrderId() != null) {
                    OmsOrder order = orderMapper.selectByPrimaryKey(afterSale.getOrderId());
                    if (order != null && order.getOrderSn() != null && !order.getOrderSn().isEmpty()) {
                        afterSale.setOrderSn(order.getOrderSn());
                        System.out.println("设置售后单 " + afterSale.getId() + " 的订单编号: " + order.getOrderSn() + ", 来自订单ID: "
                                + afterSale.getOrderId());
                    } else {
                        // 找不到订单或订单没有编号，设置默认值
                        afterSale.setOrderSn("订单" + afterSale.getOrderId());
                        System.out
                                .println("警告: 售后单 " + afterSale.getId() + " 订单编号未找到，使用默认值: " + afterSale.getOrderSn());
                    }
                } else {
                    afterSale.setOrderSn("未关联订单");
                    System.out.println("警告: 售后单 " + afterSale.getId() + " 无关联订单ID");
                }
            }
        }

        System.out.println("查询售后列表: memberId=" + memberId + ", status=" + status + ", 结果数量="
                + (afterSaleList != null ? afterSaleList.size() : 0));
        return afterSaleList;
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

        // 记录日志
        System.out.println("用户取消售后申请: afterSaleId=" + id +
                ", memberId=" + memberId +
                ", orderSn=" + afterSale.getOrderSn());

        // 更新订单的售后状态
        if (afterSale.getOrderId() != null) {
            updateOrderAfterSaleStatus(afterSale.getOrderId());
        }

        try {
            // 删除售后申请项 - 先删除子表数据
            int itemCount = afterSaleItemMapper.deleteByExample(itemExample);
            System.out.println("已删除售后申请项: " + itemCount + "条");

            // 检查是否还有其他售后项关联到该售后申请
            OmsAfterSaleItemExample checkExample = new OmsAfterSaleItemExample();
            checkExample.createCriteria().andAfterSaleIdEqualTo(id);
            long remainingItemCount = afterSaleItemMapper.countByExample(checkExample);

            // 只有当所有关联项都被删除后，才删除主表记录
            if (remainingItemCount == 0) {
                // 删除售后申请 - 后删除主表数据
                int result = afterSaleMapper.deleteByPrimaryKey(id);
                System.out.println("已删除售后申请: afterSaleId=" + id + ", 结果=" + result);
                return result;
            } else {
                System.out.println("警告：售后申请项未完全删除，仍有" + remainingItemCount + "条记录，不删除售后申请主记录");
                // 这里可以选择抛出异常回滚事务
                Asserts.fail("售后申请项删除不完全，无法完成取消操作");
                return 0;
            }
        } catch (Exception e) {
            // 捕获异常并记录日志，然后重新抛出以触发事务回滚
            System.err.println("取消售后申请过程中发生异常: " + e.getMessage());
            e.printStackTrace();
            throw e; // 重新抛出异常以确保事务回滚
        }
    }

    @Override
    public List<OmsAfterSale> listByOrderId(Long orderId, Long memberId) {
        // 查询指定订单和会员的所有售后申请，且未被删除的
        List<OmsAfterSale> afterSaleList = new ArrayList<>();

        try {
            // 先查询订单，获取订单编号
            String orderSn = null;
            OmsOrder order = null;
            if (orderId != null) {
                order = orderMapper.selectByPrimaryKey(orderId);
                if (order != null) {
                    orderSn = order.getOrderSn();
                    System.out.println("查询到订单信息: 订单ID=" + orderId + ", 订单编号=" + orderSn);
                } else {
                    System.out.println("警告: 找不到订单ID=" + orderId + "的订单信息");
                }
            }

            // 查询所有状态的记录，然后过滤
            List<OmsAfterSale> statusList = new ArrayList<>();
            // 依次查询各个状态的记录
            for (int i = 0; i <= 3; i++) {
                List<OmsAfterSale> statusRecords = afterSaleMapper.selectByStatus(i);
                if (statusRecords != null) {
                    statusList.addAll(statusRecords);
                    System.out.println("订单ID=" + orderId + "查询状态" + i + "的售后申请结果数量: " + statusRecords.size());
                }
            }

            // 在结果中过滤订单ID和会员ID，以及删除状态
            if (!CollectionUtils.isEmpty(statusList)) {
                for (OmsAfterSale afterSale : statusList) {
                    if (afterSale.getOrderId() != null && afterSale.getOrderId().equals(orderId) &&
                            afterSale.getMemberId() != null && afterSale.getMemberId().equals(memberId)) {
                        // 设置订单编号
                        if (orderSn != null) {
                            afterSale.setOrderSn(orderSn);
                            System.out.println("设置售后单 " + afterSale.getId() + " 的订单编号(来自预查询): " + orderSn);
                        } else if (afterSale.getOrderId() != null) {
                            // 如果上面没有获取到订单编号，单独查询
                            if (order == null) {
                                order = orderMapper.selectByPrimaryKey(afterSale.getOrderId());
                            }

                            if (order != null && order.getOrderSn() != null) {
                                afterSale.setOrderSn(order.getOrderSn());
                                System.out
                                        .println("设置售后单 " + afterSale.getId() + " 的订单编号(单独查询): " + order.getOrderSn());
                            } else {
                                // 找不到订单或订单没有编号，设置默认值
                                afterSale.setOrderSn("订单" + afterSale.getOrderId());
                                System.out.println(
                                        "警告: 售后单 " + afterSale.getId() + " 订单编号未找到，使用默认值: " + afterSale.getOrderSn());
                            }
                        } else {
                            afterSale.setOrderSn("未关联订单");
                            System.out.println("警告: 售后单 " + afterSale.getId() + " 无关联订单");
                        }

                        // 查询售后商品项
                        afterSale.setAfterSaleItemList(getAfterSaleItems(afterSale.getId()));

                        // 添加到结果列表
                        afterSaleList.add(afterSale);
                    }
                }
            }

            System.out.println("查询订单的售后列表: orderId=" + orderId + ", memberId=" + memberId +
                    ", 结果数量=" + afterSaleList.size());
        } catch (Exception e) {
            System.out.println("查询订单售后列表异常: " + e.getMessage());
            e.printStackTrace();
        }

        return afterSaleList;
    }

    @Override
    public List<OmsAfterSaleItem> getAfterSaleItems(Long afterSaleId) {
        if (afterSaleId == null) {
            return new ArrayList<>();
        }

        // 查询售后商品项
        OmsAfterSaleItemExample itemExample = new OmsAfterSaleItemExample();
        itemExample.createCriteria().andAfterSaleIdEqualTo(afterSaleId);
        try {
            List<OmsAfterSaleItem> afterSaleItems = afterSaleItemMapper.selectByExample(itemExample);
            return afterSaleItems != null ? afterSaleItems : new ArrayList<>();
        } catch (Exception e) {
            System.out.println("查询售后商品项异常: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}