package com.macro.mall.mapper;

import com.macro.mall.model.OmsAfterSaleItem;
import com.macro.mall.model.OmsAfterSaleItemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OmsAfterSaleItemMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale_item
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    long countByExample(OmsAfterSaleItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale_item
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    int deleteByExample(OmsAfterSaleItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale_item
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale_item
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    int insert(OmsAfterSaleItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale_item
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    int insertSelective(OmsAfterSaleItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale_item
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    List<OmsAfterSaleItem> selectByExample(OmsAfterSaleItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale_item
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    OmsAfterSaleItem selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale_item
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    int updateByExampleSelective(@Param("record") OmsAfterSaleItem record, @Param("example") OmsAfterSaleItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale_item
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    int updateByExample(@Param("record") OmsAfterSaleItem record, @Param("example") OmsAfterSaleItemExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale_item
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    int updateByPrimaryKeySelective(OmsAfterSaleItem record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale_item
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    int updateByPrimaryKey(OmsAfterSaleItem record);
}