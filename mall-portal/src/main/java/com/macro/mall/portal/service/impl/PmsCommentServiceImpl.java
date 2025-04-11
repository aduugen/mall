package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.mapper.PmsCommentMapper;
import com.macro.mall.model.PmsComment;
import com.macro.mall.model.PmsCommentExample;
import com.macro.mall.portal.domain.ProductCommentSummary;
import com.macro.mall.portal.service.PmsCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 商品评价管理Service实现类
 * Created by macro on 2024/x/x. // Replace with actual date
 */
@Service
public class PmsCommentServiceImpl implements PmsCommentService {

    @Autowired
    private PmsCommentMapper commentMapper;

    // 定义好评的最低星级
    private static final int POSITIVE_RATING_THRESHOLD = 4;

    @Override
    public CommonPage<PmsComment> list(Long productId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        PmsCommentExample example = new PmsCommentExample();
        // 查询指定商品的、显示状态为显示的评价
        example.createCriteria().andProductIdEqualTo(productId).andShowStatusEqualTo(1);
        // 按创建时间降序排列
        example.setOrderByClause("create_time desc");
        List<PmsComment> commentList = commentMapper.selectByExampleWithBLOBs(example); // 使用 WithBLOBs 获取 content 字段
        return CommonPage.restPage(commentList);
    }

    @Override
    public ProductCommentSummary getSummary(Long productId) {
        ProductCommentSummary summary = new ProductCommentSummary();

        // 查询总评价数
        PmsCommentExample countExample = new PmsCommentExample();
        countExample.createCriteria().andProductIdEqualTo(productId).andShowStatusEqualTo(1);
        long totalCount = commentMapper.countByExample(countExample);
        summary.setTotalCount(totalCount);

        if (totalCount > 0) {
            // 查询好评数（评分 >= POSITIVE_RATING_THRESHOLD）
            PmsCommentExample positiveExample = new PmsCommentExample();
            positiveExample.createCriteria()
                    .andProductIdEqualTo(productId)
                    .andShowStatusEqualTo(1)
                    .andStarGreaterThanOrEqualTo(POSITIVE_RATING_THRESHOLD);
            long positiveCount = commentMapper.countByExample(positiveExample);

            // 计算好评率，保留一位小数
            double positiveRate = BigDecimal.valueOf(positiveCount)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalCount), 1, RoundingMode.HALF_UP)
                    .doubleValue();
            summary.setPositiveRate(positiveRate);
        } else {
            // 没有评价时，好评率为0
            summary.setPositiveRate(0.0);
        }

        return summary;
    }
}