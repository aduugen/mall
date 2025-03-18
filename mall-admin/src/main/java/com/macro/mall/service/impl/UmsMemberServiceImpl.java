package com.macro.mall.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.UmsMemberDao;
import com.macro.mall.dto.MemberConsumptionInfoDTO;
import com.macro.mall.dto.MemberInfoDTO;
import com.macro.mall.mapper.OmsOrderMapper;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.OmsOrder;
import com.macro.mall.model.OmsOrderExample;
import com.macro.mall.model.UmsMember;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.service.UmsMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        // 查询是否已有相同用户名的会员
        UmsMemberExample example = new UmsMemberExample();
        example.createCriteria().andUsernameEqualTo(umsMember.getUsername());
        List<UmsMember> umsMembers = memberMapper.selectByExample(example);
        if (umsMembers.size() > 0) {
            return 0;
        }
        // 密码进行MD5加密
        if (StrUtil.isEmpty(umsMember.getPassword())) {
            umsMember.setPassword("123456"); // 默认密码
        }
        // TODO: 实际环境中需要对密码进行加密，此处略过
        return memberMapper.insert(umsMember);
    }

    @Override
    public int update(Long id, UmsMember umsMember) {
        umsMember.setId(id);
        // 如果有密码更新，需要进行加密
        if (StrUtil.isNotEmpty(umsMember.getPassword())) {
            // TODO: 实际环境中需要对密码进行加密，此处略过
            UmsMember member = memberMapper.selectByPrimaryKey(id);
            if (member == null) {
                return 0;
            }
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
    public List<OmsOrder> getMemberOrders(Long memberId, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        example.setOrderByClause("create_time desc");
        return orderMapper.selectByExample(example);
    }

    @Override
    public long getMemberOrderCount(Long memberId) {
        OmsOrderExample example = new OmsOrderExample();
        example.createCriteria().andMemberIdEqualTo(memberId);
        return orderMapper.countByExample(example);
    }
}
