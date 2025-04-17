package com.macro.mall.mapper;

import com.macro.mall.model.OmsAfterSaleProof;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 售后凭证图片数据访问层接口
 */
public interface OmsAfterSaleProofMapper {
    /**
     * 插入凭证记录
     */
    int insert(OmsAfterSaleProof record);

    /**
     * 批量插入凭证记录
     */
    int batchInsert(List<OmsAfterSaleProof> list);

    /**
     * 根据主键查询凭证
     */
    OmsAfterSaleProof selectByPrimaryKey(Long id);

    /**
     * 根据售后单ID查询凭证
     */
    List<OmsAfterSaleProof> selectByAfterSaleId(Long afterSaleId);

    /**
     * 根据售后单条目ID查询凭证
     */
    List<OmsAfterSaleProof> selectByItemId(Long itemId);

    /**
     * 根据类型查询凭证
     */
    List<OmsAfterSaleProof> selectByPicType(@Param("afterSaleId") Long afterSaleId, @Param("picType") Integer picType);

    /**
     * 统计售后单凭证数量
     */
    int countByAfterSaleId(Long afterSaleId);

    /**
     * 删除凭证
     */
    int deleteByPrimaryKey(Long id);

    /**
     * 删除售后单的所有凭证
     */
    int deleteByAfterSaleId(Long afterSaleId);

    /**
     * 删除售后单条目的所有凭证
     */
    int deleteByItemId(Long itemId);
}