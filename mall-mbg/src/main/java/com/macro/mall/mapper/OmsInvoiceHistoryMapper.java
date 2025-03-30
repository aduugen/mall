package com.macro.mall.mapper;

import com.macro.mall.model.OmsInvoiceHistory;
import com.macro.mall.model.OmsInvoiceHistoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OmsInvoiceHistoryMapper {
    long countByExample(OmsInvoiceHistoryExample example);

    int deleteByExample(OmsInvoiceHistoryExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsInvoiceHistory record);

    int insertSelective(OmsInvoiceHistory record);

    List<OmsInvoiceHistory> selectByExample(OmsInvoiceHistoryExample example);

    OmsInvoiceHistory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OmsInvoiceHistory record,
            @Param("example") OmsInvoiceHistoryExample example);

    int updateByExample(@Param("record") OmsInvoiceHistory record, @Param("example") OmsInvoiceHistoryExample example);

    int updateByPrimaryKeySelective(OmsInvoiceHistory record);

    int updateByPrimaryKey(OmsInvoiceHistory record);
}