package com.caizhilian.controller;

import com.caizhilian.common.result.ApiResult;
import com.caizhilian.common.result.SyncFeedback;
import com.caizhilian.dto.request.dap.BoqAdjustPushRequest;
import com.caizhilian.dto.request.dap.ProgressPaymentPushRequest;
import com.caizhilian.dto.request.dap.SettlementPushRequest;
import com.caizhilian.dto.request.dap.VisaPushRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.junit.Assert.*;

/**
 * DapMockController Mock推送接口 单元测试
 */
@RunWith(MockitoJUnitRunner.class)
public class DapMockControllerTest {

    @InjectMocks
    private DapMockController dapMockController;

    // ==================== receiveVisa ====================

    @Test
    public void testReceiveVisa_success() {
        VisaPushRequest request = new VisaPushRequest();
        request.setConfirmGid(12345L);
        request.setChangeNt("工程变更说明");
        request.setProjectId("1000000001");

        ApiResult<SyncFeedback> result = dapMockController.receiveVisa(request);

        assertNotNull(result);
        assertEquals("6100000", result.getCode());
        assertEquals(12345L, result.getData().getId());
        assertTrue(result.getData().getFeedbackMessage().contains("成功"));
    }

    // ==================== receiveBoqAdjust ====================

    @Test
    public void testReceiveBoqAdjust_success() {
        BoqAdjustPushRequest request = new BoqAdjustPushRequest();
        request.setGid(23456L);
        request.setChangeDocNo("BG-2026-001");
        request.setProjectId(1000000001L);
        request.setContractId(2000000001L);

        ApiResult<SyncFeedback> result = dapMockController.receiveBoqAdjust(request);

        assertNotNull(result);
        assertEquals("6100000", result.getCode());
        assertEquals(23456L, result.getData().getId());
    }

    @Test
    public void testReceiveBoqAdjust_withNullItems() {
        BoqAdjustPushRequest request = new BoqAdjustPushRequest();
        request.setGid(23456L);
        request.setItems(null);

        ApiResult<SyncFeedback> result = dapMockController.receiveBoqAdjust(request);

        assertNotNull(result);
        assertEquals("6100000", result.getCode());
    }

    // ==================== receiveProgressPayment ====================

    @Test
    public void testReceiveProgressPayment_success() {
        ProgressPaymentPushRequest request = new ProgressPaymentPushRequest();
        request.setGid(34567L);
        request.setBillid("JDK-2026-001");
        request.setProjectId("1000000001");
        request.setContractId("2000000001");

        ApiResult<SyncFeedback> result = dapMockController.receiveProgressPayment(request);

        assertNotNull(result);
        assertEquals("6100000", result.getCode());
        assertEquals(34567L, result.getData().getId());
    }

    @Test
    public void testReceiveProgressPayment_emptyItems() {
        ProgressPaymentPushRequest request = new ProgressPaymentPushRequest();
        request.setGid(34567L);
        request.setItems(Collections.emptyList());

        ApiResult<SyncFeedback> result = dapMockController.receiveProgressPayment(request);

        assertNotNull(result);
        assertTrue(result.getData().getFeedbackMessage().contains("成功"));
    }

    // ==================== receiveSettlement ====================

    @Test
    public void testReceiveSettlement_success() {
        SettlementPushRequest request = new SettlementPushRequest();
        request.setGid(45678L);
        request.setAppliNo("JS-2026-001");
        request.setProjectId(1000000001L);
        request.setContractId(2000000001L);

        ApiResult<SyncFeedback> result = dapMockController.receiveSettlement(request);

        assertNotNull(result);
        assertEquals("6100000", result.getCode());
        assertEquals(45678L, result.getData().getId());
    }

    @Test
    public void testReceiveSettlement_withAllSubItemsNull() {
        SettlementPushRequest request = new SettlementPushRequest();
        request.setGid(45678L);
        request.setDetailItems(null);
        request.setVisaItems(null);

        ApiResult<SyncFeedback> result = dapMockController.receiveSettlement(request);

        assertNotNull(result);
        assertEquals("6100000", result.getCode());
    }

    // ==================== 一致性校验 ====================

    @Test
    public void testAllMockEndpoints_returnSuccessCode() {
        VisaPushRequest visa = new VisaPushRequest();
        visa.setConfirmGid(1L);
        assertEquals("6100000", dapMockController.receiveVisa(visa).getCode());

        BoqAdjustPushRequest boqAdj = new BoqAdjustPushRequest();
        boqAdj.setGid(2L);
        assertEquals("6100000", dapMockController.receiveBoqAdjust(boqAdj).getCode());

        ProgressPaymentPushRequest pp = new ProgressPaymentPushRequest();
        pp.setGid(3L);
        assertEquals("6100000", dapMockController.receiveProgressPayment(pp).getCode());

        SettlementPushRequest st = new SettlementPushRequest();
        st.setGid(4L);
        assertEquals("6100000", dapMockController.receiveSettlement(st).getCode());
    }
}
