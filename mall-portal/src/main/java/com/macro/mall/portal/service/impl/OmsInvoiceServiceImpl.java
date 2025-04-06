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
    public int apply(OmsInvoiceParam invoiceParam) {
        OmsInvoice invoice = new OmsInvoice();
        BeanUtils.copyProperties(invoiceParam, invoice);
        // 设置当前用户会员ID
        UmsMember currentMember = memberService.getCurrentMember();
        invoice.setMemberId(currentMember.getId());
        // 设置申请时间
        invoice.setApplyTime(new Date());
        // 设置创建时间
        invoice.setCreateTime(new Date());
        // 设置更新时间
        invoice.setUpdateTime(new Date());
        // 设置初始状态为申请中
        invoice.setStatus(0);

        // 保存发票申请记录
        int count = invoiceMapper.insert(invoice);

        // 更新订单的发票状态为"申请中"
        if (count > 0) {
            OmsOrder orderUpdate = new OmsOrder();
            orderUpdate.setId(invoice.getOrderId());
            orderUpdate.setInvoiceStatus(1); // 1表示申请中
            orderMapper.updateByPrimaryKeySelective(orderUpdate);
        }

        return count;
    }

    @Override
    public List<OmsInvoice> list(Integer status) {
        OmsInvoiceExample example = new OmsInvoiceExample();
        OmsInvoiceExample.Criteria criteria = example.createCriteria();
        if (status != null) {
            criteria.andStatusEqualTo(status);
        }
        example.setOrderByClause("create_time desc");
        return invoiceMapper.selectByExample(example);
    }

    @Override
    public OmsInvoice getItem(Long id) {
        return invoiceMapper.selectByPrimaryKey(id);
    }

    @Override
    public OmsInvoice getByOrderId(Long orderId) {
        OmsInvoiceExample example = new OmsInvoiceExample();
        example.createCriteria().andOrderIdEqualTo(orderId);
        List<OmsInvoice> invoices = invoiceMapper.selectByExample(example);
        if (invoices != null && !invoices.isEmpty()) {
            return invoices.get(0);
        }
        return null;
    }

    @Override
    public int updateStatus(Long id, Integer status, String handleNote) {
        OmsInvoice invoice = new OmsInvoice();
        invoice.setId(id);
        invoice.setStatus(status);
        invoice.setNote(handleNote);
        invoice.setUpdateTime(new Date());

        // 如果状态为已开票，设置开票时间
        if (status == 1) {
            invoice.setIssueTime(new Date());
        }

        // 更新发票记录
        int count = invoiceMapper.updateByPrimaryKeySelective(invoice);

        // 如果更新成功，同时更新订单的发票状态
        if (count > 0) {
            // 获取完整的发票信息
            OmsInvoice fullInvoice = invoiceMapper.selectByPrimaryKey(id);
            if (fullInvoice != null && fullInvoice.getOrderId() != null) {
                OmsOrder orderUpdate = new OmsOrder();
                orderUpdate.setId(fullInvoice.getOrderId());

                // 根据发票状态设置订单的发票状态
                switch (status) {
                    case 1: // 已开票
                        orderUpdate.setInvoiceStatus(2);
                        break;
                    case 2: // 已拒绝
                        orderUpdate.setInvoiceStatus(3);
                        break;
                    default: // 其他状态（申请中）
                        orderUpdate.setInvoiceStatus(1);
                        break;
                }

                orderMapper.updateByPrimaryKeySelective(orderUpdate);
            }
        }

        return count;
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