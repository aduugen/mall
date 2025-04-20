package com.macro.mall.mapper;

import com.macro.mall.model.OmsAfterSaleCheck;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 售后质检信息Mapper接口
 */
public interface OmsAfterSaleCheckMapper {
    /**
     * 根据主键查询售后质检
     */
    OmsAfterSaleCheck selectByPrimaryKey(Long id);

    /**
     * 根据售后单ID查询售后质检
     */
    OmsAfterSaleCheck selectByAfterSaleId(Long afterSaleId);

    /**
     * 根据售后单ID列表批量查询售后质检
     */
    List<OmsAfterSaleCheck> selectByAfterSaleIds(@Param("afterSaleIds") List<Long> afterSaleIds);

    /**
     * 根据质检结果查询售后质检
     */
    List<OmsAfterSaleCheck> selectByCheckResult(Integer checkResult);

    /**
     * 新增售后质检
     */
    int insert(OmsAfterSaleCheck record);

    /**
     * 选择性新增售后质检
     */
    int insertSelective(OmsAfterSaleCheck record);

    /**
     * 批量插入售后质检
     */
    int batchInsert(@Param("list") List<OmsAfterSaleCheck> list);

    /**
     * 根据主键修改售后质检
     */
    int updateByPrimaryKeySelective(OmsAfterSaleCheck record);

    /**
     * 根据主键修改售后质检（全部字段）
     */
    int updateByPrimaryKey(OmsAfterSaleCheck record);

    /**
     * 根据售后单ID修改质检结果
     */
    int updateCheckResultByAfterSaleId(@Param("afterSaleId") Long afterSaleId,
            @Param("checkResult") Integer checkResult,
            @Param("checkNote") String checkNote);

    /**
     * 根据主键删除售后质检
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 根据售后单ID删除售后质检
     */
    int deleteByAfterSaleId(Long afterSaleId);

    /**
     * 根据售后单ID列表批量删除售后质检
     */
    int deleteByAfterSaleIds(@Param("afterSaleIds") List<Long> afterSaleIds);

    /**
     * 统计待处理质检数量
     */
    int countPendingCheck();

    /**
     * 统计质检通过数量
     */
    int countPassedCheck();

    /**
     * 统计质检不通过数量
     */
    int countFailedCheck();
}