package com.macro.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.UmsMemberDao;
import com.macro.mall.dto.MemberConsumptionInfoDTO;
import com.macro.mall.dto.MemberInfoDTO;
import com.macro.mall.dto.OmsOrderQueryParam;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderExample;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.service.UmsMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * 会员管理Service实现类
 */
@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UmsMemberServiceImpl.class);
    @Autowired
    private UmsMemberMapper memberMapper;
    @Autowired
    private UmsMemberDao memberDao;
    @Autowired
    private OmsOrderMapper orderMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public long geTotalMemberCount() {
        return memberMapper.countByExample(new UmsMemberExample());
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
    public int create(UmsMember umsMember) {
        umsMember.setCreateTime(new Date());

        // 使用手机号码作为登录名
        umsMember.setUsername(umsMember.getPhone());

        // 查询是否已有相同手机号的会员
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andPhoneEqualTo(umsMember.getPhone());
        List<UmsMember> umsMembers = memberMapper.selectByExample(example);
        if (umsMembers.size() > 0) {
            return 0;
        }

        // 处理密码，如果为空则设置默认密码123456
        if (StrUtil.isEmpty(umsMember.getPassword())) {
            umsMember.setPassword("123456");
        }

        // 对密码进行加密
        String encodePassword = passwordEncoder(umsMember.getPassword());
        umsMember.setPassword(encodePassword);

        return memberMapper.insert(umsMember);
    }

    /**
     * 密码加密
     */
    private String passwordEncoder(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public int update(Long id, UmsMember umsMember) {
        umsMember.setId(id);
        // 如果有密码更新，需要进行加密
        if (StrUtil.isNotEmpty(umsMember.getPassword())) {
            String encodePassword = passwordEncoder(umsMember.getPassword());
            umsMember.setPassword(encodePassword);
        } else {
            // 如果密码为空，则不更新密码字段
            umsMember.setPassword(null);
        }

        return memberMapper.updateByPrimaryKeySelective(umsMember);
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
    public List<OmsOrder> getMemberOrders(Long memberId, OmsOrderQueryParam queryParam, Integer pageSize,
            Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        OmsOrderExample example = new OmsOrderExample();
        example.setOrderByClause("create_time desc");
        OmsOrderExample.Criteria criteria = example.createCriteria();
        criteria.andMemberIdEqualTo(memberId);
        criteria.andDeleteStatusEqualTo(0);

        // 添加筛选条件
        if (queryParam.getOrderSn() != null && !queryParam.getOrderSn().isEmpty()) {
            criteria.andOrderSnLike("%" + queryParam.getOrderSn() + "%");
        }
        if (queryParam.getPayType() != null) {
            criteria.andPayTypeEqualTo(queryParam.getPayType());
        }
        if (queryParam.getSourceType() != null) {
            criteria.andSourceTypeEqualTo(queryParam.getSourceType());
        }
        if (queryParam.getStatus() != null) {
            criteria.andStatusEqualTo(queryParam.getStatus());
        }
        if (queryParam.getReturnStatus() != null) {
            criteria.andReturnStatusEqualTo(queryParam.getReturnStatus());
        }

        return orderMapper.selectByExample(example);
    }

    @Override
    public long getMemberOrderCount(Long memberId, OmsOrderQueryParam queryParam) {
        OmsOrderExample example = new OmsOrderExample();
        OmsOrderExample.Criteria criteria = example.createCriteria();
        criteria.andMemberIdEqualTo(memberId);
        criteria.andDeleteStatusEqualTo(0);

        // 添加筛选条件
        if (queryParam.getOrderSn() != null && !queryParam.getOrderSn().isEmpty()) {
            criteria.andOrderSnLike("%" + queryParam.getOrderSn() + "%");
        }
        if (queryParam.getPayType() != null) {
            criteria.andPayTypeEqualTo(queryParam.getPayType());
        }
        if (queryParam.getSourceType() != null) {
            criteria.andSourceTypeEqualTo(queryParam.getSourceType());
        }
        if (queryParam.getStatus() != null) {
            criteria.andStatusEqualTo(queryParam.getStatus());
        }
        if (queryParam.getReturnStatus() != null) {
            criteria.andReturnStatusEqualTo(queryParam.getReturnStatus());
        }

        return orderMapper.countByExample(example);
    }
}
