package com.caizhilian.dto.request.dap;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 推送结算审核定案单信息 - 请求DTO
 * 对应文档4.2.3节 "推送结算审核定案单信息服务"
 * 财智链 → DAP，结算审核定案签批完成后触发同步
 */
@Data
public class SettlementPushRequest {

    // ==================== 主表字段 ====================

    /** 单据主键 */
    private Long gid;
    /** 结算编号 */
    private String appliNo;
    /** 项目ID */
    private Long projectId;
    /** 结算审定日期 */
    private String stlCfmDate;
    /** 项目所属组织 */
    private String projCompId;
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
    /** 合同ID */
    private Long contractId;
    /** 分包单位 */
    private String mainDeptId;
    /** 结算审定金额 */
    private BigDecimal amtSubRev;
    /** 签证金额合计 */
    private BigDecimal confirmVariatOrder;
    /** 本体工程量调整金额 */
    private BigDecimal bodyAdjAmt;
    /** 施工业务外包内容 */
    private String outsourcingContent;
    /** 施工业务外包类型 */
    private String outsourcingType;

    // ==================== 结算明细子表 ====================

    /** 结算明细列表 */
    private List<SettlementDetailItem> detailItems;

    // ==================== 签证明细子表 ====================

    /** 签证明细列表 */
    private List<VisaDetailItem> visaItems;

    @Data
    public static class SettlementDetailItem {
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
        /** 变更后金额（结算金额） */
        private BigDecimal changeAfterAmount;
        /** 调整金额 */
        private BigDecimal adjustAmount;
    }

    @Data
    public static class VisaDetailItem {
        /** 签证编码 */
        private String visaCode;
        /** 签证名称 */
        private String visaName;
        /** 结算金额 */
        private BigDecimal settlementAmount;
        /** 调整金额 */
        private BigDecimal adjustAmount;
    }
}
