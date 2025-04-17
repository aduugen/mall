package com.macro.mall.aspect;

import com.macro.mall.common.exception.Asserts;
import com.macro.mall.model.UmsAdmin;
import com.macro.mall.service.UmsAdminService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 审计日志切面，用于记录关键业务操作的审计信息
 */
@Aspect
@Component
public class AuditLogAspect {
    private static final Logger LOGGER = LoggerFactory.getLogger(AuditLogAspect.class);

    @Autowired
    private UmsAdminService adminService;

    /**
     * 审计日志注解，标记需要记录审计日志的方法
     */
    @Target({ ElementType.METHOD })
    @Retention(RetentionPolicy.RUNTIME)
    public @interface AuditLog {
        /**
         * 业务操作描述
         */
        String value() default "";

        /**
         * 业务类型
         */
        String businessType() default "";

        /**
         * 是否保存请求参数
         */
        boolean isSaveRequestData() default true;

        /**
         * 是否保存响应数据
         */
        boolean isSaveResponseData() default true;
    }

    /**
     * 定义切点 - 所有带有AuditLog注解的方法
     */
    @Pointcut("@annotation(com.macro.mall.aspect.AuditLogAspect.AuditLog)")
    public void auditLogPointcut() {
    }

    /**
     * 方法执行后记录日志
     */
    @AfterReturning(value = "auditLogPointcut()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        try {
            // 获取当前登录用户
            UmsAdmin admin = getLoginAdmin();
            // 获取请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            // 获取方法信息
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();

            // 获取自定义注解中的信息
            AuditLog auditLog = method.getAnnotation(AuditLog.class);
            if (auditLog == null) {
                return;
            }

            // 构建审计日志信息
            Map<String, Object> logInfo = new HashMap<>();
            logInfo.put("operation", auditLog.value());
            logInfo.put("businessType", auditLog.businessType());
            logInfo.put("method", method.getName());
            logInfo.put("className", method.getDeclaringClass().getName());
            logInfo.put("requestUrl", request.getRequestURI());
            logInfo.put("requestMethod", request.getMethod());
            logInfo.put("requestIp", getIpAddress(request));
            logInfo.put("operateUser", admin != null ? admin.getUsername() : "unknown");
            logInfo.put("operateTime", new Date());

            // 记录请求参数
            if (auditLog.isSaveRequestData()) {
                logInfo.put("requestParams", joinPoint.getArgs());
            }

            // 记录响应结果
            if (auditLog.isSaveResponseData()) {
                logInfo.put("responseResult", result);
            }

            // 输出审计日志，实际应用中可以保存到数据库或发送到日志系统
            LOGGER.info("审计日志: {}", logInfo);

            // TODO: 在这里可以将审计日志保存到数据库或发送到专门的审计日志系统

        } catch (Exception e) {
            LOGGER.error("记录审计日志失败: {}", e.getMessage(), e);
            // 审计日志记录失败不应影响主业务流程
        }
    }

    /**
     * 获取当前登录的管理员
     */
    private UmsAdmin getLoginAdmin() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                return null;
            }
            String username = authentication.getName();
            UmsAdmin admin = adminService.getAdminByUsername(username);
            if (admin == null) {
                Asserts.fail("获取用户信息失败");
            }
            return admin;
        } catch (Exception e) {
            LOGGER.error("获取当前登录用户信息失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取请求IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}