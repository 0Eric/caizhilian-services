package com.caizhilian.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * WebMvcConfig 拦截器注册 单元测试
 */
@RunWith(MockitoJUnitRunner.class)
public class WebMvcConfigTest {

    @Mock
    private IpWhitelistInterceptor ipWhitelistInterceptor;

    @Mock
    private IpWhitelistProperties whitelistProperties;

    @InjectMocks
    private WebMvcConfig webMvcConfig;

    @Test
    public void testAddInterceptors_registersIpWhitelistInterceptor() {
        when(whitelistProperties.getProtectedPaths())
                .thenReturn(Arrays.asList("/api/sync/**", "/dap/mock/**"));

        InterceptorRegistry registry = spy(new InterceptorRegistry());
        InterceptorRegistration registration = mock(InterceptorRegistration.class);
        doReturn(registration).when(registry).addInterceptor(ipWhitelistInterceptor);
        when(registration.addPathPatterns(anyList())).thenReturn(registration);

        webMvcConfig.addInterceptors(registry);

        // 验证拦截器被注册
        verify(registry, times(1)).addInterceptor(ipWhitelistInterceptor);
        verify(registration, times(1)).addPathPatterns(anyList());
    }

    @Test
    public void testAddInterceptors_emptyProtectedPaths() {
        when(whitelistProperties.getProtectedPaths()).thenReturn(Collections.emptyList());

        InterceptorRegistry registry = spy(new InterceptorRegistry());
        InterceptorRegistration registration = mock(InterceptorRegistration.class);
        doReturn(registration).when(registry).addInterceptor(ipWhitelistInterceptor);
        when(registration.addPathPatterns(anyList())).thenReturn(registration);

        webMvcConfig.addInterceptors(registry);

        verify(registry, times(1)).addInterceptor(ipWhitelistInterceptor);
    }

    @Test
    public void testAddInterceptors_singleProtectedPath() {
        when(whitelistProperties.getProtectedPaths())
                .thenReturn(Collections.singletonList("/api/sync/**"));

        InterceptorRegistry registry = spy(new InterceptorRegistry());
        InterceptorRegistration registration = mock(InterceptorRegistration.class);
        doReturn(registration).when(registry).addInterceptor(ipWhitelistInterceptor);
        when(registration.addPathPatterns(anyList())).thenReturn(registration);

        webMvcConfig.addInterceptors(registry);

        verify(registry).addInterceptor(ipWhitelistInterceptor);
    }
}
