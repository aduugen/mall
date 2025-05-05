package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.OmsAfterSaleItemMapper;
import com.macro.mall.mapper.OmsAfterSaleMapper;
import com.macro.mall.mapper.OmsAfterSaleProofMapper;
import com.macro.mall.mapper.OmsAfterSaleProcessMapper;
import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.OmsAfterSaleLogisticsMapper;
import com.macro.mall.mapper.PtnServicePointMapper;
import com.macro.mall.mapper.OmsAfterSaleLogMapper;
import com.macro.mall.model.*;
import com.macro.mall.portal.domain.AfterSaleItemParam;
import com.macro.mall.portal.domain.AfterSaleParam;
import com.macro.mall.portal.domain.PortalOmsAfterSaleDetail;
import com.macro.mall.portal.domain.PortalOmsAfterSaleDTO;
import com.macro.mall.portal.service.MemberAfterSaleService;
import com.macro.mall.portal.service.UmsMemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.Map;
import java.util.HashMap;

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

    @Autowired
    private UmsMemberService memberService;

    @Autowired
    private OmsAfterSaleProofMapper proofMapper;

    @Autowired
    private OmsAfterSaleProcessMapper afterSaleProcessMapper;

    @Autowired
    private OmsAfterSaleLogisticsMapper afterSaleLogisticsMapper;

    @Autowired
    private PtnServicePointMapper servicePointMapper;

    @Autowired
    private OmsAfterSaleLogMapper afterSaleLogMapper;

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
        afterSale.setDelFlag(0);
        afterSale.setVersion(0);

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
                afterSaleItem.setUpdateTime(new Date()); // 设置更新时间，默认与创建时间相同
                afterSaleItem.setDelFlag(0); // 设置删除标记为0（未删除）
                afterSaleItem.setVersion(0); // 设置版本号为0
                afterSaleItem.setProductQuantity(orderItem.getProductQuantity()); // 设置购买数量，便于后续处理

                // 确保每个商品项的退货原因不为空
                if (afterSaleItem.getReturnReason() == null || afterSaleItem.getReturnReason().trim().isEmpty()) {
                    afterSaleItem.setReturnReason("商品退货");
                }

                // 从订单项获取价格信息 (及其他可能需要冗余的信息)
                afterSaleItem.setProductPrice(orderItem.getProductPrice()); // 商品原单价
                afterSaleItem.setProductRealPrice(orderItem.getRealAmount()); // 商品实付单价 (修正：使用 realAmount)
                // 可以考虑也冗余 product_id, sku_id, sku_code 等，如果 ItemParam 没有传递的话
                if (afterSaleItem.getProductId() == null)
                    afterSaleItem.setProductId(orderItem.getProductId());
                if (afterSaleItem.getProductName() == null)
                    afterSaleItem.setProductName(orderItem.getProductName());
                if (afterSaleItem.getProductPic() == null)
                    afterSaleItem.setProductPic(orderItem.getProductPic());
                if (afterSaleItem.getProductBrand() == null)
                    afterSaleItem.setProductBrand(orderItem.getProductBrand());
                if (afterSaleItem.getProductAttr() == null)
                    afterSaleItem.setProductAttr(orderItem.getProductAttr());
                if (afterSaleItem.getProductSkuId() == null)
                    afterSaleItem.setProductSkuId(orderItem.getProductSkuId());
                if (afterSaleItem.getProductSkuCode() == null)
                    afterSaleItem.setProductSkuCode(orderItem.getProductSkuCode());

                System.out.println("插入售后商品项: " + afterSaleItem);
                afterSaleItemMapper.insert(afterSaleItem);
                afterSaleItems.add(afterSaleItem);

                // 处理凭证图片
                if (StringUtils.hasText(itemParam.getProofPics())) {
                    List<OmsAfterSaleProof> proofList = new ArrayList<>();
                    String[] picUrls = itemParam.getProofPics().split(",");
                    for (String picUrl : picUrls) {
                        if (StringUtils.hasText(picUrl)) {
                            OmsAfterSaleProof proof = new OmsAfterSaleProof();
                            proof.setAfterSaleId(afterSale.getId());
                            proof.setItemId(afterSaleItem.getId());
                            proof.setPicUrl(picUrl.trim());
                            proof.setPicType(OmsAfterSaleProof.PIC_TYPE_PROOF);
                            proof.setCreateTime(new Date());
                            proof.setUpdateTime(new Date());
                            proofList.add(proof);
                        }
                    }
                    if (!proofList.isEmpty()) {
                        proofMapper.batchInsert(proofList);
                        System.out.println("插入售后商品项凭证图片: 商品项ID=" + afterSaleItem.getId() +
                                ", 凭证数量=" + proofList.size());
                    }
                }

                // 更新订单项的已申请数量
                appliedQuantity += itemParam.getReturnQuantity();
                orderItem.setAppliedQuantity(appliedQuantity);
                orderItemMapper.updateByPrimaryKeySelective(orderItem);
                System.out.println("更新订单项已申请数量: 订单项ID=" + orderItem.getId() +
                        ", 新已申请数量=" + appliedQuantity);
            }

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
    public PortalOmsAfterSaleDetail getDetail(Long id, Long memberId) {
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

        // 使用DTO返回更多信息
        PortalOmsAfterSaleDetail detail = new PortalOmsAfterSaleDetail();
        BeanUtils.copyProperties(afterSale, detail);
        detail.setAfterSaleItemList(afterSaleItems);

        // 查询凭证图片
        List<OmsAfterSaleProof> proofList = proofMapper.selectByAfterSaleId(id);
        detail.setProofList(proofList);

        // 查询售后处理记录
        OmsAfterSaleProcessExample processExample = new OmsAfterSaleProcessExample();
        processExample.createCriteria().andAfterSaleIdEqualTo(id);
        processExample.setOrderByClause("create_time ASC"); // 按创建时间升序排序
        List<OmsAfterSaleProcess> processList = afterSaleProcessMapper.selectByExample(processExample);
        detail.setProcessList(processList);

        return detail;
    }

    @Override
    public List<PortalOmsAfterSaleDTO> list(Long memberId, Integer status, Integer pageSize, Integer pageNum) {
        // 启用分页
        PageHelper.startPage(pageNum, pageSize);

        // 使用 Example 替代 selectByStatus
        OmsAfterSaleExample example = new OmsAfterSaleExample();
        OmsAfterSaleExample.Criteria criteria = example.createCriteria();
        criteria.andMemberIdEqualTo(memberId); // 查询当前会员的

        if (status != null && status >= 0) {
            // 只查询指定状态的记录
            criteria.andStatusEqualTo(status);
            System.out.println("查询指定状态的售后申请: status=" + status);
        } else {
            System.out.println("查询所有状态的售后申请");
            // 如果 status 为 null 或负数，则不添加状态条件，查询所有状态
        }
        example.setOrderByClause("create_time DESC"); // 按创建时间排序

        List<OmsAfterSale> afterSaleList = afterSaleMapper.selectByExample(example); // 使用 selectByExample
        List<PortalOmsAfterSaleDTO> resultList = new ArrayList<>();

        // 转换成DTO并为每个售后申请关联查询售后商品项和订单
        if (!CollectionUtils.isEmpty(afterSaleList)) {
            System.out.println("开始处理售后列表的附加信息，将转换为DTO...");
            for (OmsAfterSale afterSale : afterSaleList) {
                PortalOmsAfterSaleDTO dto = new PortalOmsAfterSaleDTO();
                BeanUtils.copyProperties(afterSale, dto);

                // 查询售后商品项
                OmsAfterSaleItemExample itemExample = new OmsAfterSaleItemExample();
                itemExample.createCriteria().andAfterSaleIdEqualTo(afterSale.getId());
                List<OmsAfterSaleItem> afterSaleItems = afterSaleItemMapper.selectByExample(itemExample);
                dto.setAfterSaleItemList(afterSaleItems);

                // 查询售后凭证
                List<OmsAfterSaleProof> proofList = proofMapper.selectByAfterSaleId(afterSale.getId());
                dto.setProofList(proofList);

                // 查询订单信息获取订单编号
                if (afterSale.getOrderId() != null) {
                    OmsOrder order = orderMapper.selectByPrimaryKey(afterSale.getOrderId());
                    if (order != null && order.getOrderSn() != null && !order.getOrderSn().isEmpty()) {
                        dto.setOrderSn(order.getOrderSn());
                        System.out.println("设置售后单 " + afterSale.getId() + " 的订单编号: " + order.getOrderSn());
                    } else {
                        dto.setOrderSn("订单" + afterSale.getOrderId());
                        System.out.println("售后单 " + afterSale.getId() + " 订单编号未找到，使用默认值");
                    }
                } else {
                    dto.setOrderSn("未关联订单");
                    System.out.println("警告: 售后单 " + afterSale.getId() + " 无关联订单ID");
                }

                // 初始化计算字段（总退款金额、主要退货原因等）
                dto.initializeCalculatedFields();

                resultList.add(dto);
            }
        }

        System.out.println("查询售后列表: memberId=" + memberId + ", status=" + status + ", 结果数量="
                + (resultList != null ? resultList.size() : 0));
        return resultList;
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
                ", orderId=" + afterSale.getOrderId()); // 使用 orderId

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
    public List<PortalOmsAfterSaleDetail> listByOrderId(Long orderId, Long memberId) {
        // 查询指定订单和会员的所有售后申请，且未被删除的
        List<PortalOmsAfterSaleDetail> resultList = new ArrayList<>();

        try {
            // 先查询订单，获取订单编号 (如果需要在日志或逻辑中使用)
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

            // 查询所有状态的记录，然后过滤 (使用 Example 替代 selectByStatus)
            OmsAfterSaleExample example = new OmsAfterSaleExample();
            OmsAfterSaleExample.Criteria criteria = example.createCriteria();
            criteria.andOrderIdEqualTo(orderId); // 按订单 ID 查询
            criteria.andMemberIdEqualTo(memberId); // 按会员 ID 查询
            // example.setOrderByClause("create_time DESC"); // 可选：排序

            List<OmsAfterSale> afterSaleList = afterSaleMapper.selectByExample(example); // 查询原始售后列表

            System.out.println("订单ID=" + orderId + " 查询售后申请结果数量: " + afterSaleList.size());

            // --- 新增逻辑：转换并填充 DTO ---
            if (!CollectionUtils.isEmpty(afterSaleList)) {
                for (OmsAfterSale afterSale : afterSaleList) {
                    PortalOmsAfterSaleDetail detail = new PortalOmsAfterSaleDetail();
                    BeanUtils.copyProperties(afterSale, detail); // 复制基础属性

                    // 查询关联的售后商品项
                    List<OmsAfterSaleItem> afterSaleItems = getAfterSaleItems(afterSale.getId());
                    detail.setAfterSaleItemList(afterSaleItems); // 设置商品项列表

                    // 查询凭证图片
                    List<OmsAfterSaleProof> proofList = proofMapper.selectByAfterSaleId(afterSale.getId());
                    detail.setProofList(proofList);

                    resultList.add(detail); // 添加到结果列表
                }
            }
            // --- 新增逻辑结束 ---

            System.out.println("查询订单的售后列表: orderId=" + orderId + ", memberId=" + memberId +
                    ", 结果数量=" + resultList.size());
        } catch (Exception e) {
            System.out.println("查询订单售后列表异常: " + e.getMessage());
            e.printStackTrace();
        }

        return resultList;
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

    @Override
    public Map<String, Object> checkReturnShipStatus(Long afterSaleId, Long memberId) {
        Map<String, Object> result = new HashMap<>();
        boolean canReturn = false;
        String message = "";

        // 1. 验证售后单是否存在且属于当前会员
        OmsAfterSale afterSale = afterSaleMapper.selectByPrimaryKey(afterSaleId);
        if (afterSale == null) {
            message = "售后单不存在";
            result.put("canReturn", canReturn);
            result.put("message", message);
            return result;
        }

        if (!afterSale.getMemberId().equals(memberId)) {
            message = "无权操作该售后单";
            result.put("canReturn", canReturn);
            result.put("message", message);
            return result;
        }

        // 2. 查询处理记录，检查是否有审核通过的记录
        OmsAfterSaleProcessExample example = new OmsAfterSaleProcessExample();
        example.createCriteria()
                .andAfterSaleIdEqualTo(afterSaleId)
                .andProcessTypeEqualTo(1) // 审核类型
                .andProcessResultEqualTo(1); // 审核通过

        List<OmsAfterSaleProcess> processList = afterSaleProcessMapper.selectByExample(example);

        if (!CollectionUtils.isEmpty(processList)) {
            // 有审核通过的记录，可以寄回商品
            canReturn = true;

            // 3. 还需检查是否已有发货记录（避免重复发货）
            OmsAfterSaleProcessExample shipExample = new OmsAfterSaleProcessExample();
            shipExample.createCriteria()
                    .andAfterSaleIdEqualTo(afterSaleId)
                    .andProcessTypeEqualTo(2); // 发货类型

            List<OmsAfterSaleProcess> shipProcessList = afterSaleProcessMapper.selectByExample(shipExample);

            if (!CollectionUtils.isEmpty(shipProcessList)) {
                canReturn = false;
                message = "已寄回商品，无需重复操作";
            } else {
                message = "请按照提示寄回商品";
            }
        } else {
            message = "售后申请尚未审核通过，暂不能寄回商品";
        }

        // 4. 获取服务点信息，用于寄回地址显示
        OmsAfterSaleLogistics logistics = afterSaleLogisticsMapper.selectByAfterSaleId(afterSaleId);
        if (logistics != null && logistics.getServicePointId() != null) {
            PtnServicePoint servicePoint = servicePointMapper.selectByPrimaryKey(logistics.getServicePointId());
            if (servicePoint != null) {
                Map<String, String> addressInfo = new HashMap<>();
                addressInfo.put("name", servicePoint.getContactName());
                addressInfo.put("phone", servicePoint.getContactPhone());
                addressInfo.put("address", servicePoint.getLocationAddress());
                result.put("addressInfo", addressInfo);
            }
        }

        result.put("canReturn", canReturn);
        result.put("message", message);
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int submitReturnShipping(OmsAfterSaleLogistics logistics, Long memberId) {
        // 验证售后单是否存在且属于当前会员
        Long afterSaleId = logistics.getAfterSaleId();
        OmsAfterSale afterSale = afterSaleMapper.selectByPrimaryKey(afterSaleId);
        if (afterSale == null) {
            Asserts.fail("售后单不存在");
        }

        if (!afterSale.getMemberId().equals(memberId)) {
            Asserts.fail("无权操作该售后单");
        }

        // 检查是否有审核通过的记录
        OmsAfterSaleProcessExample example = new OmsAfterSaleProcessExample();
        example.createCriteria()
                .andAfterSaleIdEqualTo(afterSaleId)
                .andProcessTypeEqualTo(1) // 审核类型
                .andProcessResultEqualTo(1); // 审核通过

        List<OmsAfterSaleProcess> processList = afterSaleProcessMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(processList)) {
            Asserts.fail("售后申请尚未审核通过，不能寄回商品");
        }

        // 检查是否已有发货记录
        OmsAfterSaleProcessExample shipExample = new OmsAfterSaleProcessExample();
        shipExample.createCriteria()
                .andAfterSaleIdEqualTo(afterSaleId)
                .andProcessTypeEqualTo(2); // 发货类型

        List<OmsAfterSaleProcess> shipProcessList = afterSaleProcessMapper.selectByExample(shipExample);

        if (!CollectionUtils.isEmpty(shipProcessList)) {
            Asserts.fail("已寄回商品，无需重复操作");
        }

        // 更新或创建物流信息
        OmsAfterSaleLogistics existingLogistics = afterSaleLogisticsMapper.selectByAfterSaleId(afterSaleId);
        int count = 0;

        // 设置物流状态为已发货
        logistics.setLogisticsStatus(1); // 已发货
        logistics.setShippingTime(new Date());

        if (existingLogistics != null) {
            // 更新已有的物流信息
            BeanUtils.copyProperties(logistics, existingLogistics, "id", "createTime", "servicePointId");
            existingLogistics.setUpdateTime(new Date());
            count = afterSaleLogisticsMapper.updateByPrimaryKeySelective(existingLogistics);
        } else {
            // 创建新的物流信息
            logistics.setCreateTime(new Date());
            logistics.setUpdateTime(new Date());
            count = afterSaleLogisticsMapper.insert(logistics);
        }

        // 创建发货处理记录
        OmsAfterSaleProcess shipProcess = new OmsAfterSaleProcess();
        shipProcess.setAfterSaleId(afterSaleId);
        shipProcess.setProcessType(2); // 发货类型
        shipProcess.setProcessResult(1); // 成功
        shipProcess.setHandleNote("用户已寄回商品");
        shipProcess.setHandleTime(new Date());
        shipProcess.setCreateTime(new Date());
        shipProcess.setUpdateTime(new Date());
        shipProcess.setHandleManId(memberId); // 用户操作
        shipProcess.setVersion(0);

        afterSaleProcessMapper.insert(shipProcess);

        // 创建售后日志记录
        OmsAfterSaleLog log = new OmsAfterSaleLog();
        log.setAfterSaleId(afterSaleId);
        log.setOperatorId(memberId);
        log.setOperatorType(OmsAfterSaleLog.OPERATOR_TYPE_MEMBER); // 用户操作
        log.setOperateType(OmsAfterSaleLog.OPERATE_TYPE_SHIP); // 发货
        log.setStatus(afterSale.getStatus()); // 维持当前状态
        log.setNote("用户已寄回商品: " + logistics.getLogisticsCompanyId() + " - " + logistics.getLogisticsNumber());
        log.setCreateTime(new Date());

        afterSaleLogMapper.insert(log);

        return count;
    }

    @Override
    public Map<String, Object> getReturnShippingInfo(Long afterSaleId, Long memberId) {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> receiver = new HashMap<>();
        Map<String, Object> sender = new HashMap<>();

        // 1. 验证售后单是否存在且属于当前会员
        OmsAfterSale afterSale = afterSaleMapper.selectByPrimaryKey(afterSaleId);
        if (afterSale == null) {
            Asserts.fail("售后单不存在");
        }

        if (!afterSale.getMemberId().equals(memberId)) {
            Asserts.fail("无权操作该售后单");
        }

        // 2. 获取收件人信息：从售后物流表获取服务点ID，然后查询服务点表获取地址信息
        OmsAfterSaleLogistics logistics = afterSaleLogisticsMapper.selectByAfterSaleId(afterSaleId);
        if (logistics != null && logistics.getServicePointId() != null) {
            PtnServicePoint servicePoint = servicePointMapper.selectByPrimaryKey(logistics.getServicePointId());
            if (servicePoint != null) {
                receiver.put("name", servicePoint.getContactName());
                receiver.put("phone", servicePoint.getContactPhone());
                receiver.put("address", servicePoint.getLocationAddress());
            }
        }

        // 3. 获取寄件人信息：从售后单获取订单ID，然后查询订单表获取收件人信息作为寄件人
        Long orderId = afterSale.getOrderId();
        if (orderId != null) {
            OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
            if (order != null) {
                sender.put("name", order.getReceiverName());
                sender.put("phone", order.getReceiverPhone());

                // 组合完整地址
                StringBuilder addressBuilder = new StringBuilder();
                if (order.getReceiverProvince() != null) {
                    addressBuilder.append(order.getReceiverProvince()).append(" ");
                }
                if (order.getReceiverCity() != null) {
                    addressBuilder.append(order.getReceiverCity()).append(" ");
                }
                if (order.getReceiverRegion() != null) {
                    addressBuilder.append(order.getReceiverRegion()).append(" ");
                }
                if (order.getReceiverDetailAddress() != null) {
                    addressBuilder.append(order.getReceiverDetailAddress());
                }

                sender.put("address", addressBuilder.toString().trim());
            }
        }

        result.put("receiver", receiver.isEmpty() ? null : receiver);
        result.put("sender", sender.isEmpty() ? null : sender);

        return result;
    }

    @Override
    public List<Map<String, Object>> getLogisticsCompanies() {
        List<Map<String, Object>> companies = new ArrayList<>();

        // 添加常用物流公司
        companies.add(createCompany("1", "顺丰速运", "SF"));
        companies.add(createCompany("2", "圆通速递", "YTO"));
        companies.add(createCompany("3", "中通快递", "ZTO"));
        companies.add(createCompany("4", "申通快递", "STO"));
        companies.add(createCompany("5", "韵达速递", "YD"));
        companies.add(createCompany("6", "邮政快递包裹", "YZPY"));
        companies.add(createCompany("7", "京东物流", "JD"));
        companies.add(createCompany("8", "德邦快递", "DBL"));
        companies.add(createCompany("9", "百世快递", "HTKY"));

        return companies;
    }

    /**
     * 创建物流公司信息
     */
    private Map<String, Object> createCompany(String id, String name, String code) {
        Map<String, Object> company = new HashMap<>();
        company.put("id", id);
        company.put("name", name);
        company.put("code", code);
        return company;
    }
}