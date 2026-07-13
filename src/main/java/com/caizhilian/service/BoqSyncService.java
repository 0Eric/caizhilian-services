package com.caizhilian.service;

import com.caizhilian.common.result.ApiResult;
import com.caizhilian.common.result.SyncFeedback;
import com.caizhilian.dto.request.BoqSyncItem;

import java.util.List;

public interface BoqSyncService {
    /**
     * 批量同步工程量清单信息
     * DAP工程量清单管理功能直接提交或审批完成时触发同步给财智链
     */
    ApiResult<List<SyncFeedback>> syncBoqInfoList(List<BoqSyncItem> items);
}
