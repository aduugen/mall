package com.macro.mall.mapper;

import com.macro.mall.model.OmsAfterSaleLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 售后日志数据访问层接口
 */
public interface OmsAfterSaleLogMapper {
    /**
     * 插入售后日志记录
     */
    int insert(OmsAfterSaleLog record);

    /**
     * 批量插入售后日志记录
     */
    int batchInsert(List<OmsAfterSaleLog> list);

    /**
     * 根据主键查询售后日志
     */
    OmsAfterSaleLog selectByPrimaryKey(Long id);

    /**
     * 根据售后单ID查询售后日志
     */
    List<OmsAfterSaleLog> selectByAfterSaleId(Long afterSaleId);

    /**
     * 根据操作人查询售后日志
     */
    List<OmsAfterSaleLog> selectByOperator(String operateMan);

    /**
     * 根据创建时间范围查询售后日志
     */
    List<OmsAfterSaleLog> selectByCreateTime(@Param("startTime") Date startTime, @Param("endTime") Date endTime);

    /**
     * 根据售后单ID和操作人查询售后日志
     */
    List<OmsAfterSaleLog> selectByAfterSaleIdAndOperator(@Param("afterSaleId") Long afterSaleId,
            @Param("operateMan") String operateMan);

    /**
     * 删除指定的售后日志
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 根据售后单ID删除售后日志
     */
    int deleteByAfterSaleId(Long afterSaleId);

    /**
     * 统计售后单的日志数量
     */
    int countByAfterSaleId(Long afterSaleId);
}