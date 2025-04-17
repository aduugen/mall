package com.macro.mall.dao;

import com.macro.mall.model.OmsAfterSaleLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 售后日志自定义Dao
 * Created by macro on 2023/7/15.
 */
public interface OmsAfterSaleLogDao {

    /**
     * 查询操作日志列表
     * 
     * @param afterSaleId     售后单ID
     * @param operateMan      操作人
     * @param operateType     操作类型
     * @param afterSaleStatus 售后状态
     * @param startTime       开始时间
     * @param endTime         结束时间
     * @return 日志列表
     */
    List<OmsAfterSaleLog> getLogList(@Param("afterSaleId") Long afterSaleId,
            @Param("operateMan") String operateMan,
            @Param("operateType") Integer operateType,
            @Param("afterSaleStatus") Integer afterSaleStatus,
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime);

    /**
     * 保存操作日志
     * 
     * @param log 日志对象
     * @return 插入行数
     */
    int insertLog(OmsAfterSaleLog log);

    /**
     * 批量插入日志
     * 
     * @param logList 日志列表
     * @return 插入行数
     */
    int batchInsert(List<OmsAfterSaleLog> logList);

    /**
     * 获取操作日志统计数据
     * 
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 各操作类型的统计数据
     */
    List<Map<String, Object>> getOperationStatistics(@Param("startTime") Date startTime,
            @Param("endTime") Date endTime);

    /**
     * 获取售后单的最后一条日志
     * 
     * @param afterSaleId 售后单ID
     * @return 最后一条日志
     */
    OmsAfterSaleLog getLastLog(@Param("afterSaleId") Long afterSaleId);
}