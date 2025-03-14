package com.macro.mall.service.impl;
import com.macro.mall.mapper.UmsMemberMapper;
import com.macro.mall.model.UmsMemberExample;
import com.macro.mall.service.UmsMemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UmsMemberServiceImpl implements UmsMemberService {
    
    @Autowired
    private UmsMemberMapper memberMapper;
    
    @Override
    public long geTotalMemberCount() {
        UmsMemberExample example = new UmsMemberExample();
        return memberMapper.countByExample(example);
    }

    @Override
    public int getTodayNewMemberCount() {
        return memberMapper.getTodayNewMemberCount();
    }
}
