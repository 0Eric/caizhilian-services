package com.caizhilian.service;

import com.caizhilian.common.result.ApiResult;
import com.caizhilian.common.result.SyncFeedback;
import com.caizhilian.dto.request.ProjectSyncItem;

import java.util.List;

public interface ProjectSyncService {
    /**
     * 批量同步项目信息
     * DAP系统在项目登记、项目变更时调用，将项目信息同步给财智链
     */
    ApiResult<List<SyncFeedback>> syncProjectInfoList(List<ProjectSyncItem> items);
}
