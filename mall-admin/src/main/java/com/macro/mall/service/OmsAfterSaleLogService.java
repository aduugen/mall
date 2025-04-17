package com.macro.mall.service;

import com.macro.mall.model.OmsAfterSaleLog;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 售后操作日志管理Service
 */
public interface OmsAfterSaleLogService {
    /**
     * 获取操作日志列表
     * 
     * @param afterSaleId     售后单ID
     * @param operateMan      操作人
     * @param operateType     操作类型
     * @param afterSaleStatus 售后状态
     * @param startTime       开始时间
     * @param endTime         结束时间
     * @return 日志列表
     */
    List<OmsAfterSaleLog> getLogList(Long afterSaleId, String operateMan, Integer operateType,
            Integer afterSaleStatus, Date startTime, Date endTime);

    /**
     * 保存操作日志
     * 
     * @param log 日志对象
     * @return 影响行数
     */
    int saveLog(OmsAfterSaleLog log);

    /**
     * 批量保存日志
     * 
     * @param logList 日志列表
     * @return 影响行数
     */
    int batchSaveLog(List<OmsAfterSaleLog> logList);

    /**
     * 获取操作统计数据
     * 
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 各操作类型的统计数据
     */
    List<Map<String, Object>> getOperationStatistics(Date startTime, Date endTime);

    /**
     * 获取售后单的最后一条日志
     * 
     * @param afterSaleId 售后单ID
     * @return 最后一条日志
     */
    OmsAfterSaleLog getLastLog(Long afterSaleId);

    /**
     * 创建并保存一条操作日志
     * 
     * @param afterSaleId 售后单ID
     * @param oldStatus   原状态
     * @param newStatus   新状态
     * @param operateMan  操作人
     * @param operateType 操作类型
     * @param note        备注
     * @return 创建的日志对象
     */
    OmsAfterSaleLog createLog(Long afterSaleId, Integer oldStatus, Integer newStatus,
            String operateMan, Integer operateType, String note);
}