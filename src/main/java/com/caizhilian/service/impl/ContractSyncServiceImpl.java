package com.caizhilian.service.impl;

import com.caizhilian.common.result.ApiResult;
import com.caizhilian.common.result.SyncFeedback;
import com.caizhilian.dto.request.ContractSyncItem;
import com.caizhilian.entity.ContractInfo;
import com.caizhilian.entity.ContractProjectRef;
import com.caizhilian.repository.ContractInfoRepository;
import com.caizhilian.repository.ContractProjectRefRepository;
import com.caizhilian.service.ContractSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractSyncServiceImpl implements ContractSyncService {

    private final ContractInfoRepository contractInfoRepository;
    private final ContractProjectRefRepository contractProjectRefRepository;

    @Override
    @Transactional
    public ApiResult<List<SyncFeedback>> syncContractInfoList(List<ContractSyncItem> items) {
        log.info("收到合同信息同步请求，数量：{}", items.size());
        List<SyncFeedback> feedbackList = new ArrayList<>();

        for (ContractSyncItem item : items) {
            try {
                // 幂等处理：如果合同已存在则更新
                ContractInfo info = contractInfoRepository.findByContractId(item.getContractId())
                        .orElse(new ContractInfo());

                info.setContractId(item.getContractId());
                info.setBaseOrgCode(item.getBaseOrgCode());
                info.setBaseOrgCodeName(item.getBaseOrgCodeName());
                info.setUnitCode(item.getUnitCode());
                info.setUnitCodeName(item.getUnitCodeName());
                info.setContractName(item.getContractName());
                info.setContractNo(item.getContractNo());
                info.setSubcontractorId(item.getSubcontractorId());
                info.setSubcontractorName(item.getSubcontractorName());
                info.setUnifiedCreditCode(item.getUnifiedCreditCode());
                info.setContractAmount(item.getContractAmount());

                // TODO 内网环境替换为当前登录用户ID：info.setOwnerId(currentUserId);
                // TODO 内网环境替换为当前登录用户ID：info.setFounder(currentUserId);
                // TODO 内网环境替换为当前登录用户ID：info.setLsModifier(currentUserId);

                contractInfoRepository.save(info);

                // 保存合同-项目关联关系
                if (item.getProjectIds() != null && !item.getProjectIds().isEmpty()) {
                    // 先清除旧的关联
                    contractProjectRefRepository.deleteByContractId(item.getContractId());
                    // 再插入新的关联
                    for (Long projectId : item.getProjectIds()) {
                        ContractProjectRef ref = new ContractProjectRef();
                        ref.setContractId(item.getContractId());
                        ref.setProjectId(projectId);
                        contractProjectRefRepository.save(ref);
                    }
                }

                feedbackList.add(new SyncFeedback(item.getContractId(), "合同信息同步成功"));
                log.info("合同同步成功：contractId={}, contractName={}", item.getContractId(), item.getContractName());
            } catch (Exception e) {
                log.error("合同同步失败：contractId={}, error={}", item.getContractId(), e.getMessage());
                feedbackList.add(new SyncFeedback(item.getContractId(), "同步失败：" + e.getMessage()));
            }
        }

        return ApiResult.success(feedbackList);
    }
}
