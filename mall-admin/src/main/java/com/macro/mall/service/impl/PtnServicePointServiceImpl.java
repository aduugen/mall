package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.mapper.PtnServicePointMapper;
import com.macro.mall.model.PtnServicePoint;
import com.macro.mall.model.PtnServicePointExample;
import com.macro.mall.service.PtnServicePointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
        if (point.getServicePointStatus() == null) {
            point.setServicePointStatus(0); // 默认状态为正常
        }
        if (point.getSelfPickBillCount() == null) {
            point.setSelfPickBillCount(0); // 默认自提单量为0
        }
        if (point.getReceiveBillCount() == null) {
            point.setReceiveBillCount(0); // 默认收货单量为0
        }
        if (point.getServiceStarRating() == null) {
            point.setServiceStarRating(3); // 默认3星
        }
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
        example.setOrderByClause("create_time desc");
        return servicePointMapper.selectByExample(example);
    }

    @Override
    public List<PtnServicePoint> listByType(Integer type) {
        return servicePointMapper.selectByType(type);
    }

    @Override
    public int updateStatus(Long id, Integer status) {
        return servicePointMapper.updateStatus(id, status);
    }

    @Override
    public int updateBillCount(Long id, Integer selfPickCount, Integer receiveCount) {
        return servicePointMapper.updateBillCount(id, selfPickCount, receiveCount);
    }

    @Override
    public int updateServiceRating(Long id, Integer rating) {
        return servicePointMapper.updateServiceRating(id, rating);
    }

    @Override
    public List<PtnServicePoint> searchReceivePoints(String keyword, Integer status) {
        List<PtnServicePoint> result = new ArrayList<>();

        // 首先通过mapper.selectByType获取两种类型的服务点
        List<PtnServicePoint> typeOnePoints = servicePointMapper.selectByType(1); // 收货点
        List<PtnServicePoint> typeTwoPoints = servicePointMapper.selectByType(2); // 综合点

        // 合并两种类型的点
        if (typeOnePoints != null) {
            result.addAll(typeOnePoints);
        }
        if (typeTwoPoints != null) {
            result.addAll(typeTwoPoints);
        }

        // 根据状态过滤
        if (status != null) {
            result = result.stream()
                    .filter(point -> status.equals(point.getServicePointStatus()))
                    .collect(Collectors.toList());
        }

        // 根据关键字过滤
        if (!StringUtils.isEmpty(keyword)) {
            final String finalKeyword = keyword.toLowerCase();
            result = result.stream()
                    .filter(point -> point.getLocationName() != null &&
                            point.getLocationName().toLowerCase().contains(finalKeyword))
                    .collect(Collectors.toList());
        }

        // 按创建时间排序
        result.sort((p1, p2) -> {
            if (p1.getCreateTime() == null)
                return 1;
            if (p2.getCreateTime() == null)
                return -1;
            return p2.getCreateTime().compareTo(p1.getCreateTime());
        });

        return result;
    }
}