package com.macro.mall.portal.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.portal.dao.PortalProductDao;
import com.macro.mall.portal.domain.PmsPortalProductDetail;
import com.macro.mall.portal.domain.PmsProductCategoryNode;
import com.macro.mall.portal.service.PmsPortalProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 前台订单管理Service实现类
 * Created by macro on 2020/4/6.
 */
@Service
public class PmsPortalProductServiceImpl implements PmsPortalProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PmsPortalProductServiceImpl.class);

    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private PmsProductCategoryMapper productCategoryMapper;
    @Autowired
    private PmsBrandMapper brandMapper;
    @Autowired
    private PmsProductAttributeMapper productAttributeMapper;
    @Autowired
    private PmsProductAttributeValueMapper productAttributeValueMapper;
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    @Autowired
    private PmsProductLadderMapper productLadderMapper;
    @Autowired
    private PmsProductFullReductionMapper productFullReductionMapper;
    @Autowired
    private PortalProductDao portalProductDao;

    @Override
    public List<PmsProduct> search(String keyword, Long brandId, Long productCategoryId, Integer pageNum,
            Integer pageSize, Integer sort) {
        PageHelper.startPage(pageNum, pageSize);
        PmsProductExample example = new PmsProductExample();
        PmsProductExample.Criteria criteria = example.createCriteria();
        criteria.andDeleteStatusEqualTo(0);
        criteria.andPublishStatusEqualTo(1);
        if (StrUtil.isNotEmpty(keyword)) {
            criteria.andNameLike("%" + keyword + "%");
        }
        if (brandId != 0) {
            criteria.andBrandIdEqualTo(brandId);
        }
        if (productCategoryId != 0) {
            criteria.andProductCategoryIdEqualTo(productCategoryId);
        }
        // 1->按新品；2->按销量；3->价格从低到高；4->价格从高到低
        if (sort == 1) {
            example.setOrderByClause("id desc");
        } else if (sort == 2) {
            example.setOrderByClause("sale desc");
        } else if (sort == 3) {
            example.setOrderByClause("price asc");
        } else if (sort == 4) {
            example.setOrderByClause("price desc");
        }
        return productMapper.selectByExample(example);
    }

    @Override
    public List<PmsProductCategoryNode> categoryTreeList() {
        PmsProductCategoryExample example = new PmsProductCategoryExample();
        List<PmsProductCategory> allList = productCategoryMapper.selectByExample(example);
        List<PmsProductCategoryNode> result = allList.stream()
                .filter(item -> item.getParentId().equals(0L))
                .map(item -> covert(item, allList))
                .collect(Collectors.toList());
        return result;
    }

    @Override
    public PmsPortalProductDetail detail(Long id) {
        PmsPortalProductDetail result = new PmsPortalProductDetail();
        // 获取商品信息
        PmsProduct product = productMapper.selectByPrimaryKey(id);

        // 获取商品SKU库存信息
        PmsSkuStockExample skuExample = new PmsSkuStockExample();
        skuExample.createCriteria().andProductIdEqualTo(product.getId());
        List<PmsSkuStock> skuStockList = skuStockMapper.selectByExample(skuExample);
        result.setSkuStockList(skuStockList);

        // 价格逻辑处理: 如果商品的price和promotion_price都为null，从SKU中获取价格
        if ((product.getPrice() == null || product.getPrice().compareTo(new java.math.BigDecimal("0")) == 0) &&
                (product.getPromotionPrice() == null
                        || product.getPromotionPrice().compareTo(new java.math.BigDecimal("0")) == 0)) {

            // 记录日志 - 使用参数化格式
            LOGGER.info("商品价格为空，尝试从SKU获取价格，商品ID: {}", id);

            // 如果有SKU数据，使用第一个SKU的价格
            if (skuStockList != null && !skuStockList.isEmpty()) {
                PmsSkuStock defaultSku = skuStockList.get(0);

                // 设置原价
                if (defaultSku.getPrice() != null
                        && defaultSku.getPrice().compareTo(new java.math.BigDecimal("0")) > 0) {
                    product.setPrice(defaultSku.getPrice());
                    LOGGER.info("从SKU获取价格: {}, 商品ID: {}", defaultSku.getPrice(), id);
                }

                // 设置促销价
                if (defaultSku.getPromotionPrice() != null
                        && defaultSku.getPromotionPrice().compareTo(new java.math.BigDecimal("0")) > 0) {
                    product.setPromotionPrice(defaultSku.getPromotionPrice());
                    LOGGER.info("从SKU获取促销价: {}, 商品ID: {}", defaultSku.getPromotionPrice(), id);
                }
            } else {
                LOGGER.info("商品没有SKU数据，无法获取价格，商品ID: {}", id);
            }
        }

        result.setProduct(product);

        // 获取品牌信息
        PmsBrand brand = brandMapper.selectByPrimaryKey(product.getBrandId());
        result.setBrand(brand);
        // 获取商品属性信息
        PmsProductAttributeExample attributeExample = new PmsProductAttributeExample();
        attributeExample.createCriteria().andProductAttributeCategoryIdEqualTo(product.getProductAttributeCategoryId());
        List<PmsProductAttribute> productAttributeList = productAttributeMapper.selectByExample(attributeExample);
        result.setProductAttributeList(productAttributeList);
        // 获取商品属性值信息
        if (CollUtil.isNotEmpty(productAttributeList)) {
            List<Long> attributeIds = productAttributeList.stream().map(PmsProductAttribute::getId)
                    .collect(Collectors.toList());
            PmsProductAttributeValueExample attributeValueExample = new PmsProductAttributeValueExample();
            attributeValueExample.createCriteria().andProductIdEqualTo(product.getId())
                    .andProductAttributeIdIn(attributeIds);
            List<PmsProductAttributeValue> productAttributeValueList = productAttributeValueMapper
                    .selectByExample(attributeValueExample);
            result.setProductAttributeValueList(productAttributeValueList);
        }

        // 商品阶梯价格设置
        if (product.getPromotionType() == 3) {
            PmsProductLadderExample ladderExample = new PmsProductLadderExample();
            ladderExample.createCriteria().andProductIdEqualTo(product.getId());
            List<PmsProductLadder> productLadderList = productLadderMapper.selectByExample(ladderExample);
            result.setProductLadderList(productLadderList);
        }
        // 商品满减价格设置
        if (product.getPromotionType() == 4) {
            PmsProductFullReductionExample fullReductionExample = new PmsProductFullReductionExample();
            fullReductionExample.createCriteria().andProductIdEqualTo(product.getId());
            List<PmsProductFullReduction> productFullReductionList = productFullReductionMapper
                    .selectByExample(fullReductionExample);
            result.setProductFullReductionList(productFullReductionList);
        }
        // 商品可用优惠券
        result.setCouponList(portalProductDao.getAvailableCouponList(product.getId(), product.getProductCategoryId()));
        return result;
    }

    @Override
    public void updateProductViewCount(Long id) {
        PmsProduct product = productMapper.selectByPrimaryKey(id);
        if (product != null) {
            // 浏览量字段是viewCnt（从实体类中可以看到）
            if (product.getViewCnt() == null) {
                product.setViewCnt(1);
            } else {
                product.setViewCnt(product.getViewCnt() + 1);
            }
            productMapper.updateByPrimaryKeySelective(product);
        }
    }

    /**
     * 初始对象转化为节点对象
     */
    private PmsProductCategoryNode covert(PmsProductCategory item, List<PmsProductCategory> allList) {
        PmsProductCategoryNode node = new PmsProductCategoryNode();
        BeanUtils.copyProperties(item, node);
        List<PmsProductCategoryNode> children = allList.stream()
                .filter(subItem -> subItem.getParentId().equals(item.getId()))
                .map(subItem -> covert(subItem, allList)).collect(Collectors.toList());
        node.setChildren(children);
        return node;
    }

    @Override
    public boolean checkProductAvailability(Long id) {
        PmsProduct product = productMapper.selectByPrimaryKey(id);
        if (product == null) {
            // 商品不存在
            return false;
        }
        // 检查上架状态（publish_status=1）和删除状态（delete_status=0）
        boolean isAvailable = product.getPublishStatus() != null && product.getPublishStatus() == 1 &&
                product.getDeleteStatus() != null && product.getDeleteStatus() == 0;
        LOGGER.info("检查商品可用性: productId={}, publishStatus={}, deleteStatus={}, isAvailable={}",
                id, product.getPublishStatus(), product.getDeleteStatus(), isAvailable);
        return isAvailable;
    }
}
