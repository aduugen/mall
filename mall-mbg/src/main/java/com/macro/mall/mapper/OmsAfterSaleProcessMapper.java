package com.macro.mall.mapper;

import com.macro.mall.model.OmsAfterSaleProcess;
import com.macro.mall.model.OmsAfterSaleProcessExample;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 售后处理记录Mapper接口
 */
public interface OmsAfterSaleProcessMapper {
    /**
     * 根据条件查询售后处理记录列表
     */
    List<OmsAfterSaleProcess> selectByExample(OmsAfterSaleProcessExample example);

    /**
     * 根据主键查询售后处理记录
     */
    OmsAfterSaleProcess selectByPrimaryKey(Long id);

    /**
     * 根据售后单ID查询售后处理记录
     */
    List<OmsAfterSaleProcess> selectByAfterSaleId(Long afterSaleId);

    /**
     * 根据售后单ID列表批量查询售后处理记录
     */
    List<OmsAfterSaleProcess> selectByAfterSaleIds(@Param("afterSaleIds") List<Long> afterSaleIds);

    /**
     * 新增售后处理记录
     */
    int insert(OmsAfterSaleProcess record);

    /**
     * 选择性新增售后处理记录
     */
    int insertSelective(OmsAfterSaleProcess record);

    /**
     * 批量插入售后处理记录
     */
    int batchInsert(@Param("list") List<OmsAfterSaleProcess> list);

    /**
     * 根据条件修改售后处理记录
     */
    int updateByExampleSelective(@Param("record") OmsAfterSaleProcess record,
            @Param("example") OmsAfterSaleProcessExample example);

    /**
     * 根据主键修改售后处理记录
     */
    int updateByPrimaryKeySelective(OmsAfterSaleProcess record);

    /**
     * 根据主键修改售后处理记录（全部字段）
     */
    int updateByPrimaryKey(OmsAfterSaleProcess record);

    /**
     * 根据条件删除售后处理记录
     */
    int deleteByExample(OmsAfterSaleProcessExample example);

    /**
     * 根据主键删除售后处理记录
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 根据售后单ID删除售后处理记录
     */
    int deleteByAfterSaleId(Long afterSaleId);

    /**
     * 根据售后单ID列表批量删除售后处理记录
     */
    int deleteByAfterSaleIds(@Param("afterSaleIds") List<Long> afterSaleIds);

    /**
     * 统计售后处理数量
     */
    long countByExample(OmsAfterSaleProcessExample example);
}