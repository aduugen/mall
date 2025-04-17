package com.macro.mall.portal.util;

import com.macro.mall.common.service.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁工具类
 * 基于Redis的SETNX命令实现的分布式锁
 */
@Component
public class RedisDistributedLock {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisDistributedLock.class);

    @Autowired
    private RedisService redisService;

    /**
     * 获取分布式锁
     *
     * @param lockKey    锁的key
     * @param requestId  请求标识（用于锁的释放验证）
     * @param expireTime 锁的过期时间
     * @param timeUnit   时间单位
     * @return 是否获取成功
     */
    public boolean acquireLock(String lockKey, String requestId, long expireTime, TimeUnit timeUnit) {
        try {
            LOGGER.info("尝试获取分布式锁，key:{}, requestId:{}, expireTime:{}", lockKey, requestId, expireTime);
            Boolean result = redisService.setIfAbsent(lockKey, requestId, expireTime, timeUnit);
            if (Boolean.TRUE.equals(result)) {
                LOGGER.info("获取分布式锁成功，key:{}, requestId:{}", lockKey, requestId);
                return true;
            }
            LOGGER.info("获取分布式锁失败，key:{}, requestId:{}", lockKey, requestId);
            return false;
        } catch (Exception e) {
            LOGGER.error("获取分布式锁异常，key:{}, requestId:{}, error:{}", lockKey, requestId, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 释放分布式锁
     *
     * @param lockKey   锁的key
     * @param requestId 请求标识（用于锁的释放验证）
     * @return 是否释放成功
     */
    public boolean releaseLock(String lockKey, String requestId) {
        try {
            LOGGER.info("尝试释放分布式锁，key:{}, requestId:{}", lockKey, requestId);

            // 获取锁当前的值
            Object currentValue = redisService.get(lockKey);

            // 判断是否为当前线程持有的锁
            if (currentValue != null && requestId.equals(currentValue.toString())) {
                Boolean result = redisService.del(lockKey);
                if (Boolean.TRUE.equals(result)) {
                    LOGGER.info("释放分布式锁成功，key:{}, requestId:{}", lockKey, requestId);
                    return true;
                }
            } else if (currentValue == null) {
                LOGGER.info("锁已过期或不存在，key:{}, requestId:{}", lockKey, requestId);
                return true;
            } else {
                LOGGER.warn("释放分布式锁失败，锁不属于当前请求，key:{}, requestId:{}, currentValue:{}",
                        lockKey, requestId, currentValue);
            }
            return false;
        } catch (Exception e) {
            LOGGER.error("释放分布式锁异常，key:{}, requestId:{}, error:{}", lockKey, requestId, e.getMessage(), e);
            return false;
        }
    }
}