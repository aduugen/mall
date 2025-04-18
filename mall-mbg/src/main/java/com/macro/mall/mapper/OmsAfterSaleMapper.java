package com.macro.mall.mapper;

import com.macro.mall.model.OmsAfterSale;
import com.macro.mall.model.OmsAfterSaleExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface OmsAfterSaleMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    long countByExample(OmsAfterSaleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    int deleteByExample(OmsAfterSaleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    int insert(OmsAfterSale record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    int insertSelective(OmsAfterSale record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    List<OmsAfterSale> selectByExample(OmsAfterSaleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    OmsAfterSale selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    int updateByExampleSelective(@Param("record") OmsAfterSale record, @Param("example") OmsAfterSaleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    int updateByExample(@Param("record") OmsAfterSale record, @Param("example") OmsAfterSaleExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    int updateByPrimaryKeySelective(OmsAfterSale record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table oms_after_sale
     *
     * @mbg.generated Mon Apr 14 10:16:04 CST 2025
     */
    int updateByPrimaryKey(OmsAfterSale record);
}