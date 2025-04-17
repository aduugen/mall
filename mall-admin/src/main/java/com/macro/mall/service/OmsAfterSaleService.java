package com.macro.mall.service;

import com.macro.mall.dto.OmsAfterSaleDetail;
import com.macro.mall.dto.OmsAfterSaleQueryParam;
import com.macro.mall.dto.OmsAfterSaleStatistic;
import com.macro.mall.dto.OmsUpdateStatusParam;
import com.macro.mall.model.OmsAfterSale;
import com.macro.mall.model.OmsAfterSaleLog;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 售后服务管理Service
 */
public interface OmsAfterSaleService {
    /**
     * 分页查询售后申请
     * 
     * @param queryParam 查询参数
     * @param pageSize   页大小
     * @param pageNum    页码
     * @return 售后列表 (包含商品项)
     */
    List<OmsAfterSaleDetail> list(OmsAfterSaleQueryParam queryParam, Integer pageSize, Integer pageNum);

    /**
     * 批量删除申请 (根据业务逻辑，可能不允许删除或有状态限制)
     * 
     * @param ids 售后ID列表
     * @return 删除数量
     */
    int delete(List<Long> ids);

    /**
     * 获取售后详情DTO
     * 
     * @param id 售后ID
     * @return 售后详情DTO
     */
    OmsAfterSaleDetail getDetailDTO(Long id);

    /**
     * 获取售后单原始信息
     * 
     * @param id 售后ID
     * @return 售后单实体
     */
    OmsAfterSale getDetail(Long id);

    /**
     * 修改申请状态
     * 
     * @param id          售后ID
     * @param statusParam 状态参数
     * @return 是否更新成功
     */
    boolean updateStatus(Long id, OmsUpdateStatusParam statusParam);

    /**
     * 获取售后状态统计
     * 
     * @return 统计结果DTO
     */
    OmsAfterSaleStatistic getAfterSaleStatistic();

    /**
     * 检查异常售后单
     * 
     * @param days 超过多少天未更新视为异常
     * @return 异常售后单列表
     */
    List<OmsAfterSale> checkAbnormalAfterSales(int days);

    /**
     * 获取售后单操作日志列表
     */
    List<OmsAfterSaleLog> getOperationLogs(Long afterSaleId, String operateMan, Integer operateType,
            Integer afterSaleStatus, Date startTime, Date endTime);

    /**
     * 统计操作类型数量
     */
    List<Map<String, Object>> countOperationsByType(Date startTime, Date endTime, String operateMan);

    /**
     * 统计状态转换耗时
     */
    List<Map<String, Object>> getStatusTransitionTime(Date startTime, Date endTime);

    /**
     * 获取售后统计信息
     */
    OmsAfterSaleStatistic getStatistic();
}