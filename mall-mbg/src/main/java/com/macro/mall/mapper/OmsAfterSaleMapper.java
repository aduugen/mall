package com.macro.mall.mapper;

import com.macro.mall.model.OmsAfterSale;
import com.macro.mall.model.OmsAfterSaleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OmsAfterSaleMapper {
    long countByExample(OmsAfterSaleExample example);

    int deleteByExample(OmsAfterSaleExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsAfterSale record);

    int insertSelective(OmsAfterSale record);

    List<OmsAfterSale> selectByExample(OmsAfterSaleExample example);

    OmsAfterSale selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OmsAfterSale record, @Param("example") OmsAfterSaleExample example);

    int updateByExample(@Param("record") OmsAfterSale record, @Param("example") OmsAfterSaleExample example);

    int updateByPrimaryKeySelective(OmsAfterSale record);

    int updateByPrimaryKey(OmsAfterSale record);
}