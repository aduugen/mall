package com.macro.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.macro.mall.dao.OmsOrderReturnApplyDao;
import com.macro.mall.dto.OmsOrderReturnApplyResult;
import com.macro.mall.dto.OmsReturnApplyQueryParam;
import com.macro.mall.dto.OmsUpdateStatusParam;
import com.macro.mall.dto.OmsReturnApplyStatistic;
import com.macro.mall.mapper.OmsOrderReturnApplyMapper;
import com.macro.mall.mapper.PmsProductMapper;
import com.macro.mall.model.OmsOrderReturnApply;
import com.macro.mall.model.OmsOrderReturnApplyExample;
import com.macro.mall.service.OmsOrderReturnApplyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 订单退货管理Service实现类
 * Created by macro on 2018/10/18.
 */
@Slf4j
@Service
public class OmsOrderReturnApplyServiceImpl implements OmsOrderReturnApplyService {
    @Autowired
    private OmsOrderReturnApplyDao returnApplyDao;
    @Autowired
    private OmsOrderReturnApplyMapper returnApplyMapper;
    @Autowired
    private PmsProductMapper productMapper;

    @Override
    public List<OmsOrderReturnApply> list(OmsReturnApplyQueryParam queryParam, Integer pageSize, Integer pageNum) {
        PageHelper.startPage(pageNum, pageSize);
        return returnApplyDao.getList(queryParam);
    }

    @Override
    public int delete(List<Long> ids) {
        OmsOrderReturnApplyExample example = new OmsOrderReturnApplyExample();
        example.createCriteria().andIdIn(ids).andStatusEqualTo(3);
        return returnApplyMapper.deleteByExample(example);
    }

    @Override
    public int updateStatus(Long id, OmsUpdateStatusParam statusParam) {
        Integer status = statusParam.getStatus();
        OmsOrderReturnApply returnApply = new OmsOrderReturnApply();
        if (status.equals(1)) {
            // 确认退货
            returnApply.setId(id);
            returnApply.setStatus(1);
            returnApply.setReturnAmount(statusParam.getReturnAmount());
            returnApply.setCompanyAddressId(statusParam.getCompanyAddressId());
            returnApply.setHandleTime(new Date());
            returnApply.setHandleMan(statusParam.getHandleMan());
            returnApply.setHandleNote(statusParam.getHandleNote());
        } else if (status.equals(2)) {
            // 完成退货
            returnApply.setId(id);
            returnApply.setStatus(2);
            returnApply.setReceiveTime(new Date());
            returnApply.setReceiveMan(statusParam.getReceiveMan());
            returnApply.setReceiveNote(statusParam.getReceiveNote());

            // 完成退货时，扣减商品销量
            // 首先需要获取原退货申请的详细信息以获得 productId 和 productCount
            OmsOrderReturnApply originalApply = returnApplyMapper.selectByPrimaryKey(id);
            if (originalApply != null && originalApply.getProductId() != null && originalApply.getProductCount() != null
                    && originalApply.getProductCount() > 0) {
                try {
                    int saleUpdateCount = productMapper.decreaseSale(originalApply.getProductId(),
                            originalApply.getProductCount());
                    if (saleUpdateCount == 0) {
                        // 销量扣减失败，记录日志，根据业务决定是否抛异常
                        log.warn("退货完成后扣减商品销量失败: returnApplyId={}, productId={}, quantity={}", id,
                                originalApply.getProductId(), originalApply.getProductCount());
                        // Asserts.fail("扣减商品销量失败！"); // 可选
                    } else {
                        log.info("商品销量扣减成功: productId={}, quantity={}", originalApply.getProductId(),
                                originalApply.getProductCount());
                    }
                } catch (Exception e) {
                    // 捕获可能的数据库异常等
                    log.error("退货完成后扣减商品销量时发生异常: returnApplyId={}, productId={}, quantity={}", id,
                            originalApply.getProductId(), originalApply.getProductCount(), e);
                    // 根据业务需要决定是否抛出异常中断流程
                    // throw new RuntimeException("扣减销量异常", e);
                }
            } else {
                log.warn("无法扣减销量，未找到退货申请或商品信息不完整: returnApplyId={}", id);
            }
        } else if (status.equals(3)) {
            // 拒绝退货
            returnApply.setId(id);
            returnApply.setStatus(3);
            returnApply.setHandleTime(new Date());
            returnApply.setHandleMan(statusParam.getHandleMan());
            returnApply.setHandleNote(statusParam.getHandleNote());
        } else {
            return 0;
        }
        return returnApplyMapper.updateByPrimaryKeySelective(returnApply);
    }

    @Override
    public OmsOrderReturnApplyResult getItem(Long id) {
        return returnApplyDao.getDetail(id);
    }

    @Override
    public OmsReturnApplyStatistic getReturnApplyStatistic() {
        return returnApplyDao.getReturnApplyStatistic();
    }
}
