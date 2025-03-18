package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.UmsMemberDao;
import com.macro.mall.dto.MemberConsumptionInfoDTO;
import com.macro.mall.dto.MemberInfoDTO;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.mapper.UmsMemberReceiveAddressMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.model.UmsMemberReceiveAddressExample;
import com.macro.mall.service.UmsMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsMemberServiceImpl.class);

    @Autowired
    private UmsMemberMapper memberMapper;

    @Autowired
    private UmsMemberDao memberDao;

    @Autowired
    private UmsMemberReceiveAddressMapper memberReceiveAddressMapper;

    @Override
    public long geTotalMemberCount() {
        UmsMemberExample example = new UmsMemberExample();
        return memberMapper.countByExample(example);
    }

    @Override
    public int getTodayNewMemberCount() {
        return memberDao.getTodayNewMemberCount();
    }

    @Override
    public List<MemberInfoDTO> list(String keyword, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return memberDao.getMemberList(keyword);
    }

    @Override
    public MemberInfoDTO getMemberInfo(Long id) {
        return memberDao.getMemberInfo(id);
    }

    @Override
    public List<MemberConsumptionInfoDTO> listConsumptionInfo(String keyword, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return memberDao.getMemberConsumptionList(keyword);
    }

    @Override
    public MemberConsumptionInfoDTO getMemberConsumptionInfo(Long id) {
        return memberDao.getMemberConsumptionInfo(id);
    }

    @Override
    public List<OmsOrder> getMemberOrders(Long memberId, Integer pageSize, Integer pageNum) {
        LOGGER.info("开始查询会员订单数据: memberId={}", memberId);
        PageHelper.startPage(pageNum, pageSize);
        List<OmsOrder> orderList = memberDao.getMemberOrders(memberId);
        LOGGER.info("查询到的订单数量: {}", orderList.size());

        // 打印每条订单信息的关键字段
        for (int i = 0; i < orderList.size() && i < 3; i++) { // 只打印前3条记录避免日志过多
            OmsOrder order = orderList.get(i);
            LOGGER.info("订单[{}]: id={}, orderSn={}, totalAmount={}, payAmount={}, payType={}, sourceType={}, status={}",
                    i, order.getId(), order.getOrderSn(), order.getTotalAmount(),
                    order.getPayAmount(), order.getPayType(), order.getSourceType(),
                    order.getStatus());
        }

        return orderList;
    }

    @Override
    public long getMemberOrderCount(Long memberId) {
        return memberDao.getMemberOrders(memberId).size();
    }
}
