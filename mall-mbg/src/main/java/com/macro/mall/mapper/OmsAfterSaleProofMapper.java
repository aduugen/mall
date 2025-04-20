package com.macro.mall.mapper;

import com.macro.mall.model.OmsAfterSaleProof;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 售后凭证Mapper接口
 */
public interface OmsAfterSaleProofMapper {
    /**
     * 根据主键查询售后凭证
     */
    OmsAfterSaleProof selectByPrimaryKey(Long id);

    /**
     * 根据售后单ID查询售后凭证
     */
    List<OmsAfterSaleProof> selectByAfterSaleId(Long afterSaleId);

    /**
     * 根据售后单ID列表批量查询售后凭证
     */
    List<OmsAfterSaleProof> selectByAfterSaleIds(@Param("afterSaleIds") List<Long> afterSaleIds);

    /**
     * 根据售后商品项ID查询售后凭证
     */
    List<OmsAfterSaleProof> selectByItemId(Long itemId);

    /**
     * 新增售后凭证
     */
    int insert(OmsAfterSaleProof record);

    /**
     * 批量插入售后凭证
     */
    int batchInsert(@Param("list") List<OmsAfterSaleProof> list);

    /**
     * 根据主键修改售后凭证
     */
    int updateByPrimaryKeySelective(OmsAfterSaleProof record);

    /**
     * 根据主键修改售后凭证（全部字段）
     */
    int updateByPrimaryKey(OmsAfterSaleProof record);

    /**
     * 根据主键删除售后凭证
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 根据售后单ID删除售后凭证
     */
    int deleteByAfterSaleId(Long afterSaleId);

    /**
     * 根据售后单ID列表批量删除售后凭证
     */
    int deleteByAfterSaleIds(@Param("afterSaleIds") List<Long> afterSaleIds);

    /**
     * 根据售后商品项ID删除售后凭证
     */
    int deleteByItemId(Long itemId);
}