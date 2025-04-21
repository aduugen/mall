package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.PtnServicePointMapper;
import com.macro.mall.model.PtnServicePoint;
import com.macro.mall.model.PtnServicePointExample;
import com.macro.mall.service.PtnServicePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Service
public class PtnServicePointServiceImpl implements PtnServicePointService {
    @Autowired
    private PtnServicePointMapper servicePointMapper;

    @Override
    public List<PtnServicePoint> listAll() {
        return servicePointMapper.selectByExample(new PtnServicePointExample());
    }

    @Override
    public PtnServicePoint getItem(Long id) {
        return servicePointMapper.selectByPrimaryKey(id);
    }

    @Override
    public int create(PtnServicePoint point) {
        point.setCreateTime(new Date());
        point.setUpdateTime(new Date());
        return servicePointMapper.insertSelective(point);
    }

    @Override
    public int update(Long id, PtnServicePoint point) {
        point.setId(id);
        point.setUpdateTime(new Date());
        return servicePointMapper.updateByPrimaryKeySelective(point);
    }

    @Override
    public int delete(Long id) {
        return servicePointMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<PtnServicePoint> list(String keyword, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        PtnServicePointExample example = new PtnServicePointExample();
        PtnServicePointExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(keyword)) {
            criteria.andLocationNameLike("%" + keyword + "%");
        }
        return servicePointMapper.selectByExample(example);
    }
}