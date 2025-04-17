package com.macro.mall.service.impl;

import com.macro.mall.model.OmsAfterSale;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * OmsAfterSale的包装类，用于解决IDE类型检查问题
 * 使用反射和本地缓存处理可能不存在于model中的属性
 */
@Slf4j
public class OmsAfterSaleWrapper {

    private final OmsAfterSale afterSale;
    // 用于缓存不存在于model中的属性值
    private final Map<String, Object> extraProperties = new HashMap<>();

    public OmsAfterSaleWrapper(OmsAfterSale afterSale) {
        this.afterSale = afterSale;
    }

    /**
     * 获取内部的OmsAfterSale实例
     */
    public OmsAfterSale getAfterSale() {
        return afterSale;
    }

    /**
     * 获取版本号
     */
    public Integer getVersion() {
        try {
            // 先尝试通过getter方法获取
            Method method = afterSale.getClass().getMethod("getVersion");
            return (Integer) method.invoke(afterSale);
        } catch (NoSuchMethodException e) {
            // 如果没有getter方法，尝试直接访问字段
            try {
                Field field = afterSale.getClass().getDeclaredField("version");
                field.setAccessible(true);
                return (Integer) field.get(afterSale);
            } catch (Exception ex) {
                // 如果字段也不存在，从额外属性中获取
                return (Integer) extraProperties.getOrDefault("version", 0);
            }
        } catch (Exception e) {
            log.warn("获取version失败: {}", e.getMessage());
            return (Integer) extraProperties.getOrDefault("version", 0);
        }
    }

    /**
     * 设置版本号
     */
    public void setVersion(Integer version) {
        try {
            // 先尝试通过setter方法设置
            Method method = afterSale.getClass().getMethod("setVersion", Integer.class);
            method.invoke(afterSale, version);
        } catch (NoSuchMethodException e) {
            // 如果没有setter方法，尝试直接设置字段
            try {
                Field field = afterSale.getClass().getDeclaredField("version");
                field.setAccessible(true);
                field.set(afterSale, version);
            } catch (Exception ex) {
                // 如果字段也不存在，存入额外属性
                extraProperties.put("version", version);
            }
        } catch (Exception e) {
            log.warn("设置version失败: {}", e.getMessage());
            extraProperties.put("version", version);
        }
    }

    /**
     * 设置描述信息
     */
    public void setDescription(String description) {
        try {
            Method method = afterSale.getClass().getMethod("setDescription", String.class);
            method.invoke(afterSale, description);
        } catch (NoSuchMethodException e) {
            try {
                Field field = afterSale.getClass().getDeclaredField("description");
                field.setAccessible(true);
                field.set(afterSale, description);
            } catch (Exception ex) {
                extraProperties.put("description", description);
            }
        } catch (Exception e) {
            log.warn("设置description失败: {}", e.getMessage());
            extraProperties.put("description", description);
        }
    }

    /**
     * 获取描述信息
     */
    public String getDescription() {
        try {
            Method method = afterSale.getClass().getMethod("getDescription");
            return (String) method.invoke(afterSale);
        } catch (NoSuchMethodException e) {
            try {
                Field field = afterSale.getClass().getDeclaredField("description");
                field.setAccessible(true);
                return (String) field.get(afterSale);
            } catch (Exception ex) {
                return (String) extraProperties.getOrDefault("description", "");
            }
        } catch (Exception e) {
            log.warn("获取description失败: {}", e.getMessage());
            return (String) extraProperties.getOrDefault("description", "");
        }
    }

    /**
     * 设置退款金额
     */
    public void setReturnAmount(BigDecimal returnAmount) {
        afterSale.setReturnAmount(returnAmount);
    }

    /**
     * 获取退款金额
     */
    public BigDecimal getReturnAmount() {
        return afterSale.getReturnAmount();
    }

    /**
     * 设置状态
     */
    public void setStatus(Integer status) {
        afterSale.setStatus(status);
    }

    /**
     * 获取状态
     */
    public Integer getStatus() {
        return afterSale.getStatus();
    }

    /**
     * 设置更新时间
     */
    public void setUpdateTime(Date updateTime) {
        afterSale.setUpdateTime(updateTime);
    }

    /**
     * 获取更新时间
     */
    public Date getUpdateTime() {
        return afterSale.getUpdateTime();
    }

    /**
     * 设置ID
     */
    public void setId(Long id) {
        afterSale.setId(id);
    }

    /**
     * 获取ID
     */
    public Long getId() {
        return afterSale.getId();
    }

    /**
     * 获取订单ID
     */
    public Long getOrderId() {
        return afterSale.getOrderId();
    }
}