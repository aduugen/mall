package com.macro.mall.dao;

import com.macro.mall.dto.OmsAfterSaleDetail;
import com.macro.mall.dto.OmsAfterSaleQueryParam;
import com.macro.mall.dto.OmsAfterSaleStatistic;
import com.macro.mall.model.OmsAfterSaleLog;
// import com.macro.mall.model.OmsAfterSale; // 不再返回基础模型
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 售后订单管理自定义Dao
 * Created by macro on 2023/6/30.
 */
public interface OmsAfterSaleDao {

        /**
         * 获取售后申请列表
         * 
         * @param queryParam 查询参数
         * @return 售后列表 (包含商品项)
         */
        List<OmsAfterSaleDetail> getList(@Param("queryParam") OmsAfterSaleQueryParam queryParam);

        /**
         * 获取售后详情
         * 
         * @param id 售后ID
         * @return 售后详情 DTO
         */
        OmsAfterSaleDetail getDetail(@Param("id") Long id);

        /**
         * 获取售后状态统计数据
         * 
         * @return 统计结果 DTO
         */
        OmsAfterSaleStatistic getAfterSaleStatistic();

        /**
         * 获取售后统计信息
         * 
         * @return 售后统计数据
         */
        OmsAfterSaleStatistic getStatistic();

        /**
         * 查询售后操作日志，用于审计和统计分析
         */
        List<OmsAfterSaleLog> queryOperationLogs(@Param("afterSaleId") Long afterSaleId,
                        @Param("operateMan") String operateMan,
                        @Param("operateType") Integer operateType,
                        @Param("afterSaleStatus") Integer afterSaleStatus,
                        @Param("startTime") Date startTime,
                        @Param("endTime") Date endTime);

        /**
         * 统计各种操作类型的数量
         */
        List<Map<String, Object>> countOperationsByType(@Param("startTime") Date startTime,
                        @Param("endTime") Date endTime,
                        @Param("operateMan") String operateMan);

        /**
         * 统计各状态售后单操作耗时
         */
        List<Map<String, Object>> getStatusTransitionTime(@Param("startTime") Date startTime,
                        @Param("endTime") Date endTime);
}