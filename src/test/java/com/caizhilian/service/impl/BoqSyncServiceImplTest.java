package com.caizhilian.service.impl;

import com.caizhilian.common.result.ApiResult;
import com.caizhilian.common.result.SyncFeedback;
import com.caizhilian.dto.request.BoqSyncItem;
import com.caizhilian.entity.BoqDetail;
import com.caizhilian.entity.BoqMain;
import com.caizhilian.repository.BoqDetailRepository;
import com.caizhilian.repository.BoqMainRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * BoqSyncServiceImpl 工程量清单同步服务 单元测试
 */
@RunWith(MockitoJUnitRunner.class)
public class BoqSyncServiceImplTest {

    @Mock
    private BoqMainRepository boqMainRepository;

    @Mock
    private BoqDetailRepository boqDetailRepository;

    @InjectMocks
    private BoqSyncServiceImpl boqSyncService;

    @Before
    public void setUp() {
        when(boqMainRepository.findByBoqId(anyLong())).thenReturn(Optional.empty());
    }

    // ==================== 新增场景（含明细） ====================

    @Test
    public void testSyncBoqInfoList_insertNew_withDetails() {
        List<BoqSyncItem> items = new ArrayList<>();
        BoqSyncItem item = createBoqItem(3000000001L, 3);
        items.add(item);

        ApiResult<List<SyncFeedback>> result = boqSyncService.syncBoqInfoList(items);

        assertEquals("6100000", result.getCode());
        assertEquals(1, result.getData().size());
        assertTrue(result.getData().get(0).getFeedbackMessage().contains("成功"));

        // 验证主表保存
        verify(boqMainRepository, times(1)).save(any(BoqMain.class));
        // 验证明细：先删旧、再插新
        verify(boqDetailRepository, times(1)).deleteByBoqId(3000000001L);
        verify(boqDetailRepository, times(3)).save(any(BoqDetail.class));
    }

    // ==================== 更新场景 ====================

    @Test
    public void testSyncBoqInfoList_updateExisting_replacesDetails() {
        BoqMain existing = new BoqMain();
        existing.setGid(1L);
        existing.setBoqId(3000000001L);
        when(boqMainRepository.findByBoqId(3000000001L)).thenReturn(Optional.of(existing));

        List<BoqSyncItem> items = new ArrayList<>();
        items.add(createBoqItem(3000000001L, 2));

        ApiResult<List<SyncFeedback>> result = boqSyncService.syncBoqInfoList(items);

        assertTrue(result.getData().get(0).getFeedbackMessage().contains("成功"));
        // 更新时也要先清除旧明细
        verify(boqDetailRepository).deleteByBoqId(3000000001L);
        verify(boqDetailRepository, times(2)).save(any(BoqDetail.class));
    }

    // ==================== 无明细 ====================

    @Test
    public void testSyncBoqInfoList_noDetails() {
        BoqSyncItem item = new BoqSyncItem();
        item.setBoqId(3000000001L);
        item.setProjectId(1000000001L);
        item.setBoqDetailList(Collections.emptyList());

        ApiResult<List<SyncFeedback>> result = boqSyncService.syncBoqInfoList(Collections.singletonList(item));

        assertEquals("6100000", result.getCode());
        verify(boqDetailRepository, times(1)).deleteByBoqId(3000000001L);
        verify(boqDetailRepository, never()).save(any(BoqDetail.class));
    }

    @Test
    public void testSyncBoqInfoList_nullDetails() {
        BoqSyncItem item = new BoqSyncItem();
        item.setBoqId(3000000001L);
        item.setBoqDetailList(null);

        ApiResult<List<SyncFeedback>> result = boqSyncService.syncBoqInfoList(Collections.singletonList(item));

        assertEquals("6100000", result.getCode());
        verify(boqDetailRepository, never()).save(any(BoqDetail.class));
    }

    // ==================== 业务时间字段 ====================

    @Test
    public void testSyncBoqInfoList_bizTimeFields() {
        LocalDateTime bizCreateTime = LocalDateTime.of(2026, 6, 1, 10, 30, 0);
        LocalDateTime bizLastUpdateTime = LocalDateTime.of(2026, 7, 1, 14, 0, 0);

        BoqSyncItem item = new BoqSyncItem();
        item.setBoqId(3000000001L);
        item.setCreateTime(bizCreateTime);
        item.setLastUpdateTime(bizLastUpdateTime);

        boqSyncService.syncBoqInfoList(Collections.singletonList(item));

        verify(boqMainRepository, times(1)).save(any(BoqMain.class));
    }

