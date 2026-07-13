package com.caizhilian.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 工程量清单主表实体
 * 对应文档 CM_SCH_BOQ_MAIN
 */
@Entity
@Table(name = "customize_boq_main")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoqMain {

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

    /** 外部BOQ主键，BOQ_GID */
    @Column(name = "boq_id")
    private Long boqId;

    /** 项目对象ID */
    @Column(name = "project_id")
    private Long projectId;

    /** 合同对象ID */
    @Column(name = "contract_id")
    private Long contractId;

    /** 基层组织编码 */
    @Column(name = "base_org_code", length = 8)
    private String baseOrgCode;

    /** 项目责任部门 */
    @Column(name = "unit_code", length = 10)
    private String unitCode;

    /** 工程量清单创建时间（来自DAP，业务字段） */
    @Column(name = "biz_create_time")
    private LocalDateTime bizCreateTime;

    /** 工程量清单最后修改时间（来自DAP，业务字段） */
    @Column(name = "biz_last_update_time")
    private LocalDateTime bizLastUpdateTime;

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
