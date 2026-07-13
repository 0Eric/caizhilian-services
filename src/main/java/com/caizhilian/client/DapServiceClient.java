package com.caizhilian.client;

import com.caizhilian.common.result.ApiResult;
import com.caizhilian.common.result.SyncFeedback;
import com.caizhilian.dto.request.dap.BoqAdjustPushRequest;
import com.caizhilian.dto.request.dap.ProgressPaymentPushRequest;
import com.caizhilian.dto.request.dap.SettlementPushRequest;
import com.caizhilian.dto.request.dap.VisaPushRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * DAP系统服务客户端
 * 财智链系统通过此客户端调用DAP系统提供的4个接口
 *
 * 对应文档4.2.3节 "DAP系统提供的服务"
 *
 * 调用时机：
 * - 工程签证单签批完成 → pushVisa()
 * - 工程量调整单签批完成 → pushBoqAdjust()
 * - 进度款审批单签批完成 → pushProgressPayment()
 * - 结算审核定案单签批完成 → pushSettlement()
 */
@Slf4j
@Component
public class DapServiceClient {

    private final RestTemplate restTemplate;

    @Value("${dap.base-url:http://localhost:8080/dap/mock}")
    private String dapBaseUrl;

    public DapServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 1. 推送工程签证单信息到DAP
     * 触发时机：工程签证功能签批完成或单据作废后
     */
    public ApiResult<SyncFeedback> pushVisa(VisaPushRequest request) {
        String url = dapBaseUrl + "/receive/visa";
        log.info("调用DAP推送工程签证单信息: url={}, confirmGid={}", url, request.getConfirmGid());

        try {
            ApiResult<SyncFeedback> result = restTemplate.postForObject(url, request, ApiResult.class);
            log.info("DAP推送工程签证单响应: {}", result);
            return result;
        } catch (Exception e) {
            log.error("DAP推送工程签证单失败: confirmGid={}, error={}", request.getConfirmGid(), e.getMessage());
            return ApiResult.networkError("调用DAP工程签证接口失败: " + e.getMessage());
        }
    }

    /**
     * 2. 推送工程量调整单信息到DAP
     * 触发时机：工程量调整功能签批完成或单据作废后
     */
    public ApiResult<SyncFeedback> pushBoqAdjust(BoqAdjustPushRequest request) {
        String url = dapBaseUrl + "/receive/boq-adjust";
        log.info("调用DAP推送工程量调整单信息: url={}, gid={}", url, request.getGid());

        try {
            ApiResult<SyncFeedback> result = restTemplate.postForObject(url, request, ApiResult.class);
            log.info("DAP推送工程量调整单响应: {}", result);
            return result;
        } catch (Exception e) {
            log.error("DAP推送工程量调整单失败: gid={}, error={}", request.getGid(), e.getMessage());
            return ApiResult.networkError("调用DAP工程量调整接口失败: " + e.getMessage());
        }
    }

    /**
     * 3. 推送进度款审批单信息到DAP
     * 触发时机：进度款审批功能签批完成或单据作废后
     */
    public ApiResult<SyncFeedback> pushProgressPayment(ProgressPaymentPushRequest request) {
        String url = dapBaseUrl + "/receive/progress-payment";
        log.info("调用DAP推送进度款审批单信息: url={}, gid={}", url, request.getGid());

        try {
            ApiResult<SyncFeedback> result = restTemplate.postForObject(url, request, ApiResult.class);
            log.info("DAP推送进度款审批单响应: {}", result);
            return result;
        } catch (Exception e) {
            log.error("DAP推送进度款审批单失败: gid={}, error={}", request.getGid(), e.getMessage());
            return ApiResult.networkError("调用DAP进度款审批接口失败: " + e.getMessage());
        }
    }

    /**
     * 4. 推送结算审核定案单信息到DAP
     * 触发时机：结算审核定案单功能签批完成或单据作废后
     */
    public ApiResult<SyncFeedback> pushSettlement(SettlementPushRequest request) {
        String url = dapBaseUrl + "/receive/settlement";
        log.info("调用DAP推送结算审核定案单信息: url={}, gid={}", url, request.getGid());

        try {
            ApiResult<SyncFeedback> result = restTemplate.postForObject(url, request, ApiResult.class);
            log.info("DAP推送结算审核定案单响应: {}", result);
            return result;
        } catch (Exception e) {
            log.error("DAP推送结算审核定案单失败: gid={}, error={}", request.getGid(), e.getMessage());
            return ApiResult.networkError("调用DAP结算审核定案接口失败: " + e.getMessage());
        }
    }
}
