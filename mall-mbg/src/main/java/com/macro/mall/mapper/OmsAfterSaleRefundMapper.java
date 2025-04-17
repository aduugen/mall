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
     * 根据主键查询退款记录
     */
    OmsAfterSaleRefund selectByPrimaryKey(Long id);

    /**
     * 根据售后单ID查询退款记录
     */
    OmsAfterSaleRefund selectByAfterSaleId(Long afterSaleId);

    /**
     * 根据退款单号查询退款记录
     */
    OmsAfterSaleRefund selectByRefundNo(String refundNo);

    /**
     * 根据退款状态查询退款记录
     */
    List<OmsAfterSaleRefund> selectByRefundStatus(Integer refundStatus);

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
     * 删除退款记录
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 删除售后单的所有退款记录
     */
    int deleteByAfterSaleId(Long afterSaleId);

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
}