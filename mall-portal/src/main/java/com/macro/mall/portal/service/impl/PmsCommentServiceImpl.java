package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.PmsCommentMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.PmsComment;
import com.macro.mall.model.PmsCommentExample;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsProductExample;
import com.macro.mall.model.UmsMember;
import com.macro.mall.portal.domain.ProductCommentSummary;
import com.macro.mall.portal.domain.PmsMemberCommentDto;
import com.macro.mall.portal.service.PmsCommentService;
import com.macro.mall.portal.service.UmsMemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品评价管理Service实现类
 * Created by macro on 2024/x/x. // Replace with actual date
 */
@Service
public class PmsCommentServiceImpl implements PmsCommentService {

    @Autowired
    private PmsCommentMapper commentMapper;
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private PmsProductMapper productMapper;

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

    @Override
    public CommonPage<PmsMemberCommentDto> getMyList(Integer pageNum, Integer pageSize) {
        // 获取当前登录用户
        UmsMember member = memberService.getCurrentMember();
        if (member == null) {
            // 可以返回空数据或抛出异常，这里返回空数据
            return CommonPage.restPage(new ArrayList<>());
        }

        PageHelper.startPage(pageNum, pageSize);
        PmsCommentExample example = new PmsCommentExample();
        example.createCriteria().andMemberIdEqualTo(member.getId());
        example.setOrderByClause("create_time desc");
        List<PmsComment> commentList = commentMapper.selectByExampleWithBLOBs(example);

        // 将 PmsComment 转换为 PmsMemberCommentDto 并填充商品图片
        List<PmsMemberCommentDto> dtoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(commentList)) {
            // 提取所有 productId
            List<Long> productIds = commentList.stream()
                    .map(PmsComment::getProductId)
                    .distinct()
                    .collect(Collectors.toList());

            // 批量查询商品信息 (只需要 id 和 pic)
            PmsProductExample productExample = new PmsProductExample();
            productExample.createCriteria().andIdIn(productIds);
            List<PmsProduct> productList = productMapper.selectByExample(productExample);
            Map<Long, String> productPicMap = productList.stream()
                    .collect(Collectors.toMap(PmsProduct::getId, PmsProduct::getPic));

            // 组装 DTO
            for (PmsComment comment : commentList) {
                PmsMemberCommentDto dto = new PmsMemberCommentDto();
                BeanUtils.copyProperties(comment, dto);
                dto.setProductPic(productPicMap.get(comment.getProductId()));
                dtoList.add(dto);
            }
        }

        // 封装分页信息
        CommonPage<PmsComment> originalPage = CommonPage.restPage(commentList);
        CommonPage<PmsMemberCommentDto> resultPage = new CommonPage<>();
        BeanUtils.copyProperties(originalPage, resultPage, "list"); // 复制分页属性，除了 list
        resultPage.setList(dtoList);

        return resultPage;
    }
}