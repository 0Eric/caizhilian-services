package com.caizhilian.config;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * IpWhitelistProperties 配置属性类 单元测试
 */
public class IpWhitelistPropertiesTest {

    @Test
    public void testDefaultValues() {
        IpWhitelistProperties props = new IpWhitelistProperties();
        assertTrue("默认应启用白名单", props.isEnabled());
        assertTrue("默认IP列表为空", props.getIps().isEmpty());
        assertTrue("默认受保护路径为空", props.getProtectedPaths().isEmpty());
    }

    @Test
    public void testSetterGetter() {
        IpWhitelistProperties props = new IpWhitelistProperties();
        props.setEnabled(false);
        props.setIps(Arrays.asList("127.0.0.1", "::1"));
        props.setProtectedPaths(Arrays.asList("/api/sync/**", "/dap/mock/**"));

        assertFalse(props.isEnabled());
        assertEquals(2, props.getIps().size());
        assertEquals("127.0.0.1", props.getIps().get(0));
        assertEquals("::1", props.getIps().get(1));
        assertEquals(2, props.getProtectedPaths().size());
    }

    @Test
    public void testDisabled() {
        IpWhitelistProperties props = new IpWhitelistProperties();
        props.setEnabled(false);
        assertFalse("关闭后enabled为false", props.isEnabled());
    }

    @Test
    public void testSingleIp() {
        IpWhitelistProperties props = new IpWhitelistProperties();
        props.setIps(Collections.singletonList("127.0.0.1"));
        assertEquals(1, props.getIps().size());
        assertEquals("127.0.0.1", props.getIps().get(0));
    }

    @Test
    public void testCidrEntries() {
        IpWhitelistProperties props = new IpWhitelistProperties();
        props.setIps(Arrays.asList("192.168.1.0/24", "10.0.0.0/8", "172.16.0.0/12"));
        assertEquals(3, props.getIps().size());
        assertTrue(props.getIps().get(0).contains("/"));
    }
}
