package com.caizhilian.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 同步合同信息 - 请求DTO
 * 对应文档4.2.2节 "同步合同信息服务"
 */
@Data
public class ContractSyncItem {
    /** 所属单位，固定取河北送变电基础组织的comp_id，如0442 */
    @NotBlank
    private String baseOrgCode;

    /** 基层组织名称 */
    private String baseOrgCodeName;

    /** 项目责任部门（组织机构ID） */
    @NotBlank
    private String unitCode;

    /** 项目责任部门名称 */
    private String unitCodeName;

    /** 合同ID，dxid */
    @NotNull
    private Long contractId;

    /** 合同名称，dxmc */
    @NotBlank
    private String contractName;

    /** 合同编号 */
    @NotBlank
    private String contractNo;

    /** 分包商ID */
    @NotNull
    private Long subcontractorId;

    /** 分包商名称 */
    @NotBlank
    private String subcontractorName;

    /** 统一信用证编码 */
    private String unifiedCreditCode;

    /** 合同金额 */
    private BigDecimal contractAmount;

    /** 关联项目ID列表 */
    private List<Long> projectIds;
}
