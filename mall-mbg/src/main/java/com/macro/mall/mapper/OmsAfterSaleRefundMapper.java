package com.macro.mall.mapper;

import com.macro.mall.model.OmsAfterSaleRefund;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 售后退款记录数据访问层接口
 */
public interface OmsAfterSaleRefundMapper {
    /**
     * 插入退款记录
     */
    int insert(OmsAfterSaleRefund record);

    /**
     * 选择性插入退款记录
     */
    int insertSelective(OmsAfterSaleRefund record);

    /**
     * 批量插入退款记录
     */
    int batchInsert(@Param("list") List<OmsAfterSaleRefund> list);

    /**
     * 根据主键查询退款记录
     */
    OmsAfterSaleRefund selectByPrimaryKey(Long id);

    /**
     * 根据售后单ID查询退款记录
     */
    OmsAfterSaleRefund selectByAfterSaleId(Long afterSaleId);

    /**
     * 根据售后单ID列表批量查询退款记录
     */
    List<OmsAfterSaleRefund> selectByAfterSaleIds(@Param("afterSaleIds") List<Long> afterSaleIds);

    /**
     * 根据退款单号查询退款记录
     */
    OmsAfterSaleRefund selectByRefundNo(String refundNo);

    /**
     * 根据退款状态查询退款记录
     */
    List<OmsAfterSaleRefund> selectByRefundStatus(Integer refundStatus);

    /**
     * 根据退款时间范围查询退款记录
     */
    List<OmsAfterSaleRefund> selectByRefundTime(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 根据主键修改退款记录
     */
    int updateByPrimaryKeySelective(OmsAfterSaleRefund record);

    /**
     * 根据主键修改退款记录（全部字段）
     */
    int updateByPrimaryKey(OmsAfterSaleRefund record);

    /**
     * 更新退款状态
     */
    int updateRefundStatus(OmsAfterSaleRefund record);

    /**
     * 根据售后单ID更新退款状态
     */
    int updateRefundStatusByAfterSaleId(@Param("afterSaleId") Long afterSaleId,
            @Param("refundStatus") Integer refundStatus,
            @Param("refundTime") Date refundTime,
            @Param("refundNote") String refundNote);

    /**
     * 根据主键删除退款记录
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 根据售后单ID删除退款记录
     */
    int deleteByAfterSaleId(Long afterSaleId);

    /**
     * 根据售后单ID列表批量删除退款记录
     */
    int deleteByAfterSaleIds(@Param("afterSaleIds") List<Long> afterSaleIds);

    /**
     * 统计待处理退款数量
     */
    int countPendingRefund();

    /**
     * 统计退款成功数量
     */
    int countSuccessRefund();

    /**
     * 统计退款失败数量
     */
    int countFailedRefund();

    /**
     * 查询退款金额总和
     */
    BigDecimal sumRefundAmount();

    /**
     * 根据时间范围查询退款金额总和
     */
    BigDecimal sumRefundAmountByTime(@Param("startTime") Date startTime, @Param("endTime") Date endTime);
}