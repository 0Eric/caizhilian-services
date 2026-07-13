package com.caizhilian.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 * 注册IP白名单拦截器
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final IpWhitelistInterceptor ipWhitelistInterceptor;
    private final IpWhitelistProperties whitelistProperties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册IP白名单拦截器，应用到受保护的路径
        registry.addInterceptor(ipWhitelistInterceptor)
                .addPathPatterns(whitelistProperties.getProtectedPaths());
    }
}
