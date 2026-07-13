package com.caizhilian.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 合同与项目关联关系实体
 * 用于存储合同关联的多个项目ID
 */
@Entity
@Table(name = "customize_contract_project_ref")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractProjectRef {

    // ==================== 通用字段 ====================

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gid")
    private Long gid;

    /** 所有者ID */
    @Column(name = "owner_id", length = 36)
    private String ownerId;

    /** 业务状态（默认0=正常） */
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

    /** 合同ID（关联customize_contract_info的contract_id字段） */
    @Column(name = "contract_id")
    private Long contractId;

    /** 项目ID */
    @Column(name = "project_id")
    private Long projectId;

    @PrePersist
    protected void onCreate() {
        if (createTime == null) createTime = LocalDateTime.now();
        lastTime = LocalDateTime.now();
        if (ownerId == null) ownerId = "admin";
        if (fBizstate == null) fBizstate = 0;
        if (founder == null) founder = 0;
        if (lsModifier == null) lsModifier = 0;
    }

    @PreUpdate
    protected void onUpdate() {
        lastTime = LocalDateTime.now();
        lsModifier = 0;
    }
}
