package com.macro.mall.mapper;

import com.macro.mall.model.OmsAfterSale;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OmsAfterSaleMapper {
    int deleteByPrimaryKey(Long id);

    int insert(OmsAfterSale record);

    int insertSelective(OmsAfterSale record);

    OmsAfterSale selectByPrimaryKey(Long id);

    List<OmsAfterSale> selectByStatus(Integer status);

    int updateByPrimaryKeySelective(OmsAfterSale record);

    int updateByPrimaryKey(OmsAfterSale record);
}