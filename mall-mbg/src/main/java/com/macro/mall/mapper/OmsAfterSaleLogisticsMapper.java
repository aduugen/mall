package com.macro.mall.mapper;

import com.macro.mall.model.OmsAfterSaleLogistics;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 售后物流信息Mapper接口
 */
public interface OmsAfterSaleLogisticsMapper {
    /**
     * 根据主键查询售后物流
     */
    OmsAfterSaleLogistics selectByPrimaryKey(Long id);

    /**
     * 根据售后单ID查询售后物流
     */
    OmsAfterSaleLogistics selectByAfterSaleId(Long afterSaleId);

    /**
     * 根据售后单ID列表批量查询售后物流
     */
    List<OmsAfterSaleLogistics> selectByAfterSaleIds(@Param("afterSaleIds") List<Long> afterSaleIds);

    /**
     * 根据物流单号查询售后物流
     */
    OmsAfterSaleLogistics selectByLogisticsNumber(String logisticsNumber);

    /**
     * 新增售后物流
     */
    int insert(OmsAfterSaleLogistics record);

    /**
     * 选择性新增售后物流
     */
    int insertSelective(OmsAfterSaleLogistics record);

    /**
     * 根据主键修改售后物流
     */
    int updateByPrimaryKeySelective(OmsAfterSaleLogistics record);

    /**
     * 根据主键修改售后物流（全部字段）
     */
    int updateByPrimaryKey(OmsAfterSaleLogistics record);

    /**
     * 根据售后单ID修改物流状态
     */
    int updateLogisticsStatusByAfterSaleId(@Param("afterSaleId") Long afterSaleId,
            @Param("logisticsStatus") Integer logisticsStatus);

    /**
     * 根据主键删除售后物流
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 根据售后单ID删除售后物流
     */
    int deleteByAfterSaleId(Long afterSaleId);

    /**
     * 根据售后单ID列表批量删除售后物流
     */
    int deleteByAfterSaleIds(@Param("afterSaleIds") List<Long> afterSaleIds);

    /**
     * 统计待处理物流数量
     */
    int countPendingLogistics();

    /**
     * 统计已收货物流数量
     */
    int countReceivedLogistics();
}