package com.macro.mall.service;

import com.macro.mall.dto.OmsAfterSaleDetail;
import com.macro.mall.dto.OmsAfterSaleQueryParam;
import com.macro.mall.dto.OmsAfterSaleStatistic;
import com.macro.mall.dto.OmsUpdateStatusParam;
// import com.macro.mall.model.OmsAfterSale; // 不再返回基础类型

import java.util.List;

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
    List<OmsAfterSaleDetail> list(OmsAfterSaleQueryParam queryParam, Integer pageSize, Integer pageNum); // 修改返回类型

    /**
     * 批量删除申请 (根据业务逻辑，可能不允许删除或有状态限制)
     * 
     * @param ids 售后ID列表
     * @return 删除数量
     */
    int delete(List<Long> ids);

    /**
     * 修改申请状态
     * 
     * @param id          售后ID
     * @param statusParam 状态参数
     * @return 更新数量
     */
    int updateStatus(Long id, OmsUpdateStatusParam statusParam);

    /**
     * 获取售后详情
     * 
     * @param id 售后ID
     * @return 售后详情 DTO
     */
    OmsAfterSaleDetail getDetail(Long id);

    /**
     * 获取售后状态统计
     * 
     * @return 统计结果 DTO
     */
    OmsAfterSaleStatistic getAfterSaleStatistic();
}