package com.macro.mall.dao;

import com.macro.mall.dto.OmsAfterSaleDetail;
import com.macro.mall.dto.OmsAfterSaleQueryParam;
import com.macro.mall.dto.OmsAfterSaleStatistic;
import com.macro.mall.model.OmsAfterSale; // 列表返回基础模型或自定义DTO
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 售后管理自定义Dao
 */
public interface OmsAfterSaleDao {

    /**
     * 查询售后申请列表
     * @param queryParam 查询参数
     * @return 售后列表
     */
    List<OmsAfterSale> getList(@Param("queryParam") OmsAfterSaleQueryParam queryParam); // 返回基础 OmsAfterSale 或自定义列表 DTO

    /**
     * 获取售后申请详情
     * @param id 售后ID
     * @return 售后详情 DTO
     */
    OmsAfterSaleDetail getDetail(@Param("id") Long id);

    /**
     * 获取售后申请状态统计
     * @return 统计结果 DTO
     */
    OmsAfterSaleStatistic getAfterSaleStatistic();
}