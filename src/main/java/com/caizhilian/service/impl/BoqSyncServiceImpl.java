package com.caizhilian.service.impl;

import com.caizhilian.common.result.ApiResult;
import com.caizhilian.common.result.SyncFeedback;
import com.caizhilian.dto.request.BoqSyncItem;
import com.caizhilian.entity.BoqDetail;
import com.caizhilian.entity.BoqMain;
import com.caizhilian.repository.BoqDetailRepository;
import com.caizhilian.repository.BoqMainRepository;
import com.caizhilian.service.BoqSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BoqSyncServiceImpl implements BoqSyncService {

    private final BoqMainRepository boqMainRepository;
    private final BoqDetailRepository boqDetailRepository;

    @Override
    @Transactional
    public ApiResult<List<SyncFeedback>> syncBoqInfoList(List<BoqSyncItem> items) {
        log.info("收到工程量清单同步请求，数量：{}", items.size());
        List<SyncFeedback> feedbackList = new ArrayList<>();

        for (BoqSyncItem item : items) {
            try {
                // 幂等处理：如果BOQ已存在则更新
                BoqMain main = boqMainRepository.findByBoqId(item.getBoqId())
                        .orElse(new BoqMain());

                main.setBoqId(item.getBoqId());
                main.setProjectId(item.getProjectId());
                main.setContractId(item.getContractId());
                main.setBaseOrgCode(item.getBaseOrgCode());
                main.setUnitCode(item.getUnitCode());
                main.setBizCreateTime(item.getCreateTime());
                main.setBizLastUpdateTime(item.getLastUpdateTime());

                boqMainRepository.save(main);

                // 处理明细：先清除旧明细，再插入新明细
                boqDetailRepository.deleteByBoqId(item.getBoqId());

                if (item.getBoqDetailList() != null && !item.getBoqDetailList().isEmpty()) {
                    for (BoqSyncItem.BoqDetailItem detailItem : item.getBoqDetailList()) {
                        BoqDetail detail = new BoqDetail();
                        detail.setDetailId(detailItem.getDetailId());
                        detail.setBoqId(item.getBoqId());
                        detail.setDetailCode(detailItem.getDetailCode());
                        detail.setDetailName(detailItem.getDetailName());
                        detail.setFeatureDescription(detailItem.getFeatureDescription());
                        detail.setMeasurementUnit(detailItem.getMeasurementUnit());
                        detail.setUnitPriceWithTax(detailItem.getUnitPriceWithTax());
                        detail.setTaxrate(detailItem.getTaxrate());
                        detail.setOriginalQuantity(detailItem.getOriginalQuantity());
                        detail.setCumulativeChangeQuantity(detailItem.getCumulativeChangeQuantity());
                        detail.setContractAmount(detailItem.getContractAmount());
                        detail.setCurrentQuantity(detailItem.getCurrentQuantity());
                        detail.setRemark(detailItem.getRemark());
                        detail.setCurrentTotalAmount(detailItem.getCurrentTotalAmount());
                        detail.setIsLeafNode(detailItem.getIsLeafNode());
                        detail.setNodeLevel(detailItem.getNodeLevel());
                        detail.setApprovedQuantityCumulative(detailItem.getApprovedQuantityCumulative());

                        boqDetailRepository.save(detail);
                    }
                }

                feedbackList.add(new SyncFeedback(item.getBoqId(), "工程量清单同步成功"));
                log.info("BOQ同步成功：boqId={}", item.getBoqId());
            } catch (Exception e) {
                log.error("BOQ同步失败：boqId={}, error={}", item.getBoqId(), e.getMessage());
                feedbackList.add(new SyncFeedback(item.getBoqId(), "同步失败：" + e.getMessage()));
            }
        }

        return ApiResult.success(feedbackList);
    }
}
