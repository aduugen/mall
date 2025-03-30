package com.macro.mall.mapper;

import com.macro.mall.model.OmsInvoice;
import com.macro.mall.model.OmsInvoiceExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OmsInvoiceMapper {
    long countByExample(OmsInvoiceExample example);

    int deleteByExample(OmsInvoiceExample example);

    int deleteByPrimaryKey(Long id);

    int insert(OmsInvoice record);

    int insertSelective(OmsInvoice record);

    List<OmsInvoice> selectByExample(OmsInvoiceExample example);

    OmsInvoice selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") OmsInvoice record, @Param("example") OmsInvoiceExample example);

    int updateByExample(@Param("record") OmsInvoice record, @Param("example") OmsInvoiceExample example);

    int updateByPrimaryKeySelective(OmsInvoice record);

    int updateByPrimaryKey(OmsInvoice record);
}