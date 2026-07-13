package com.caizhilian.controller;

import com.caizhilian.common.result.ApiResult;
import com.caizhilian.common.result.SyncFeedback;
import com.caizhilian.dto.request.dap.BoqAdjustPushRequest;
import com.caizhilian.dto.request.dap.ProgressPaymentPushRequest;
import com.caizhilian.dto.request.dap.SettlementPushRequest;
import com.caizhilian.dto.request.dap.VisaPushRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

/**
 * DAP系统 - 数据接收Mock Controller
 * 对应文档4.2.3节 "DAP系统提供的服务"
 * 模拟DAP系统提供的接收接口，供财智链系统回调
 *
 * ⚠️ 注意：此为Mock实现，业务逻辑均为 //TODO 占位
 * 实际集成时需替换为真实的DAP业务处理代码
 */
@Slf4j
@RestController
@RequestMapping("/dap/mock")
public class DapMockController {

    /**
     * 1. 推送工程签证单信息
     * 财智链系统工程签证功能签批完成后触发同步给DAP系统
     *
     * POST /dap/mock/receive/visa
     */
    @PostMapping("/receive/visa")
    public ApiResult<SyncFeedback> receiveVisa(@RequestBody VisaPushRequest request) {
        log.info("=== DAP接收工程签证单信息 ===");
        log.info("confirmGid={}, changeNt={}, projectId={}", request.getConfirmGid(), request.getChangeNt(), request.getProjectId());

        // TODO: 将签证数据写入DAP业务表
        // TODO: 处理UDS附件下载并关联单据
        // TODO: 根据docuStatus判断是新增还是作废，作废时处理后续逻辑
        // TODO: 基于projectId自动填充项目编码、名称等对象属性
        // TODO: 基于orgCotNo自动填充合同编码、名称等对象属性

        SyncFeedback feedback = new SyncFeedback(request.getConfirmGid(), "工程签证信息接收成功");
        return ApiResult.success(feedback);
    }

    /**
     * 2. 推送工程量调整单信息
     * 财智链系统工程量调整功能签批完成后触发同步给DAP系统
     *
     * POST /dap/mock/receive/boq-adjust
     */
    @PostMapping("/receive/boq-adjust")
    public ApiResult<SyncFeedback> receiveBoqAdjust(@RequestBody BoqAdjustPushRequest request) {
        log.info("=== DAP接收工程量调整单信息 ===");
        log.info("gid={}, changeDocNo={}, projectId={}, contractId={}, detailCount={}",
                request.getGid(), request.getChangeDocNo(), request.getProjectId(), request.getContractId(),
                request.getItems() != null ? request.getItems().size() : 0);

        // TODO: 将工程量调整主表数据写入DAP业务表
        // TODO: 循环处理子表明细，写入工程量调整明细表
        // TODO: 处理UDS附件下载并关联单据
        // TODO: 根据docuStatus判断是新增还是作废
        // TODO: 基于projectId/contractId自动填充对象属性

        SyncFeedback feedback = new SyncFeedback(request.getGid(), "工程量调整信息接收成功");
        return ApiResult.success(feedback);
    }

    /**
     * 3. 推送进度款审批单信息
     * 财智链系统进度款审批功能签批完成后触发同步给DAP系统
     *
     * POST /dap/mock/receive/progress-payment
     */
    @PostMapping("/receive/progress-payment")
    public ApiResult<SyncFeedback> receiveProgressPayment(@RequestBody ProgressPaymentPushRequest request) {
        log.info("=== DAP接收进度款审批单信息 ===");
        log.info("gid={}, billid={}, projectId={}, contractId={}, itemCount={}",
                request.getGid(), request.getBillid(), request.getProjectId(), request.getContractId(),
                request.getItems() != null ? request.getItems().size() : 0);

        // TODO: 将进度款审批主表数据写入DAP业务表
        // TODO: 循环处理子表明细（工程量计量清单数据），写入进度款审批明细表
        // TODO: 处理UDS附件下载并关联单据
        // TODO: 根据docuStatus判断是新增还是作废
        // TODO: 自动填充项目/合同对象属性

        SyncFeedback feedback = new SyncFeedback(request.getGid(), "进度款审批信息接收成功");
        return ApiResult.success(feedback);
    }

    /**
     * 4. 推送结算审核定案单信息
     * 财智链系统结算审核定案单功能签批完成后触发同步给DAP系统
     *
     * POST /dap/mock/receive/settlement
     */
    @PostMapping("/receive/settlement")
    public ApiResult<SyncFeedback> receiveSettlement(@RequestBody SettlementPushRequest request) {
        log.info("=== DAP接收结算审核定案单信息 ===");
        log.info("gid={}, appliNo={}, projectId={}, contractId={}, detailCount={}, visaCount={}",
                request.getGid(), request.getAppliNo(), request.getProjectId(), request.getContractId(),
                request.getDetailItems() != null ? request.getDetailItems().size() : 0,
                request.getVisaItems() != null ? request.getVisaItems().size() : 0);

        // TODO: 将结算审核定案主表数据写入DAP业务表
        // TODO: 循环处理结算明细子表，写入结算明细表
        // TODO: 循环处理签证明细子表，写入签证明细表
        // TODO: 处理UDS附件下载并关联单据
        // TODO: 根据docuStatus判断是新增还是作废
        // TODO: 自动填充对象属性

        SyncFeedback feedback = new SyncFeedback(request.getGid(), "结算审核定案信息接收成功");
        return ApiResult.success(feedback);
    }
}
