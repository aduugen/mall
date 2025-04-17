package com.macro.mall.service.impl;

import com.macro.mall.dao.OmsAfterSaleLogDao;
import com.macro.mall.mapper.OmsAfterSaleLogMapper;
import com.macro.mall.model.OmsAfterSale;
import com.macro.mall.model.OmsAfterSaleLog;
import com.macro.mall.service.OmsAfterSaleLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 售后日志服务实现类
 */
@Slf4j
@Service
public class OmsAfterSaleLogServiceImpl implements OmsAfterSaleLogService {

    @Autowired
    private OmsAfterSaleLogDao afterSaleLogDao;

    @Autowired
    private OmsAfterSaleLogMapper afterSaleLogMapper;

    @Override
    public List<OmsAfterSaleLog> getLogList(Long afterSaleId, String operateMan, Integer operateType,
            Integer afterSaleStatus, Date startTime, Date endTime) {
        log.info("查询售后操作日志列表，参数：afterSaleId={}, operateMan={}, operateType={}, afterSaleStatus={}, " +
                "startTime={}, endTime={}", afterSaleId, operateMan, operateType, afterSaleStatus, startTime, endTime);
        return afterSaleLogDao.getLogList(afterSaleId, operateMan, operateType, afterSaleStatus, startTime, endTime);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveLog(OmsAfterSaleLog afterSaleLog) {
        afterSaleLog.setCreateTime(new Date());
        afterSaleLog.setId(null); // 确保使用数据库自增主键
        afterSaleLog.setNote(afterSaleLog.getNote() == null ? "" : afterSaleLog.getNote());

        // 日志记录
        try {
            int result = afterSaleLogDao.insertLog(afterSaleLog);
            if (result > 0) {
                log.info("保存售后操作日志成功：afterSaleId={}, operateType={}, 操作人ID={}",
                        afterSaleLog.getAfterSaleId(), afterSaleLog.getOperateType(), afterSaleLog.getOperatorId());
            } else {
                log.warn("保存售后操作日志失败：afterSaleId={}, operateType={}, 操作人ID={}",
                        afterSaleLog.getAfterSaleId(), afterSaleLog.getOperateType(), afterSaleLog.getOperatorId());
            }
            return result;
        } catch (Exception e) {
            log.error("保存售后操作日志异常", e);
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchSaveLog(List<OmsAfterSaleLog> logList) {
        if (logList == null || logList.isEmpty()) {
            return 0;
        }

        // 设置默认值
        for (OmsAfterSaleLog afterSaleLog : logList) {
            if (afterSaleLog.getCreateTime() == null) {
                afterSaleLog.setCreateTime(new Date());
            }
            afterSaleLog.setId(null); // 确保使用数据库自增主键
            afterSaleLog.setNote(afterSaleLog.getNote() == null ? "" : afterSaleLog.getNote());
        }

        try {
            int result = afterSaleLogDao.batchInsert(logList);
            log.info("批量保存售后操作日志成功，数量：{}", logList.size());
            return result;
        } catch (Exception e) {
            log.error("批量保存售后操作日志异常", e);
            throw e;
        }
    }

    @Override
    public List<Map<String, Object>> getOperationStatistics(Date startTime, Date endTime) {
        return afterSaleLogDao.getOperationStatistics(startTime, endTime);
    }

    @Override
    public OmsAfterSaleLog getLastLog(Long afterSaleId) {
        if (afterSaleId == null) {
            return null;
        }
        return afterSaleLogDao.getLastLog(afterSaleId);
    }

    @Override
    public OmsAfterSaleLog createLog(Long afterSaleId, Integer oldStatus, Integer newStatus,
            String operateMan, Integer operateType, String note) {
        OmsAfterSaleLog afterSaleLog = new OmsAfterSaleLog();
        afterSaleLog.setAfterSaleId(afterSaleId);
        // 设置状态为新状态
        afterSaleLog.setStatus(newStatus);
        // 设置操作人ID，需要根据实际情况获取管理员ID
        // 这里假设使用Long.valueOf(1L)作为默认管理员ID
        afterSaleLog.setOperatorId(1L);
        // 设置操作人类型为管理员
        afterSaleLog.setOperatorType(OmsAfterSaleLog.OPERATOR_TYPE_ADMIN);
        afterSaleLog.setOperateType(operateType);
        afterSaleLog.setNote(note);
        afterSaleLog.setCreateTime(new Date());

        // 保存日志
        saveLog(afterSaleLog);

        return afterSaleLog;
    }
}