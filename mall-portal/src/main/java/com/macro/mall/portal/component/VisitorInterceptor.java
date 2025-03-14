package com.macro.mall.portal.component;

import com.macro.mall.model.UmsVisitorLog;
import com.macro.mall.mapper.UmsVisitorMapper;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VisitorInterceptor implements HandlerInterceptor {
    @Autowired
    private UmsVisitorMapper visitorLogMapper;

    public String getIpAddress(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        // 如果有多个IP地址，取第一个
        if (ipAddress != null && ipAddress.contains(",")) {
            ipAddress = ipAddress.split(",")[0];
        }
        return ipAddress;
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取访客信息
        String ip = getIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String sessionId = request.getSession().getId();
        String referer = request.getHeader("Referer");
        String pageUrl = request.getRequestURL().toString();
    
        // 创建访问日志
        UmsVisitorLog visitorLog = new UmsVisitorLog();
        visitorLog.setIp(ip);
        visitorLog.setUserAgent(userAgent);
        visitorLog.setSessionId(sessionId);
        visitorLog.setReferer(referer);
        visitorLog.setPageUrl(pageUrl);
        visitorLog.setVisitTime(new Date());
    
        // 如果用户已登录，设置会员ID
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            // 设置会员ID
            // visitorLog.setMemberId(获取会员ID的逻辑);
        }
    
        // 异步保存访问日志
        CompletableFuture.runAsync(() -> visitorLogMapper.insert(visitorLog));
    
        return true;
    }
}
