package com.caizhilian.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * IP白名单配置属性类
 */
@Data
@Component
@ConfigurationProperties(prefix = "api.whitelist")
public class IpWhitelistProperties {

    /** 是否启用白名单 */
    private boolean enabled = true;

    /** 允许访问的IP列表，支持单IP和CIDR网段 */
    private List<String> ips = new ArrayList<>();

    /** 受保护的路径前缀 */
    private List<String> protectedPaths = new ArrayList<>();
}
