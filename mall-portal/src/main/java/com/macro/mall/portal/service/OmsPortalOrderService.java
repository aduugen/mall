package com.macro.mall.portal.service;

import com.macro.mall.common.api.CommonPage;
import com.macro.mall.model.OmsCartItem;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderItem;
import com.macro.mall.portal.domain.ConfirmOrderResult;
import com.macro.mall.portal.domain.OmsOrderDetail;
import com.macro.mall.portal.domain.OrderParam;
import com.macro.mall.portal.domain.PmsCommentParam;
import com.macro.mall.portal.domain.PmsBatchCommentParam;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 前台订单管理Service
 * Created by macro on 2018/8/30.
 */
public interface OmsPortalOrderService {
    /**
     * 根据用户购物车信息生成确认单信息
     */
    ConfirmOrderResult generateConfirmOrder(List<Long> cartIds);

    /**
     * 根据提交信息生成订单
     */
    @Transactional
    Map<String, Object> generateOrder(OrderParam orderParam);

    /**
     * 支付成功后的回调
     */
    @Transactional
    Integer paySuccess(Long orderId, Integer payType);

    /**
     * 自动取消超时订单
     */
    @Transactional
    Integer cancelTimeOutOrder();

    /**
     * 取消单个超时订单
     */
    @Transactional
    void cancelOrder(Long orderId);

    /**
     * 发送延迟消息取消订单
     */
    void sendDelayMessageCancelOrder(Long orderId);

    /**
     * 确认收货
     */
    void confirmReceiveOrder(Long orderId);

    /**
     * 分页获取用户订单
     */
    CommonPage<OmsOrderDetail> list(Integer status, Integer pageNum, Integer pageSize);

    /**
     * 根据订单ID获取订单详情
     */
    OmsOrderDetail detail(Long orderId);

    /**
     * 用户根据订单ID删除订单
     */
    void deleteOrder(Long orderId);

    /**
     * 根据orderSn来实现的支付成功逻辑
     */
    @Transactional
    void paySuccessByOrderSn(String orderSn, Integer payType);

    /**
     * 添加商品评价
     */
    int createProductComment(PmsCommentParam commentParam);

    /**
     * 批量添加商品评价
     */
    int createBatchProductComment(PmsBatchCommentParam batchCommentParam);

    /**
     * 获取指定订单中待评价的商品列表
     */
    List<OmsOrderItem> getOrderProductsForComment(Long orderId);
}
