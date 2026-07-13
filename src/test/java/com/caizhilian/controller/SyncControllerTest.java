package com.caizhilian.controller;

import com.caizhilian.common.result.ApiResult;
import com.caizhilian.common.result.SyncFeedback;
import com.caizhilian.dto.request.BoqSyncItem;
import com.caizhilian.dto.request.ContractSyncItem;
import com.caizhilian.dto.request.ProjectSyncItem;
import com.caizhilian.service.BoqSyncService;
import com.caizhilian.service.ContractSyncService;
import com.caizhilian.service.ProjectSyncService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * SyncController 数据同步接口 单元测试
 */
@RunWith(MockitoJUnitRunner.class)
public class SyncControllerTest {

    @Mock
    private ProjectSyncService projectSyncService;

    @Mock
    private ContractSyncService contractSyncService;

    @Mock
    private BoqSyncService boqSyncService;

    @InjectMocks
    private SyncController syncController;

    private List<SyncFeedback> mockFeedbackList;

    @Before
    public void setUp() {
        SyncFeedback feedback = new SyncFeedback(1000000001L, "同步成功");
        mockFeedbackList = Collections.singletonList(feedback);
    }

    // ==================== syncProjectInfoList ====================

    @Test
    public void testSyncProjectInfoList_success() {
        when(projectSyncService.syncProjectInfoList(anyList())).thenReturn(ApiResult.success(mockFeedbackList));

        List<ProjectSyncItem> items = createProjectItems();
        ApiResult<List<SyncFeedback>> result = syncController.syncProjectInfoList(items);

        assertNotNull(result);
        assertEquals("6100000", result.getCode());
        assertEquals(1, result.getData().size());
        verify(projectSyncService, times(1)).syncProjectInfoList(items);
    }

    @Test
    public void testSyncProjectInfoList_emptyList() {
        when(projectSyncService.syncProjectInfoList(anyList()))
                .thenReturn(ApiResult.success(Collections.emptyList()));

        ApiResult<List<SyncFeedback>> result = syncController.syncProjectInfoList(Collections.emptyList());

        assertNotNull(result);
        assertTrue(result.getData().isEmpty());
        verify(projectSyncService, times(1)).syncProjectInfoList(Collections.emptyList());
    }

    @Test
    public void testSyncProjectInfoList_multipleItems() {
        List<SyncFeedback> feedbacks = Arrays.asList(
                new SyncFeedback(1L, "同步成功"),
                new SyncFeedback(2L, "同步成功"),
                new SyncFeedback(3L, "同步失败：xxx")
        );
        when(projectSyncService.syncProjectInfoList(anyList())).thenReturn(ApiResult.success(feedbacks));

        ApiResult<List<SyncFeedback>> result = syncController.syncProjectInfoList(createProjectItems());

        assertEquals(3, result.getData().size());
    }

    // ==================== syncContractInfoList ====================

    @Test
    public void testSyncContractInfoList_success() {
        when(contractSyncService.syncContractInfoList(anyList())).thenReturn(ApiResult.success(mockFeedbackList));

        List<ContractSyncItem> items = createContractItems();
        ApiResult<List<SyncFeedback>> result = syncController.syncContractInfoList(items);

        assertNotNull(result);
        assertEquals("6100000", result.getCode());
        verify(contractSyncService, times(1)).syncContractInfoList(items);
    }

    @Test
    public void testSyncContractInfoList_withProjectIds() {
        when(contractSyncService.syncContractInfoList(anyList())).thenReturn(ApiResult.success(mockFeedbackList));

        ContractSyncItem item = new ContractSyncItem();
        item.setContractId(2000000001L);
        item.setProjectIds(Arrays.asList(1000000001L, 1000000002L));
        item.setContractName("测试合同");

        ApiResult<List<SyncFeedback>> result = syncController.syncContractInfoList(Collections.singletonList(item));

        assertNotNull(result);
        verify(contractSyncService, times(1)).syncContractInfoList(anyList());
    }

    // ==================== syncBoqInfoList ====================

    @Test
    public void testSyncBoqInfoList_success() {
        when(boqSyncService.syncBoqInfoList(anyList())).thenReturn(ApiResult.success(mockFeedbackList));

        List<BoqSyncItem> items = createBoqItems();
        ApiResult<List<SyncFeedback>> result = syncController.syncBoqInfoList(items);

        assertNotNull(result);
        assertEquals("6100000", result.getCode());
        verify(boqSyncService, times(1)).syncBoqInfoList(items);
    }

    @Test
    public void testSyncBoqInfoList_withDetails() {
        when(boqSyncService.syncBoqInfoList(anyList())).thenReturn(ApiResult.success(mockFeedbackList));

        BoqSyncItem item = new BoqSyncItem();
        item.setBoqId(3000000001L);
        BoqSyncItem.BoqDetailItem detail = new BoqSyncItem.BoqDetailItem();
        detail.setDetailId(1L);
        detail.setDetailName("土方开挖");
        item.setBoqDetailList(Collections.singletonList(detail));

        ApiResult<List<SyncFeedback>> result = syncController.syncBoqInfoList(Collections.singletonList(item));

        assertNotNull(result);
        verify(boqSyncService, times(1)).syncBoqInfoList(anyList());
    }

    // ==================== helpers ====================

    private List<ProjectSyncItem> createProjectItems() {
        ProjectSyncItem item = new ProjectSyncItem();
        item.setProjectId(1000000001L);
        item.setBaseOrgCode("0442");
        item.setBaseOrgCodeName("河北送变电");
        item.setProjectName("测试项目");
        return Collections.singletonList(item);
    }

    private List<ContractSyncItem> createContractItems() {
        ContractSyncItem item = new ContractSyncItem();
        item.setContractId(2000000001L);
        item.setContractName("测试合同");
        item.setContractAmount(new BigDecimal("1000000"));
        return Collections.singletonList(item);
    }

    private List<BoqSyncItem> createBoqItems() {
        BoqSyncItem item = new BoqSyncItem();
        item.setBoqId(3000000001L);
        item.setProjectId(1000000001L);
        return Collections.singletonList(item);
    }
}
