package com.caizhilian.service.impl;

import com.caizhilian.common.result.ApiResult;
import com.caizhilian.common.result.SyncFeedback;
import com.caizhilian.dto.request.ContractSyncItem;
import com.caizhilian.entity.ContractInfo;
import com.caizhilian.repository.ContractInfoRepository;
import com.caizhilian.repository.ContractProjectRefRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * ContractSyncServiceImpl 合同信息同步服务 单元测试
 */
@RunWith(MockitoJUnitRunner.class)
public class ContractSyncServiceImplTest {

    @Mock
    private ContractInfoRepository contractInfoRepository;

    @Mock
    private ContractProjectRefRepository contractProjectRefRepository;

    @InjectMocks
    private ContractSyncServiceImpl contractSyncService;

    @Before
    public void setUp() {
        when(contractInfoRepository.findByContractId(anyLong())).thenReturn(Optional.empty());
    }

    // ==================== 新增场景 ====================

    @Test
    public void testSyncContractInfoList_insertNew_withProjectIds() {
        List<ContractSyncItem> items = new ArrayList<>();
        ContractSyncItem item = new ContractSyncItem();
        item.setContractId(2000000001L);
        item.setContractName("测试合同");
        item.setBaseOrgCode("0442");
        item.setSubcontractorId(50001L);
        item.setSubcontractorName("测试分包商");
        item.setContractAmount(new BigDecimal("5000000"));
        item.setProjectIds(Arrays.asList(1000000001L, 1000000002L));
        items.add(item);

        ApiResult<List<SyncFeedback>> result = contractSyncService.syncContractInfoList(items);

        assertEquals("6100000", result.getCode());
        assertEquals(1, result.getData().size());
        assertTrue(result.getData().get(0).getFeedbackMessage().contains("成功"));

        // 验证主表保存
        verify(contractInfoRepository, times(1)).save(any(ContractInfo.class));
        // 验证关联表：先删旧、再插新
        verify(contractProjectRefRepository, times(1)).deleteByContractId(2000000001L);
        verify(contractProjectRefRepository, times(2)).save(any()); // 2个projectId
    }

    // ==================== 更新场景 ====================

    @Test
    public void testSyncContractInfoList_updateExisting_replacesProjectRefs() {
        ContractInfo existing = new ContractInfo();
        existing.setGid(1L);
        existing.setContractId(2000000001L);
        existing.setContractName("旧合同名");
        when(contractInfoRepository.findByContractId(2000000001L)).thenReturn(Optional.of(existing));

        List<ContractSyncItem> items = new ArrayList<>();
        ContractSyncItem item = new ContractSyncItem();
        item.setContractId(2000000001L);
        item.setContractName("新合同名");
        item.setProjectIds(Collections.singletonList(1000000003L));
        items.add(item);

        ApiResult<List<SyncFeedback>> result = contractSyncService.syncContractInfoList(items);

        assertTrue(result.getData().get(0).getFeedbackMessage().contains("成功"));
        // 更新时应先删除旧关联，再插入新关联
        verify(contractProjectRefRepository).deleteByContractId(2000000001L);
        verify(contractProjectRefRepository, times(1)).save(any());
    }

    // ==================== 无项目关联 ====================

    @Test
    public void testSyncContractInfoList_noProjectIds() {
        List<ContractSyncItem> items = new ArrayList<>();
        ContractSyncItem item = new ContractSyncItem();
        item.setContractId(2000000001L);
        item.setContractName("独立合同");
        item.setProjectIds(Collections.emptyList());
        items.add(item);

        ApiResult<List<SyncFeedback>> result = contractSyncService.syncContractInfoList(items);

        assertEquals("6100000", result.getCode());
        verify(contractInfoRepository, times(1)).save(any(ContractInfo.class));
        // 空列表时不触发关联表操作
        verify(contractProjectRefRepository, never()).deleteByContractId(anyLong());
        verify(contractProjectRefRepository, never()).save(any());
    }

    @Test
    public void testSyncContractInfoList_nullProjectIds() {
        List<ContractSyncItem> items = new ArrayList<>();
        ContractSyncItem item = new ContractSyncItem();
        item.setContractId(2000000001L);
        item.setContractName("独立合同");
        item.setProjectIds(null);
        items.add(item);

        ApiResult<List<SyncFeedback>> result = contractSyncService.syncContractInfoList(items);

        assertEquals("6100000", result.getCode());
        verify(contractProjectRefRepository, never()).deleteByContractId(anyLong());
    }

    // ==================== 批量同步 ====================

    @Test
    public void testSyncContractInfoList_batch() {
        List<ContractSyncItem> items = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            ContractSyncItem item = new ContractSyncItem();
            item.setContractId(2000000000L + i);
            item.setContractName("合同" + i);
            items.add(item);
        }

        ApiResult<List<SyncFeedback>> result = contractSyncService.syncContractInfoList(items);

        assertEquals(5, result.getData().size());
        verify(contractInfoRepository, times(5)).save(any(ContractInfo.class));
    }

    // ==================== 异常处理 ====================

    @Test
    public void testSyncContractInfoList_saveException_perItemFeedback() {
        when(contractInfoRepository.save(any(ContractInfo.class)))
                .thenThrow(new RuntimeException("数据库异常"));

        List<ContractSyncItem> items = new ArrayList<>();
        ContractSyncItem item = new ContractSyncItem();
        item.setContractId(2000000001L);
        items.add(item);

        ApiResult<List<SyncFeedback>> result = contractSyncService.syncContractInfoList(items);

        assertEquals("6100000", result.getCode());
        assertTrue(result.getData().get(0).getFeedbackMessage().contains("失败"));
    }

    @Test
    public void testSyncContractInfoList_partialFailure() {
        when(contractInfoRepository.save(any(ContractInfo.class)))
                .thenReturn(new ContractInfo())
                .thenThrow(new RuntimeException("第二个失败"));

        List<ContractSyncItem> items = new ArrayList<>();
        for (long i = 1; i <= 2; i++) {
            ContractSyncItem item = new ContractSyncItem();
            item.setContractId(2000000000L + i);
            items.add(item);
        }

        ApiResult<List<SyncFeedback>> result = contractSyncService.syncContractInfoList(items);

        assertEquals(2, result.getData().size());
        assertTrue(result.getData().get(0).getFeedbackMessage().contains("成功"));
        assertTrue(result.getData().get(1).getFeedbackMessage().contains("失败"));
    }

    // ==================== 空列表 ====================

    @Test
    public void testSyncContractInfoList_emptyList() {
        ApiResult<List<SyncFeedback>> result = contractSyncService.syncContractInfoList(Collections.emptyList());

        assertEquals("6100000", result.getCode());
        assertTrue(result.getData().isEmpty());
        verify(contractInfoRepository, never()).save(any());
    }
}
