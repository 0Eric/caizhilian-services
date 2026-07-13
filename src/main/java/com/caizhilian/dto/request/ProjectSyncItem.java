package com.caizhilian.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 同步项目信息 - 请求DTO
 * 对应文档4.2.2节 "同步项目信息服务"
 */
@Data
public class ProjectSyncItem {
    /** 基层组织编码，固定取河北送变电基础组织的comp_id，如0442 */
    @NotBlank
    private String baseOrgCode;

    /** 基层组织名称 */
    private String baseOrgCodeName;

    /** 项目责任部门（组织机构ID） */
    @NotBlank
    private String unitCode;

    /** 项目责任部门名称 */
    private String unitCodeName;

    /** 项目ID，dxid */
    @NotNull
    private Long projectId;

    /** 项目名称，dxmc */
    @NotBlank
    private String projectName;

    /** 项目编码，xmbm */
    @NotBlank
    private String projectCode;
}