    // ==================== 批量同步 ====================

    @Test
    public void testSyncBoqInfoList_batch() {
        List<BoqSyncItem> items = new ArrayList<>();
        for (long i = 1; i <= 5; i++) {
            BoqSyncItem item = new BoqSyncItem();
            item.setBoqId(3000000000L + i);
            item.setProjectId(1000000001L);
            items.add(item);
        }

        ApiResult<List<SyncFeedback>> result = boqSyncService.syncBoqInfoList(items);

        assertEquals(5, result.getData().size());
        verify(boqMainRepository, times(5)).save(any(BoqMain.class));
    }

    // ==================== 异常处理 ====================

    @Test
    public void testSyncBoqInfoList_saveException_perItemFeedback() {
        when(boqMainRepository.save(any(BoqMain.class)))
                .thenThrow(new RuntimeException("数据库异常"));

        BoqSyncItem item = new BoqSyncItem();
        item.setBoqId(3000000001L);

        ApiResult<List<SyncFeedback>> result = boqSyncService.syncBoqInfoList(Collections.singletonList(item));

        assertEquals("6100000", result.getCode());
        assertTrue(result.getData().get(0).getFeedbackMessage().contains("失败"));
    }

    @Test
    public void testSyncBoqInfoList_detailSaveException_perItemFeedback() {
        when(boqDetailRepository.save(any(BoqDetail.class)))
                .thenThrow(new RuntimeException("明细保存异常"));

        List<BoqSyncItem> items = new ArrayList<>();
        items.add(createBoqItem(3000000001L, 2));

        ApiResult<List<SyncFeedback>> result = boqSyncService.syncBoqInfoList(items);

        // 单个item内部的明细异常，整个item算失败
        assertTrue(result.getData().get(0).getFeedbackMessage().contains("失败"));
    }

    @Test
    public void testSyncBoqInfoList_partialFailure() {
        when(boqMainRepository.save(any(BoqMain.class)))
                .thenReturn(new BoqMain())
                .thenThrow(new RuntimeException("第二个失败"));

        List<BoqSyncItem> items = new ArrayList<>();
        BoqSyncItem item1 = new BoqSyncItem();
        item1.setBoqId(3000000001L);
        BoqSyncItem item2 = new BoqSyncItem();
        item2.setBoqId(3000000002L);
        items.add(item1);
        items.add(item2);

        ApiResult<List<SyncFeedback>> result = boqSyncService.syncBoqInfoList(items);

        assertEquals(2, result.getData().size());
        assertTrue(result.getData().get(0).getFeedbackMessage().contains("成功"));
        assertTrue(result.getData().get(1).getFeedbackMessage().contains("失败"));
    }

    // ==================== 空列表 ====================

    @Test
    public void testSyncBoqInfoList_emptyList() {
        ApiResult<List<SyncFeedback>> result = boqSyncService.syncBoqInfoList(Collections.emptyList());

        assertEquals("6100000", result.getCode());
        assertTrue(result.getData().isEmpty());
        verify(boqMainRepository, never()).save(any());
    }

    // ==================== helpers ====================

    private BoqSyncItem createBoqItem(Long boqId, int detailCount) {
        BoqSyncItem item = new BoqSyncItem();
        item.setBoqId(boqId);
        item.setProjectId(1000000001L);
        item.setContractId(2000000001L);
        item.setBaseOrgCode("0442");
        item.setUnitCode("01000101");

        List<BoqSyncItem.BoqDetailItem> details = new ArrayList<>();
        for (int i = 0; i < detailCount; i++) {
            BoqSyncItem.BoqDetailItem detail = new BoqSyncItem.BoqDetailItem();
            detail.setDetailId((long) i + 1);
            detail.setDetailName("明细项" + (i + 1));
            detail.setMeasurementUnit("m3");
            detail.setUnitPriceWithTax(new BigDecimal("100"));
            detail.setOriginalQuantity(new BigDecimal("50"));
            detail.setCurrentQuantity(new BigDecimal("60"));
            detail.setNodeLevel("1");
            details.add(detail);
        }
        item.setBoqDetailList(details);
        return item;
    }
}
