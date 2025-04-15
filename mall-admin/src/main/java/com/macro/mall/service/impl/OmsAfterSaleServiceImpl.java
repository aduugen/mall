package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.OmsAfterSaleDao; // 引入新的自定义 DAO
import com.macro.mall.dto.OmsAfterSaleDetail;
import com.macro.mall.dto.OmsAfterSaleQueryParam;
import com.macro.mall.dto.OmsAfterSaleStatistic;
import com.macro.mall.dto.OmsUpdateStatusParam;
import com.macro.mall.mapper.OmsAfterSaleItemMapper; // 引入 Item Mapper
import com.macro.mall.mapper.OmsAfterSaleMapper; // 引入基础 Mapper
import com.macro.mall.model.OmsAfterSale;
import com.macro.mall.model.OmsAfterSaleExample; // 引入 Example 类
import com.macro.mall.model.OmsAfterSaleItemExample;
import com.macro.mall.service.OmsAfterSaleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// 引入必要的 Mapper 和 Model
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsAfterSaleItem;

/**
 * 售后服务管理Service实现类
 */
@Slf4j
@Service
public class OmsAfterSaleServiceImpl implements OmsAfterSaleService {

    // 注入新的 DAO 和 Mapper
    @Autowired
    private OmsAfterSaleDao afterSaleDao;
    @Autowired
    private OmsAfterSaleMapper afterSaleMapper;
    @Autowired
    private OmsAfterSaleItemMapper afterSaleItemMapper;

    // 注入 Member 和 Order Mapper
    @Autowired
    private UmsMemberMapper memberMapper;
    @Autowired
    private OmsOrderMapper orderMapper;

    // @Autowired // 如果需要扣减销量等操作，可能需要引入 ProductMapper
    // private PmsProductMapper productMapper;

    @Override
    public List<OmsAfterSaleDetail> list(OmsAfterSaleQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        // 调用自定义 DAO 的 getList 方法，它现在返回 OmsAfterSaleDetail
        List<OmsAfterSaleDetail> list = afterSaleDao.getList(queryParam); // 获取基础列表

        // 可以在这里补充额外信息，如果DAO查询不方便处理的话
        // 例如，遍历列表，为每个 OmsAfterSaleDetail 设置用户信息等
        // 但更好的做法是尽量在 DAO/XML 中完成关联查询

        return list;
    }

    @Override
    @Transactional // 删除操作通常需要事务
    public int delete(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }
        // 注意：业务上可能只允许删除已完成或已拒绝的售后单，需要根据需求添加状态检查
        // OmsAfterSaleExample checkExample = new OmsAfterSaleExample();
        // checkExample.createCriteria().andIdIn(ids).andStatusIn(Arrays.asList(2, 3));
        // List<OmsAfterSale> allowedToDelete =
        // afterSaleMapper.selectByExample(checkExample);
        // if (allowedToDelete.size() != ids.size()) { // 如果不是所有传入ID都允许删除
        // log.warn("部分售后单状态不允许删除: {}", ids);
        // // 可以选择抛异常或只删除允许的
        // ids =
        // allowedToDelete.stream().map(OmsAfterSale::getId).collect(Collectors.toList());
        // if (CollectionUtils.isEmpty(ids)) return 0;
        // }

        // 1. 先删除关联的售后商品项
        OmsAfterSaleItemExample itemExample = new OmsAfterSaleItemExample();
        itemExample.createCriteria().andAfterSaleIdIn(ids);
        afterSaleItemMapper.deleteByExample(itemExample);

