package com.macro.mall.portal.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.common.api.CommonPage;
import com.macro.mall.common.exception.Asserts;
import com.macro.mall.mapper.OmsInvoiceMapper;
import com.macro.mall.mapper.OmsInvoiceHistoryMapper;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.model.*;
import com.macro.mall.portal.domain.OmsInvoiceParam;
import com.macro.mall.portal.service.OmsInvoiceService;
import com.macro.mall.portal.service.UmsMemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 发票管理Service实现类
 */
@Service
public class OmsInvoiceServiceImpl implements OmsInvoiceService {
    @Autowired
    private OmsInvoiceMapper invoiceMapper;
    @Autowired
    private OmsInvoiceHistoryMapper invoiceHistoryMapper;
    @Autowired
    private OmsOrderMapper orderMapper;
    @Autowired
    private UmsMemberService memberService;

    @Override
    public OmsInvoice apply(OmsInvoiceParam invoiceParam) {
        // 检查订单是否存在
        OmsOrder order = orderMapper.selectByPrimaryKey(invoiceParam.getOrderId());
        if (order == null) {
            Asserts.fail("订单不存在");
        }

        // 检查订单是否属于当前用户
        UmsMember currentMember = memberService.getCurrentMember();
        if (!order.getMemberId().equals(currentMember.getId())) {
            Asserts.fail("不是您的订单");
        }

        // 检查订单状态是否已支付
        if (order.getStatus() < 1) {
            Asserts.fail("订单尚未支付，无法开票");
        }

        // 检查是否已经申请过发票
        OmsInvoiceExample example = new OmsInvoiceExample();
        example.createCriteria().andOrderIdEqualTo(invoiceParam.getOrderId());
        List<OmsInvoice> existInvoices = invoiceMapper.selectByExample(example);
        if (existInvoices != null && !existInvoices.isEmpty()) {
            Asserts.fail("该订单已申请过发票");
        }

        // 数据校验
        if (invoiceParam.getInvoiceType() == 1) {
            // 电子发票
            if (StringUtils.isEmpty(invoiceParam.getReceiverEmail())) {
                Asserts.fail("电子发票必须填写收票邮箱");
            }
        } else if (invoiceParam.getInvoiceType() == 2) {
            // 纸质发票
            if (StringUtils.isEmpty(invoiceParam.getReceiverName()) ||
                    StringUtils.isEmpty(invoiceParam.getReceiverPhone()) ||
                    StringUtils.isEmpty(invoiceParam.getReceiverAddress())) {
                Asserts.fail("纸质发票必须填写收件人信息");
            }
        } else {
            Asserts.fail("不支持的发票类型");
        }

        if (invoiceParam.getTitleType() == 2 && StringUtils.isEmpty(invoiceParam.getTaxNumber())) {
            Asserts.fail("企业发票必须填写税号");
        }

        // 创建发票记录
        OmsInvoice invoice = new OmsInvoice();
        BeanUtils.copyProperties(invoiceParam, invoice);

        // 设置基本信息
        invoice.setMemberId(currentMember.getId());
        invoice.setInvoiceAmount(order.getTotalAmount());
        invoice.setApplyTime(new Date());
        invoice.setStatus(0); // 待开票
        invoice.setCreateTime(new Date());

        // 保存发票记录
        invoiceMapper.insert(invoice);

        // 添加发票操作历史记录
        addInvoiceHistory(invoice, "用户申请发票");

        return invoice;
    }

    @Override
    public OmsInvoice getByOrderId(Long orderId) {
        UmsMember currentMember = memberService.getCurrentMember();
        OmsInvoiceExample example = new OmsInvoiceExample();
        example.createCriteria()
                .andOrderIdEqualTo(orderId)
                .andMemberIdEqualTo(currentMember.getId());
        List<OmsInvoice> invoiceList = invoiceMapper.selectByExample(example);
        if (invoiceList != null && !invoiceList.isEmpty()) {
            return invoiceList.get(0);
        }
        return null;
    }

    @Override
    public CommonPage<OmsInvoice> list(Integer status, Integer pageNum, Integer pageSize) {
        UmsMember currentMember = memberService.getCurrentMember();
        PageHelper.startPage(pageNum, pageSize);
        OmsInvoiceExample example = new OmsInvoiceExample();
        OmsInvoiceExample.Criteria criteria = example.createCriteria();
        criteria.andMemberIdEqualTo(currentMember.getId());
        if (status != null && status != -1) {
            criteria.andStatusEqualTo(status);
        }
        example.setOrderByClause("create_time desc");
        List<OmsInvoice> invoiceList = invoiceMapper.selectByExample(example);
        return CommonPage.restPage(invoiceList);
    }

    @Override
    public OmsInvoice detail(Long id) {
        UmsMember currentMember = memberService.getCurrentMember();
        OmsInvoice invoice = invoiceMapper.selectByPrimaryKey(id);
        if (invoice == null || !invoice.getMemberId().equals(currentMember.getId())) {
            Asserts.fail("发票不存在");
        }
        return invoice;
    }

    /**
     * 添加发票操作历史记录
     */
    private void addInvoiceHistory(OmsInvoice invoice, String note) {
        OmsInvoiceHistory history = new OmsInvoiceHistory();
        history.setInvoiceId(invoice.getId());
        history.setOrderId(invoice.getOrderId());
        history.setOrderSn(invoice.getOrderSn());
        history.setOperateMan("用户:" + memberService.getCurrentMember().getUsername());
        history.setStatus(invoice.getStatus());
        history.setNote(note);
        history.setCreateTime(new Date());
        invoiceHistoryMapper.insert(history);
    }
}