package com.macro.mall.mapper;

import com.macro.mall.model.PtnLogisticsCompany;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * 物流公司Mapper
 */
public interface PtnLogisticsCompanyMapper {
    /**
     * 根据主键查询
     */
    PtnLogisticsCompany selectByPrimaryKey(Long id);

    /**
     * 查询所有物流公司
     */
    List<PtnLogisticsCompany> selectAll();

    /**
     * 插入物流公司记录
     */
    int insert(PtnLogisticsCompany record);

    /**
     * 根据物流公司名称查询
     */
    List<PtnLogisticsCompany> selectByName(@Param("name") String name);
}