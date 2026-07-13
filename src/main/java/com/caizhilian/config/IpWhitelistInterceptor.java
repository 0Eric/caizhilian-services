package com.caizhilian.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * IP白名单拦截器
 * 拦截受保护的API路径，只允许白名单中的IP访问
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IpWhitelistInterceptor implements HandlerInterceptor {

    private final IpWhitelistProperties whitelistProperties;

    /** 缓存的CIDR匹配规则 */
    private List<CidrRule> cidrRules;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        // 白名单未启用 → 直接放行
        if (!whitelistProperties.isEnabled()) {
            return true;
        }

        String clientIp = getClientIp(request);
        log.debug("客户端IP: {}", clientIp);

        // 检查IP是否在白名单中
        if (!isAllowed(clientIp)) {
            log.warn("IP被拦截: {} | 请求路径: {}", clientIp, request.getRequestURI());
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write(
                    "{\"code\":\"6100010\",\"message\":\"IP不在访问白名单中，请联系管理员添加: " + clientIp + "\",\"data\":null}"
            );
            return false;
        }

        return true;
    }

    /**
     * 获取客户端真实IP（考虑反向代理）
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
        // X-Forwarded-For 可能有多级代理，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * 判断IP是否在白名单中
     */
    private boolean isAllowed(String clientIp) {
        List<String> whitelist = whitelistProperties.getIps();

        // 白名单为空 → 所有人禁止访问（安全原则：白名单空=无人可访问）
        if (whitelist == null || whitelist.isEmpty()) {
            return false;
        }

        // 将客户端IP转为规范化地址（解决 IPv6 ::1 与 0:0:0:0:0:0:0:1 的差异）
        String normalizedClientIp = normalizeIp(clientIp);

        // 1. 精确匹配（先对白名单中的单IP做规范化比较）
        for (String entry : whitelist) {
            // 跳过CIDR网段，只处理单IP
            if (!entry.contains("/")) {
                String normalizedEntry = normalizeIp(entry);
                if (normalizedEntry.equals(normalizedClientIp)) {
                    return true;
                }
            }
        }

        // 2. CIDR网段匹配（如 192.168.1.0/24, ::1/128）
        if (cidrRules == null) {
            cidrRules = buildCidrRules(whitelist);
        }
        for (CidrRule rule : cidrRules) {
            if (rule.matches(clientIp)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 将IP字符串归一化为标准格式
     * 例如：0:0:0:0:0:0:0:1 → ::1
     *       0:0:0:0:0:0:0:0 → ::
     */
    private String normalizeIp(String ip) {
        try {
            return InetAddress.getByName(ip).getHostAddress();
        } catch (UnknownHostException e) {
            return ip;
        }
    }

    /**
     * 从白名单中构建CIDR规则
     */
    private List<CidrRule> buildCidrRules(List<String> whitelist) {
        List<CidrRule> rules = new ArrayList<>();
        for (String entry : whitelist) {
            if (entry.contains("/")) {
                try {
                    rules.add(new CidrRule(entry));
                } catch (Exception e) {
                    log.warn("无效的CIDR配置: {}", entry);
                }
            }
        }
        return rules;
    }

    /**
     * CIDR网段匹配规则
     */
    static class CidrRule {
        private final byte[] networkAddress;
        private final int prefixLength;

        CidrRule(String cidr) throws UnknownHostException {
            String[] parts = cidr.split("/");
            this.networkAddress = InetAddress.getByName(parts[0]).getAddress();
            this.prefixLength = Integer.parseInt(parts[1]);
        }

        boolean matches(String ip) {
            try {
                byte[] addr = InetAddress.getByName(ip).getAddress();
                if (addr.length != networkAddress.length) {
                    return false;
                }
                // 逐位比较网络前缀
                int fullBytes = prefixLength / 8;
                int remainingBits = prefixLength % 8;

                for (int i = 0; i < fullBytes; i++) {
                    if (addr[i] != networkAddress[i]) {
                        return false;
                    }
                }

                if (remainingBits > 0) {
                    int mask = (0xFF << (8 - remainingBits)) & 0xFF;
                    if ((addr[fullBytes] & mask) != (networkAddress[fullBytes] & mask)) {
                        return false;
                    }
                }

                return true;
            } catch (UnknownHostException e) {
                return false;
            }
        }
    }
}
