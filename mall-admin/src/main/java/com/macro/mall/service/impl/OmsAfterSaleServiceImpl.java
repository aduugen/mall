package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.OmsAfterSaleDao;
import com.macro.mall.dto.AdminOmsAfterSaleDTO;
import com.macro.mall.dto.OmsAfterSaleQueryParam;
import com.macro.mall.dto.OmsAfterSaleStatistic;
import com.macro.mall.dto.OmsUpdateStatusParam;
import com.macro.mall.exception.BusinessException;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.service.OmsAfterSaleService;
import com.macro.mall.service.OmsAfterSaleLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * 售后服务管理Service实现类
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class OmsAfterSaleServiceImpl implements OmsAfterSaleService {

    @Autowired
    private OmsAfterSaleDao afterSaleDao;
    @Autowired
    private OmsAfterSaleMapper afterSaleMapper;
    @Autowired
    private OmsAfterSaleItemMapper afterSaleItemMapper;
    @Autowired
    private OmsAfterSaleLogMapper afterSaleLogMapper;

    // 添加其他所需的Mapper
    @Autowired
    private OmsAfterSaleProcessMapper afterSaleProcessMapper;
    @Autowired
    private OmsAfterSaleProofMapper afterSaleProofMapper;
    @Autowired
    private OmsAfterSaleLogisticsMapper afterSaleLogisticsMapper;
    @Autowired
    private OmsAfterSaleCheckMapper afterSaleCheckMapper;
    @Autowired
    private OmsAfterSaleRefundMapper afterSaleRefundMapper;

    @Autowired
    private OmsAfterSaleLogService afterSaleLogService;

    /**
     * 分页查询售后申请
     */
    @Override
    public List<AdminOmsAfterSaleDTO> list(OmsAfterSaleQueryParam queryParam, Integer pageSize, Integer pageNum) {
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }
        PageHelper.startPage(pageNum, pageSize);
        // 调用自定义 DAO 的 getList 方法，获取基本售后单信息（不包含关联数据）
        List<AdminOmsAfterSaleDTO> list = afterSaleDao.getList(queryParam);

        if (list == null || list.isEmpty()) {
            log.info("售后申请查询返回空列表，查询条件：{}", queryParam);
            return list;
        }

        log.debug("售后申请查询成功，查询条件：{}，结果数量：{}", queryParam, list.size());
        /*
         * // 批量获取所有售后单ID
         * List<Long> afterSaleIds = list.stream()
         * .map(AdminOmsAfterSaleDTO::getId)
         * .collect(Collectors.toList());
         * 
         * // 批量查询售后商品项
         * OmsAfterSaleItemExample itemExample = new OmsAfterSaleItemExample();
         * itemExample.createCriteria().andAfterSaleIdIn(afterSaleIds);
         * List<OmsAfterSaleItem> allItems =
         * afterSaleItemMapper.selectByExample(itemExample);
         * 
         * // 查询日志，使用DAO方法获取
         * Map<Long, List<OmsAfterSaleLog>> logMap = new HashMap<>();
         * for (Long afterSaleId : afterSaleIds) {
         * List<OmsAfterSaleLog> logs = afterSaleDao.queryOperationLogs(afterSaleId,
         * null, null, null, null, null);
         * if (logs != null && !logs.isEmpty()) {
         * logMap.put(afterSaleId, logs);
         * }
         * }
         * 
         * // 使用新增加的Mapper查询关联数据
         * // 批量查询处理记录
         * OmsAfterSaleProcessExample processExample = new OmsAfterSaleProcessExample();
         * processExample.createCriteria().andAfterSaleIdIn(afterSaleIds);
         * processExample.setOrderByClause("create_time DESC");
         * List<OmsAfterSaleProcess> allProcesses =
         * afterSaleProcessMapper.selectByExample(processExample);
         * 
         * // 批量查询凭证
         * List<OmsAfterSaleProof> allProofs =
         * afterSaleProofMapper.selectByAfterSaleIds(afterSaleIds);
         * 
         * // 批量查询物流信息
         * List<OmsAfterSaleLogistics> allLogistics =
         * afterSaleLogisticsMapper.selectByAfterSaleIds(afterSaleIds);
         * 
         * // 批量查询质检信息
         * List<OmsAfterSaleCheck> allChecks =
         * afterSaleCheckMapper.selectByAfterSaleIds(afterSaleIds);
         * 
         * // 批量查询退款信息
         * List<OmsAfterSaleRefund> allRefunds =
         * afterSaleRefundMapper.selectByAfterSaleIds(afterSaleIds);
         * 
         * // 使用Map进行分组，提高查找效率
         * Map<Long, List<OmsAfterSaleItem>> itemMap = allItems.stream()
         * .collect(Collectors.groupingBy(OmsAfterSaleItem::getAfterSaleId));
         * 
         * Map<Long, List<OmsAfterSaleProcess>> processMap = allProcesses.stream()
         * .collect(Collectors.groupingBy(OmsAfterSaleProcess::getAfterSaleId));
         * 
         * Map<Long, List<OmsAfterSaleProof>> proofMap = new HashMap<>();
         * if (!CollectionUtils.isEmpty(allProofs)) {
         * proofMap = allProofs.stream()
         * .collect(Collectors.groupingBy(OmsAfterSaleProof::getAfterSaleId));
         * }
         * 
         * Map<Long, OmsAfterSaleLogistics> logisticsMap = new HashMap<>();
         * if (!CollectionUtils.isEmpty(allLogistics)) {
         * logisticsMap = allLogistics.stream()
         * .collect(Collectors.toMap(OmsAfterSaleLogistics::getAfterSaleId, logistics ->
         * logistics));
         * }
         * 
         * Map<Long, OmsAfterSaleCheck> checkMap = new HashMap<>();
         * if (!CollectionUtils.isEmpty(allChecks)) {
         * checkMap = allChecks.stream()
         * .collect(Collectors.toMap(OmsAfterSaleCheck::getAfterSaleId, check ->
         * check));
         * }
         * 
         * Map<Long, OmsAfterSaleRefund> refundMap = new HashMap<>();
         * if (!CollectionUtils.isEmpty(allRefunds)) {
         * refundMap = allRefunds.stream()
         * .collect(Collectors.toMap(OmsAfterSaleRefund::getAfterSaleId, refund ->
         * refund));
         * }
         * 
         * // 为每个售后单填充关联数据
         * for (AdminOmsAfterSaleDTO detail : list) {
         * Long afterSaleId = detail.getId();
         * 
         * // 设置售后商品项
         * detail.setItemList(itemMap.getOrDefault(afterSaleId, new ArrayList<>()));
         * 
         * // 设置处理记录
         * detail.setProcessList(processMap.getOrDefault(afterSaleId, new
         * ArrayList<>()));
         * 
         * // 设置凭证列表
         * detail.setProofList(proofMap.getOrDefault(afterSaleId, new ArrayList<>()));
         * 
         * // 设置操作日志
         * detail.setLogList(logMap.getOrDefault(afterSaleId, new ArrayList<>()));
         * 
         * // 设置物流信息
         * detail.setLogistics(logisticsMap.get(afterSaleId));
         * 
         * // 设置质检信息
         * detail.setCheck(checkMap.get(afterSaleId));
         * 
         * // 设置退款信息
         * detail.setRefund(refundMap.get(afterSaleId));
         * 
         * // 设置可用操作列表
         * detail.setAllowableOperations(getAllowableOperations(detail.getStatus()));
         * 
         * // 添加调试日志
         * log.
         * debug("售后单ID={}, 商品项数量={}, 处理记录数量={}, 凭证数量={}, 操作日志数量={}, 物流信息={}, 质检信息={}, 退款信息={}"
         * ,
         * afterSaleId,
         * detail.getItemList() != null ? detail.getItemList().size() : 0,
         * detail.getProcessList() != null ? detail.getProcessList().size() : 0,
         * detail.getProofList() != null ? detail.getProofList().size() : 0,
         * detail.getLogList() != null ? detail.getLogList().size() : 0,
         * detail.getLogistics() != null ? "有" : "无",
         * detail.getCheck() != null ? "有" : "无",
         * detail.getRefund() != null ? "有" : "无");
         * }
         */
        return list;
    }

    /**
     * 根据售后单状态获取可用操作列表
     */
    private List<String> getAllowableOperations(Integer status) {
        List<String> operations = new ArrayList<>();
        if (status == null) {
            return operations;
        }

        switch (status) {
            case OmsAfterSale.STATUS_PENDING:
                operations.add("审核通过");
                operations.add("审核拒绝");
                break;
            case OmsAfterSale.STATUS_APPROVED:
                operations.add("确认发货");
                break;
            case OmsAfterSale.STATUS_SHIPPED:
                operations.add("确认收货");
                break;
            case OmsAfterSale.STATUS_RECEIVED:
                operations.add("开始质检");
                break;
            case OmsAfterSale.STATUS_CHECKING:
                operations.add("质检通过");
                operations.add("质检不通过");
                break;
            case OmsAfterSale.STATUS_CHECK_PASS:
                operations.add("退款处理");
                break;
            case OmsAfterSale.STATUS_REFUNDING:
                operations.add("完成退款");
                operations.add("退款失败");
                break;
            default:
                break;
        }

        return operations;
    }

    /**
     * 批量删除售后申请
     */
    @Override
    @Transactional
    public int delete(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return 0;
        }

        // 验证状态，只允许删除特定状态的售后单
        OmsAfterSaleExample checkExample = new OmsAfterSaleExample();
        checkExample.createCriteria().andIdIn(ids).andStatusIn(
                List.of(OmsAfterSale.STATUS_COMPLETED, OmsAfterSale.STATUS_REJECTED));
        List<OmsAfterSale> allowedToDelete = afterSaleMapper.selectByExample(checkExample);

        // 检查是否所有ID都允许删除
        if (allowedToDelete.size() != ids.size()) {
            log.warn("部分售后单状态不允许删除: 请求删除{}个, 允许删除{}个", ids.size(), allowedToDelete.size());
            ids = allowedToDelete.stream().map(OmsAfterSale::getId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(ids)) {
                return 0;
            }
        }

        // 1. 先删除关联的售后商品项
        OmsAfterSaleItemExample itemExample = new OmsAfterSaleItemExample();
        itemExample.createCriteria().andAfterSaleIdIn(ids);
        int itemCount = afterSaleItemMapper.deleteByExample(itemExample);
        log.info("删除售后商品项: 售后单IDs={}, 删除商品项数量={}", ids, itemCount);

        // 2. 删除售后日志
        try {
            for (Long id : ids) {
                afterSaleLogMapper.deleteByAfterSaleId(id);
            }
        } catch (Exception e) {
            log.error("删除售后日志失败", e);
            // 不影响主流程，继续执行
        }

        // 3. 删除售后主记录
        OmsAfterSaleExample example = new OmsAfterSaleExample();
        example.createCriteria().andIdIn(ids);
        int count = afterSaleMapper.deleteByExample(example);
        log.info("删除售后申请: IDs={}, 删除数量={}", ids, count);

        return count;
    }

    /**
     * 修改售后单状态
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateStatus(Long id, OmsUpdateStatusParam statusParam) {
        log.info("更新售后单状态: id={}, status={}", id, statusParam.getStatus());
        try {
            // 获取售后单信息
            OmsAfterSale afterSale = afterSaleMapper.selectByPrimaryKey(id);
            if (afterSale == null) {
                log.error("售后单不存在: id={}", id);
                throw new BusinessException("售后单不存在");
            }

            // 乐观锁控制，检查版本
            if (statusParam.getVersion() != null && !statusParam.getVersion().equals(afterSale.getVersion())) {
                log.error("数据已被修改，请刷新后重试: 当前版本={}, 请求版本={}",
                        afterSale.getVersion(), statusParam.getVersion());
                throw new BusinessException("数据已被修改，请刷新后重试");
            }

            // 验证状态转换是否合法
            if (!isValidStatusTransition(afterSale.getStatus(), statusParam.getStatus())) {
                log.error("状态转换不合法: 当前状态={}, 目标状态={}",
                        afterSale.getStatus(), statusParam.getStatus());
                throw new BusinessException("状态转换不合法");
            }

            // 验证退款金额
            if (statusParam.getStatus() == OmsAfterSale.STATUS_REFUNDING ||
                    statusParam.getStatus() == OmsAfterSale.STATUS_COMPLETED) {
                if (statusParam.getReturnAmount() == null) {
                    log.error("退款金额不能为空: status={}", statusParam.getStatus());
                    throw new BusinessException("退款金额不能为空");
                }

                if (statusParam.getReturnAmount().compareTo(BigDecimal.ZERO) < 0) {
                    log.error("退款金额不能小于0: 退款金额={}", statusParam.getReturnAmount());
                    throw new BusinessException("退款金额不能小于0");
                }

                // 验证退款金额不超过订单总金额
                if (afterSale.getOrderTotalAmount() != null &&
                        statusParam.getReturnAmount().compareTo(afterSale.getOrderTotalAmount()) > 0) {
                    log.error("退款金额不能大于订单总金额: 退款金额={}, 订单总金额={}",
                            statusParam.getReturnAmount(), afterSale.getOrderTotalAmount());
                    throw new BusinessException("退款金额不能大于订单总金额");
                }
            }

            // 更新售后单状态
            OmsAfterSale record = new OmsAfterSale();
            record.setId(id);
            record.setStatus(statusParam.getStatus());
            record.setHandleMan(statusParam.getHandleMan());
            record.setHandleNote(statusParam.getHandleNote());
            record.setUpdateTime(new Date());

            // 根据状态设置其他字段
            if (statusParam.getStatus() == OmsAfterSale.STATUS_SHIPPED) {
                record.setLogisticsCompany(statusParam.getLogisticsCompany());
                record.setLogisticsNumber(statusParam.getLogisticsNumber());
                record.setShippingTime(new Date());
            } else if (statusParam.getStatus() == OmsAfterSale.STATUS_RECEIVED) {
                record.setReceiveMan(statusParam.getHandleMan());
                record.setReceiveNote(statusParam.getReceiveNote());
                record.setReceiveTime(new Date());
            } else if (statusParam.getStatus() == OmsAfterSale.STATUS_REFUNDING) {
                record.setReturnAmount(statusParam.getReturnAmount());
                record.setRefundType(statusParam.getRefundType());
                record.setRefundNote(statusParam.getRefundNote());
                record.setRefundTime(new Date());
            }

            // 更新版本号
            Integer newVersion = afterSale.getVersion() != null ? afterSale.getVersion() + 1 : 1;
            record.setVersion(newVersion);

            // 执行更新
            int count = afterSaleMapper.updateByPrimaryKeySelective(record);

            if (count > 0) {
                // 记录操作日志
                try {
                    saveOperationLog(id, afterSale.getStatus(), statusParam);
                    log.info("售后单状态更新成功: id={}, status={}", id, statusParam.getStatus());
                    return true;
                } catch (Exception e) {
                    // 记录日志失败不影响主业务
                    log.error("保存售后操作日志失败: id={}", id, e);
                    return true;
                }
            }

            log.error("售后单状态更新失败: id={}", id);
            return false;
        } catch (BusinessException e) {
            // 业务异常直接抛出
            throw e;
        } catch (Exception e) {
            log.error("更新售后单状态异常: id={}", id, e);
            throw new BusinessException("系统异常，请稍后重试");
        }
    }

    /**
     * 保存售后单操作日志
     * 
     * @param afterSaleId 售后单ID
     * @param oldStatus   原状态
     * @param statusParam 状态更新参数
     */
    private void saveOperationLog(Long afterSaleId, Integer oldStatus, OmsUpdateStatusParam statusParam) {
        try {
            OmsAfterSaleLog log = new OmsAfterSaleLog();
            log.setAfterSaleId(afterSaleId);

            // 设置操作人ID，根据实际情况，这里可能需要从上下文获取
            log.setOperatorId(1L); // 使用默认ID，实际应用中需要替换为真实的管理员ID
            log.setOperatorType(OmsAfterSaleLog.OPERATOR_TYPE_ADMIN); // 设置为管理员类型

            log.setOperateType(getOperateTypeFromStatus(statusParam.getStatus()));
            log.setStatus(statusParam.getStatus()); // 使用新状态
            log.setNote(statusParam.getHandleNote());
            log.setCreateTime(new Date());

            // 调用日志服务保存
            afterSaleLogService.saveLog(log);
        } catch (Exception e) {
            // 记录日志失败不影响主业务
            this.log.error("保存售后操作日志失败: id={}", afterSaleId, e);
        }
    }

    /**
     * 根据状态获取操作类型
     */
    private Integer getOperateTypeFromStatus(Integer status) {
        switch (status) {
            case OmsAfterSale.STATUS_APPROVED:
                return 1; // 同意申请
            case OmsAfterSale.STATUS_REJECTED:
                return 2; // 拒绝申请
            case OmsAfterSale.STATUS_SHIPPED:
                return 3; // 已发货
            case OmsAfterSale.STATUS_RECEIVED:
                return 4; // 已收货
            case OmsAfterSale.STATUS_CHECKING:
                return 5; // 质检中
            case OmsAfterSale.STATUS_CHECK_PASS:
                return 6; // 质检通过
            case OmsAfterSale.STATUS_CHECK_FAIL:
                return 7; // 质检不通过
            case OmsAfterSale.STATUS_REFUNDING:
                return 8; // 退款中
            case OmsAfterSale.STATUS_COMPLETED:
                return 9; // 已完成
            default:
                return 0;
        }
    }

    /**
     * 获取售后详情DTO
     */
    @Override
    public AdminOmsAfterSaleDTO getDetailDTO(Long id) {
        return afterSaleDao.getDetail(id);
    }

    /**
     * 获取售后单原始信息
     */
    @Override
    public OmsAfterSale getDetail(Long id) {
        return afterSaleMapper.selectByPrimaryKey(id);
    }

    @Override
    public OmsAfterSaleStatistic getAfterSaleStatistic() {
        OmsAfterSaleStatistic statistic = afterSaleDao.getAfterSaleStatistic();
        // 对可能为 null 的 count 值进行处理，确保返回的 DTO 中 count 不为 null
        if (statistic == null) {
            statistic = new OmsAfterSaleStatistic(); // 返回一个全为 0 的对象
        } else {
            statistic.setPendingCount(statistic.getPendingCount() != null ? statistic.getPendingCount() : 0);
            statistic.setProcessingCount(statistic.getProcessingCount() != null ? statistic.getProcessingCount() : 0);
            statistic.setCompletedCount(statistic.getCompletedCount() != null ? statistic.getCompletedCount() : 0);
            statistic.setRejectedCount(statistic.getRejectedCount() != null ? statistic.getRejectedCount() : 0);
            statistic.setTotalCount(statistic.getTotalCount() != null ? statistic.getTotalCount() : 0);
        }
        return statistic;
    }

    /**
     * 检查售后进度是否异常
     * 定时任务可调用此方法检查长时间未流转的售后单
     */
    public List<OmsAfterSale> checkAbnormalAfterSales(int days) {
        log.info("检查异常售后单，超过{}天未流转", days);
        // 查询超过指定天数未更新的售后单
        Date checkTime = new Date(System.currentTimeMillis() - days * 24 * 60 * 60 * 1000L);

        OmsAfterSaleExample example = new OmsAfterSaleExample();
        example.createCriteria()
                .andStatusNotEqualTo(OmsAfterSale.STATUS_COMPLETED)
                .andStatusNotEqualTo(OmsAfterSale.STATUS_REJECTED)
                .andUpdateTimeLessThan(checkTime);

        List<OmsAfterSale> abnormalList = afterSaleMapper.selectByExample(example);

        if (!CollectionUtils.isEmpty(abnormalList)) {
            log.warn("发现{}个异常售后单需要处理", abnormalList.size());
            // 可以添加自动通知逻辑
        }

        return abnormalList;
    }

    // 添加版本号检查方法
    private void checkVersion(OmsAfterSale current, OmsUpdateStatusParam param) {
        // 使用临时变量替代getVersion调用
        Integer currentVersion = 0; // 默认版本为0
        if (!currentVersion.equals(param.getVersion())) {
            throw new BusinessException("数据已被其他用户修改，请刷新后重试");
        }
    }

    /**
     * 获取售后单操作日志列表
     */
    @Override
    public List<OmsAfterSaleLog> getOperationLogs(Long afterSaleId, String operateMan, Integer operateType,
            Integer afterSaleStatus, Date startTime, Date endTime) {
        try {
            List<OmsAfterSaleLog> logs = afterSaleDao.queryOperationLogs(
                    afterSaleId, operateMan, operateType, afterSaleStatus, startTime, endTime);

            log.info("查询售后操作日志成功，条件：afterSaleId={}, operateMan={}, operateType={}, afterSaleStatus={}, " +
                    "startTime={}, endTime={}, 结果数量：{}",
                    afterSaleId, operateMan, operateType, afterSaleStatus, startTime, endTime,
                    logs != null ? logs.size() : 0);

            return logs;
        } catch (Exception e) {
            log.error("查询售后操作日志失败", e);
            throw new BusinessException("查询售后操作日志失败：" + e.getMessage());
        }
    }

    /**
     * 统计操作类型数量
     */
    @Override
    public List<Map<String, Object>> countOperationsByType(Date startTime, Date endTime, String operateMan) {
        try {
            List<Map<String, Object>> result = afterSaleDao.countOperationsByType(startTime, endTime, operateMan);

            // 进行数据转换，将操作类型代码转换为可读名称
            if (result != null && !result.isEmpty()) {
                for (Map<String, Object> item : result) {
                    Integer operateType = (Integer) item.get("operate_type");
                    String typeName = getOperationTypeName(operateType);
                    item.put("operate_type_name", typeName);
                }
            }

            return result;
        } catch (Exception e) {
            log.error("统计操作类型数量失败", e);
            throw new BusinessException("统计操作类型数量失败：" + e.getMessage());
        }
    }

    /**
     * 获取操作类型的名称
     */
    private String getOperationTypeName(Integer operateType) {
        if (operateType == null)
            return "未知操作";

        Map<Integer, String> typeMap = new HashMap<>();
        typeMap.put(0, "处理中");
        typeMap.put(1, "同意申请");
        typeMap.put(2, "拒绝申请");
        typeMap.put(3, "确认发货");
        typeMap.put(4, "确认收货");
        typeMap.put(5, "开始质检");
        typeMap.put(6, "质检通过");
        typeMap.put(7, "质检不通过");
        typeMap.put(8, "发起退款");
        typeMap.put(9, "完成退款");

        return typeMap.getOrDefault(operateType, "未知操作(" + operateType + ")");
    }

    /**
     * 统计状态转换耗时
     */
    @Override
    public List<Map<String, Object>> getStatusTransitionTime(Date startTime, Date endTime) {
        try {
            List<Map<String, Object>> result = afterSaleDao.getStatusTransitionTime(startTime, endTime);

            // 进行数据转换，将状态代码转换为可读名称
            if (result != null && !result.isEmpty()) {
                for (Map<String, Object> item : result) {
                    Integer fromStatus = (Integer) item.get("from_status");
                    Integer toStatus = (Integer) item.get("to_status");

                    item.put("from_status_name", getStatusName(fromStatus));
                    item.put("to_status_name", getStatusName(toStatus));

                    // 计算平均处理时间
                    Integer hoursSpent = (Integer) item.get("hours_spent");
                    if (hoursSpent != null) {
                        if (hoursSpent < 24) {
                            item.put("time_description", hoursSpent + "小时");
                        } else {
                            int days = hoursSpent / 24;
                            int remainingHours = hoursSpent % 24;
                            item.put("time_description", days + "天" + remainingHours + "小时");
                        }
                    }
                }
            }

            return result;
        } catch (Exception e) {
            log.error("统计状态转换耗时失败", e);
            throw new BusinessException("统计状态转换耗时失败：" + e.getMessage());
        }
    }

    /**
     * 获取状态名称
     */
    private String getStatusName(Integer status) {
        if (status == null)
            return "未知状态";

        Map<Integer, String> statusMap = new HashMap<>();
        statusMap.put(0, "待处理");
        statusMap.put(1, "已同意");
        statusMap.put(2, "已拒绝");
        statusMap.put(3, "已发货");
        statusMap.put(4, "已收货");
        statusMap.put(5, "质检中");
        statusMap.put(6, "质检通过");
        statusMap.put(7, "质检不通过");
        statusMap.put(8, "退款中");
        statusMap.put(9, "已完成");

        return statusMap.getOrDefault(status, "未知状态(" + status + ")");
    }

    /**
     * 获取售后统计信息
     */
    @Override
    public OmsAfterSaleStatistic getStatistic() {
        // 调用DAO方法
        OmsAfterSaleStatistic statistic = afterSaleDao.getStatistic();
        if (statistic == null) {
            statistic = new OmsAfterSaleStatistic();
        }

        // 计算处理率和完成率
        if (statistic.getTotalCount() > 0) {
            double handledRate = (double) (statistic.getTotalCount() - statistic.getPendingCount())
                    / statistic.getTotalCount() * 100;
            double completedRate = (double) statistic.getCompletedCount()
                    / statistic.getTotalCount() * 100;

            statistic.setHandledRate(Math.round(handledRate * 100) / 100.0);
            statistic.setCompletedRate(Math.round(completedRate * 100) / 100.0);
        }

        return statistic;
    }

    /**
     * 验证状态转换是否合法
     * 
     * @param oldStatus 旧状态
     * @param newStatus 新状态
     */
    private boolean isValidStatusTransition(Integer oldStatus, Integer newStatus) {
        // 状态不能倒退
        if (newStatus < oldStatus) {
            return false;
        }

        // 已完成和已拒绝的订单不能再修改
        if (oldStatus == OmsAfterSale.STATUS_COMPLETED || oldStatus == OmsAfterSale.STATUS_REJECTED) {
            return false;
        }

        // 其他状态转换验证规则
        boolean isValid = false;
        switch (oldStatus) {
            case OmsAfterSale.STATUS_PENDING: // 待处理
                // 待处理可以转为已同意、已拒绝
                isValid = (newStatus == OmsAfterSale.STATUS_APPROVED || newStatus == OmsAfterSale.STATUS_REJECTED);
                break;
            case OmsAfterSale.STATUS_APPROVED: // 已同意
                // 已同意可以转为已发货
                isValid = (newStatus == OmsAfterSale.STATUS_SHIPPED);
                break;
            case OmsAfterSale.STATUS_SHIPPED: // 已发货
                // 已发货可以转为已收货
                isValid = (newStatus == OmsAfterSale.STATUS_RECEIVED);
                break;
            case OmsAfterSale.STATUS_RECEIVED: // 已收货
                // 已收货可以转为质检中
                isValid = (newStatus == OmsAfterSale.STATUS_CHECKING);
                break;
            case OmsAfterSale.STATUS_CHECKING: // 质检中
                // 质检中可以转为质检通过或质检不通过
                isValid = (newStatus == OmsAfterSale.STATUS_CHECK_PASS || newStatus == OmsAfterSale.STATUS_CHECK_FAIL);
                break;
            case OmsAfterSale.STATUS_CHECK_PASS: // 质检通过
                // 质检通过可以转为退款中
                isValid = (newStatus == OmsAfterSale.STATUS_REFUNDING);
                break;
            case OmsAfterSale.STATUS_CHECK_FAIL: // 质检不通过
                // 质检不通过可以转为已拒绝
                isValid = (newStatus == OmsAfterSale.STATUS_REJECTED);
                break;
            case OmsAfterSale.STATUS_REFUNDING: // 退款中
                // 退款中可以转为已完成
                isValid = (newStatus == OmsAfterSale.STATUS_COMPLETED);
                break;
            default:
                isValid = false;
        }

        return isValid;
    }
}