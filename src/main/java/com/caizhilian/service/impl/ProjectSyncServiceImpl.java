package com.caizhilian.service.impl;

import com.caizhilian.common.result.ApiResult;
import com.caizhilian.common.result.SyncFeedback;
import com.caizhilian.dto.request.ProjectSyncItem;
import com.caizhilian.entity.ProjectInfo;
import com.caizhilian.repository.ProjectInfoRepository;
import com.caizhilian.service.ProjectSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectSyncServiceImpl implements ProjectSyncService {

    private final ProjectInfoRepository projectInfoRepository;

    @Override
    @Transactional
    public ApiResult<List<SyncFeedback>> syncProjectInfoList(List<ProjectSyncItem> items) {
        log.info("收到项目信息同步请求，数量：{}", items.size());
        List<SyncFeedback> feedbackList = new ArrayList<>();

        for (ProjectSyncItem item : items) {
            try {
                // 幂等处理：如果项目已存在则更新，不存在则新增
                ProjectInfo info = projectInfoRepository.findByProjectId(item.getProjectId())
                        .orElse(new ProjectInfo());

                info.setProjectId(item.getProjectId());
                info.setBaseOrgCode(item.getBaseOrgCode());
                info.setBaseOrgCodeName(item.getBaseOrgCodeName());
                info.setUnitCode(item.getUnitCode());
                info.setUnitCodeName(item.getUnitCodeName());
                info.setProjectName(item.getProjectName());
                info.setProjectCode(item.getProjectCode());

                // TODO 内网环境替换为当前登录用户ID：info.setOwnerId(currentUserId);
                // TODO 内网环境替换为当前登录用户ID：info.setFounder(currentUserId);
                // TODO 内网环境替换为当前登录用户ID：info.setLsModifier(currentUserId);

                projectInfoRepository.save(info);

                feedbackList.add(new SyncFeedback(item.getProjectId(), "项目信息同步成功"));
                log.info("项目同步成功：projectId={}, projectName={}", item.getProjectId(), item.getProjectName());
            } catch (Exception e) {
                log.error("项目同步失败：projectId={}, error={}", item.getProjectId(), e.getMessage());
                feedbackList.add(new SyncFeedback(item.getProjectId(), "同步失败：" + e.getMessage()));
            }
        }

        return ApiResult.success(feedbackList);
    }
}
