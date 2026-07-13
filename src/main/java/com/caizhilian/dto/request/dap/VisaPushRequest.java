package com.caizhilian.dto.request.dap;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 推送工程签证单信息 - 请求DTO
 * 对应文档4.2.3节 "推送工程签证单信息服务"
 * 财智链 → DAP，签批完成后触发同步
 */
@Data
public class VisaPushRequest {
    /** 财智链单据主键 */
    private Long confirmGid;
    /** 申请编号 */
    private String changeNt;
    /** 项目对象ID */
    private String projectId;
    /** 合同对象ID */
    private String orgCotNo;
    /** 签证事由 */
    private String busContent;
    /** 备注 */
    private String note;
    /** 调整/核减金额 */
    private BigDecimal adjAmt;
    /** 项目部审定金额 */
    private BigDecimal projDeptApprAmt;
    /** 分公司审定金额 */
    private BigDecimal branchApprAmt;
    /** 施工管理部审定金额 */
    private BigDecimal pmsceApprAmt;
    /** 技经中心审定金额（签证金额） */
    private BigDecimal ttAmtIcTax;
    /** 批准日期 */
    private String compleApprTime;
    /** 创建日期 */
    private String creTime;
    /** 创建单位代码 */
    private String compId;
    /** 项目责任部门 */
    private Integer projCompId;
    /** 关联签证单位（施工方管理对象ID） */
    private Integer conCompanyId;
    /** UDS附件ID列表 */
    private List<String> udsId;
    /** 单据状态 */
    private String docuStatus;
}
