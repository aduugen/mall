package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.PmsCommentMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.mapper.OmsOrderItemMapper;
import com.macro.mall.model.PmsComment;
import com.macro.mall.model.PmsCommentExample;
import com.macro.mall.model.PmsProduct;
import com.macro.mall.model.PmsProductExample;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.OmsOrderItem;
import com.macro.mall.model.OmsOrderItemExample;
import com.macro.mall.portal.domain.ProductCommentSummary;
import com.macro.mall.portal.domain.PmsMemberCommentDto;
import com.macro.mall.portal.service.PmsCommentService;
import com.macro.mall.portal.service.UmsMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Objects;
import java.util.HashMap;

/**
 * 商品评价管理Service实现类
 * Created by macro on 2024/x/x. // Replace with actual date
 */
@Service
public class PmsCommentServiceImpl implements PmsCommentService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PmsCommentServiceImpl.class);

    @Autowired
    private PmsCommentMapper commentMapper;
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private OmsOrderItemMapper orderItemMapper;

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
        UmsMember member = memberService.getCurrentMember();
        if (member == null) {
            return CommonPage.restPage(new ArrayList<>());
        }

        PageHelper.startPage(pageNum, pageSize);
        PmsCommentExample example = new PmsCommentExample();
        example.createCriteria().andMemberIdEqualTo(member.getId());
        example.setOrderByClause("create_time desc");
        List<PmsComment> commentList = commentMapper.selectByExampleWithBLOBs(example);

        List<PmsMemberCommentDto> dtoList = new ArrayList<>();
        if (!CollectionUtils.isEmpty(commentList)) {
            List<Long> productIds = commentList.stream()
                    .map(PmsComment::getProductId).filter(Objects::nonNull).distinct().collect(Collectors.toList());
            List<Long> orderItemIds = commentList.stream()
                    .map(PmsComment::getOrderItemId).filter(Objects::nonNull).distinct().collect(Collectors.toList());

            Map<Long, String> productPicMap = getProductPicMap(productIds);
            Map<Long, BigDecimal> productPriceMap = getProductPriceMap(orderItemIds);

            for (PmsComment comment : commentList) {
                PmsMemberCommentDto dto = new PmsMemberCommentDto();
                BeanUtils.copyProperties(comment, dto);

                Long currentOrderItemId = comment.getOrderItemId();

                if (comment.getProductId() != null) {
                    dto.setProductPic(productPicMap.get(comment.getProductId()));
                }
                if (currentOrderItemId != null) {
                    BigDecimal price = productPriceMap.get(currentOrderItemId);
                    dto.setProductPrice(price);
                }
                dto.setStatusText(mapStatusToText(comment.getShowStatus()));
                dtoList.add(dto);
            }
        }

        CommonPage<PmsComment> originalPage = CommonPage.restPage(commentList);
        CommonPage<PmsMemberCommentDto> resultPage = new CommonPage<>();
        BeanUtils.copyProperties(originalPage, resultPage, "list");
        resultPage.setList(dtoList);

        if (!dtoList.isEmpty()) {
            LOGGER.info("getMyList DTO result (sample): {}", dtoList.get(0).toString());
        }

        return resultPage;
    }

    // 辅助方法：批量获取商品图片
    private Map<Long, String> getProductPicMap(List<Long> productIds) {
        Map<Long, String> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(productIds)) {
            PmsProductExample productExample = new PmsProductExample();
            productExample.createCriteria().andIdIn(productIds);
            // 使用 selectByExample 查询所有字段
            List<PmsProduct> productList = productMapper.selectByExample(productExample);
            map = productList.stream()
                    .filter(p -> p.getPic() != null) // 确保 pic 不为 null
                    .collect(Collectors.toMap(PmsProduct::getId, PmsProduct::getPic, (k1, k2) -> k1));
        }
        return map;
    }

    // 辅助方法：批量获取订单项价格
    private Map<Long, BigDecimal> getProductPriceMap(List<Long> orderItemIds) {
        Map<Long, BigDecimal> map = new HashMap<>();
        if (!CollectionUtils.isEmpty(orderItemIds)) {
            OmsOrderItemExample itemExample = new OmsOrderItemExample();
            itemExample.createCriteria().andIdIn(orderItemIds);
            // 使用 selectByExample 查询所有字段
            List<OmsOrderItem> itemList = orderItemMapper.selectByExample(itemExample);
            map = itemList.stream()
                    .filter(i -> i.getProductPrice() != null) // 确保 productPrice 不为 null
                    .collect(Collectors.toMap(OmsOrderItem::getId, OmsOrderItem::getProductPrice, (k1, k2) -> k1));
        }
        return map;
    }

    // 辅助方法：映射评价状态码到文本
    private String mapStatusToText(Integer status) {
        if (status == null) {
            return "未知"; // 或者 "审核中"，取决于业务定义
        }
        switch (status) {
            case 0:
                return "审核中"; // 假设 0 是待审核/审核中
            case 1:
                return "已显示"; // 假设 1 是审核通过并显示
            case 2:
                return "已隐藏"; // 假设 2 是审核通过但隐藏 (或审核不通过)
            // case 3: return "审核不通过"; // 根据实际状态定义添加
            default:
                return "未知状态 (" + status + ")";
        }
    }
}