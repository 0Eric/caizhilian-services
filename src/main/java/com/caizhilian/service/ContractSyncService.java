package com.caizhilian.service;

import com.caizhilian.common.result.ApiResult;
import com.caizhilian.common.result.SyncFeedback;
import com.caizhilian.dto.request.ContractSyncItem;

import java.util.List;

public interface ContractSyncService {
    /**
     * 批量同步合同信息
     * DAP在分包合同登记、分包合同变更完成后，同步合同信息给财智链
     */
    ApiResult<List<SyncFeedback>> syncContractInfoList(List<ContractSyncItem> items);
}
