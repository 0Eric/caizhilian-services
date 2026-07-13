package com.caizhilian.dto.request.dap;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 推送工程量调整单信息 - 请求DTO
 * 对应文档4.2.3节 "推送工程量调整单信息服务"
 * 财智链 → DAP，工程量调整签批完成后触发同步
 */
@Data
public class BoqAdjustPushRequest {

    // ==================== 主表字段 ====================

    /** 单据主键 */
    private Long gid;
    /** 变更单号 */
    private String changeDocNo;
    /** 项目对象ID */
    private Long projectId;
    /** 合同对象ID */
    private Long contractId;
    /** 变更原因 */
    private String purChaRea;
    /** 调整/核减金额 */
    private BigDecimal adjAmt;
    /** 项目部审定金额 */
    private BigDecimal projDeptApprAmt;
    /** 分公司审定金额 */
    private BigDecimal branchApprAmt;
    /** 施工管理部审定金额 */
    private BigDecimal pmsceApprAmt;
    /** 技经中心审定金额 */
    private BigDecimal ttAmtIcTax;
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
    /** UDS附件ID列表 */
    private List<String> udsId;
    /** 单据状态 */
    private String docuStatus;

    // ==================== 子表明细 ====================

    /** 明细列表 */
    private List<BoqAdjustDetailItem> items;

    @Data
    public static class BoqAdjustDetailItem {
        /** 明细主键 */
        private Long billid;
        /** 清单主键 */
        private String listGid;
        /** 父节点清单ID */
        private String parentListId;
        /** 清单编码 */
        private String listCode;
        /** 当前工程量（合同数量） */
        private BigDecimal mateQuan;
        /** 变更后工程量（实际数量） */
        private BigDecimal changeAftCumuCptWorkload;
        /** 变更后金额（实际金额） */
        private BigDecimal changeAfterAmount;
        /** 调整金额 */
        private BigDecimal adjustAmount;
        /** 变更类型：0=无变更 1=调整 2=新增 */
        private Integer changeType;
    }
}
