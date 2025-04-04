package com.macro.mall.mapper;

import com.macro.mall.model.OmsAfterSaleItem;
import com.macro.mall.model.OmsAfterSaleItemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OmsAfterSaleItemMapper {
    long countByExample(OmsAfterSaleItemExample example);

    int deleteByExample(OmsAfterSaleItemExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsAfterSaleItem record);

    int insertSelective(OmsAfterSaleItem record);

    List<OmsAfterSaleItem> selectByExample(OmsAfterSaleItemExample example);

    OmsAfterSaleItem selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OmsAfterSaleItem record,
            @Param("example") OmsAfterSaleItemExample example);

    int updateByExample(@Param("record") OmsAfterSaleItem record, @Param("example") OmsAfterSaleItemExample example);

    int updateByPrimaryKeySelective(OmsAfterSaleItem record);

    int updateByPrimaryKey(OmsAfterSaleItem record);
}