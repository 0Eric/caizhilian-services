package com.caizhilian.controller;

import com.caizhilian.common.result.ApiResult;
import com.caizhilian.common.result.SyncFeedback;
import com.caizhilian.dto.request.BoqSyncItem;
import com.caizhilian.dto.request.ContractSyncItem;
import com.caizhilian.dto.request.ProjectSyncItem;
import com.caizhilian.service.BoqSyncService;
import com.caizhilian.service.ContractSyncService;
import com.caizhilian.service.ProjectSyncService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DAP与财智链系统集成 - 数据同步Controller
 * 对应文档第4.2.2节 "财智链系统提供的服务"
 */
@Slf4j
@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
public class SyncController {

    private final ProjectSyncService projectSyncService;
    private final ContractSyncService contractSyncService;
    private final BoqSyncService boqSyncService;

    // ==================== 1. 同步项目信息服务 ====================

    /**
     * 同步项目信息（批量）
     * 接口技术名：syncProjectInfo
     * 调用端：DAP系统
     * 触发时机：项目登记、项目变更时
     */
    @PostMapping("/projectInfoList")
    public ApiResult<List<SyncFeedback>> syncProjectInfoList(
            @Valid @RequestBody List<ProjectSyncItem> items) {
        log.info("=== 同步项目信息 (POST /api/sync/projectInfoList) ===");
        return projectSyncService.syncProjectInfoList(items);
    }

    // ==================== 2. 同步合同信息服务 ====================

    /**
     * 同步合同信息（批量）
     * 接口技术名：syncContractInfo
     * 调用端：DAP系统
     * 触发时机：分包合同登记、分包合同变更完成后
     */
    @PostMapping("/contractInfoList")
    public ApiResult<List<SyncFeedback>> syncContractInfoList(
            @Valid @RequestBody List<ContractSyncItem> items) {
        log.info("=== 同步合同信息 (POST /api/sync/contractInfoList) ===");
        return contractSyncService.syncContractInfoList(items);
    }

    // ==================== 3. 同步工程量清单信息服务 ====================

    /**
     * 同步工程量清单信息（批量）
     * 接口技术名：syncBoqInfo
     * 调用端：DAP系统
     * 触发时机：工程量清单管理功能直接提交或审批完成时
     */
    @PostMapping("/boqInfoList")
    public ApiResult<List<SyncFeedback>> syncBoqInfoList(
            @Valid @RequestBody List<BoqSyncItem> items) {
        log.info("=== 同步工程量清单信息 (POST /api/sync/boqInfoList) ===");
        return boqSyncService.syncBoqInfoList(items);
    }
}
