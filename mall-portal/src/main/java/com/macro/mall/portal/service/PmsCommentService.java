package com.macro.mall.portal.service;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.model.PmsComment;
import com.macro.mall.portal.domain.ProductCommentSummary;
import com.macro.mall.portal.domain.PmsMemberCommentDto;

/**
 * 商品评价管理Service
 * Created by macro on 2024/x/x. // Replace with actual date
 */
public interface PmsCommentService {
    /**
     * 分页查询商品评价列表
     *
     * @param productId 商品ID
     * @param pageNum   当前页码
     * @param pageSize  每页数量
     * @return 评价分页数据
     */
    CommonPage<PmsComment> list(Long productId, Integer pageNum, Integer pageSize);

    /**
     * 获取商品评价统计信息（总数、好评率）
     *
     * @param productId 商品ID
     * @return 评价统计信息
     */
    ProductCommentSummary getSummary(Long productId);

    /**
     * 分页获取当前用户的评价列表
     * 
     * @param pageNum  页码
     * @param pageSize 每页数量
     * @return 包含商品信息的评价分页数据
     */
    CommonPage<PmsMemberCommentDto> getMyList(Integer pageNum, Integer pageSize);
}