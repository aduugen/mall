package com.macro.mall.service;

import com.macro.mall.model.UmsMember;

public interface UmsMemberService {
    /**
     * 获取会员总数
     */
    long geTotalMemberCount();
    /**
     * 获取今日新增注册用户数
     */
    int getTodayNewMemberCount();

}
