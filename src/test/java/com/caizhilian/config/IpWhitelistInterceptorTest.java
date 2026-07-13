package com.caizhilian.config;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

/**
 * IpWhitelistInterceptor IP白名单拦截器 单元测试
 */
@RunWith(MockitoJUnitRunner.class)
public class IpWhitelistInterceptorTest {

    @Mock
    private IpWhitelistProperties whitelistProperties;

    private IpWhitelistInterceptor interceptor;

    @Before
    public void setUp() {
        interceptor = new IpWhitelistInterceptor(whitelistProperties);
    }

    // ==================== 白名单未启用 ====================

    @Test
    public void testPreHandle_disabled_allowAll() throws Exception {
        when(whitelistProperties.isEnabled()).thenReturn(false);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.0.0.99");
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean result = interceptor.preHandle(request, response, null);

        assertTrue("白名单未启用时应放行所有请求", result);
    }

    // ==================== 精确IP匹配 ====================

    @Test
    public void testPreHandle_exactMatch_ipv4() throws Exception {
        when(whitelistProperties.isEnabled()).thenReturn(true);
        when(whitelistProperties.getIps()).thenReturn(Arrays.asList("127.0.0.1", "192.168.1.100"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.100");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertTrue(interceptor.preHandle(request, response, null));
    }

    @Test
    public void testPreHandle_exactMatch_ipv6() throws Exception {
        when(whitelistProperties.isEnabled()).thenReturn(true);
        when(whitelistProperties.getIps()).thenReturn(Collections.singletonList("::1"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("0:0:0:0:0:0:0:1"); // IPv6 展开形式
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertTrue("IPv6 ::1 应匹配 0:0:0:0:0:0:0:1", interceptor.preHandle(request, response, null));
    }

    @Test
    public void testPreHandle_exactMatch_ipv6Normalized() throws Exception {
        when(whitelistProperties.isEnabled()).thenReturn(true);
        when(whitelistProperties.getIps()).thenReturn(Collections.singletonList("0:0:0:0:0:0:0:1"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("::1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertTrue("IPv6 规范化后应匹配", interceptor.preHandle(request, response, null));
    }

    // ==================== CIDR网段匹配 ====================

    @Test
    public void testPreHandle_cidrMatch_ipv4() throws Exception {
        when(whitelistProperties.isEnabled()).thenReturn(true);
        when(whitelistProperties.getIps()).thenReturn(Collections.singletonList("192.168.1.0/24"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.1.50");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertTrue(interceptor.preHandle(request, response, null));
    }

    @Test
    public void testPreHandle_cidrNoMatch_outsideRange() throws Exception {
        when(whitelistProperties.isEnabled()).thenReturn(true);
        when(whitelistProperties.getIps()).thenReturn(Collections.singletonList("192.168.1.0/24"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("192.168.2.50");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse("CIDR范围外的IP应被拦截", interceptor.preHandle(request, response, null));
    }

    @Test
    public void testPreHandle_cidrMatch_edgeIP() throws Exception {
        when(whitelistProperties.isEnabled()).thenReturn(true);
        when(whitelistProperties.getIps()).thenReturn(Collections.singletonList("10.0.0.0/8"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("10.255.255.255");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertTrue("网段边缘IP应匹配", interceptor.preHandle(request, response, null));
    }

    // ==================== 白名单为空 ====================

    @Test
    public void testPreHandle_emptyWhitelist_denyAll() throws Exception {
        when(whitelistProperties.isEnabled()).thenReturn(true);
        when(whitelistProperties.getIps()).thenReturn(Collections.emptyList());

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse("空白名单应拒绝所有", interceptor.preHandle(request, response, null));
    }

    @Test
    public void testPreHandle_nullWhitelist_denyAll() throws Exception {
        when(whitelistProperties.isEnabled()).thenReturn(true);
        when(whitelistProperties.getIps()).thenReturn(null);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse("null白名单应拒绝所有", interceptor.preHandle(request, response, null));
    }

    // ==================== 不匹配场景 ====================

    @Test
    public void testPreHandle_noMatch() throws Exception {
        when(whitelistProperties.isEnabled()).thenReturn(true);
        when(whitelistProperties.getIps()).thenReturn(Collections.singletonList("127.0.0.1"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("203.0.113.50");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(interceptor.preHandle(request, response, null));
        assertEquals(403, response.getStatus());
    }

    // ==================== 代理IP头 ====================

    @Test
    public void testPreHandle_xForwardedFor() throws Exception {
        when(whitelistProperties.isEnabled()).thenReturn(true);
        when(whitelistProperties.getIps()).thenReturn(Collections.singletonList("10.0.0.1"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "10.0.0.1");
        request.setRemoteAddr("192.168.1.1"); // 实际代理IP，不是客户端真实IP
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertTrue("应通过 X-Forwarded-For 获取真实IP", interceptor.preHandle(request, response, null));
    }

    @Test
    public void testPreHandle_xForwardedFor_multiProxy() throws Exception {
        when(whitelistProperties.isEnabled()).thenReturn(true);
        when(whitelistProperties.getIps()).thenReturn(Collections.singletonList("10.0.0.1"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "10.0.0.1, 172.16.0.1, 192.168.1.1");
        request.setRemoteAddr("192.168.1.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertTrue("多级代理应取第一个IP", interceptor.preHandle(request, response, null));
    }

    @Test
    public void testPreHandle_unknownXFF_fallbackToRemoteAddr() throws Exception {
        when(whitelistProperties.isEnabled()).thenReturn(true);
        when(whitelistProperties.getIps()).thenReturn(Collections.singletonList("127.0.0.1"));

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("X-Forwarded-For", "unknown");
        request.setRemoteAddr("127.0.0.1");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertTrue("X-Forwarded-For为unknown时应回退到RemoteAddr", interceptor.preHandle(request, response, null));
    }

    // ==================== 混合白名单 ====================

    @Test
    public void testPreHandle_mixed_whitelist() throws Exception {
        when(whitelistProperties.isEnabled()).thenReturn(true);
        when(whitelistProperties.getIps()).thenReturn(Arrays.asList("127.0.0.1", "10.0.0.0/8"));

        // 精确匹配
        MockHttpServletRequest req1 = new MockHttpServletRequest();
        req1.setRemoteAddr("127.0.0.1");
        assertTrue(interceptor.preHandle(req1, new MockHttpServletResponse(), null));

        // CIDR匹配
        MockHttpServletRequest req2 = new MockHttpServletRequest();
        req2.setRemoteAddr("10.1.2.3");
        assertTrue(interceptor.preHandle(req2, new MockHttpServletResponse(), null));

        // 不匹配
        MockHttpServletRequest req3 = new MockHttpServletRequest();
        req3.setRemoteAddr("203.0.113.1");
        assertFalse(interceptor.preHandle(req3, new MockHttpServletResponse(), null));
    }
}
