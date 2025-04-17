package com.macro.mall.aspect;

import com.macro.mall.common.exception.ApiException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * 幂等性控制切面，防止表单重复提交
 */
@Aspect
@Component
public class IdempotentAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(IdempotentAspect.class);

    // 最大重试次数
    private static final int MAX_RETRIES = 3;
    // 重试延迟时间 (毫秒)
    private static final long RETRY_DELAY = 100;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 幂等性控制注解，标记需要进行幂等性控制的方法
     */
    @Target({ ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Idempotent {
        /**
         * 幂等性操作的超时时间，单位为秒
         */
        int timeout() default 10;

        /**
         * 是否在请求完成后自动删除token
         */
        boolean autoRemove() default true;

        /**
         * 幂等性校验失败时的提示信息
         */
        String message() default "请勿重复提交";

        /**
         * 如果Redis不可用，是否继续执行业务逻辑
         */
        boolean continueOnRedisFailure() default false;
    }

    /**
     * 定义切点 - 所有带有Idempotent注解的方法
     */
    @Pointcut("@annotation(com.macro.mall.aspect.IdempotentAspect.Idempotent)")
    public void idempotentPointcut() {
    }

    /**
     * 在方法执行前后进行幂等性控制
     */
    @Around("idempotentPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取请求信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            LOGGER.warn("无法获取请求上下文，跳过幂等性控制");
            return joinPoint.proceed();
        }

        HttpServletRequest request = attributes.getRequest();

        // 获取方法信息
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取自定义注解中的信息
        Idempotent idempotent = method.getAnnotation(Idempotent.class);
        if (idempotent == null) {
            return joinPoint.proceed();
        }

        // 获取客户端IP，用于日志记录和问题排查
        String clientIp = getClientIp(request);

        // 获取token，优先从请求头中获取，如果没有则从请求参数中获取
        String token = request.getHeader("Idempotent-Token");
        if (token == null || token.isEmpty()) {
            token = request.getParameter("idempotentToken");
        }

        // 如果没有token，说明不是幂等性请求，直接放行
        if (token == null || token.isEmpty()) {
            LOGGER.debug("幂等性请求没有提供token，直接放行");
            return joinPoint.proceed();
        }

        // 构建Redis键
        String redisKey = String.format("idempotent:%s:%s:%s",
                method.getDeclaringClass().getName(),
                method.getName(),
                token);

        LOGGER.info("幂等性控制：请求方法={}, 路径={}, 客户端IP={}, token={}",
                request.getMethod(), request.getRequestURI(), clientIp, token);

        boolean isFirstRequest = false;
        boolean redisFailure = false;

        // 使用重试机制处理Redis临时不可用的情况
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                isFirstRequest = redisTemplate.opsForValue().setIfAbsent(redisKey,
                        UUID.randomUUID().toString(), idempotent.timeout(), TimeUnit.SECONDS);

                // 操作成功，跳出循环
                break;
            } catch (DataAccessException e) {
                LOGGER.warn("Redis连接异常 (尝试 {}/{}): {}", attempt + 1, MAX_RETRIES, e.getMessage());

                if (attempt == MAX_RETRIES - 1) {
                    // 最后一次尝试也失败
                    redisFailure = true;
                    LOGGER.error("Redis服务不可用，无法执行幂等性控制: {}", e.getMessage());
                } else {
                    // 等待一段时间后重试
                    try {
                        Thread.sleep(RETRY_DELAY * (attempt + 1));
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        LOGGER.warn("重试等待被中断", ie);
                    }
                }
            }
        }

        // 处理Redis不可用的情况
        if (redisFailure) {
            if (idempotent.continueOnRedisFailure()) {
                LOGGER.warn("Redis不可用，跳过幂等性控制继续执行，可能导致重复提交: token={}", token);
                return joinPoint.proceed();
            } else {
                LOGGER.error("Redis不可用且配置不允许跳过幂等性控制: token={}", token);
                throw new ApiException("系统繁忙，请稍后重试");
            }
        }

        if (!isFirstRequest) {
            // 该请求已经被处理过
            LOGGER.warn("幂等性控制：检测到重复提交 token={}, 客户端IP={}", token, clientIp);
            throw new ApiException(idempotent.message());
        }

        try {
            // 执行目标方法
            LOGGER.info("幂等性控制：首次请求，执行业务逻辑 token={}", token);
            Object result = joinPoint.proceed();

            // 如果自动删除token，则在请求完成后删除
            if (idempotent.autoRemove()) {
                try {
                    boolean deleted = redisTemplate.delete(redisKey);
                    if (deleted) {
                        LOGGER.debug("幂等性控制：请求完成后成功删除token {}", token);
                    } else {
                        LOGGER.warn("幂等性控制：请求完成后删除token失败，可能已过期 {}", token);
                    }
                } catch (Exception e) {
                    // 删除token失败不影响正常业务
                    LOGGER.warn("幂等性控制：删除token时发生异常 {}: {}", token, e.getMessage());
                }
            }

            return result;
        } catch (Throwable e) {
            // 如果请求出现异常，也需要删除token，以便用户可以重新提交
            try {
                redisTemplate.delete(redisKey);
                LOGGER.info("幂等性控制：请求异常，删除token {} 以允许重试", token);
            } catch (Exception ex) {
                // 删除token异常不影响主流程异常抛出
                LOGGER.warn("幂等性控制：请求异常后删除token时发生异常 {}: {}", token, ex.getMessage());
            }
            throw e;
        }
    }

    /**
     * 生成幂等性token
     * 
     * @return 幂等性token
     */
    public String generateToken() {
        String token = UUID.randomUUID().toString();
        LOGGER.debug("生成幂等性token: {}", token);
        return token;
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多个代理的情况，第一个IP为客户端真实IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}