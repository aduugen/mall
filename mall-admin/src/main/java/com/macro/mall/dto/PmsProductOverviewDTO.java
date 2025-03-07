package com.macro.mall.dto;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PmsProductOverviewDTO implements Serializable {
    @ApiModelProperty("自增ID")
    private Long id;
    @ApiModelProperty("记录生成时间")
    private Date create_time;
    @ApiModelProperty("商品总数")
    private Integer product_total_count;
    @ApiModelProperty("上架商品总数")
    private Integer product_on_shelf_count;
    @ApiModelProperty("下架商品总数")
    private Integer product_off_shelf_count;
}