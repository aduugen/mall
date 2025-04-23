package com.macro.mall.service;

import com.macro.mall.model.PtnServicePoint;

import java.util.List;

public interface PtnServicePointService {
    /**
     * 获取所有合作网点
     */
    List<PtnServicePoint> listAll();

    /**
     * 根据ID获取单个合作网点
     */
    PtnServicePoint getItem(Long id);

    /**
     * 添加合作网点
     */
    int create(PtnServicePoint point);

    /**
     * 更新合作网点
     */
    int update(Long id, PtnServicePoint point);

    /**
     * 删除合作网点
     */
    int delete(Long id);

    /**
     * 分页查询合作网点
     */
    List<PtnServicePoint> list(String keyword, Integer pageSize, Integer pageNum);

    /**
     * 根据网点类型查询
     */
    List<PtnServicePoint> listByType(Integer type);

    /**
     * 更新网点状态
     */
    int updateStatus(Long id, Integer status);

    /**
     * 更新业务数量
     */
    int updateBillCount(Long id, Integer selfPickCount, Integer receiveCount);

    /**
     * 更新网点服务星级
     */
    int updateServiceRating(Long id, Integer rating);

    /**
     * 搜索收货点
     */
    List<PtnServicePoint> searchReceivePoints(String keyword, Integer status);
}