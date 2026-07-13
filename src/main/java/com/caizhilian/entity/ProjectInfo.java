package com.caizhilian.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 项目信息实体
 * 对应文档中的项目信息同步
 */
@Entity
@Table(name = "customize_project_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInfo {

    // ==================== 通用字段 ====================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gid")
    private Long gid;

    /** 所有者ID */
    @Column(name = "owner_id", length = 36)
    private String ownerId;

    /** 业务状态 */
    @Column(name = "f_bizstate")
    private Integer fBizstate;

    /** 记录创建时间 */
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /** 创建人 */
    @Column(name = "founder")
    private Integer founder;

    /** 记录修改时间 */
    @Column(name = "last_time")
    private LocalDateTime lastTime;

    /** 修改人 */
    @Column(name = "ls_modifier")
    private Integer lsModifier;

    // ==================== 业务字段 ====================

    /** 基层组织编码 */
    @Column(name = "base_org_code", length = 8)
    private String baseOrgCode;

    /** 基层组织名称 */
    @Column(name = "base_org_code_name", length = 100)
    private String baseOrgCodeName;

    /** 项目责任部门 */
    @Column(name = "unit_code", length = 10)
    private String unitCode;

    /** 项目责任部门名称 */
    @Column(name = "unit_code_name", length = 100)
    private String unitCodeName;

    /** 外部项目ID（文档中的projectId） */
    @Column(name = "project_id")
    private Long projectId;

    /** 项目名称 */
    @Column(name = "project_name", length = 300)
    private String projectName;

    /** 项目编码 */
    @Column(name = "project_code", length = 500)
    private String projectCode;

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        lastTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        lastTime = LocalDateTime.now();
    }
}
