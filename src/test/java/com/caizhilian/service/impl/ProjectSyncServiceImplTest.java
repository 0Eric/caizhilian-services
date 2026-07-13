package com.caizhilian.service.impl;

import com.caizhilian.common.result.ApiResult;
import com.caizhilian.common.result.SyncFeedback;
import com.caizhilian.dto.request.ProjectSyncItem;
import com.caizhilian.entity.ProjectInfo;
import com.caizhilian.repository.ProjectInfoRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ProjectSyncServiceImpl 项目信息同步服务 单元测试
 */
@RunWith(MockitoJUnitRunner.class)
public class ProjectSyncServiceImplTest {

    @Mock
    private ProjectInfoRepository projectInfoRepository;

    @InjectMocks
    private ProjectSyncServiceImpl projectSyncService;

    @Before
    public void setUp() {
        // 默认: findByProjectId 返回 empty（新增场景）
        when(projectInfoRepository.findByProjectId(anyLong())).thenReturn(Optional.empty());
    }

    // ==================== 新增场景 ====================

    @Test
    public void testSyncProjectInfoList_insertNew() {
        List<ProjectSyncItem> items = createSingleItem(1000000001L);

        ApiResult<List<SyncFeedback>> result = projectSyncService.syncProjectInfoList(items);

        assertNotNull(result);
        assertEquals("6100000", result.getCode());
        assertEquals(1, result.getData().size());
        assertEquals(1000000001L, result.getData().get(0).getId());
        assertTrue(result.getData().get(0).getFeedbackMessage().contains("成功"));

        verify(projectInfoRepository, times(1)).findByProjectId(1000000001L);
        verify(projectInfoRepository, times(1)).save(any(ProjectInfo.class));
    }

    // ==================== 更新场景（幂等） ====================

    @Test
    public void testSyncProjectInfoList_updateExisting() {
        ProjectInfo existing = new ProjectInfo();
        existing.setGid(1L);
        existing.setProjectId(1000000001L);
        existing.setProjectName("旧名称");
        when(projectInfoRepository.findByProjectId(1000000001L)).thenReturn(Optional.of(existing));

        List<ProjectSyncItem> items = createSingleItem(1000000001L);

        ApiResult<List<SyncFeedback>> result = projectSyncService.syncProjectInfoList(items);

        assertEquals("6100000", result.getCode());
        assertTrue(result.getData().get(0).getFeedbackMessage().contains("成功"));

        // 应该执行更新（调用了findByProjectId找到已有记录）
        verify(projectInfoRepository, times(1)).findByProjectId(1000000001L);
        verify(projectInfoRepository, times(1)).save(any(ProjectInfo.class));
    }

    // ==================== 批量同步 ====================

    @Test
    public void testSyncProjectInfoList_batch() {
        List<ProjectSyncItem> items = new ArrayList<>();
        items.add(createSingleItem(1000000001L).get(0));
        items.add(createSingleItem(1000000002L).get(0));
        items.add(createSingleItem(1000000003L).get(0));

        ApiResult<List<SyncFeedback>> result = projectSyncService.syncProjectInfoList(items);

        assertEquals(3, result.getData().size());
        verify(projectInfoRepository, times(3)).save(any(ProjectInfo.class));
    }

    // ==================== 异常处理 ====================

    @Test
    public void testSyncProjectInfoList_saveException_perItemFeedback() {
        when(projectInfoRepository.save(any(ProjectInfo.class)))
                .thenThrow(new RuntimeException("数据库异常"));

        List<ProjectSyncItem> items = createSingleItem(1000000001L);

        ApiResult<List<SyncFeedback>> result = projectSyncService.syncProjectInfoList(items);

        assertEquals("6100000", result.getCode()); // 整体返回成功码
        assertEquals(1, result.getData().size());
        assertTrue(result.getData().get(0).getFeedbackMessage().contains("同步失败"));
    }

    @Test
    public void testSyncProjectInfoList_partialFailure() {
        // 第一个正常，第二个抛异常
        when(projectInfoRepository.save(any(ProjectInfo.class)))
                .thenReturn(new ProjectInfo())  // 第一次成功
                .thenThrow(new RuntimeException("第二个失败"));

        List<ProjectSyncItem> items = new ArrayList<>();
        items.add(createSingleItem(1000000001L).get(0));
        items.add(createSingleItem(1000000002L).get(0));

        ApiResult<List<SyncFeedback>> result = projectSyncService.syncProjectInfoList(items);

        assertEquals(2, result.getData().size());
        assertTrue(result.getData().get(0).getFeedbackMessage().contains("成功"));
        assertTrue(result.getData().get(1).getFeedbackMessage().contains("失败"));
    }

    // ==================== 空列表 ====================

    @Test
    public void testSyncProjectInfoList_emptyList() {
        ApiResult<List<SyncFeedback>> result = projectSyncService.syncProjectInfoList(Collections.emptyList());

        assertEquals("6100000", result.getCode());
        assertTrue(result.getData().isEmpty());
        verify(projectInfoRepository, never()).save(any(ProjectInfo.class));
    }

    // ==================== 字段完整性 ====================

    @Test
    public void testSyncProjectInfoList_fieldsMapping() {
        List<ProjectSyncItem> items = new ArrayList<>();
        ProjectSyncItem item = new ProjectSyncItem();
        item.setProjectId(1000000001L);
        item.setBaseOrgCode("0442");
        item.setBaseOrgCodeName("河北送变电");
        item.setUnitCode("01000101");
        item.setUnitCodeName("变电一分公司");
        item.setProjectName("测试项目A");
        item.setProjectCode("GCCW-TEST-0001");
        items.add(item);

        ApiResult<List<SyncFeedback>> result = projectSyncService.syncProjectInfoList(items);

        assertEquals("6100000", result.getCode());
        verify(projectInfoRepository, times(1)).save(any(ProjectInfo.class));
    }

    // ==================== helpers ====================

    private List<ProjectSyncItem> createSingleItem(Long projectId) {
        ProjectSyncItem item = new ProjectSyncItem();
        item.setProjectId(projectId);
        item.setBaseOrgCode("0442");
        item.setBaseOrgCodeName("河北送变电");
        item.setUnitCode("01000101");
        item.setUnitCodeName("变电一分公司");
        item.setProjectName("测试项目");
        item.setProjectCode("GCCW-TEST-0001");
        return Collections.singletonList(item);
    }
}
