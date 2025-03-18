package com.macro.mall.dao;

import com.macro.mall.dto.MemberConsumptionInfoDTO;
import com.macro.mall.dto.MemberInfoDTO;
import com.macro.mall.model.OmsOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 会员管理自定义Dao
 */
public interface UmsMemberDao {
    /**
     * 获取今日新增注册用户数
     */
    int getTodayNewMemberCount();

    /**
     * 获取会员信息列表
     */
    List<MemberInfoDTO> getMemberList(@Param("keyword") String keyword);

    /**
     * 获取会员详情信息
     */
    MemberInfoDTO getMemberInfo(@Param("id") Long id);

    /**
     * 获取会员消费信息列表
     */
    List<MemberConsumptionInfoDTO> getMemberConsumptionList(@Param("keyword") String keyword);

    /**
     * 获取会员消费信息详情
     */
    MemberConsumptionInfoDTO getMemberConsumptionInfo(@Param("id") Long id);

    /**
     * 获取会员订单列表
     */
    List<OmsOrder> getMemberOrders(@Param("memberId") Long memberId);
}