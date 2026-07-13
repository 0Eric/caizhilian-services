package com.caizhilian.dto.request.dap;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 推送进度款审批单信息 - 请求DTO
 * 对应文档4.2.3节 "推送进度款审批单信息服务"
 * 财智链 → DAP，进度款审批签批完成后触发同步
 */
@Data
public class ProgressPaymentPushRequest {

    // ==================== 主表字段 ====================

    /** 单据主键 */
    private Long gid;
    /** 单据编号 */
    private String billid;
    /** 工程项目ID */
    private String projectId;
    /** 工程合同ID */
    private String contractId;
    /** 备注项目 */
    private String remarkItem;
    /** 项目责任部门 */
    private String projCompId;
    /** 提出单位（施工单位对象ID） */
    private String mainDeptId;
    /** 创建时间 */
    private String creTime;
    /** 批准时间 */
    private String compleApprTime;
    /** 所属单位 */
    private String compId;
    /** 本次进度款申请金额 */
    private BigDecimal currentPaymentAmount;
    /** 支付工资月份 */
    private String paySalaryMonth;
    /** 支付工资金额 */
    private BigDecimal paySalaryAmount;
    /** 统一支付金额 */
    private BigDecimal unifiedPaymentAmount;
    /** 其中安全文明施工费 */
    private BigDecimal safetyCivilFee;
    /** UDS附件ID列表 */
    private List<String> udsId;
    /** 单据状态 */
    private String docuStatus;

    // ==================== 子表明细 ====================

    /** 明细列表 */
    private List<ProgressPaymentDetailItem> items;

    @Data
    public static class ProgressPaymentDetailItem {
        /** 明细主键 */
        private Long billid;
        /** 外键（关联主表主键） */
        private Long ywid;
        /** 清单主键 */
        private String listGid;
        /** 清单编码 */
        private String listCode;
        /** 本期核定量 */
        private BigDecimal apprvQuantity;
        /** 本期核定金额 */
        private BigDecimal currTermApprvAmt;
        /** 备注 */
        private String remarkItem;
    }
}
