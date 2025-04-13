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
    // @Autowired // 如果需要扣减销量等操作，可能需要引入 ProductMapper
    // private PmsProductMapper productMapper;


    @Override
    public List<OmsAfterSale> list(OmsAfterSaleQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        // 调用自定义 DAO 的 getList 方法
        return afterSaleDao.getList(queryParam);
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
        // List<OmsAfterSale> allowedToDelete = afterSaleMapper.selectByExample(checkExample);
        // if (allowedToDelete.size() != ids.size()) { // 如果不是所有传入ID都允许删除
        //     log.warn("部分售后单状态不允许删除: {}", ids);
        //     // 可以选择抛异常或只删除允许的
        //     ids = allowedToDelete.stream().map(OmsAfterSale::getId).collect(Collectors.toList());
        //     if (CollectionUtils.isEmpty(ids)) return 0;
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
            // updateApply.setHandleTime(new Date());
            //updateApply.setHandleMan(statusParam.getHandleMan());
            //updateApply.setHandleNote(statusParam.getHandleNote());
            // 可能需要设置退货地址ID (companyAddressId)
            // updateApply.setReturnAddressId(statusParam.getCompanyAddressId()); // 假设表中有此字段
        } else if (status == 2) { // 已完成 (例如：收到退货，完成退款)
            // 记录收货信息
            // updateApply.setReceiveTime(new Date());
            // updateApply.setReceiveMan(statusParam.getReceiveMan());
            // updateApply.setReceiveNote(statusParam.getReceiveNote());

            // TODO: 此处可能需要触发实际的退款操作，调用支付渠道接口等

            // TODO: 是否需要恢复/扣减库存或销量？
            // 原代码在完成退货时扣减销量，逻辑需要根据新需求确认
            // 如果需要，则取消下面 productMapper 的注释并实现逻辑
            /*
            OmsAfterSaleItemExample itemExample = new OmsAfterSaleItemExample();
            itemExample.createCriteria().andAfterSaleIdEqualTo(id);
            List<OmsAfterSaleItem> items = afterSaleItemMapper.selectByExample(itemExample);
            for(OmsAfterSaleItem item : items) {
                if (item.getProductId() != null && item.getReturnQuantity() != null && item.getReturnQuantity() > 0) {
                    try {
                        // 之前是扣减销量，这里可能需要反向操作？或者根据业务定义
                        // int saleUpdateCount = productMapper.decreaseSale(item.getProductId(), item.getReturnQuantity());
                        // log.info("售后完成，处理商品销量: productId={}, quantity={}", item.getProductId(), item.getReturnQuantity());
                    } catch (Exception e) {
                        log.error("售后完成后处理商品销量异常: afterSaleId={}, productId={}", id, item.getProductId(), e);
                    }
                }
            }
            */

        } else if (status == 3) { // 已拒绝
            // updateApply.setHandleTime(new Date());
            // updateApply.setHandleMan(statusParam.getHandleMan());
            // updateApply.setHandleNote(statusParam.getHandleNote());
            // 拒绝时，可能需要恢复之前更新的订单项的 applied_quantity
            // (需要读取 OmsAfterSaleItem 列表，再更新 OmsOrderItem)
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
        // 调用自定义 DAO 的 getDetail 方法
        return afterSaleDao.getDetail(id);
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