        // 2. 再删除售后主记录
        OmsAfterSaleExample example = new OmsAfterSaleExample();
        example.createCriteria().andIdIn(ids);
        return afterSaleMapper.deleteByExample(example);
    }

    @Override
    @Transactional // 更新状态通常需要事务
    public int updateStatus(Long id, OmsUpdateStatusParam statusParam) {
        Integer status = statusParam.getStatus();
        if (status == null) {
            log.warn("更新售后状态失败，状态参数为空: id={}", id);
            return 0;
        }

        // 1. 获取当前的售后申请信息 (用于校验状态流转和获取必要信息)
        OmsAfterSale currentApply = afterSaleMapper.selectByPrimaryKey(id);
        if (currentApply == null) {
            log.warn("更新售后状态失败，售后申请不存在: id={}", id);
            return 0;
        }

        // 2. 校验状态流转是否合法 (简单示例，实际业务可能更复杂)
        // 例如：只有 待处理(0) 才能变为 处理中(1) 或 已拒绝(3)
        // 只有 处理中(1) 才能变为 已完成(2) 或 已拒绝(3)
        if (currentApply.getStatus() == 2 || currentApply.getStatus() == 3) {
            log.warn("更新售后状态失败，当前状态({})已是终态，无法变更: id={}", currentApply.getStatus(), id);
            return 0; // 已是终态，不允许修改
        }
        if (currentApply.getStatus() == 0 && !(status == 1 || status == 3)) {
            log.warn("更新售后状态失败，待处理状态({})只能变更为处理中(1)或已拒绝(3), 请求变更为: {}", currentApply.getStatus(), status, id);
            return 0;
        }
        if (currentApply.getStatus() == 1 && !(status == 2 || status == 3)) {
            log.warn("更新售后状态失败，处理中状态({})只能变更为已完成(2)或已拒绝(3), 请求变更为: {}", currentApply.getStatus(), status, id);
            return 0;
        }

        // 3. 构建更新对象
        OmsAfterSale updateApply = new OmsAfterSale();
        updateApply.setId(id);
        updateApply.setStatus(status);
        updateApply.setUpdateTime(new Date()); // 记录更新时间

        // 根据目标状态设置不同字段
        if (status == 1) { // 处理中 (例如：同意退货，等待用户发货)
            updateApply.setHandleTime(new Date());
            updateApply.setHandleMan(statusParam.getHandleMan());
            updateApply.setHandleNote(statusParam.getHandleNote());
            // 设置退款金额 (可能是管理员手动确认的)
            updateApply.setReturnAmount(statusParam.getReturnAmount());
            // 设置退货地址ID
            updateApply.setCompanyAddressId(statusParam.getCompanyAddressId());
        } else if (status == 2) { // 已完成 (例如：收到退货，完成退款)
            // 记录收货信息
            updateApply.setReceiveTime(new Date());
            updateApply.setReceiveMan(statusParam.getReceiveMan());
            updateApply.setReceiveNote(statusParam.getReceiveNote());

            // 最终确认的退款金额
            updateApply.setReturnAmount(statusParam.getReturnAmount());

            // TODO: 此处可能需要触发实际的退款操作

            // TODO: 库存/销量处理逻辑...

        } else if (status == 3) { // 已拒绝
            updateApply.setHandleTime(new Date());
            updateApply.setHandleMan(statusParam.getHandleMan());
            updateApply.setHandleNote(statusParam.getHandleNote());
            // 拒绝时，退款金额通常为0或保持不变
            // updateApply.setReturnAmount(BigDecimal.ZERO);
        }

        // 4. 执行更新
        int count = afterSaleMapper.updateByPrimaryKeySelective(updateApply);
        if (count > 0) {
            log.info("更新售后状态成功: id={}, newStatus={}", id, status);
            // TODO: 可能需要添加操作日志记录
        } else {
            log.warn("更新售后状态失败，数据库更新返回0: id={}", id);
        }
        return count;
    }

    @Override
    public OmsAfterSaleDetail getDetail(Long id) {
        // 1. 使用 DAO 获取基础售后信息及商品列表
        OmsAfterSaleDetail detail = afterSaleDao.getDetail(id);
        if (detail == null) {
            log.warn("获取售后详情失败，售后申请不存在: id={}", id);
            return null;
        }

        // 2. 获取并设置关联用户信息
        if (detail.getMemberId() != null) {
            UmsMember member = memberMapper.selectByPrimaryKey(detail.getMemberId());
            if (member != null) {
                detail.setMemberNickname(member.getNickname());
                detail.setMemberPhone(member.getPhone());
                // detail.setMember(member); // 如果需要完整 member 对象
            } else {
                log.warn("无法找到售后申请 {} 关联的会员信息，MemberId: {}", id, detail.getMemberId());
            }
        }

        // 3. 获取并设置关联订单信息 (主要是订单总金额)
        if (detail.getOrderId() != null) {
            OmsOrder order = orderMapper.selectByPrimaryKey(detail.getOrderId());
            if (order != null) {
                detail.setOrderTotalAmount(order.getTotalAmount());
                // 确保 orderSn 一致性
                if (detail.getOrderSn() == null || detail.getOrderSn().isEmpty()) {
                    detail.setOrderSn(order.getOrderSn());
                }
                // detail.setOrder(order); // 如果需要完整 order 对象
            } else {
                log.warn("无法找到售后申请 {} 关联的订单信息，OrderId: {}", id, detail.getOrderId());
            }
        } else if (detail.getOrderSn() != null && !detail.getOrderSn().isEmpty()) {
            // 如果只有OrderSn，可以尝试通过OrderSn查询订单，但这可能效率较低且需要额外方法
            log.warn("售后申请 {} 缺少 OrderId，仅通过 OrderSn 查询订单信息可能不准确或无法获取", id);
        }

        // 4. 计算退货商品的总金额 (虽然DTO有字段，但建议由Service计算确保准确性)
        // 如果 afterSaleItemList 在 afterSaleDao.getDetail 中已正确填充
        if (!CollectionUtils.isEmpty(detail.getAfterSaleItemList())) {
            BigDecimal calculatedReturnAmount = detail.getAfterSaleItemList().stream()
                    .map(item -> {
                        BigDecimal price = item.getProductRealPrice() != null ? item.getProductRealPrice()
                                : BigDecimal.ZERO;
                        Integer quantity = item.getReturnQuantity() != null ? item.getReturnQuantity() : 0;
                        return price.multiply(new BigDecimal(quantity));
                    })
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            // 注意：这里计算出的金额可能与 OmsAfterSale 表中存储的 returnAmount 不同
            // （例如管理员手动修改过退款金额）。前端显示时需要决定使用哪个。
            // 可以在 DTO 中再加一个字段存储这个计算值，或者让前端根据需要计算。
            log.debug("售后申请 {} 计算出的商品退款总额: {}", id, calculatedReturnAmount);
            // detail.setCalculatedReturnAmount(calculatedReturnAmount); // 假设DTO有此字段
        } else {
            log.warn("售后申请 {} 未找到关联的商品项，无法计算商品退款总额", id);
        }

        return detail;
    }

    @Override
    public OmsAfterSaleStatistic getAfterSaleStatistic() {
        OmsAfterSaleStatistic statistic = afterSaleDao.getAfterSaleStatistic();
        // 对可能为 null 的 count 值进行处理，确保返回的 DTO 中 count 不为 null
        if (statistic == null) {
            statistic = new OmsAfterSaleStatistic(); // 返回一个全为 0 的对象
        } else {
            statistic.setPendingCount(statistic.getPendingCount() != null ? statistic.getPendingCount() : 0L);
            statistic.setProcessingCount(statistic.getProcessingCount() != null ? statistic.getProcessingCount() : 0L);
            statistic.setFinishedCount(statistic.getFinishedCount() != null ? statistic.getFinishedCount() : 0L);
            statistic.setRejectedCount(statistic.getRejectedCount() != null ? statistic.getRejectedCount() : 0L);
            statistic.setTotalCount(statistic.getTotalCount() != null ? statistic.getTotalCount() : 0L);
        }
        return statistic;
    }
}