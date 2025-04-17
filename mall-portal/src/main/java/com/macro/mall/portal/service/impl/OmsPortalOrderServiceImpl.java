package com.macro.mall.portal.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.common.service.RedisService;
import com.macro.mall.mapper.*;
import com.macro.mall.model.*;
import com.macro.mall.portal.component.CancelOrderSender;
import com.macro.mall.portal.dao.PortalOrderDao;
import com.macro.mall.portal.dao.PortalOrderItemDao;
import com.macro.mall.portal.dao.SmsCouponHistoryDao;
import com.macro.mall.portal.domain.*;
import com.macro.mall.portal.service.*;
import com.macro.mall.portal.util.RedisDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.ArrayList;

/**
 * 前台订单管理Service
 * Created by macro on 2018/8/30.
 */
@Slf4j
@Service
public class OmsPortalOrderServiceImpl implements OmsPortalOrderService {
    @Autowired
    private UmsMemberService memberService;
    @Autowired
    private OmsCartItemService cartItemService;
    @Autowired
    private UmsMemberReceiveAddressService memberReceiveAddressService;
    @Autowired
    private UmsMemberCouponService memberCouponService;
    @Autowired
    private UmsIntegrationConsumeSettingMapper integrationConsumeSettingMapper;
    @Autowired
    private PmsSkuStockMapper skuStockMapper;
    @Autowired
    private SmsCouponHistoryDao couponHistoryDao;
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private PortalOrderItemDao orderItemDao;
    @Autowired
    private SmsCouponHistoryMapper couponHistoryMapper;
    @Autowired
    private RedisService redisService;
    @Value("${redis.key.orderId}")
    private String REDIS_KEY_ORDER_ID;
    @Value("${redis.database}")
    private String REDIS_DATABASE;
    @Autowired
    private PortalOrderDao portalOrderDao;
    @Autowired
    private OmsOrderSettingMapper orderSettingMapper;
    @Autowired
    private OmsOrderItemMapper orderItemMapper;
    @Autowired
    private CancelOrderSender cancelOrderSender;
    @Autowired
    private PmsCommentMapper commentMapper;
    @Autowired
    private PmsProductMapper productMapper;
    @Autowired
    private RedisDistributedLock redisDistributedLock;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Override
    public ConfirmOrderResult generateConfirmOrder(List<Long> cartIds) {
        ConfirmOrderResult result = new ConfirmOrderResult();
        // 获取购物车信息
        UmsMember currentMember = memberService.getCurrentMember();
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(), cartIds);
        result.setCartPromotionItemList(cartPromotionItemList);
        // 获取用户收货地址列表
        List<UmsMemberReceiveAddress> memberReceiveAddressList = memberReceiveAddressService.list();
        result.setMemberReceiveAddressList(memberReceiveAddressList);
        // 获取用户可用优惠券列表
        List<SmsCouponHistoryDetail> couponHistoryDetailList = memberCouponService.listCart(cartPromotionItemList, 1);
        result.setCouponHistoryDetailList(couponHistoryDetailList);
        // 获取用户积分
        result.setMemberIntegration(currentMember.getIntegration());
        // 获取积分使用规则
        UmsIntegrationConsumeSetting integrationConsumeSetting = integrationConsumeSettingMapper.selectByPrimaryKey(1L);
        result.setIntegrationConsumeSetting(integrationConsumeSetting);
        // 计算总金额、活动优惠、应付金额
        ConfirmOrderResult.CalcAmount calcAmount = calcCartAmount(cartPromotionItemList);
        result.setCalcAmount(calcAmount);
        return result;
    }

    @Override
    public Map<String, Object> generateOrder(OrderParam orderParam) {
        List<OmsOrderItem> orderItemList = new ArrayList<>();
        // 校验收货地址
        if (orderParam.getMemberReceiveAddressId() == null) {
            Asserts.fail("请选择收货地址！");
        }
        // 获取购物车及优惠信息
        UmsMember currentMember = memberService.getCurrentMember();
        List<CartPromotionItem> cartPromotionItemList = cartItemService.listPromotion(currentMember.getId(),
                orderParam.getCartIds());
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            // 生成下单商品信息
            OmsOrderItem orderItem = new OmsOrderItem();
            orderItem.setProductId(cartPromotionItem.getProductId());
            orderItem.setProductName(cartPromotionItem.getProductName());
            orderItem.setProductPic(cartPromotionItem.getProductPic());
            orderItem.setProductAttr(cartPromotionItem.getProductAttr());
            orderItem.setProductBrand(cartPromotionItem.getProductBrand());
            orderItem.setProductSn(cartPromotionItem.getProductSn());
            orderItem.setProductPrice(cartPromotionItem.getPrice());
            orderItem.setProductQuantity(cartPromotionItem.getQuantity());
            orderItem.setProductSkuId(cartPromotionItem.getProductSkuId());
            orderItem.setProductSkuCode(cartPromotionItem.getProductSkuCode());
            orderItem.setProductCategoryId(cartPromotionItem.getProductCategoryId());
            orderItem.setPromotionAmount(cartPromotionItem.getReduceAmount());
            orderItem.setPromotionName(cartPromotionItem.getPromotionMessage());
            orderItem.setGiftIntegration(cartPromotionItem.getIntegration());
            orderItem.setGiftGrowth(cartPromotionItem.getGrowth());
            orderItemList.add(orderItem);
        }
        // 判断购物车中商品是否都有库存
        if (!hasStock(cartPromotionItemList)) {
            Asserts.fail("库存不足，无法下单");
        }
        // 判断使用使用了优惠券
        if (orderParam.getCouponId() == null) {
            // 不用优惠券
            for (OmsOrderItem orderItem : orderItemList) {
                orderItem.setCouponAmount(new BigDecimal(0));
            }
        } else {
            // 使用优惠券
            SmsCouponHistoryDetail couponHistoryDetail = getUseCoupon(cartPromotionItemList, orderParam.getCouponId());
            if (couponHistoryDetail == null) {
                Asserts.fail("该优惠券不可用");
            }
            // 对下单商品的优惠券进行处理
            handleCouponAmount(orderItemList, couponHistoryDetail);
        }
        // 判断是否使用积分
        if (orderParam.getUseIntegration() == null || orderParam.getUseIntegration().equals(0)) {
            // 不使用积分
            for (OmsOrderItem orderItem : orderItemList) {
                orderItem.setIntegrationAmount(new BigDecimal(0));
            }
        } else {
            // 使用积分
            BigDecimal totalAmount = calcTotalAmount(orderItemList);
            BigDecimal integrationAmount = getUseIntegrationAmount(orderParam.getUseIntegration(), totalAmount,
                    currentMember, orderParam.getCouponId() != null);
            if (integrationAmount.compareTo(new BigDecimal(0)) == 0) {
                Asserts.fail("积分不可用");
            } else {
                // 可用情况下分摊到可用商品中
                for (OmsOrderItem orderItem : orderItemList) {
                    BigDecimal perAmount = orderItem.getProductPrice().divide(totalAmount, 3, RoundingMode.HALF_EVEN)
                            .multiply(integrationAmount);
                    orderItem.setIntegrationAmount(perAmount);
                }
            }
        }
        // 计算order_item的实付金额
        handleRealAmount(orderItemList);
        // 进行库存锁定
        lockStock(cartPromotionItemList);
        // 根据商品合计、运费、活动优惠、优惠券、积分计算应付金额
        OmsOrder order = new OmsOrder();
        order.setDiscountAmount(new BigDecimal(0));
        order.setTotalAmount(calcTotalAmount(orderItemList));
        order.setFreightAmount(new BigDecimal(0));
        order.setPromotionAmount(calcPromotionAmount(orderItemList));
        order.setPromotionInfo(getOrderPromotionInfo(orderItemList));
        if (orderParam.getCouponId() == null) {
            order.setCouponAmount(new BigDecimal(0));
        } else {
            order.setCouponId(orderParam.getCouponId());
            order.setCouponAmount(calcCouponAmount(orderItemList));
        }
        if (orderParam.getUseIntegration() == null) {
            order.setIntegration(0);
            order.setIntegrationAmount(new BigDecimal(0));
        } else {
            order.setIntegration(orderParam.getUseIntegration());
            order.setIntegrationAmount(calcIntegrationAmount(orderItemList));
        }
        order.setPayAmount(calcPayAmount(order));
        // 转化为订单信息并插入数据库
        order.setMemberId(currentMember.getId());
        order.setCreateTime(new Date());
        order.setMemberUsername(currentMember.getUsername());
        // 支付方式：0->未支付；1->支付宝；2->微信
        order.setPayType(orderParam.getPayType());
        // 订单来源：0->PC订单；1->app订单
        order.setSourceType(1);
        // 订单状态：0->待付款；1->待发货；2->已发货；3->已完成；4->已关闭；5->无效订单
        order.setStatus(0);
        // 订单类型：0->正常订单；1->秒杀订单
        order.setOrderType(0);
        // 售后状态：0->未申请；1->部分申请；2->全部申请
        order.setAfterSaleStatus((byte) 0);
        // 发票状态：0->未申请；1->申请中；2->已开票；3->申请失败
        order.setInvoiceStatus(0);
        // 收货人信息：姓名、电话、邮编、地址
        UmsMemberReceiveAddress address = memberReceiveAddressService.getItem(orderParam.getMemberReceiveAddressId());
        order.setReceiverName(address.getName());
        order.setReceiverPhone(address.getPhoneNumber());
        order.setReceiverPostCode(address.getPostCode());
        order.setReceiverProvince(address.getProvince());
        order.setReceiverCity(address.getCity());
        order.setReceiverRegion(address.getRegion());
        order.setReceiverDetailAddress(address.getDetailAddress());
        // 0->未确认；1->已确认
        order.setConfirmStatus(0);
        order.setDeleteStatus(0);
        // 计算赠送积分
        order.setIntegration(calcGifIntegration(orderItemList));
        // 计算赠送成长值
        order.setGrowth(calcGiftGrowth(orderItemList));
        // 生成订单号
        order.setOrderSn(generateOrderSn(order));
        // 设置自动收货天数
        List<OmsOrderSetting> orderSettings = orderSettingMapper.selectByExample(new OmsOrderSettingExample());
        if (CollUtil.isNotEmpty(orderSettings)) {
            order.setAutoConfirmDay(orderSettings.get(0).getConfirmOvertime());
        }
        // TODO: 2018/9/3 bill_*,delivery_* -> 已确认 bill_* 字段未使用，delivery_*
        // 字段可能在其他地方使用，暂时保留注释
        // 插入order表和order_item表
        orderMapper.insert(order);
        for (OmsOrderItem orderItem : orderItemList) {
            orderItem.setOrderId(order.getId());
            orderItem.setOrderSn(order.getOrderSn());
        }
        orderItemDao.insertList(orderItemList);
        // 如使用优惠券更新优惠券使用状态
        if (orderParam.getCouponId() != null) {
            updateCouponStatus(orderParam.getCouponId(), currentMember.getId(), 1);
        }
        // 如使用积分需要扣除积分
        if (orderParam.getUseIntegration() != null) {
            order.setUseIntegration(orderParam.getUseIntegration());
            if (currentMember.getIntegration() == null) {
                currentMember.setIntegration(0);
            }
            memberService.updateIntegration(currentMember.getId(),
                    currentMember.getIntegration() - orderParam.getUseIntegration());
        }
        // 删除购物车中的下单商品
        deleteCartItemList(cartPromotionItemList, currentMember);
        // 发送延迟消息取消订单
        sendDelayMessageCancelOrder(order.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("order", order);
        result.put("orderItemList", orderItemList);
        return result;
    }

    @Override
    @Transactional
    public Integer paySuccess(Long orderId, Integer payType) {
        log.info("处理订单支付成功：orderId={}, payType={}", orderId, payType);

        // 生成锁的唯一标识
        String requestId = UUID.randomUUID().toString();
        try {
            // 获取分布式锁
            boolean locked = acquirePayLock(orderId);
            if (!locked) {
                log.warn("获取订单支付锁失败，可能有其他线程正在处理该订单: orderId={}", orderId);
                // 此处可以选择等待并重试，或者直接返回
                return 0;
            }

            // 检查订单支付状态，实现幂等性
            OmsOrder existOrder = orderMapper.selectByPrimaryKey(orderId);
            if (existOrder == null) {
                log.error("订单不存在: orderId={}", orderId);
                Asserts.fail("订单不存在");
            }

            // 订单已支付，直接返回成功，实现幂等性
            if (existOrder.getStatus() != 0) {
                log.info("订单已处理，无需重复处理: orderId={}, status={}", orderId, existOrder.getStatus());
                return 1; // 返回1表示处理成功，但实际未做变更
            }

            // 修改订单支付状态
            OmsOrder order = new OmsOrder();
            order.setId(orderId);
            order.setStatus(1);
            order.setPaymentTime(new Date());
            order.setPayType(payType);

            OmsOrderExample orderExample = new OmsOrderExample();
            orderExample.createCriteria()
                    .andIdEqualTo(order.getId())
                    .andDeleteStatusEqualTo(0)
                    .andStatusEqualTo(0);

            // 只修改未付款状态的订单
            int updateCount = orderMapper.updateByExampleSelective(order, orderExample);
            if (updateCount == 0) {
                log.warn("订单支付状态更新失败：orderId={}, 可能订单不存在或状态不是未支付", orderId);
                Asserts.fail("订单不存在或订单状态不是未支付！");
            }

            log.info("订单支付状态更新成功：orderId={}, 新状态=已支付", orderId);

            // 获取订单详情，包含订单项
            OmsOrderDetail orderDetail = portalOrderDao.getDetail(orderId);
            if (orderDetail == null || CollectionUtils.isEmpty(orderDetail.getOrderItemList())) {
                log.error("无法获取订单详情或订单项为空：orderId={}", orderId);
                Asserts.fail("订单详情获取失败");
            }

            // 处理库存和销量
            processStockAndSales(orderId, orderDetail.getOrderItemList());

            // 记录订单支付完成事件
            savePaymentSuccessLog(orderId, payType);

            log.info("订单支付完成处理完毕：orderId={}", orderId);

            // 返回更新的订单数量
            return updateCount;
        } finally {
            // 最终释放分布式锁
            releasePayLock(orderId);
        }
    }

    /**
     * 获取支付分布式锁
     * 
     * @param orderId   订单ID
     * @param requestId 请求ID，用于锁的唯一标识
     * @return 是否获取成功
     */
    private boolean acquirePayLock(Long orderId) {
        String lockKey = String.format("order:pay:%d", orderId);
        return redisDistributedLock.acquireLock(lockKey, orderId.toString(), 30L, TimeUnit.SECONDS);
    }

    /**
     * 释放支付分布式锁
     * 
     * @param orderId 订单ID
     */
    private void releasePayLock(Long orderId) {
        String lockKey = String.format("order:pay:%d", orderId);
        boolean released = redisDistributedLock.releaseLock(lockKey, orderId.toString());
        if (released) {
            log.info("释放支付分布式锁成功: orderId={}", orderId);
        } else {
            log.warn("释放支付分布式锁失败: orderId={}", orderId);
        }
    }

    /**
     * 记录支付成功日志
     */
    private void savePaymentSuccessLog(Long orderId, Integer payType) {
        try {
            // 可以在这里添加支付成功的日志记录，如支付流水号、支付时间等
            log.info("记录支付成功信息: orderId={}, payType={}, 支付时间={}",
                    orderId, payType, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));

            // 未来可以扩展：
            // 1. 插入支付流水记录表
            // 2. 触发支付成功事件，进行异步处理如发送通知等
        } catch (Exception e) {
            // 不影响主流程，只记录日志
            log.error("记录支付日志异常: orderId={}", orderId, e);
        }
    }

    @Override
    public Integer cancelTimeOutOrder() {
        Integer count = 0;
        OmsOrderSetting orderSetting = orderSettingMapper.selectByPrimaryKey(1L);
        // 查询超时、未支付的订单及订单详情
        List<OmsOrderDetail> timeOutOrders = portalOrderDao.getTimeOutOrders(orderSetting.getNormalOrderOvertime());
        if (CollectionUtils.isEmpty(timeOutOrders)) {
            return count;
        }
        // 修改订单状态为交易取消
        List<Long> ids = new ArrayList<>();
        for (OmsOrderDetail timeOutOrder : timeOutOrders) {
            ids.add(timeOutOrder.getId());
        }
        portalOrderDao.updateOrderStatus(ids, 4);
        for (OmsOrderDetail timeOutOrder : timeOutOrders) {
            // 解除订单商品库存锁定
            portalOrderDao.releaseSkuStockLock(timeOutOrder.getOrderItemList());
            // 修改优惠券使用状态
            updateCouponStatus(timeOutOrder.getCouponId(), timeOutOrder.getMemberId(), 0);
            // 返还使用积分
            if (timeOutOrder.getUseIntegration() != null) {
                UmsMember member = memberService.getById(timeOutOrder.getMemberId());
                memberService.updateIntegration(timeOutOrder.getMemberId(),
                        member.getIntegration() + timeOutOrder.getUseIntegration());
            }
        }
        return timeOutOrders.size();
    }

    @Override
    public void cancelOrder(Long orderId) {
        log.info("开始取消订单: orderId={}", orderId);

        // 查询未付款的取消订单
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andIdEqualTo(orderId).andStatusEqualTo(0).andDeleteStatusEqualTo(0);
        List<OmsOrder> cancelOrderList = orderMapper.selectByExample(example);

        if (CollectionUtils.isEmpty(cancelOrderList)) {
            log.warn("未找到可取消的订单，可能已经处理: orderId={}", orderId);
            return;
        }

        OmsOrder cancelOrder = cancelOrderList.get(0);
        log.info("找到需要取消的订单: orderId={}, orderSn={}, status={}",
                cancelOrder.getId(), cancelOrder.getOrderSn(), cancelOrder.getStatus());

        try {
            // 修改订单状态为取消
            cancelOrder.setStatus(4);
            cancelOrder.setNote(cancelOrder.getNote() == null ? "超时自动取消" : cancelOrder.getNote() + ";超时自动取消");
            // OmsOrder没有setCancelTime方法，使用现有字段
            cancelOrder.setModifyTime(new Date());
            orderMapper.updateByPrimaryKeySelective(cancelOrder);
            log.info("订单状态已更新为已取消: orderId={}", orderId);

            // 获取订单商品项
            OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
            orderItemExample.createCriteria().andOrderIdEqualTo(orderId);
            List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);

            if (CollectionUtils.isEmpty(orderItemList)) {
                log.warn("未找到订单商品项，无法释放库存: orderId={}", orderId);
            } else {
                log.info("开始释放订单商品库存锁定，商品数量: {}", orderItemList.size());
                // 批量释放库存锁定
                List<String> failedItems = new ArrayList<>();

                // 逐个释放库存，记录失败项
                for (OmsOrderItem orderItem : orderItemList) {
                    try {
                        int count = portalOrderDao.releaseStockBySkuId(orderItem.getProductSkuId(),
                                orderItem.getProductQuantity());
                        if (count == 0) {
                            failedItems.add(orderItem.getProductName());
                            log.warn("释放库存失败，库存数据可能不一致: skuId={}, quantity={}",
                                    orderItem.getProductSkuId(), orderItem.getProductQuantity());
                        } else {
                            log.info("成功释放商品库存: skuId={}, quantity={}",
                                    orderItem.getProductSkuId(), orderItem.getProductQuantity());
                        }
                    } catch (Exception e) {
                        failedItems.add(orderItem.getProductName());
                        log.error("释放库存异常: skuId={}, quantity={}, error={}",
                                orderItem.getProductSkuId(), orderItem.getProductQuantity(), e.getMessage(), e);
                    }
                }

                // 如果有释放失败的项，记录日志，但不影响后续流程
                if (!failedItems.isEmpty()) {
                    log.error("部分商品库存释放失败: {}", String.join(", ", failedItems));
                    // 可以考虑发送警报或插入到异常处理队列中以便人工介入
                } else {
                    log.info("所有商品库存成功释放");
                }
            }

            // 处理优惠券和积分
            handleCouponAndIntegrationAfterCancel(cancelOrder);

            log.info("订单取消处理完成: orderId={}", orderId);
        } catch (Exception e) {
            log.error("取消订单过程发生异常: orderId={}, error={}", orderId, e.getMessage(), e);
            throw new RuntimeException("取消订单失败", e);
        }
    }

    /**
     * 处理取消订单后的优惠券和积分返还
     */
    private void handleCouponAndIntegrationAfterCancel(OmsOrder cancelOrder) {
        // 修改优惠券使用状态
        if (cancelOrder.getCouponId() != null) {
            try {
                updateCouponStatus(cancelOrder.getCouponId(), cancelOrder.getMemberId(), 0);
                log.info("优惠券状态已更新为未使用: couponId={}, memberId={}",
                        cancelOrder.getCouponId(), cancelOrder.getMemberId());
            } catch (Exception e) {
                log.error("更新优惠券状态失败: couponId={}, memberId={}, error={}",
                        cancelOrder.getCouponId(), cancelOrder.getMemberId(), e.getMessage(), e);
            }
        }

        // 返还使用积分
        if (cancelOrder.getUseIntegration() != null && cancelOrder.getUseIntegration() > 0) {
            try {
                UmsMember member = memberService.getById(cancelOrder.getMemberId());
                if (member != null) {
                    int currentIntegration = member.getIntegration() == null ? 0 : member.getIntegration();
                    memberService.updateIntegration(cancelOrder.getMemberId(),
                            currentIntegration + cancelOrder.getUseIntegration());
                    log.info("已返还会员使用的积分: memberId={}, returnIntegration={}, currentIntegration={}",
                            cancelOrder.getMemberId(), cancelOrder.getUseIntegration(),
                            currentIntegration + cancelOrder.getUseIntegration());
                } else {
                    log.error("无法返还积分，会员不存在: memberId={}", cancelOrder.getMemberId());
                }
            } catch (Exception e) {
                log.error("返还会员积分失败: memberId={}, integration={}, error={}",
                        cancelOrder.getMemberId(), cancelOrder.getUseIntegration(), e.getMessage(), e);
            }
        }
    }

    @Override
    public void sendDelayMessageCancelOrder(Long orderId) {
        // 获取订单超时时间
        OmsOrderSetting orderSetting = orderSettingMapper.selectByPrimaryKey(1L);
        long delayTimes = orderSetting.getNormalOrderOvertime() * 60 * 1000;
        // 发送延迟消息
        cancelOrderSender.sendMessage(orderId, delayTimes);
    }

    @Override
    public void confirmReceiveOrder(Long orderId) {
        UmsMember member = memberService.getCurrentMember();
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if (!member.getId().equals(order.getMemberId())) {
            Asserts.fail("不能确认他人订单！");
        }
        if (order.getStatus() != 2) {
            Asserts.fail("该订单还未发货！");
        }
        // 修改状态为待评价，而不是直接设为已完成
        order.setStatus(3);
        order.setConfirmStatus(1);
        order.setReceiveTime(new Date());
        orderMapper.updateByPrimaryKey(order);
    }

    @Override
    public CommonPage<OmsOrderDetail> list(Integer status, Integer pageNum, Integer pageSize) {
        if (status == -1) {
            status = null;
        }
        UmsMember member = memberService.getCurrentMember();
        log.info("查询用户订单列表: memberId={}, status={}, pageNum={}, pageSize={}", member.getId(), status, pageNum,
                pageSize);

        PageHelper.startPage(pageNum, pageSize);
        OmsOrderExample orderExample = new OmsOrderExample();
        OmsOrderExample.Criteria criteria = orderExample.createCriteria();
        criteria.andDeleteStatusEqualTo(0)
                .andMemberIdEqualTo(member.getId());
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }
        orderExample.setOrderByClause("create_time desc");
        List<OmsOrder> orderList = orderMapper.selectByExample(orderExample);
        CommonPage<OmsOrder> orderPage = CommonPage.restPage(orderList);
        // 设置分页信息
        CommonPage<OmsOrderDetail> resultPage = new CommonPage<>();
        resultPage.setPageNum(orderPage.getPageNum());
        resultPage.setPageSize(orderPage.getPageSize());
        resultPage.setTotal(orderPage.getTotal());
        resultPage.setTotalPage(orderPage.getTotalPage());
        if (CollUtil.isEmpty(orderList)) {
            log.info("未查询到符合条件的订单列表");
            return resultPage;
        }
        log.info("查询到符合条件的订单数量: {}", orderList.size());

        // 设置数据信息
        List<Long> orderIds = orderList.stream().map(OmsOrder::getId).collect(Collectors.toList());
        log.info("准备查询订单项信息，订单ID列表: {}", orderIds);

        OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
        orderItemExample.createCriteria().andOrderIdIn(orderIds);
        List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
        log.info("查询到订单项总数量: {}", orderItemList != null ? orderItemList.size() : 0);

        // 处理订单项，确保appliedQuantity不为null
        if (orderItemList != null && !orderItemList.isEmpty()) {
            log.info("开始处理订单项的appliedQuantity字段");
            int nullCount = 0;
            for (OmsOrderItem item : orderItemList) {
                if (item.getAppliedQuantity() == null) {
                    nullCount++;
                    item.setAppliedQuantity(0);
                }
            }
            log.info("订单项中appliedQuantity为null的记录数: {}", nullCount);

            // 按订单ID分组统计订单项
            Map<Long, Long> orderItemCountMap = orderItemList.stream()
                    .collect(Collectors.groupingBy(OmsOrderItem::getOrderId, Collectors.counting()));
            log.info("各订单包含的订单项数量统计: {}", orderItemCountMap);
        } else {
            log.warn("未查询到任何订单项信息!");
        }

        // 检查并更新订单售后状态
        for (OmsOrder order : orderList) {
            // 如果订单没有售后状态或状态为0，但有订单项已申请售后，则更新订单售后状态
            if (order.getAfterSaleStatus() == null || order.getAfterSaleStatus() == 0) {
                boolean needUpdate = false;
                boolean allApplied = true;
                boolean partialApplied = false;

                // 获取当前订单的所有订单项
                List<OmsOrderItem> items = orderItemList.stream()
                        .filter(item -> item.getOrderId().equals(order.getId()))
                        .collect(Collectors.toList());

                if (!items.isEmpty()) {
                    for (OmsOrderItem item : items) {
                        Integer appliedQuantity = item.getAppliedQuantity() == null ? 0 : item.getAppliedQuantity();
                        Integer productQuantity = item.getProductQuantity() == null ? 0 : item.getProductQuantity();

                        if (appliedQuantity < productQuantity) {
                            allApplied = false;
                        }

                        if (appliedQuantity > 0) {
                            partialApplied = true;
                            needUpdate = true;
                        }
                    }

                    // 如果需要更新订单状态
                    if (needUpdate) {
                        byte afterSaleStatus;
                        if (allApplied) {
                            afterSaleStatus = 2; // 全部申请
                        } else if (partialApplied) {
                            afterSaleStatus = 1; // 部分申请
                        } else {
                            afterSaleStatus = 0; // 未申请
                        }

                        // 只有当状态需要变更时才更新
                        if (order.getAfterSaleStatus() == null || order.getAfterSaleStatus() != afterSaleStatus) {
                            log.info("更新订单售后状态: 订单ID={}, 原状态={}, 新状态={}",
                                    order.getId(), order.getAfterSaleStatus(), afterSaleStatus);

                            OmsOrder updateOrder = new OmsOrder();
                            updateOrder.setId(order.getId());
                            updateOrder.setAfterSaleStatus(afterSaleStatus);
                            orderMapper.updateByPrimaryKeySelective(updateOrder);

                            // 更新当前列表中的状态，确保前端显示正确
                            order.setAfterSaleStatus(afterSaleStatus);
                        }
                    }
                }
            }
        }

        List<OmsOrderDetail> orderDetailList = new ArrayList<>();
        for (OmsOrder omsOrder : orderList) {
            OmsOrderDetail orderDetail = new OmsOrderDetail();
            BeanUtil.copyProperties(omsOrder, orderDetail);
            List<OmsOrderItem> relatedItemList = orderItemList.stream()
                    .filter(item -> item.getOrderId().equals(orderDetail.getId())).collect(Collectors.toList());

            log.info("订单ID: {}, 关联的订单项数量: {}", orderDetail.getId(), relatedItemList.size());
            if (relatedItemList.isEmpty()) {
                log.warn("订单ID: {} 没有关联的订单项!", orderDetail.getId());
            } else {
                // 检查第一个订单项的字段值
                OmsOrderItem firstItem = relatedItemList.get(0);
                log.info("订单 {} 的第一个订单项: ID={}, 商品名称={}, appliedQuantity={}",
                        orderDetail.getId(), firstItem.getId(), firstItem.getProductName(),
                        firstItem.getAppliedQuantity());
            }

            orderDetail.setOrderItemList(relatedItemList);
            orderDetailList.add(orderDetail);
        }

        // 为每个 OmsOrderDetail 设置 canComment 标志
        for (OmsOrderDetail detail : orderDetailList) {
            boolean canCommentOrder = false;
            // 检查订单状态是否允许评价 (例如：已完成3 或 待评价4)
            if (detail.getStatus() != null && (detail.getStatus() == 3 || detail.getStatus() == 4)) {
                boolean hasUncommentedItem = false;
                if (detail.getOrderItemList() != null) {
                    for (OmsOrderItem item : detail.getOrderItemList()) {
                        // 检查订单项的 commentStatus
                        if (item.getCommentStatus() == null || item.getCommentStatus() == 0) {
                            hasUncommentedItem = true;
                            break;
                        }
                    }
                }
                // 如果有未评价的项，则可以评价
                if (hasUncommentedItem) {
                    canCommentOrder = true;
                }
            }
            detail.setCanComment(canCommentOrder);
        }

        resultPage.setList(orderDetailList);
        return resultPage;
    }

    @Override
    public OmsOrderDetail detail(Long orderId) {
        OmsOrder omsOrder = orderMapper.selectByPrimaryKey(orderId);
        if (omsOrder == null) {
            return null; // 或者抛出异常
        }
        UmsMember member = memberService.getCurrentMember();
        if (!member.getId().equals(omsOrder.getMemberId())) {
            Asserts.fail("不能查看他人订单详情！");
        }

        OmsOrderItemExample example = new OmsOrderItemExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(example);

        // 处理订单项，确保appliedQuantity不为null
        if (orderItemList != null) {
            for (OmsOrderItem item : orderItemList) {
                if (item.getAppliedQuantity() == null) {
                    item.setAppliedQuantity(0);
                }
                // 确保 commentStatus 不为 null (虽然数据库有默认值，防御性编程)
                if (item.getCommentStatus() == null) {
                    item.setCommentStatus(0);
                }
            }
        }

        OmsOrderDetail orderDetail = new OmsOrderDetail();
        BeanUtil.copyProperties(omsOrder, orderDetail);
        orderDetail.setOrderItemList(orderItemList);

        // 计算 canComment
        boolean canCommentOrder = false;
        // 检查订单状态是否允许评价 (例如：已完成3 或 待评价4)
        if (orderDetail.getStatus() != null && (orderDetail.getStatus() == 3 || orderDetail.getStatus() == 4)) {
            boolean hasUncommentedItem = false;
            if (orderItemList != null) {
                for (OmsOrderItem item : orderItemList) {
                    // 检查订单项的 commentStatus
                    if (item.getCommentStatus() == 0) { // 0 表示未评价
                        hasUncommentedItem = true;
                        break;
                    }
                }
            }
            // 如果有未评价的项，则可以评价
            if (hasUncommentedItem) {
                canCommentOrder = true;
            }
        }
        orderDetail.setCanComment(canCommentOrder);

        return orderDetail;
    }

    @Override
    public void deleteOrder(Long orderId) {
        UmsMember member = memberService.getCurrentMember();
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if (!member.getId().equals(order.getMemberId())) {
            Asserts.fail("不能删除他人订单！");
        }
        if (order.getStatus() == 3 || order.getStatus() == 4) {
            order.setDeleteStatus(1);
            orderMapper.updateByPrimaryKey(order);
        } else {
            Asserts.fail("只能删除已完成或已关闭的订单！");
        }
    }

    @Override
    public void paySuccessByOrderSn(String orderSn, Integer payType) {
        log.info("根据订单号处理订单支付成功：orderSn={}, payType={}", orderSn, payType);

        // 生成锁的唯一标识
        String requestId = UUID.randomUUID().toString();
        String lockKey = "order:pay:sn:" + orderSn;

        try {
            // 获取分布式锁
            boolean locked = redisDistributedLock.acquireLock(lockKey, requestId, 30, TimeUnit.SECONDS);
            if (!locked) {
                log.warn("获取订单号支付锁失败，可能有其他线程正在处理该订单: orderSn={}", orderSn);
                return;
            }

            // 查询订单信息
            OmsOrderExample example = new OmsOrderExample();
            example.createCriteria()
                    .andOrderSnEqualTo(orderSn)
                    .andStatusEqualTo(0) // 未支付状态
                    .andDeleteStatusEqualTo(0); // 未删除
            List<OmsOrder> orderList = orderMapper.selectByExample(example);

            if (CollUtil.isNotEmpty(orderList)) {
                OmsOrder order = orderList.get(0);
                paySuccess(order.getId(), payType);
                log.info("订单号支付处理完成：orderSn={}, orderId={}", orderSn, order.getId());
            } else {
                log.warn("找不到符合条件的待支付订单：orderSn={}", orderSn);
            }
        } finally {
            // 释放分布式锁
            redisDistributedLock.releaseLock(lockKey, requestId);
        }
    }

    /**
     * 生成18位订单编号:8位日期+2位平台号码+2位支付方式+6位以上自增id
     */
    private String generateOrderSn(OmsOrder order) {
        StringBuilder sb = new StringBuilder();
        String date = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String key = REDIS_DATABASE + ":" + REDIS_KEY_ORDER_ID + date;
        Long increment = redisService.incr(key, 1);
        sb.append(date);
        sb.append(String.format("%02d", order.getSourceType()));
        sb.append(String.format("%02d", order.getPayType()));
        String incrementStr = increment.toString();
        if (incrementStr.length() <= 6) {
            sb.append(String.format("%06d", increment));
        } else {
            sb.append(incrementStr);
        }
        return sb.toString();
    }

    /**
     * 从购物车中删除已下单的商品信息
     */
    private void deleteCartItemList(List<CartPromotionItem> cartPromotionItemList, UmsMember currentMember) {
        List<Long> ids = new ArrayList<>();
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            ids.add(cartPromotionItem.getId());
        }
        cartItemService.delete(currentMember.getId(), ids);
    }

    /**
     * 计算该订单赠送的成长值
     */
    private Integer calcGiftGrowth(List<OmsOrderItem> orderItemList) {
        Integer sum = 0;
        for (OmsOrderItem orderItem : orderItemList) {
            sum = sum + orderItem.getGiftGrowth() * orderItem.getProductQuantity();
        }
        return sum;
    }

    /**
     * 计算该订单赠送的积分
     */
    private Integer calcGifIntegration(List<OmsOrderItem> orderItemList) {
        int sum = 0;
        for (OmsOrderItem orderItem : orderItemList) {
            sum += orderItem.getGiftIntegration() * orderItem.getProductQuantity();
        }
        return sum;
    }

    /**
     * 将优惠券信息更改为指定状态
     *
     * @param couponId  优惠券id
     * @param memberId  会员id
     * @param useStatus 0->未使用；1->已使用
     */
    private void updateCouponStatus(Long couponId, Long memberId, Integer useStatus) {
        if (couponId == null)
            return;
        // 查询第一张优惠券
        SmsCouponHistoryExample example = new SmsCouponHistoryExample();
        example.createCriteria().andMemberIdEqualTo(memberId)
                .andCouponIdEqualTo(couponId).andUseStatusEqualTo(useStatus == 0 ? 1 : 0);
        List<SmsCouponHistory> couponHistoryList = couponHistoryMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(couponHistoryList)) {
            SmsCouponHistory couponHistory = couponHistoryList.get(0);
            couponHistory.setUseTime(new Date());
            couponHistory.setUseStatus(useStatus);
            couponHistoryMapper.updateByPrimaryKeySelective(couponHistory);
        }
    }

    private void handleRealAmount(List<OmsOrderItem> orderItemList) {
        for (OmsOrderItem orderItem : orderItemList) {
            // 原价-促销优惠-优惠券抵扣-积分抵扣
            BigDecimal realAmount = orderItem.getProductPrice()
                    .subtract(orderItem.getPromotionAmount())
                    .subtract(orderItem.getCouponAmount())
                    .subtract(orderItem.getIntegrationAmount());
            orderItem.setRealAmount(realAmount);
        }
    }

    /**
     * 获取订单促销信息
     */
    private String getOrderPromotionInfo(List<OmsOrderItem> orderItemList) {
        StringBuilder sb = new StringBuilder();
        for (OmsOrderItem orderItem : orderItemList) {
            sb.append(orderItem.getPromotionName());
            sb.append(";");
        }
        String result = sb.toString();
        if (result.endsWith(";")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    /**
     * 计算订单应付金额
     */
    private BigDecimal calcPayAmount(OmsOrder order) {
        // 总金额+运费-促销优惠-优惠券优惠-积分抵扣
        BigDecimal payAmount = order.getTotalAmount()
                .add(order.getFreightAmount())
                .subtract(order.getPromotionAmount())
                .subtract(order.getCouponAmount())
                .subtract(order.getIntegrationAmount());
        return payAmount;
    }

    /**
     * 计算订单优惠券金额
     */
    private BigDecimal calcIntegrationAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal integrationAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getIntegrationAmount() != null) {
                integrationAmount = integrationAmount
                        .add(orderItem.getIntegrationAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return integrationAmount;
    }

    /**
     * 计算订单优惠券金额
     */
    private BigDecimal calcCouponAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal couponAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getCouponAmount() != null) {
                couponAmount = couponAmount
                        .add(orderItem.getCouponAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return couponAmount;
    }

    /**
     * 计算订单活动优惠
     */
    private BigDecimal calcPromotionAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal promotionAmount = new BigDecimal(0);
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getPromotionAmount() != null) {
                promotionAmount = promotionAmount
                        .add(orderItem.getPromotionAmount().multiply(new BigDecimal(orderItem.getProductQuantity())));
            }
        }
        return promotionAmount;
    }

    /**
     * 获取可用积分抵扣金额
     *
     * @param useIntegration 使用的积分数量
     * @param totalAmount    订单总金额
     * @param currentMember  使用的用户
     * @param hasCoupon      是否已经使用优惠券
     */
    private BigDecimal getUseIntegrationAmount(Integer useIntegration, BigDecimal totalAmount, UmsMember currentMember,
            boolean hasCoupon) {
        BigDecimal zeroAmount = new BigDecimal(0);
        // 判断用户是否有这么多积分
        if (useIntegration.compareTo(currentMember.getIntegration()) > 0) {
            return zeroAmount;
        }
        // 根据积分使用规则判断是否可用
        // 是否可与优惠券共用
        UmsIntegrationConsumeSetting integrationConsumeSetting = integrationConsumeSettingMapper.selectByPrimaryKey(1L);
        if (hasCoupon && integrationConsumeSetting.getCouponStatus().equals(0)) {
            // 不可与优惠券共用
            return zeroAmount;
        }
        // 是否达到最低使用积分门槛
        if (useIntegration.compareTo(integrationConsumeSetting.getUseUnit()) < 0) {
            return zeroAmount;
        }
        // 是否超过订单抵用最高百分比
        BigDecimal integrationAmount = new BigDecimal(useIntegration)
                .divide(new BigDecimal(integrationConsumeSetting.getUseUnit()), 2, RoundingMode.HALF_EVEN);
        BigDecimal maxPercent = new BigDecimal(integrationConsumeSetting.getMaxPercentPerOrder())
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_EVEN);
        if (integrationAmount.compareTo(totalAmount.multiply(maxPercent)) > 0) {
            return zeroAmount;
        }
        return integrationAmount;
    }

    /**
     * 对优惠券优惠进行处理
     *
     * @param orderItemList       order_item列表
     * @param couponHistoryDetail 可用优惠券详情
     */
    private void handleCouponAmount(List<OmsOrderItem> orderItemList, SmsCouponHistoryDetail couponHistoryDetail) {
        SmsCoupon coupon = couponHistoryDetail.getCoupon();
        if (coupon.getUseType().equals(0)) {
            // 全场通用
            calcPerCouponAmount(orderItemList, coupon);
        } else if (coupon.getUseType().equals(1)) {
            // 指定分类
            List<OmsOrderItem> couponOrderItemList = getCouponOrderItemByRelation(couponHistoryDetail, orderItemList,
                    0);
            calcPerCouponAmount(couponOrderItemList, coupon);
        } else if (coupon.getUseType().equals(2)) {
            // 指定商品
            List<OmsOrderItem> couponOrderItemList = getCouponOrderItemByRelation(couponHistoryDetail, orderItemList,
                    1);
            calcPerCouponAmount(couponOrderItemList, coupon);
        }
    }

    /**
     * 对每个下单商品进行优惠券金额分摊的计算
     *
     * @param orderItemList 可用优惠券的下单商品商品
     */
    private void calcPerCouponAmount(List<OmsOrderItem> orderItemList, SmsCoupon coupon) {
        BigDecimal totalAmount = calcTotalAmount(orderItemList);
        for (OmsOrderItem orderItem : orderItemList) {
            // (商品价格/可用商品总价)*优惠券面额
            BigDecimal couponAmount = orderItem.getProductPrice().divide(totalAmount, 3, RoundingMode.HALF_EVEN)
                    .multiply(coupon.getAmount());
            orderItem.setCouponAmount(couponAmount);
        }
    }

    /**
     * 获取与优惠券有关系的下单商品
     *
     * @param couponHistoryDetail 优惠券详情
     * @param orderItemList       下单商品
     * @param type                使用关系类型：0->相关分类；1->指定商品
     */
    private List<OmsOrderItem> getCouponOrderItemByRelation(SmsCouponHistoryDetail couponHistoryDetail,
            List<OmsOrderItem> orderItemList, int type) {
        List<OmsOrderItem> result = new ArrayList<>();
        if (type == 0) {
            List<Long> categoryIdList = new ArrayList<>();
            for (SmsCouponProductCategoryRelation productCategoryRelation : couponHistoryDetail
                    .getCategoryRelationList()) {
                categoryIdList.add(productCategoryRelation.getProductCategoryId());
            }
            for (OmsOrderItem orderItem : orderItemList) {
                if (categoryIdList.contains(orderItem.getProductCategoryId())) {
                    result.add(orderItem);
                } else {
                    orderItem.setCouponAmount(new BigDecimal(0));
                }
            }
        } else if (type == 1) {
            List<Long> productIdList = new ArrayList<>();
            for (SmsCouponProductRelation productRelation : couponHistoryDetail.getProductRelationList()) {
                productIdList.add(productRelation.getProductId());
            }
            for (OmsOrderItem orderItem : orderItemList) {
                if (productIdList.contains(orderItem.getProductId())) {
                    result.add(orderItem);
                } else {
                    orderItem.setCouponAmount(new BigDecimal(0));
                }
            }
        }
        return result;
    }

    /**
     * 获取该用户可以使用的优惠券
     *
     * @param cartPromotionItemList 购物车优惠列表
     * @param couponId              使用优惠券id
     */
    private SmsCouponHistoryDetail getUseCoupon(List<CartPromotionItem> cartPromotionItemList, Long couponId) {
        List<SmsCouponHistoryDetail> couponHistoryDetailList = memberCouponService.listCart(cartPromotionItemList, 1);
        for (SmsCouponHistoryDetail couponHistoryDetail : couponHistoryDetailList) {
            if (couponHistoryDetail.getCoupon().getId().equals(couponId)) {
                return couponHistoryDetail;
            }
        }
        return null;
    }

    /**
     * 计算总金额
     */
    private BigDecimal calcTotalAmount(List<OmsOrderItem> orderItemList) {
        BigDecimal totalAmount = new BigDecimal("0");
        for (OmsOrderItem item : orderItemList) {
            totalAmount = totalAmount.add(item.getProductPrice().multiply(new BigDecimal(item.getProductQuantity())));
        }
        return totalAmount;
    }

    /**
     * 锁定下单商品的所有库存
     */
    private void lockStock(List<CartPromotionItem> cartPromotionItemList) {
        log.info("开始锁定商品库存，商品数量：{}", cartPromotionItemList.size());

        if (CollectionUtils.isEmpty(cartPromotionItemList)) {
            log.warn("商品列表为空，无需锁定库存");
            return;
        }

        // 先检查所有SKU是否存在并有足够库存
        List<StockOperation> stockOperationList = new ArrayList<>();
        Map<Long, PmsSkuStock> skuStockMap = new HashMap<>();

        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            PmsSkuStock skuStock = skuStockMapper.selectByPrimaryKey(cartPromotionItem.getProductSkuId());
            if (skuStock == null) {
                log.error("锁定库存失败：SKU不存在, skuId={}", cartPromotionItem.getProductSkuId());
                Asserts.fail("商品SKU不存在，无法下单");
            }

            // 缓存SKU信息，避免重复查询
            skuStockMap.put(skuStock.getId(), skuStock);

            // 预检查库存是否足够
            if (skuStock.getStock() < cartPromotionItem.getQuantity()) {
                log.error("锁定库存失败：库存不足, skuId={}, 当前库存={}, 需要数量={}",
                        cartPromotionItem.getProductSkuId(), skuStock.getStock(), cartPromotionItem.getQuantity());
                Asserts.fail("商品" + cartPromotionItem.getProductName() + "库存不足，无法下单");
            }

            // 创建库存操作对象
            StockOperation stockOperation = new StockOperation(
                    cartPromotionItem.getProductSkuId(),
                    cartPromotionItem.getQuantity(),
                    cartPromotionItem.getProductName());
            stockOperationList.add(stockOperation);
        }

        // 使用事务模板确保操作的原子性
        try {
            // 1. 先检查库存是否充足 (双重检查，防止高并发下的超卖)
            List<Long> insufficientStockIds = portalOrderDao.checkStockBySkuIds(stockOperationList);
            if (!insufficientStockIds.isEmpty()) {
                // 查找库存不足的商品名称，用于错误提示
                List<String> insufficientStockNames = new ArrayList<>();
                for (Long skuId : insufficientStockIds) {
                    for (CartPromotionItem item : cartPromotionItemList) {
                        if (item.getProductSkuId().equals(skuId)) {
                            insufficientStockNames.add(item.getProductName());
                            break;
                        }
                    }
                }
                String errorItems = String.join(", ", insufficientStockNames);
                log.error("锁定库存时检测到库存不足的商品: {}", errorItems);
                Asserts.fail("商品[" + errorItems + "]库存不足，无法下单");
            }

            // 2. 执行批量库存锁定
            int affectedRows = portalOrderDao.batchLockStock(stockOperationList);
            if (affectedRows <= 0) {
                log.error("批量锁定库存失败，数据库更新行数为0");
                Asserts.fail("系统繁忙，请稍后再试");
            }

            log.info("批量锁定库存成功，影响的行数: {}", affectedRows);
        } catch (Exception e) {
            log.error("锁定库存过程发生异常", e);
            if (e instanceof IllegalArgumentException) {
                throw e; // 将自定义业务异常往上层抛
            } else {
                Asserts.fail("系统异常，无法完成下单");
            }
        }

        log.info("所有商品库存锁定成功");
    }

    /**
     * 判断下单商品是否都有库存
     */
    private boolean hasStock(List<CartPromotionItem> cartPromotionItemList) {
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            if (cartPromotionItem.getRealStock() == null // 判断真实库存是否为空
                    || cartPromotionItem.getRealStock() <= 0 // 判断真实库存是否小于0
                    || cartPromotionItem.getRealStock() < cartPromotionItem.getQuantity()) // 判断真实库存是否小于下单的数量
            {
                return false;
            }
        }
        return true;
    }

    /**
     * 计算购物车中商品的价格
     */
    private ConfirmOrderResult.CalcAmount calcCartAmount(List<CartPromotionItem> cartPromotionItemList) {
        ConfirmOrderResult.CalcAmount calcAmount = new ConfirmOrderResult.CalcAmount();
        calcAmount.setFreightAmount(new BigDecimal(0));
        BigDecimal totalAmount = new BigDecimal("0");
        BigDecimal promotionAmount = new BigDecimal("0");
        for (CartPromotionItem cartPromotionItem : cartPromotionItemList) {
            totalAmount = totalAmount
                    .add(cartPromotionItem.getPrice().multiply(new BigDecimal(cartPromotionItem.getQuantity())));
            promotionAmount = promotionAmount
                    .add(cartPromotionItem.getReduceAmount().multiply(new BigDecimal(cartPromotionItem.getQuantity())));
        }
        calcAmount.setTotalAmount(totalAmount);
        calcAmount.setPromotionAmount(promotionAmount);
        calcAmount.setPayAmount(totalAmount.subtract(promotionAmount));
        return calcAmount;
    }

    @Override
    @Transactional
    public int createProductComment(PmsCommentParam commentParam) {
        // 获取当前登录会员
        UmsMember member = memberService.getCurrentMember();
        if (member == null) {
            Asserts.fail("用户未登录");
        }

        // 检查是否已经评价过该订单项
        // !!! TEMPORARY: Commented out due to missing MBG method. Uncomment after
        // running MBG.
        /*
         * PmsCommentExample existExample = new PmsCommentExample();
         * existExample.createCriteria().andOrderItemIdEqualTo(commentParam.
         * getOrderItemId());
         * long existCount = commentMapper.countByExample(existExample);
         * if (existCount > 0) {
         * Asserts.fail("您已评价过此商品");
         * }
         */

        PmsComment comment = new PmsComment();
        comment.setProductId(commentParam.getProductId());
        comment.setOrderId(commentParam.getOrderId()); // 设置 orderId
        comment.setOrderItemId(commentParam.getOrderItemId()); // 设置 orderItemId
        comment.setMemberId(member.getId()); // 设置 memberId
        comment.setContent(commentParam.getComment());
        comment.setStar(commentParam.getRating());

        // 设置评价图片
        if (commentParam.getPics() != null && !commentParam.getPics().isEmpty()) {
            String pics = String.join(",", commentParam.getPics());
            comment.setPics(pics);
        }

        comment.setMemberNickName(member.getNickname());
        comment.setMemberIcon(member.getIcon());

        // 获取订单商品信息 (可以通过 orderItemId 直接获取，或者从 order 关联查询)
        // 为了简化，我们假设 PmsCommentParam 已经包含了 ProductName 和 ProductAttribute
        // 如果 PmsCommentParam 没有，则需要根据 orderItemId 查询 OmsOrderItem
        OmsOrderItem orderItem = orderItemMapper.selectByPrimaryKey(commentParam.getOrderItemId());
        if (orderItem != null) {
            comment.setProductAttribute(orderItem.getProductAttr());
            comment.setProductName(orderItem.getProductName());
            // 也可以在这里再次校验 productId 和 orderId 是否匹配
            if (!orderItem.getProductId().equals(commentParam.getProductId())
                    || !orderItem.getOrderId().equals(commentParam.getOrderId())) {
                log.warn("评价信息与订单项信息不匹配: commentParam={}, orderItem={}", commentParam, orderItem);
                Asserts.fail("评价信息错误");
            }
        } else {
            log.warn("无法找到对应的订单项: orderItemId={}", commentParam.getOrderItemId());
            // 可以选择从 PmsCommentParam 获取商品名和属性，如果前端有传的话
            // comment.setProductName(commentParam.getProductNameFromFrontend);
            // comment.setProductAttribute(commentParam.getProductAttrFromFrontend);
            // 或者直接报错
            Asserts.fail("找不到订单商品信息");
        }

        // 其他默认设置
        comment.setCreateTime(new Date());
        comment.setShowStatus(1); // 默认显示
        comment.setReplayCount(0);
        comment.setReadCount(0);
        comment.setCollectCouont(0);
        // memberIp 可以在 Controller 层通过 HttpServletRequest 获取，或者暂时不设置

        int insertResult = commentMapper.insert(comment);
        if (insertResult > 0) {
            // 评价插入成功后，更新对应订单项的评价状态
            if (comment.getOrderItemId() != null) {
                try {
                    OmsOrderItem updateItem = new OmsOrderItem();
                    updateItem.setId(comment.getOrderItemId());
                    updateItem.setCommentStatus(1); // 1 表示已评价
                    orderItemMapper.updateByPrimaryKeySelective(updateItem);
                    log.info("更新订单项 {} 的评价状态为已评价", comment.getOrderItemId());
                } catch (Exception e) {
                    log.error("更新订单项评价状态失败: orderItemId={}, error: {}", comment.getOrderItemId(), e.getMessage());
                    // 抛出异常以确保事务回滚
                    throw new RuntimeException("更新订单项评价状态失败", e);
                }
            }
            // 更新订单的评价时间
            OmsOrder updateOrder = new OmsOrder();
            updateOrder.setId(comment.getOrderId());
            updateOrder.setCommentTime(new Date());
            orderMapper.updateByPrimaryKeySelective(updateOrder);

            return insertResult; // 返回插入成功的结果 (通常是 1)
        } else {
            return 0; // 插入失败
        }
    }

    @Override
    @Transactional
    public int createBatchProductComment(PmsBatchCommentParam batchCommentParam) {
        if (batchCommentParam.getCommentItems() == null || batchCommentParam.getCommentItems().isEmpty()) {
            return 0;
        }

        // 获取当前登录会员
        UmsMember member = memberService.getCurrentMember();
        if (member == null) {
            Asserts.fail("用户未登录");
        }

        Long orderId = batchCommentParam.getOrderId();

        // 获取订单信息 (可选，主要用于后续更新订单状态)
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        if (order == null) {
            Asserts.fail("订单不存在");
        }
        // 校验订单是否属于当前用户
        if (!order.getMemberId().equals(member.getId())) {
            Asserts.fail("只能评价自己的订单");
        }
        // 校验订单状态，例如：是否是已收货(status=3)或待评价(status=4) ?
        // if (order.getStatus() != 3 && order.getStatus() != 4) {
        // Asserts.fail("当前订单状态无法评价");
        // }

        int successCount = 0;
        List<Long> orderItemIds = batchCommentParam.getCommentItems().stream()
                .map(PmsBatchCommentParam.CommentItem::getOrderItemId)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(orderItemIds)) {
            Asserts.fail("评价项缺少 orderItemId");
        }

        // 批量检查是否已经评价过 (使用手动添加的 Mapper 方法)
        long existCount = commentMapper.countByOrderItemIds(orderItemIds);
        if (existCount > 0) {
            // 可以更精确地指出哪些已评价，但为了简单先统一报错
            Asserts.fail("包含已评价过的商品");
        }

        // 获取订单中的商品信息 Map (用于填充商品名和属性)
        OmsOrderItemExample orderItemExample = new OmsOrderItemExample();
        orderItemExample.createCriteria().andOrderIdEqualTo(orderId).andIdIn(orderItemIds);
        List<OmsOrderItem> orderItemList = orderItemMapper.selectByExample(orderItemExample);
        Map<Long, OmsOrderItem> productItemMap = orderItemList.stream()
                .collect(Collectors.toMap(OmsOrderItem::getId, item -> item));

        List<PmsComment> commentsToInsert = new ArrayList<>();

        // 处理每个评价项
        for (PmsBatchCommentParam.CommentItem commentItem : batchCommentParam.getCommentItems()) {
            if (commentItem.getOrderItemId() == null) {
                log.warn("评价项缺少 orderItemId: {}", commentItem);
                continue; // 跳过缺少 orderItemId 的项
            }

            OmsOrderItem orderItem = productItemMap.get(commentItem.getOrderItemId());
            if (orderItem == null) {
                log.warn("找不到评价项对应的订单商品: orderItemId={}", commentItem.getOrderItemId());
                continue; // 跳过找不到订单项的评价
            }

            // 创建评价
            PmsComment comment = new PmsComment();
            comment.setProductId(commentItem.getProductId());
            comment.setOrderId(orderId); // 设置 orderId
            comment.setOrderItemId(commentItem.getOrderItemId()); // 设置 orderItemId
            comment.setMemberId(member.getId()); // 设置 memberId
            comment.setContent(commentItem.getComment());
            comment.setStar(commentItem.getRating());

            // 设置评价图片
            if (commentItem.getPics() != null && !commentItem.getPics().isEmpty()) {
                String pics = String.join(",", commentItem.getPics());
                comment.setPics(pics);
            }

            // 设置用户信息
            comment.setMemberNickName(member.getNickname());
            comment.setMemberIcon(member.getIcon());

            // 设置商品属性和商品名称 (从查询到的 orderItem 获取)
            comment.setProductAttribute(orderItem.getProductAttr());
            comment.setProductName(orderItem.getProductName());

            // 其他默认设置
            comment.setCreateTime(new Date());
            comment.setShowStatus(1); // 显示
            comment.setReplayCount(0);
            comment.setReadCount(0);
            comment.setCollectCouont(0);

            commentsToInsert.add(comment);
        }

        // 批量插入评价
        if (!commentsToInsert.isEmpty()) {
            List<Long> successfullyCommentedItemIds = new ArrayList<>();
            for (PmsComment comment : commentsToInsert) {
                int insertResult = commentMapper.insert(comment);
                if (insertResult > 0) {
                    successCount++;
                    // 收集成功评价的 orderItemId
                    if (comment.getOrderItemId() != null) {
                        successfullyCommentedItemIds.add(comment.getOrderItemId());
                    }
                } else {
                    // 可选：记录插入失败的评论项
                    log.error("插入评论失败: orderItemId={}, productId={}", comment.getOrderItemId(), comment.getProductId());
                }
            }

            // 如果有成功插入的评论，批量更新对应订单项的状态
            if (!successfullyCommentedItemIds.isEmpty()) {
                try {
                    orderItemMapper.updateCommentStatusBatch(successfullyCommentedItemIds, 1); // 1 表示已评价
                    log.info("批量更新 {} 个订单项的评价状态为已评价", successfullyCommentedItemIds.size());
                } catch (Exception e) {
                    // 记录更新失败的错误，但由于主事务会回滚，通常不需要额外处理
                    log.error("批量更新订单项评价状态失败: itemIds={}, error: {}", successfullyCommentedItemIds, e.getMessage());
                    // 这里可以选择抛出异常，确保事务回滚
                    throw new RuntimeException("批量更新订单项评价状态失败", e);
                }
            }
        }

        // 更新订单状态
        if (successCount > 0 && successCount == batchCommentParam.getCommentItems().size()) {
            // 只有当所有请求的评价项都成功插入时才更新订单状态
            // 检查是否所有订单项都已评价 (可选，逻辑较复杂)
            // boolean allCommented = checkAllOrderItemsCommented(orderId);
            // if (allCommented) { ... }

            // 暂时先简单处理：只要有评价成功，就更新订单评价时间，状态维持不变或按需更新
            OmsOrder updateOrder = new OmsOrder();
            updateOrder.setId(orderId);
            // 更新为已完成状态 (status=3) 还是其他状态？
            // updateOrder.setStatus(3);
            updateOrder.setCommentTime(new Date());
            orderMapper.updateByPrimaryKeySelective(updateOrder);
        }

        return successCount;
    }

    @Override
    public List<OmsOrderItem> getOrderProductsForComment(Long orderId) {
        // 获取订单信息
        OmsOrder order = orderMapper.selectByPrimaryKey(orderId);
        // 订单不存在，或者状态不是已完成或待评价，不允许获取待评价商品
        // 注意：状态 3 通常是"已完成"，4 是"待评价"或"已关闭"，需根据实际业务调整
        if (order == null || (order.getStatus() != 3 && order.getStatus() != 4)) {
            log.warn("订单 {} 不存在或状态不允许评价 (status={})", orderId, order != null ? order.getStatus() : "null");
            return new ArrayList<>();
        }

        // 获取当前登录会员
        UmsMember member = memberService.getCurrentMember();
        if (member == null || !order.getMemberId().equals(member.getId())) {
            log.warn("非当前用户 {} 的订单 {}，不允许获取待评价商品", member != null ? member.getId() : "null", orderId);
            return new ArrayList<>();
        }

        // 获取订单中的所有商品列表
        OmsOrderItemExample example = new OmsOrderItemExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        List<OmsOrderItem> allItems = orderItemMapper.selectByExample(example);

        // 在 Java 代码中过滤出未评价的商品 (commentStatus 为 null 或 0)
        List<OmsOrderItem> uncommentedItems = allItems.stream()
                .filter(item -> item.getCommentStatus() == null || item.getCommentStatus() == 0)
                .collect(Collectors.toList());

        log.info("订单 {} 待评价商品数量: {}", orderId, uncommentedItems.size());
        return uncommentedItems;

        // SQL 优化说明 (如果需要):
        // 当前实现在 Java 中过滤，如果订单项非常多，可能有效率问题。
        // 优化方向：在 Mapper XML 中添加自定义 SQL 查询，直接筛选出未评价的商品项。
        // 例如 PmsCommentMapper.xml 中添加 <select id="selectUncommentedItemsByOrderId" ...>
        // SQL: SELECT * FROM oms_order_item WHERE order_id = #{orderId} AND
        // (comment_status = 0 OR comment_status IS NULL)
    }

    /**
     * 处理订单支付成功后的库存和销量更新
     */
    private void processStockAndSales(Long orderId, List<OmsOrderItem> orderItemList) {
        log.info("开始处理订单商品库存和销量：orderId={}, 商品数量={}", orderId, orderItemList.size());

        if (CollectionUtils.isEmpty(orderItemList)) {
            log.warn("订单商品列表为空，无需处理库存和销量: orderId={}", orderId);
            return;
        }

        // 准备批量处理库存的参数
        List<StockOperation> stockOperationList = orderItemList.stream()
                .map(item -> new StockOperation(item.getProductSkuId(), item.getProductQuantity(),
                        item.getProductName()))
                .collect(Collectors.toList());

        // 处理库存：恢复所有下单商品的锁定库存，扣减真实库存
        try {
            // 执行批量库存扣减
            int affectedRows = portalOrderDao.batchReduceStock(stockOperationList);
            if (affectedRows <= 0) {
                String errorMsg = "订单支付成功，但库存扣减失败，请联系客服处理";
                log.error("批量扣减库存失败，数据库更新行数为0: orderId={}", orderId);
                Asserts.fail(errorMsg);
            }
            log.info("批量扣减库存成功，影响的行数: {}", affectedRows);
        } catch (Exception e) {
            log.error("批量扣减库存过程发生异常: orderId={}", orderId, e);
            Asserts.fail("系统异常，无法完成支付处理");
        }

        // 增加商品销量
        // 批量处理销量的参数，按产品ID分组
        Map<Long, Integer> productSalesMap = new HashMap<>();
        for (OmsOrderItem orderItem : orderItemList) {
            if (orderItem.getProductId() != null && orderItem.getProductQuantity() != null
                    && orderItem.getProductQuantity() > 0) {
                // 累加同一产品的数量
                productSalesMap.compute(orderItem.getProductId(),
                        (k, v) -> (v == null) ? orderItem.getProductQuantity() : v + orderItem.getProductQuantity());
            }
        }

        if (productSalesMap.isEmpty()) {
            log.info("没有需要更新销量的商品: orderId={}", orderId);
            return;
        }

        // 批量更新销量（需要在PmsProductMapper中添加batchIncreaseSales方法）
        try {
            // 因为涉及多表更新，暂时保留循环方式，未来可以优化为批量处理
            List<String> salesErrorItems = new ArrayList<>();
            for (Map.Entry<Long, Integer> entry : productSalesMap.entrySet()) {
                try {
                    int updateCount = productMapper.increaseSale(entry.getKey(), entry.getValue());
                    if (updateCount <= 0) {
                        // 查找对应商品名称用于日志
                        String productName = orderItemList.stream()
                                .filter(item -> entry.getKey().equals(item.getProductId()))
                                .map(OmsOrderItem::getProductName)
                                .findFirst()
                                .orElse("未知商品");

                        String errorMsg = String.format("商品[%s]销量更新失败，产品ID: %d, 数量: %d",
                                productName, entry.getKey(), entry.getValue());
                        salesErrorItems.add(errorMsg);
                        log.warn(errorMsg);
                    } else {
                        log.info("商品销量更新成功: productId={}, quantity={}",
                                entry.getKey(), entry.getValue());
                    }
                } catch (Exception e) {
                    String productName = orderItemList.stream()
                            .filter(item -> entry.getKey().equals(item.getProductId()))
                            .map(OmsOrderItem::getProductName)
                            .findFirst()
                            .orElse("未知商品");

                    String errorMsg = String.format("商品[%s]销量更新异常: %s", productName, e.getMessage());
                    salesErrorItems.add(errorMsg);
                    log.error("销量更新异常: orderId={}, productId={}, quantity={}, error={}",
                            orderId, entry.getKey(), entry.getValue(), e.getMessage(), e);
                }
            }

            // 销量更新失败不一定需要回滚整个事务，可以根据业务需求决定
            // 这里仅记录警告日志，但如果销量更新至关重要，也可以抛出异常回滚事务
            if (!salesErrorItems.isEmpty()) {
                log.warn("部分商品销量更新失败，但不影响订单处理: {}", String.join("; ", salesErrorItems));

                // 可以考虑将失败的记录到一个表中，供后续补偿处理
                // saveFailedSalesUpdateRecords(orderId, salesErrorItems);
            } else {
                log.info("所有商品销量更新成功: orderId={}", orderId);
            }
        } catch (Exception e) {
            // 销量更新总异常，记录但不抛出
            log.error("销量更新过程发生系统异常: orderId={}", orderId, e);
        }
    }
}
