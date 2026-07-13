package com.caizhilian.client;

import com.caizhilian.common.result.ApiResult;
import com.caizhilian.common.result.SyncFeedback;
import com.caizhilian.dto.request.dap.BoqAdjustPushRequest;
import com.caizhilian.dto.request.dap.ProgressPaymentPushRequest;
import com.caizhilian.dto.request.dap.SettlementPushRequest;
import com.caizhilian.dto.request.dap.VisaPushRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * DapServiceClient DAP系统服务客户端 单元测试
 */
@RunWith(MockitoJUnitRunner.class)
public class DapServiceClientTest {

    @Mock
    private RestTemplate restTemplate;

    private DapServiceClient dapServiceClient;

    private static final String TEST_BASE_URL = "http://test-dap:8080";

    @Before
    public void setUp() {
        dapServiceClient = new DapServiceClient(restTemplate);
        ReflectionTestUtils.setField(dapServiceClient, "dapBaseUrl", TEST_BASE_URL);
    }

    // ==================== pushVisa ====================

    @Test
    public void testPushVisa_success() {
        ApiResult<SyncFeedback> mockResult = ApiResult.success(new SyncFeedback(12345L, "成功"));
        when(restTemplate.postForObject(eq(TEST_BASE_URL + "/receive/visa"), any(), eq(ApiResult.class)))
                .thenReturn(mockResult);

        VisaPushRequest request = createVisaRequest();
        ApiResult<SyncFeedback> result = dapServiceClient.pushVisa(request);

        assertNotNull(result);
        assertEquals("6100000", result.getCode());
        verify(restTemplate, times(1)).postForObject(
                eq(TEST_BASE_URL + "/receive/visa"), eq(request), eq(ApiResult.class));
    }

    @Test
    public void testPushVisa_networkError() {
        when(restTemplate.postForObject(anyString(), any(), eq(ApiResult.class)))
                .thenThrow(new RuntimeException("连接超时"));

        VisaPushRequest request = createVisaRequest();
        ApiResult<SyncFeedback> result = dapServiceClient.pushVisa(request);

        assertNotNull(result);
        assertEquals("6100020", result.getCode());
        assertTrue(result.getMessage().contains("失败"));
        assertTrue(result.getMessage().contains("连接超时"));
    }

    // ==================== pushBoqAdjust ====================

    @Test
    public void testPushBoqAdjust_success() {
        ApiResult<SyncFeedback> mockResult = ApiResult.success(new SyncFeedback(23456L, "成功"));
        when(restTemplate.postForObject(eq(TEST_BASE_URL + "/receive/boq-adjust"), any(), eq(ApiResult.class)))
                .thenReturn(mockResult);

        BoqAdjustPushRequest request = new BoqAdjustPushRequest();
        request.setGid(23456L);

        ApiResult<SyncFeedback> result = dapServiceClient.pushBoqAdjust(request);

        assertNotNull(result);
        assertEquals("6100000", result.getCode());
    }

    @Test
    public void testPushBoqAdjust_networkError() {
        when(restTemplate.postForObject(anyString(), any(), eq(ApiResult.class)))
                .thenThrow(new RuntimeException("网络不可达"));

        BoqAdjustPushRequest request = new BoqAdjustPushRequest();
        request.setGid(23456L);

        ApiResult<SyncFeedback> result = dapServiceClient.pushBoqAdjust(request);

        assertEquals("6100020", result.getCode());
        assertTrue(result.getMessage().contains("接口失败"));
    }

    // ==================== pushProgressPayment ====================

    @Test
    public void testPushProgressPayment_success() {
        ApiResult<SyncFeedback> mockResult = ApiResult.success(new SyncFeedback(34567L, "成功"));
        when(restTemplate.postForObject(eq(TEST_BASE_URL + "/receive/progress-payment"), any(), eq(ApiResult.class)))
                .thenReturn(mockResult);

        ProgressPaymentPushRequest request = new ProgressPaymentPushRequest();
        request.setGid(34567L);

        ApiResult<SyncFeedback> result = dapServiceClient.pushProgressPayment(request);

        assertNotNull(result);
        assertEquals("6100000", result.getCode());
    }

    @Test
    public void testPushProgressPayment_networkError() {
        when(restTemplate.postForObject(anyString(), any(), eq(ApiResult.class)))
                .thenThrow(new RuntimeException("502 Bad Gateway"));

        ProgressPaymentPushRequest request = new ProgressPaymentPushRequest();
        request.setGid(34567L);

        ApiResult<SyncFeedback> result = dapServiceClient.pushProgressPayment(request);

        assertEquals("6100020", result.getCode());
    }

    // ==================== pushSettlement ====================

    @Test
    public void testPushSettlement_success() {
        ApiResult<SyncFeedback> mockResult = ApiResult.success(new SyncFeedback(45678L, "成功"));
        when(restTemplate.postForObject(eq(TEST_BASE_URL + "/receive/settlement"), any(), eq(ApiResult.class)))
                .thenReturn(mockResult);

        SettlementPushRequest request = new SettlementPushRequest();
        request.setGid(45678L);

        ApiResult<SyncFeedback> result = dapServiceClient.pushSettlement(request);

        assertNotNull(result);
        assertEquals("6100000", result.getCode());
    }

    @Test
    public void testPushSettlement_networkError() {
        when(restTemplate.postForObject(anyString(), any(), eq(ApiResult.class)))
                .thenThrow(new RuntimeException("连接被拒绝"));

        SettlementPushRequest request = new SettlementPushRequest();
        request.setGid(45678L);

        ApiResult<SyncFeedback> result = dapServiceClient.pushSettlement(request);

        assertEquals("6100020", result.getCode());
        assertTrue(result.getMessage().contains("连接被拒绝"));
    }

    // ==================== 统一失败码 ====================

    @Test
    public void testAllMethods_returnNetworkErrorCode_onException() {
        when(restTemplate.postForObject(anyString(), any(), eq(ApiResult.class)))
                .thenThrow(new RuntimeException("error"));

        assertEquals("6100020", dapServiceClient.pushVisa(createVisaRequest()).getCode());
        assertEquals("6100020", dapServiceClient.pushBoqAdjust(new BoqAdjustPushRequest()).getCode());
        assertEquals("6100020", dapServiceClient.pushProgressPayment(new ProgressPaymentPushRequest()).getCode());
        assertEquals("6100020", dapServiceClient.pushSettlement(new SettlementPushRequest()).getCode());
    }

    // ==================== helpers ====================

    private VisaPushRequest createVisaRequest() {
        VisaPushRequest request = new VisaPushRequest();
        request.setConfirmGid(12345L);
        request.setChangeNt("工程变更说明");
        request.setProjectId("1000000001");
        return request;
    }
}
