package com.macro.mall.service;

import com.macro.mall.dto.MemberConsumptionInfoDTO;
import com.macro.mall.dto.MemberInfoDTO;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.UmsMember;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UmsMemberService {
    /**
     * 获取会员总数
     */
    long geTotalMemberCount();

    /**
     * 获取今日新增注册用户数
     */
    int getTodayNewMemberCount();

    /**
     * 分页获取会员列表
     */
    List<MemberInfoDTO> list(String keyword, Integer pageSize, Integer pageNum);

    /**
     * 获取会员详情
     */
    MemberInfoDTO getMemberInfo(Long id);

    /**
     * 创建会员
     */
    int create(UmsMember umsMember);

    /**
     * 更新会员信息
     */
    int update(Long id, UmsMember umsMember);

    /**
     * 分页获取会员消费信息列表
     */
    List<MemberConsumptionInfoDTO> listConsumptionInfo(String keyword, Integer pageSize, Integer pageNum);

    /**
     * 获取会员的消费信息
     */
    MemberConsumptionInfoDTO getMemberConsumptionInfo(Long id);

    /**
     * 获取会员的订单列表
     */
    List<OmsOrder> getMemberOrders(Long memberId, Integer pageSize, Integer pageNum);

    /**
     * 获取会员订单总数
     */
    long getMemberOrderCount(Long memberId);
}
